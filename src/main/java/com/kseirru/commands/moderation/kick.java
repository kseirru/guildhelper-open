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

public class kick extends SlashCommand {
    public kick() {
        this.name = "kick";
        this.help = "Kick a Member";
        this.descriptionLocalization = Map.ofEntries(
                Map.entry(DiscordLocale.RUSSIAN, "Кикнуть участника")
        );
        List<OptionData> optionDataList = new ArrayList<>();

        OptionData user_data = new OptionData(OptionType.USER, "member", "Member to Kick", true, false)
                .setDescriptionLocalizations(Map.ofEntries(
                        Map.entry(DiscordLocale.RUSSIAN, "Участник, которого нужно выгнать")
                ));

        OptionData reason = new OptionData(OptionType.STRING, "reason", "Reason of Kick", false, false)
                .setDescriptionLocalizations(Map.ofEntries(
                        Map.entry(DiscordLocale.RUSSIAN, "Причина для кика")
                ));

        optionDataList.add(user_data); optionDataList.add(reason);

        this.options = optionDataList;
        List<Permission> requiredPermissions = new ArrayList<>();
        requiredPermissions.add(Permission.KICK_MEMBERS);

        this.userPermissions = requiredPermissions.toArray(new Permission[0]);
        this.guildOnly = true;

    }

    @Override
    protected void execute(SlashCommandEvent event) {
        assert event.getGuild() != null;
        Translator tr = new Translator(event.getGuild().getId());
        User moderator = event.getUser();
        User violator = Objects.requireNonNull(event.getOption("member")).getAsUser();
        String reason = tr.get("empty-reason");
        if(event.getOption("reason") != null) {
            reason = Objects.requireNonNull(event.getOption("reason")).getAsString();
        }

        Member violatorMember = null;

        try {
            violatorMember = event.getGuild().retrieveMember(violator).complete();
        } catch (Exception ignored) {}

        if(violatorMember == null) {
            FastAnswers.error(event, "error.user-not-found", true); return;
        }

        if(CheckActions.selfPurge(event, violator)) {
            FastAnswers.error(event, "error.self-purge", true); return;
        }

        if(CheckActions.ownerCheck(event, violator)) {
            FastAnswers.error(event, "error.owner-purge", true); return;
        }

        if(CheckActions.botPurge(event, violator)) {
            FastAnswers.error(event, "error.bot-purge", true); return;
        }

        if(!CheckActions.botHasPermission(event, Permission.KICK_MEMBERS)) {
            FastAnswers.error(event, "error.missing-permissions", true); return;
        }

        if(!CheckActions.canInteract(event, violatorMember)) {
            FastAnswers.error(event, "error.cant-interact", true); return;
        }

        try {
            event.getGuild().kick(violatorMember)
                    .reason(reason).queue();
        } catch (Exception ignored) {
            FastAnswers.error(event, "unknown", true); return;
        }

        EmbedBuilder success = new EmbedBuilder()
                .setTitle(tr.get("success"))
                .setColor(JDAColors.DEFAULT)
                .setDescription(tr.get("kick.success"))
                .addField(tr.get("moderator-is"), moderator.getAsTag() + " | " + moderator.getAsMention(), true)
                .addField(tr.get("violator-is"), violator.getAsTag() + " | " + violator.getAsMention(), true)
                .addField(tr.get("reason-is"), reason, false)
                .setTimestamp(Instant.now());

        EmbedBuilder logMessage = new EmbedBuilder()
                .setTitle(tr.get("kick.logMessage.title"))
                .setColor(JDAColors.DEFAULT)
                .addField(tr.get("moderator-is"), moderator.getAsTag() + " | " + moderator.getAsMention(), true)
                .addField(tr.get("violator-is"), violator.getAsTag() + " | " + violator.getAsMention(), true)
                .addField(tr.get("reason-is"), reason, false)
                .setTimestamp(Instant.now());

        event.replyEmbeds(success.build()).queue(interactionHook -> {
            interactionHook.deleteOriginal().queueAfter(5, TimeUnit.SECONDS);
        });

        GuildConfig guildConfig = new GuildConfig(event.getGuild().getId());

        if(!guildConfig.LogEnabledStatus() || !guildConfig.ModActionEventStatus() || guildConfig.getLogChannelId().equals("0")) {return;}

        try {
            event.getJDA().getChannelById(TextChannel.class, guildConfig.getLogChannelId()).sendMessageEmbeds(logMessage.build()).queue();
        } catch (Exception ignored) {}
    }
}