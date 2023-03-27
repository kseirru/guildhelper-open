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

import static java.util.Map.entry;

public class ban extends SlashCommand {
    public ban() {
        this.name = "ban";
        this.help = "Ban a User";
        this.descriptionLocalization = Map.ofEntries(
                entry(DiscordLocale.RUSSIAN, "Заблокировать пользователя")
        );

        OptionData user_option = new OptionData(OptionType.USER, "user", "User to Ban", true, false)
                .setDescriptionLocalizations(Map.ofEntries(
                        entry(DiscordLocale.RUSSIAN, "Пользователь, которого нужно заблокировать")
                ));

        OptionData reason_option = new OptionData(OptionType.STRING, "reason", "Reason of Ban", false, false)
                .setDescriptionLocalizations(Map.ofEntries(
                        entry(DiscordLocale.RUSSIAN, "Причина блокировки")
                ));

        List<OptionData> optionData = new ArrayList<>();
        optionData.add(user_option); optionData.add(reason_option);

        this.options = optionData;

        List<Permission> requiredPermissions = new ArrayList<>();
        requiredPermissions.add(Permission.BAN_MEMBERS);

        this.userPermissions = requiredPermissions.toArray(new Permission[0]);
        this.guildOnly = true;

    }

    @Override
    protected void execute(SlashCommandEvent event) {
        assert event.getGuild() != null;
        Translator tr = new Translator(event.getGuild().getId());
        User moderator = event.getUser();
        User violator = Objects.requireNonNull(event.getOption("user")).getAsUser();
        String reason = tr.get("empty-reason");
        if(event.getOption("reason") != null) {
            reason = Objects.requireNonNull(event.getOption("reason")).getAsString();
        }

        if(CheckActions.selfPurge(event, violator)) {
            FastAnswers.error(event, "error.self-purge", true);
            return;
        }
        if(CheckActions.botPurge(event, violator)) {
            FastAnswers.error(event, "error.bot-purge", true);
            return;
        }
        if(CheckActions.ownerCheck(event, violator)) {
            FastAnswers.error(event, "error.owner-purge", true);
            return;
        }
        if(!CheckActions.botHasPermission(event, Permission.BAN_MEMBERS)) {
            FastAnswers.error(event, "error.missing-permissions", true);
            return;
        }

        try {
            event.getGuild().ban(violator, 1, TimeUnit.DAYS)
                    .reason(reason).queue();

            EmbedBuilder success = new EmbedBuilder()
                    .setTitle(tr.get("success"))
                    .setColor(JDAColors.DEFAULT)
                    .setDescription(tr.get("ban.success"))
                    .addField(tr.get("moderator-is"), moderator.getAsTag() + " | " + moderator.getAsMention(), true)
                    .addField(tr.get("violator-is"), violator.getAsTag() + " | " + violator.getAsMention(), true)
                    .addField(tr.get("reason-is"), reason, false)
                    .setTimestamp(Instant.now());
            event.replyEmbeds(success.build()).queue(interactionHook -> {
                interactionHook.deleteOriginal().queueAfter(5, TimeUnit.SECONDS);
            });

            GuildConfig guildConfig = new GuildConfig(event.getGuild().getId());

            if(!guildConfig.LogEnabledStatus() || !guildConfig.ModActionEventStatus() || guildConfig.getLogChannelId().equals("0")) {return;}

            EmbedBuilder logMessage = new EmbedBuilder()
                    .setTitle(tr.get("ban.logMessage.title"))
                    .setColor(JDAColors.DEFAULT)
                    .addField(tr.get("moderator-is"), moderator.getAsTag() + " | " + moderator.getAsMention(), true)
                    .addField(tr.get("violator-is"), violator.getAsTag() + " | " + violator.getAsMention(), true)
                    .addField(tr.get("reason-is"), reason, false)
                    .setTimestamp(Instant.now());

            try {
                Objects.requireNonNull(event.getJDA().getChannelById(TextChannel.class, guildConfig.getLogChannelId())).sendMessageEmbeds(logMessage.build()).queue();
            } catch (Exception ignored) {}


        } catch (Exception e) {
            FastAnswers.error(event, tr.get("unknown"), true);
        }

    }
}