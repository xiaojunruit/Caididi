package com.laoodao.caididi.event;

import com.laoodao.caididi.retrofit.user.CheckMessage;

/**
 * Created by WORK on 2017/3/2.
 */

public class RedDotEvent {
    public CheckMessage checkMessage;
    public boolean isMes;
    public RedDotEvent(CheckMessage checkMessage,boolean isMes){
        this.checkMessage=checkMessage;
        this.isMes=isMes;
    }
}
