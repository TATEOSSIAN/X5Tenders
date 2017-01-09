package com.assembly;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ParsedTenderTable implements Iterable<TenderRow>{
    
    private static final List<TenderRow> ROWS = new ArrayList<TenderRow>();
    private static final Columns COLUMNS = new Columns();    

    public Columns columns() {
        return COLUMNS;
    }

    public static List<TenderRow> getRows() {
        return ROWS;
    }
    
    public TenderRow add() {
        
        TenderRow newRow = new TenderRow();
        ROWS.add(newRow);
        
        return newRow;
    }
    
    public static ParsedTenderTable getInstance() {
        return ParsedTenderTableHolder.INSTANCE;
    }

    
    
    private static class ParsedTenderTableHolder {

        private static final ParsedTenderTable INSTANCE = new ParsedTenderTable();
    }

    @Override
    public Iterator<TenderRow> iterator() {
        return new TableIterator();
    }
    
    public static void print() {
        
        try {
            
            File file = new File(X5Tenders.WORKFLOW);
            if (!file.exists())
                file.mkdir();
            file = new File(X5Tenders.WORKFLOW.concat("\\tendersReport.csv"));
            file.createNewFile();
            
            PrintWriter out = new PrintWriter(file, "Cp1251");          
            out.print(getInstance().toString());
            out.close();          
            
        } catch (FileNotFoundException ex) {
            X5Tenders.logThrownRecord(X5Tenders.LABELS.getString("Error_print_out"), ex);
        } catch (IOException ex) {
            X5Tenders.logThrownRecord(X5Tenders.LABELS.getString("Error_print_out"), ex);
        }
    }

    @Override
    public String toString() {
        String report = "";
        
        report = report + COLUMNS.toString().concat(System.lineSeparator());
        
        for (TenderRow row : ROWS) {
            report += row.toString().concat(System.lineSeparator());            
        }
        return report;
    }
    
    public static class Columns {

        @Override
        public String toString() {
            String st = "";
            for (Field c : Columns.class.getDeclaredFields()) {
                String[] cName = c.getName().split("_");
                String mName = "";
                for (String lcName : cName) {
                    mName += lcName.substring(0,1) + 
                        lcName.substring(1).toLowerCase();                    
                }
                st = st.concat(mName).concat(",");         
            }
            st = st.substring(0, st.length()-1);
            return st;
        }   
        
        public static final int NUMBER = 0;
        public static final int DESCRIPTION = 1;
        public static final int START_REQ = 2;
        public static final int START_DATE = 3;
        public static final int FINISH_DATE = 4;              
    }   
}

class TableIterator implements Iterator<TenderRow> {
   
    private int index = -1;
    private ParsedTenderTable tbl = ParsedTenderTable.getInstance();

    public TableIterator() {

    }

    @Override
    public boolean hasNext() {
        boolean result;
        if (tbl.getRows().isEmpty()) { 
            result = false;
        } else result = index != tbl.getRows().size()-1;
        
        return result;
    }

    @Override
    public TenderRow next() {
        index++;
        if (index >= tbl.getRows().size())
            throw new NoSuchElementException();
        return tbl.getRows().get(index);
    }

    @Override
    public void remove() {
        if (index==-1) {
            throw new IllegalStateException();
        } 
        tbl.getRows().remove(index);
        index--;
    }
    
}

class TenderRow {

    private String number;        
    private String description;
    private Date startReq;
    private Date startDate;
    private Date finishDate;
    
    public TenderRow() {
        
        GregorianCalendar cal = new GregorianCalendar(1, 1, 1);
        
        startReq = cal.getTime();
        startDate = cal.getTime();
        finishDate = cal.getTime();
    }

    @Override
    public String toString() {
        String line = "";
        for (int i=0; i<5; i++)
            line += getStringField(i).concat(",");
        line = line.substring(0, line.length()-1);
        
        return line;
        
    }
    
    private String getStringField(int index) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm:00");
        String ret = "";
        switch (index) {
            case 0: ret = getNumber();
            break;
            case 1: ret = getDescription();
            break;
            case 2: ret = fmt.format(startReq);
            break;
            case 3: ret = fmt.format(startDate);
            break;            
            case 4: ret = fmt.format(finishDate);
            break;
            default: ret = "";
            break;
        }
        return ret;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    } 

    public Date getStartReq() {
        return startReq;
    }

    public void setStartReq(Date startReq) {
        this.startReq = startReq;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }
}
