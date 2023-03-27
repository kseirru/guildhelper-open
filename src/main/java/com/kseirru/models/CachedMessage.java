package com.kseirru.models;

import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;

public class CachedMessage {
    private final String authorId;
    private final String messageId;
    private final String content;
    private final List<String> attachments = new ArrayList<>();

    public CachedMessage(Message message) {
        this.authorId = message.getAuthor().getId();
        this.messageId = message.getId();
        this.content = message.getContentRaw();

        for (Message.Attachment attachment : message.getAttachments()) {
            attachments.add(attachment.getUrl());
        }

    }

    public String getAuthorId() {
        return authorId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getContent() {
        return content;
    }

    public List<String> getAttachments() {
        return attachments;
    }
}
