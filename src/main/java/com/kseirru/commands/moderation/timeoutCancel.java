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
import net.dv8tion.jda.api.entities.Guild;
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

public class timeoutCancel extends SlashCommand {
    public timeoutCancel() {
        this.name = "unmute";
        this.help = "Unmute a Member";
        this.descriptionLocalization = Map.ofEntries(
                Map.entry(DiscordLocale.RUSSIAN, "Снять заглушку с пользователя")
        );
        List<OptionData> optionData = new ArrayList<>();
        OptionData user = new OptionData(OptionType.USER, "user", "Description", true, false);
        optionData.add(user);

        this.options = optionData;
        List<Permission> permissionList = new ArrayList<>();
        permissionList.add(Permission.MODERATE_MEMBERS);
        this.userPermissions = permissionList.toArray(new Permission[0]);
        this.guildOnly = true;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Guild guild = event.getGuild();
        assert guild != null;
        Translator tr = new Translator(guild.getId());

        User moderator = event.getUser();
        User violator = Objects.requireNonNull(event.getOption("user")).getAsUser();

        Member violatorMember = null;

        try {
            violatorMember = event.getGuild().retrieveMember(violator).complete();
        } catch (Exception ignored) {}

        if(violatorMember == null) {
            FastAnswers.error(event, "error.user-not-found", true); return;
        }

        if (CheckActions.selfPurge(event, violator)) {
            FastAnswers.error(event, "error.self-unmute", true);
            return;
        }

        if (!CheckActions.botHasPermission(event, Permission.MODERATE_MEMBERS)) {
            FastAnswers.error(event, "error.missing-permissions", true);
            return;
        }

        if(!CheckActions.canInteract(event, violatorMember)) {
            FastAnswers.error(event, "error.cant-interact", true); return;
        }

        if(!violatorMember.isTimedOut()) {
            FastAnswers.error(event, "error.not-muted", true); return;
        }

        String timeout_end = "<t:" + Objects.requireNonNull(violatorMember.getTimeOutEnd()).toEpochSecond() + ">";

        try {
            guild.removeTimeout(violator).reason(moderator.getAsTag()).queue();
        } catch (Exception e) {
            FastAnswers.error(event, "unknown", true); return;
        }

        EmbedBuilder success = new EmbedBuilder()
                .setTitle(tr.get("success"))
                .setColor(JDAColors.DEFAULT)
                .setDescription(tr.get("unmute.success"))
                .setTimestamp(Instant.now());
        success.addField(tr.get("moderator-is"), moderator.getAsTag() + " | " + moderator.getAsMention(), true);
        success.addField(tr.get("user-is"), violator.getAsTag() + " | " + violator.getAsMention(), true);
        success.addField(tr.get("unmute.unmute-date"), timeout_end, false);

        event.replyEmbeds(success.build()).queue(interactionHook -> {
            interactionHook.deleteOriginal().queueAfter(5, TimeUnit.SECONDS);
        });

        GuildConfig guildConfig = new GuildConfig(event.getGuild().getId());

        if(!guildConfig.LogEnabledStatus() || !guildConfig.ModActionEventStatus() || guildConfig.getLogChannelId().equals("0")) {return;}

        EmbedBuilder logMessage = new EmbedBuilder()
                .setTitle(tr.get("unmute.logMessage.title"))
                .setColor(JDAColors.DEFAULT)
                .setTimestamp(Instant.now());

        logMessage.addField(tr.get("moderator-is"), moderator.getAsTag() + " | " + moderator.getAsMention(), true);
        logMessage.addField(tr.get("user-is"), violator.getAsTag() + " | " + violator.getAsMention(), true);
        logMessage.addField(tr.get("unmute.unmute-date"), timeout_end, false);

        try {
            Objects.requireNonNull(event.getJDA().getChannelById(TextChannel.class, guildConfig.getLogChannelId())).sendMessageEmbeds(logMessage.build()).queue();
        } catch (Exception ignored) {}
    }
}