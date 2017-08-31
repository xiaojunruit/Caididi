package com.laoodao.caididi.event;

import android.content.Intent;

public class Login3rdResultEvent {

    public int requestCode;
    public int resultCode;
    public Intent data;

    public Login3rdResultEvent(int requestCode, int resultCode, Intent data){
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }
}