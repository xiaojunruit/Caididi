package com.laoodao.caididi.event;

/**
 * Created by WORK on 2017/3/8.
 */

public class ReserveEvent {
    public String state;
    public String stateDesc;
    public ReserveEvent(String state,String stateDesc){
        this.state=state;
        this.stateDesc=stateDesc;
    }
}
