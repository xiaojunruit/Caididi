package com.laoodao.caididi.event;

import com.laoodao.caididi.retrofit.main.Comment;

/**
 * Created by WORK on 2016/12/29.
 */

public class CommentEvent {

    public Comment comment;


    public CommentEvent(Comment comment){
        this.comment = comment;
    }
}
