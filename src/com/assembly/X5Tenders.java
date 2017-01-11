/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly;

import com.support.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.ConnectException;
import java.sql.*;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author AZAZELLO
 */
public class X5Tenders implements Runnable {

    private static void collectLotData() {
        for (TenderRow row : ParsedTenderTable.getRows()) {
            
            Lot lot = new Lot(row);
            
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        DBSession.getConnection().close();
    }
    
    private static final Logger LOG = Logger.getLogger("X5Tender");
    public static final ResourceBundle LABELS = ResourceBundle.getBundle("LStrings", new Locale("ru", "RU"));
    
    public static final String WORKFLOW = System.getenv("LOCALAPPDATA")
            .concat("\\").concat("X5Tenders").concat("\\");

    /**
     * @param args Аргументы командной строки
     */
    public static void main(String[] args) throws IllegalArgumentException {
        
        Thread t = new Thread(new X5Tenders());
        t.start();
        
        Runnable runnable = () -> DBSession.getInstance();
        Thread r = new Thread(runnable);
        r.start();
        
        try {
            r.join();
            t.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(X5Tenders.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        checkData();
        
        collectLotData();
        
    }
    
    public static void logThrownRecord(String Message, Throwable thrown) {
        LOG.log(Level.SEVERE, Message, thrown);
    }
   
    public static void logMessageRecord(String Message) {
        LOG.log(Level.SEVERE, Message);
    }

    private static void checkData() {

        if (DBSession.getConnection() != null) {
            
            Connection link = DBSession.getConnection();
            try {
                link.setCatalog("X5Tenders");
                
                ResultSet rs = null;
                boolean bl = false;
                
                PreparedStatement stmt = link.prepareStatement(
                        "IF NOT EXISTS (SELECT 1 FROM dbo.TenderList WHERE number = ?)\n"
                        + "INSERT INTO [dbo].[TenderList] \n"
                        + "([Number],[Description],[StartReq],[Start],[Finish]) \n"
                        + "VALUES (?,?,?,?,?)");
                
                for (TenderRow tr : ParsedTenderTable.getRows()) {
                    stmt.setNString(1, tr.getNumber());
                    stmt.setNString(2, tr.getNumber());
                    stmt.setNString(3, tr.getDescription());
                    Timestamp ts = castTimestamp(tr.getStartReq());
                    stmt.setTimestamp(4, ts);
                    ts = castTimestamp(tr.getStartDate());
                    stmt.setTimestamp(5, ts);
                    ts = castTimestamp(tr.getFinishDate());
                    stmt.setTimestamp(6, ts);
                    
                    bl = stmt.execute();
                }                
                
            } catch (SQLException ex) {
                Logger.getLogger(X5Tenders.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }

    private static Timestamp castTimestamp(java.util.Date d) throws SQLException {        
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        return new Timestamp(cal.getTimeInMillis());
    }
    
    @Override
    public void run() {
        
        try {
            Extractor extractor = new Extractor();
        } catch (ConnectException ex) {
            logThrownRecord(LABELS.getString("Conn_Error_construct_Extractor"), ex);
        } catch (IOException exio) {
            logThrownRecord(LABELS.getString("IO_Error_construct_Extractor"), exio);
        }
        
    }
}
