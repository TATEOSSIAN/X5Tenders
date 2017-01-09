package com.support;

import com.assembly.X5Tenders;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 *
 * @author AZAZELLO
 */
public class DBSession {

    private DBSession() {
        
        Connection saConnection = null;
        
        try {
            
            ResourceBundle saCredentials = ResourceBundle.getBundle("DDL\\sacredential");
            
            saConnection = DriverManager.getConnection(
                    saCredentials.getString("jdbc.url"),
                    saCredentials.getString("jdbc.username"),
                    saCredentials.getString("jdbc.password"));

        } catch (SQLException ex) {

            X5Tenders.logThrownRecord(ConnectionProps.DRIVER, ex);
        }
        
        if (saConnection != null) {        
            initializeDB(saConnection);
        
            try {
                saConnection.close();
            } catch (SQLException ex) {
                X5Tenders.logThrownRecord(ConnectionProps.DRIVER, ex);
            }
        }
        
        try {
            
            ConnectionProps.CONNECTION = DriverManager.getConnection(
                    ConnectionProps.PROPERTIES.getString("jdbc.url"),
                    ConnectionProps.PROPERTIES.getString("jdbc.username"),
                    ConnectionProps.PROPERTIES.getString("jdbc.password"));
            
            ConnectionProps.CONNECTION.setCatalog("X5Tenders");

        } catch (SQLException ex) {

            X5Tenders.logThrownRecord(ConnectionProps.DRIVER, ex);
        }        
              
        createTables();

    }

    public static DBSession getInstance() {
        return DBSessionHolder.INSTANCE;
    }
    
    public static Connection getConnection() {
        return ConnectionProps.CONNECTION;  
    }


    private static class DBSessionHolder {

        private static final DBSession INSTANCE = new DBSession();

    }

    private static class ConnectionProps {

        private static final ResourceBundle STRINGS = ResourceBundle.getBundle("Strings");
        private static final String DRIVER = STRINGS.getString("java.sql.driver");
        private static final ResourceBundle PROPERTIES = ResourceBundle.getBundle("DDL\\database");        
        private static final URL DDL_INITDB = ClassLoader.getSystemResource("DDL\\DBINIT.sql");
        private static final URL DDL_CREATETABLE = ClassLoader.getSystemResource("DDL\\CreateTables.sql");
        
        private static Connection CONNECTION;

    }
    
    private static void createTables() {
        URL locname = ConnectionProps.DDL_CREATETABLE;
        String tSQL = getQuery(locname);       
        
        try {        
            Statement stmt = ConnectionProps.CONNECTION.createStatement();
            
            stmt.addBatch("SET ANSI_NULLS ON");
            stmt.addBatch("SET QUOTED_IDENTIFIER ON");
            stmt.addBatch(tSQL);
            stmt.executeBatch();
            
        } catch (SQLException ex) {
            X5Tenders.logThrownRecord("Error while SQL query execute", ex);
        }
        
    }
    
