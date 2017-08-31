package com.laoodao.caididi.event;

/**
 * Created by WORK on 2017/3/2.
 */

public class FarmlandOperateEvent {
    public int id;
    public int farmlandId;
    public String name;

    public FarmlandOperateEvent(int id, int farmlandId, String name) {
        this.id = id;
        this.farmlandId = farmlandId;
        this.name = name;
    }
}
