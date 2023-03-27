package com.kseirru.models;

public class EmbedCached {
    public String title;
    public String channel_id;
    public String message_id;

    public EmbedCached(String title, String channel_id, String message_id) {
        this.title = title;
        this.channel_id = channel_id;
        this.message_id = message_id;
    }
}