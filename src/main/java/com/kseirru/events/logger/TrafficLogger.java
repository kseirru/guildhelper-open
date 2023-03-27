package com.kseirru.events.logger;

import com.kseirru.core.Translator;
import com.kseirru.models.GuildConfig;
import com.kseirru.utils.JDAColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class TrafficLogger extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        GuildConfig guildConfig = new GuildConfig(event.getGuild().getId());

        if(!guildConfig.LogEnabledStatus() || !guildConfig.TrafficEventStatus() || guildConfig.getLogChannelId().equals("0")) {
            return;
        }

        Translator tr = new Translator(event.getGuild().getId());

        TextChannel log;

        try {
            log = event.getJDA().getTextChannelById(guildConfig.getLogChannelId());
        } catch (Exception ignored) {
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(tr.get("trafficLogger.join.title"))
                .setColor(JDAColors.DEFAULT);

        String prepare = """
                **%s:** `%s`
                **%s:** %s""";

        String value = String.format(prepare, tr.get("trafficLogger.discord.tag"), event.getMember().getUser().getAsTag(),
                tr.get("trafficLogger.discord.registerDate"), "<t:" + event.getMember().getUser().getTimeCreated().toEpochSecond() + "> ( <t:" + event.getMember().getUser().getTimeCreated().toEpochSecond() + ":R> )");

        embedBuilder.addField(tr.get("trafficLogger.userInfo"), value, false);

        try {
            embedBuilder.setThumbnail(event.getUser().getAvatarUrl());
        } catch (Exception ignored) {}


        embedBuilder.setFooter(String.format(tr.get("trafficLogger.discord.members"), event.getGuild().getMemberCount()));

        try {
            assert log != null;
            log.sendMessageEmbeds(embedBuilder.build()).queue();
        } catch (Exception ignored) {}

    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        GuildConfig guildConfig = new GuildConfig(event.getGuild().getId());

        if(!guildConfig.LogEnabledStatus() || !guildConfig.TrafficEventStatus() || guildConfig.getLogChannelId().equals("0")) {
            return;
        }

        User user = event.getUser();

        Translator tr = new Translator(event.getGuild().getId());

        TextChannel log;

        try {
            log = event.getJDA().getTextChannelById(guildConfig.getLogChannelId());
        } catch (Exception ignored) {
            return;
        }

        String prepare = """
                **%s:** `%s`""";

        String was_from = " 🤷";

        String value = String.format(prepare, tr.get("trafficLogger.discord.tag"), user.getAsTag());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(tr.get("trafficLogger.leave.title"))
                .setColor(JDAColors.DEFAULT);

        embedBuilder.addField(tr.get("trafficLogger.userInfo"), value, false);

        try {
            embedBuilder.setThumbnail(event.getUser().getAvatarUrl());
        } catch (Exception ignored) {}

        embedBuilder.setFooter(String.format(tr.get("trafficLogger.discord.members"), event.getGuild().getMemberCount()));

        try {
            assert log != null;
            log.sendMessageEmbeds(embedBuilder.build()).queue();
        } catch (Exception ignored) {}
    }
}