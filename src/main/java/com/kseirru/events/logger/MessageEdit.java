package com.kseirru.events.logger;

import com.kseirru.core.Cache;
import com.kseirru.core.GuildHelper;
import com.kseirru.core.Translator;
import com.kseirru.models.CachedMessage;
import com.kseirru.models.GuildConfig;
import com.kseirru.utils.JDAColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MessageEdit extends ListenerAdapter {
    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        Guild guild = event.getGuild();
        GuildConfig guildConfig = new GuildConfig(guild.getId());
        CachedMessage cachedMessage;
        cachedMessage = Cache.getCachedMessage(event.getMessageId());

        Message message = event.getMessage();
        CachedMessage newCachedMessage = new CachedMessage(message);

        if(!guildConfig.LogEnabledStatus() || !guildConfig.MessageEditEventStatus() || guildConfig.getLogChannelId().equals("0") || cachedMessage == null) {
            return;
        }

        if(event.getAuthor().isSystem() || event.getAuthor().isBot()) {
            return;
        }

        if(cachedMessage.getAuthorId() == null || cachedMessage.getContent() == null) {
            return;
        }

        if(event.getMessage().getContentDisplay().equals(cachedMessage.getContent())) {
            return;
        }

        Translator tr = new Translator(guild.getId());

        try {
            TextChannel logChannel = event.getJDA().getChannelById(TextChannel.class, guildConfig.getLogChannelId());

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(tr.get("messageEdit.embed.title"))
                    .setColor(JDAColors.DEFAULT)
                    .setDescription(tr.get("user-is") + " <@" + cachedMessage.getAuthorId() + ">\n" +
                            tr.get("messageEdit.channel") + " <#" + event.getChannel().getId() + ">")

                    .addField(tr.get("messageEdit.oldMessage"), "```\n" + cachedMessage.getContent() + "\n```", false)
                    .addField(tr.get("messageEdit.newMessage"), "```\n" + event.getMessage().getContentDisplay() + "\n```", false)

                    .setTimestamp(Instant.now());

            MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder()
                    .setEmbeds(embedBuilder.build());

            assert logChannel != null;
            logChannel.sendMessage(messageCreateBuilder.build()).queue();

            cachedMessage.setContent(event.getMessage().getContentRaw());

            Cache.editCachedMessage(newCachedMessage);

            if(GuildHelper.cachedMessages.toArray().length == 1001) {
                Cache.deleteExcessMessages();
            }

        } catch (Exception ignored) {}
    }
}