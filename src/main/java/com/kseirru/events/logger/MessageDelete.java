package com.kseirru.events.logger;

import com.kseirru.core.Cache;
import com.kseirru.core.Translator;
import com.kseirru.models.CachedMessage;
import com.kseirru.models.GuildConfig;
import com.kseirru.utils.JDAColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.time.Instant;

public class MessageDelete extends ListenerAdapter {
    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        Guild guild = event.getGuild();

        GuildConfig guildConfig = new GuildConfig(guild.getId());
        CachedMessage cachedMessage = Cache.getCachedMessage(event.getMessageId());

        if(!guildConfig.LogEnabledStatus() || !guildConfig.MessageDeleteEventStatus() || guildConfig.getLogChannelId().equals("0") || cachedMessage == null) {
            return;
        }

        if(cachedMessage.getAuthorId() == null || cachedMessage.getContent() == null) {
            return;
        }

        Translator tr = new Translator(guild.getId());

        try {
            TextChannel logChannel = event.getJDA().getChannelById(TextChannel.class, guildConfig.getLogChannelId());
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(tr.get("messageDelete.embed.title"))
                    .setColor(JDAColors.DEFAULT)
                    .setDescription(tr.get("user-is") + " <@" + cachedMessage.getAuthorId() + ">\n" +
                            tr.get("messageDelete.channel") + "<#" + event.getChannel().getId() + ">\n\n```\n" + cachedMessage.getContent().replace("```", "'''") + "\n```")
                    .setTimestamp(Instant.now());
            if(cachedMessage.getAttachments().toArray().length != 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String url : cachedMessage.getAttachments()) {
                    stringBuilder.append(url).append("\n");
                }
                String attachments = stringBuilder.toString();
                attachments = attachments.substring(0, attachments.length());
                embedBuilder.addField(tr.get("messageDelete.attachments"), attachments, false);
            }

            MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder()
                    .setEmbeds(embedBuilder.build());

            assert logChannel != null;
            logChannel.sendMessage(messageCreateBuilder.build()).queue();
            Cache.deleteCachedMessage(cachedMessage);

        } catch (Exception ignored) {}
    }
}
