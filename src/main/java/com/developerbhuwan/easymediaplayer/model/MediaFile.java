/*
 * Copyright (C) 2014 DeveloperBhuwan
 * Developer: Bhuwan Pd. Upadhyay
 */
package com.developerbhuwan.easymediaplayer.model;

/**
 *
 * @author DeveloperBhuwan
 */
public class MediaFile {

    private String fileName;
    private String filePath;
    private Double fileSize;
    private String fileType;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Double getFileSize() {
        return fileSize;
    }

    public void setFileSize(Double fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

}
