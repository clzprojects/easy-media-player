/*
 * Copyright (C) 2014 DeveloperBhuwan
 * Developer: Bhuwan Pd. Upadhyay
 */
package com.developerbhuwan.easymediaplayer.exception;

/**
 *
 * @author DeveloperBhuwan
 */
public class MediaFileEmptyException extends Exception {

    private String msg = "No Media Files Found";
    public MediaFileEmptyException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

}
