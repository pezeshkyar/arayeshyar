package com.example.doctorsbuilding.nav;

import java.net.ConnectException;

/**
 * Created by hossein on 8/24/2016.
 */
public class MyException extends Exception {
    private String msg;
    public MyException(String msg){
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
