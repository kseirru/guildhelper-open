package com.kseirru.events.logger;

import com.kseirru.core.Cache;
import com.kseirru.core.GuildHelper;
import com.kseirru.models.CachedMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class NewMessage extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if(event.getAuthor().isBot() || event.getAuthor().isSystem() || event.isWebhookMessage() || event.getMessage().getEmbeds().toArray().length != 0) {
            return;
        }

        if(event.getMessage().getContentRaw().equals("") && event.getMessage().getAttachments().toArray().length == 0) {
            return;
        }

        Message message = event.getMessage();
        Cache.addCachedMessage(new CachedMessage(message));

        if(GuildHelper.cachedMessages.toArray().length == 1001) {
            Cache.deleteExcessMessages();
        }
    }
}