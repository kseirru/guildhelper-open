package com.kseirru.core;

import com.kseirru.models.CachedMessage;

import java.util.Objects;

public class Cache {
    public static void addCachedMessage(CachedMessage cachedMessage) {
        GuildHelper.cachedMessages.add(cachedMessage);
    }
    public static CachedMessage getCachedMessage(String message_id) {
        CachedMessage cachedMessage;
        try {
            cachedMessage = GuildHelper.cachedMessages.stream()
                    .filter(p -> Objects.equals(p.getMessageId(), message_id)).toList().get(0);
        } catch (Exception e) {
            return null;
        }
        return cachedMessage;
    }

    public static void editCachedMessage(CachedMessage newCachedMessage) {
        GuildHelper.cachedMessages.remove(GuildHelper.cachedMessages.stream().filter(p -> Objects.equals(p.getMessageId(), newCachedMessage.getMessageId())).toList().get(0));
        GuildHelper.cachedMessages.add(newCachedMessage);
    }

    public static void deleteCachedMessage(CachedMessage cachedMessage) {
        GuildHelper.cachedMessages.remove(cachedMessage);
    }

    public static void deleteExcessMessages() {
        GuildHelper.cachedMessages.remove(0);
    }

    public static void printAllMessages() {
        for (CachedMessage cachedMessage : GuildHelper.cachedMessages) {
            System.out.print(cachedMessage.getMessageId() + " | ");
        }
        System.out.println();
    }
}