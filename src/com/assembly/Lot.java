package com.assembly;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Lot implements Serializable {
    
    private final TenderRow owner;
    private String description;
    private String number;    
    private Date startReq;
    private Date startDate;
    private Date finishDate;
    private ArrayList<Appendix>[] appendix;
    private boolean allowed;
    private HashMap regulations;
    private ArrayList<Appendix>[] reports;

    Lot(TenderRow row) {
        owner = row;
        description = row.getDescription();
        number = row.getNumber();
        startReq = row.getStartReq();
        startDate = row.getStartDate();
        finishDate = row.getFinishDate();
        
        Extractor.getLotData(this);
        
    }

    /**
     * Get the value of startReq
     *
     * @return the value of startReq
     */
    public Date getStartReq() {
        return startReq;
    }

    /**
     * Set the value of startReq
     *
     * @param startReq new value of startReq
     */
    public void setStartReq(Date startReq) {
        this.startReq = startReq;
    }


    /**
     * Get the value of number
     *
     * @return the value of number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Set the value of number
     *
     * @param number new value of number
     */
    public void setNumber(String number) {
        this.number = number;
    }


    /**
     * Get the value of description
     *
     * @return the value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of description
     *
     * @param description new value of description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Appendix>[] getAppendix() {
        return appendix;
    }

    public void setAppendix(ArrayList<Appendix>[] appendix) {
        this.appendix = appendix;
    }

    
}

class Appendix {

    public Appendix() {  }
    
    private File file;
    private Date birth;
    private int size;
    private byte[] binary;

    public File getFile() {
        return file;
    }

    public void setFile(File f) {
        this.file = f;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getBinary() {
        return binary;
    }

    public void setBinary(byte[] binary) {
        this.binary = binary;
    }
    
}

class SKU {
    
    private int pos;
    private String description;
    private String comment;
    private Measure unit;
    private long count;
    private float startPrice;
    private float myBid;
    private float bestPrice;
    private Date timestamp;

    /**
     * Get the value of comment
     *
     * @return the value of comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Set the value of comment
     *
     * @param comment new value of comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    

    /**
     * Get the value of description
     *
     * @return the value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of description
     *
     * @param Description new value of description
     */
    public void setDescription(String Description) {
        this.description = Description;
    }


    /**
     * Get the value of pos
     *
     * @return the value of pos
     */
    public int getPos() {
        return pos;
    }

    /**
     * Set the value of pos
     *
     * @param pos new value of pos
     */
    public void setPos(int pos) {
        this.pos = pos;
    }

}

/**
 * 
 * Единицы измерения
 */
class Measure {
    private String name;
}