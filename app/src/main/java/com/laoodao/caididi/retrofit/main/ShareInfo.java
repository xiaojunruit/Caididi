package com.laoodao.caididi.retrofit.main;

public class ShareInfo {
    public String title;    // 标题
    public String content;
    public String img;    // 图片
    public String url;
    public String qrcode;


    public ShareInfo() {

    }

    public ShareInfo(String title, String content, String img, String url) {
        this.title = title;
        this.content = content;
        this.img = img;
        this.url = url;
    }
}