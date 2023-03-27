package com.kseirru.commands.moderation;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.kseirru.RestActions.CheckActions;
import com.kseirru.RestActions.FastAnswers;
import com.kseirru.core.Translator;
import com.kseirru.models.GuildConfig;
import com.kseirru.utils.JDAColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class timeout extends SlashCommand {
    public timeout() {
        this.name = "mute";
        this.help = "Mute a member";
        this.descriptionLocalization = Map.ofEntries(
                Map.entry(DiscordLocale.RUSSIAN, "Заглушить пользователя")
        );

        List<OptionData> optionData = new ArrayList<>();

        OptionData user = new OptionData(OptionType.USER, "member", "Member to mute", true, false)
                .setDescriptionLocalizations(Map.ofEntries(
                        Map.entry(DiscordLocale.RUSSIAN, "Пользователь, которого нужно заглушить")
                ));

        OptionData duration = new OptionData(OptionType.STRING, "duration", "Duration of mute | 1s, 1m, 1h", true, false)
                .setDescriptionLocalizations(Map.ofEntries(
                        Map.entry(DiscordLocale.RUSSIAN, "Срок действия заглушки | 1с, 1м, 1ч")
                ));

        OptionData reason = new OptionData(OptionType.STRING, "reason", "Reason of Mute", false, false)
                .setDescriptionLocalizations(Map.ofEntries(
                        Map.entry(DiscordLocale.RUSSIAN, "Причина заглушки")
                ));

        optionData.add(user);
        optionData.add(duration);
        optionData.add(reason);

        this.options = optionData;

        List<Permission> permissions = new ArrayList<>();
        permissions.add(Permission.MODERATE_MEMBERS);

        this.userPermissions = permissions.toArray(new Permission[0]);
        this.guildOnly = true;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        assert event.getGuild() != null;
        Translator tr = new Translator(event.getGuild().getId());
        User moderator = event.getUser();
        User violator = Objects.requireNonNull(event.getOption("member")).getAsUser();
        String duration = Objects.requireNonNull(event.getOption("duration")).getAsString();

        String reason = tr.get("empty-reason");
        if (event.getOption("reason") != null) {
            reason = Objects.requireNonNull(event.getOption("reason")).getAsString();
        }

        Member violatorMember = null;

        try {
            violatorMember = event.getGuild().retrieveMember(violator).complete();
        } catch (Exception ignored) {
        }

        if (violatorMember == null) {
            FastAnswers.error(event, "error.user-not-found", true);
            return;
        }

        TimeUnit timeUnit;

        int size_of_time = duration.length();
        int ready_size_of_time = size_of_time - 1;

        int ready_time;

        try {
            Integer.parseInt(duration.substring(0, ready_size_of_time));
        } catch (Exception e) {
            FastAnswers.error(event, "error.duration-timeunit", true);
            return;
        }

        try {
            ready_time = Integer.parseInt(duration.substring(0, ready_size_of_time));
        } catch (Exception e) {
            FastAnswers.error(event, "error.duration-timeunit", true);
            return;
        }

        long end_timestamp;

        if (duration.contains("с") || duration.contains("s")) {
            end_timestamp = Instant.now().getEpochSecond() + ready_time;
            timeUnit = TimeUnit.SECONDS;
        } else if (duration.contains("м") || duration.contains("m")) {
            end_timestamp = Instant.now().getEpochSecond() + (ready_time * 60L);
            timeUnit = TimeUnit.MINUTES;
        } else if (duration.contains("ч") || duration.contains("h")) {
            end_timestamp = Instant.now().getEpochSecond() + (ready_time * 60L * 60L);
            timeUnit = TimeUnit.HOURS;
        } else {
            FastAnswers.error(event, "error.duration-timeunit", true);
            return;
        }

        if (CheckActions.selfPurge(event, violator)) {
            FastAnswers.error(event, "error.self-purge", true);
            return;
        }

        if (CheckActions.botPurge(event, violator)) {
            FastAnswers.error(event, "error.bot-purge", true);
            return;
        }

        if (CheckActions.ownerCheck(event, violator)) {
            FastAnswers.error(event, "error.owner-purge", true);
            return;
        }

        if (!CheckActions.botHasPermission(event, Permission.MODERATE_MEMBERS)) {
            FastAnswers.error(event, "error.missing-permissions", true);
            return;
        }

        if (!CheckActions.canInteract(event, violatorMember)) {
            FastAnswers.error(event, "error.cant-interact", true);
            return;
        }

        EmbedBuilder success = new EmbedBuilder()
                .setTitle(tr.get("success"))
                .setColor(JDAColors.DEFAULT)
                .setDescription(tr.get("mute.success"))
                .setTimestamp(Instant.now())

                .addField(tr.get("moderator-is"), moderator.getAsTag() + " | " + moderator.getAsMention(), true)
                .addField(tr.get("violator-is"), violator.getAsTag() + " | " + violator.getAsMention(), true)
                .addField(tr.get("duration-is"), "<t:" + end_timestamp + ">", false)
                .addField(tr.get("reason-is"), reason, false);

        try {
            event.getGuild().timeoutFor(violator, ready_time, timeUnit).reason(reason).queue();
        } catch (Exception ignored) {
            FastAnswers.error(event, "unknown", true);
            return;
        }

        event.replyEmbeds(success.build()).queue(interactionHook -> {
            interactionHook.deleteOriginal().queueAfter(5, TimeUnit.SECONDS);
        });


        GuildConfig guildConfig = new GuildConfig(event.getGuild().getId());

        if (!guildConfig.LogEnabledStatus() || !guildConfig.ModActionEventStatus() || guildConfig.getLogChannelId().equals("0")) {
            return;
        }

        EmbedBuilder logMessage = new EmbedBuilder()
                .setTitle(tr.get("mute.logMessage.title"))
                .setColor(JDAColors.DEFAULT)
                .setTimestamp(Instant.now())

                .addField(tr.get("moderator-is"), moderator.getAsTag() + " | " + moderator.getAsMention(), true)
                .addField(tr.get("violator-is"), violator.getAsTag() + " | " + violator.getAsMention(), true)
                .addField(tr.get("duration-is"), "<t:" + end_timestamp + ">", false)
                .addField(tr.get("reason-is"), reason, false);

        try {
            Objects.requireNonNull(event.getJDA().getChannelById(TextChannel.class, guildConfig.getLogChannelId())).sendMessageEmbeds(logMessage.build()).queue();
        } catch (Exception ignored) {}

    }
}