    private static void initializeDB(Connection conn) {

    //<editor-fold defaultstate="collapsed" desc="Возможно понадобится">
    /*URL locname = ConnectionProps.DDL_INITDB;

    String tSQL = getQuery(locname);

    if (tSQL.isEmpty()) {
    X5Tenders.logMessageRecord("SQL query is empty");
    return;
    }*/
    //</editor-fold>
        
        ResourceBundle rb = ResourceBundle.getBundle("DDL\\sacredential");
        
        String dirs = rb.getString("jdbc.dirs");
        String mdf = "N'".concat(dirs.concat("\\X5Tenders.mdf")).concat("'");
        String ldf = "N'".concat(dirs.concat("\\X5Tenders_log.ldf")).concat("'");

        String key = "N'".concat(ConnectionProps.PROPERTIES.
                getString("jdbc.password")).concat("'");

        try {
            
            Statement stmt = conn.createStatement();
            conn.setAutoCommit(true);
            
            conn.setCatalog("master");
            stmt.addBatch("SET NOCOUNT ON");
            stmt.addBatch("IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'X5Tenders')\n"
                    + "BEGIN \n"
                    + "CREATE DATABASE [X5Tenders] ON PRIMARY\n"
                    + "(NAME = N'X5Tenders', FILENAME ="+mdf+", SIZE=1800MB,"
                                + "MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB)\n"
                    + "LOG ON\n"
                    + "(NAME = N'X5Tenders_log', FILENAME ="+ldf+", SIZE=5120KB,"
                                + "MAXSIZE = 260046848KB , FILEGROWTH = 10%)\n"
                    + "END");
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET COMPATIBILITY_LEVEL = 100");
            stmt.executeBatch();
            conn.setCatalog("X5Tenders");
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET ANSI_NULL_DEFAULT OFF");
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET ANSI_NULLS OFF");
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET ANSI_PADDING OFF");
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET ANSI_WARNINGS OFF");
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET ARITHABORT OFF");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET AUTO_CLOSE OFF");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET AUTO_SHRINK ON");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET AUTO_UPDATE_STATISTICS ON");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET CURSOR_CLOSE_ON_COMMIT OFF");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET CURSOR_DEFAULT  GLOBAL");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET CONCAT_NULL_YIELDS_NULL OFF");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET NUMERIC_ROUNDABORT OFF");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET QUOTED_IDENTIFIER OFF");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET RECURSIVE_TRIGGERS OFF");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET  DISABLE_BROKER");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET AUTO_UPDATE_STATISTICS_ASYNC OFF");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET DATE_CORRELATION_OPTIMIZATION OFF");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET TRUSTWORTHY OFF");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET ALLOW_SNAPSHOT_ISOLATION OFF");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET PARAMETERIZATION SIMPLE");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET READ_COMMITTED_SNAPSHOT OFF");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET HONOR_BROKER_PRIORITY OFF");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET RECOVERY SIMPLE");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET  MULTI_USER");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET PAGE_VERIFY CHECKSUM");            
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET DB_CHAINING OFF");
            stmt.executeBatch();
            conn.setCatalog("master");
            stmt.addBatch("ALTER DATABASE [X5Tenders] SET  READ_WRITE");
            stmt.addBatch("IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE name = N'scripter')\n"
                    + "CREATE LOGIN [scripter] WITH PASSWORD="+key+", DEFAULT_DATABASE=[X5Tenders], DEFAULT_LANGUAGE=[русский], "
                    + "CHECK_EXPIRATION=OFF, CHECK_POLICY=ON");
            stmt.executeBatch();
            
            CallableStatement cstmt = conn.prepareCall("{call master..sp_addsrvrolemember(?,?)}");
            conn.setCatalog("master");
            cstmt.setNString(1, "scripter");
            cstmt.setNString(2, "sysadmin");
            cstmt.execute();
            
            cstmt = conn.prepareCall("{call [X5Tenders].[dbo].sp_changedbowner(?,?)}");
            conn.setCatalog("master");
            cstmt.setNString(1, "scripter");
            cstmt.setBoolean(2, false);
            cstmt.execute();

            } catch (SQLException ex) {
                X5Tenders.logThrownRecord("Error while SQL query execute", ex);
        }        
    }    
    
    private static String getQuery(URL path) {
        
        String tSQL = "";
        BufferedInputStream bin = null;
        
        try {
            //create object of BufferedInputStream
            bin = new BufferedInputStream((InputStream) path.getContent());
                       
            //create a byte array
            byte[] contents = new byte[4096];
            int bytesRead=0;
            
            while((bytesRead = bin.read(contents)) != -1){
                tSQL = new String(contents, 0, bytesRead);
            }          
        } catch(FileNotFoundException e) {
            X5Tenders.logThrownRecord("File not found", e);
        } catch(IOException ioe) {
            X5Tenders.logThrownRecord("Exception while reading the file", ioe);
        }
        finally {
            //close the BufferedInputStream using close method
            try {
                if(bin != null)
                    bin.close();
            }catch(IOException ioe) {
                    X5Tenders.logThrownRecord("Error while closing the stream :",
                            ioe);
            }                
        }        
        return tSQL;        
    }

    /**
     *
     * @param tSQL String, SQL query
     */
    public static void execSQL(String tSQL) {
        
        if (tSQL.isEmpty()) {
            X5Tenders.logMessageRecord("SQL query is empty");
            return;
        }
        
        Connection conn = ConnectionProps.CONNECTION;

        try {
            
            Statement stat = conn.createStatement();
            //System.out.println(tSQL);
            stat.execute(tSQL);            

            } catch (SQLException ex) {
                X5Tenders.logThrownRecord("Error while SQL query execute", ex);
        }
        
    }
    
}