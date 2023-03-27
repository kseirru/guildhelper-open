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
import net.dv8tion.jda.api.entities.UserSnowflake;
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

public class unban extends SlashCommand {

    public unban() {

        this.name = "unban";
        this.help = "Unban a user";
        this.descriptionLocalization = Map.ofEntries(
                Map.entry(DiscordLocale.RUSSIAN, "Разблокировать пользователя")
        );
        List<OptionData> optionData = new ArrayList<>();

        OptionData user = new OptionData(OptionType.STRING, "user", "User to Unban", true, true)
                .setDescriptionLocalizations(Map.ofEntries(
                        Map.entry(DiscordLocale.RUSSIAN, "Пользователь, которого нужно разблокировать")
                ));

        optionData.add(user);
        this.options = optionData;

        List<Permission> permissions = new ArrayList<>();
        permissions.add(Permission.BAN_MEMBERS);

        this.userPermissions = permissions.toArray(new Permission[0]);
        this.guildOnly = true;

    }

    @Override
    protected void execute(SlashCommandEvent event) {
        assert event.getGuild() != null;
        Translator tr = new Translator(event.getGuild().getId());
        User moderator = event.getUser();
        String user_id = Objects.requireNonNull(event.getOption("user")).getAsString();

        if(!CheckActions.botHasPermission(event, Permission.BAN_MEMBERS)) {
            FastAnswers.error(event, "error.missing-permissions", true);
            return;
        }

        try {
            event.getGuild().unban(UserSnowflake.fromId(user_id)).reason(moderator.getAsTag()).queue();
        } catch (Exception e) {
            FastAnswers.error(event, "unknown", true);
            return;
        }

        String user = "<@" + user_id + ">";

        EmbedBuilder success = new EmbedBuilder()
                .setTitle(tr.get("success"))
                .setColor(JDAColors.DEFAULT)
                .setDescription(tr.get("unban.success"))
                .setTimestamp(Instant.now())
                .addField(tr.get("moderator-is"), moderator.getAsTag() + " | " + moderator.getAsMention(), true)
                .addField(tr.get("user-is"), user, false);

        event.replyEmbeds(success.build()).queue(interactionHook -> {
            interactionHook.deleteOriginal().queueAfter(5, TimeUnit.SECONDS);
        });
        GuildConfig guildConfig = new GuildConfig(event.getGuild().getId());

        if(!guildConfig.LogEnabledStatus() || !guildConfig.ModActionEventStatus() || guildConfig.getLogChannelId().equals("0")) {return;}

        EmbedBuilder logMessage = new EmbedBuilder()
                .setTitle(tr.get("unban.logMessage.title"))
                .setColor(JDAColors.DEFAULT)
                .setTimestamp(Instant.now())
                .addField(tr.get("moderator-is"), moderator.getAsTag() + " | " + moderator.getAsMention(), true)
                .addField(tr.get("user-is"), user, false);

        try {
            Objects.requireNonNull(event.getJDA().getChannelById(TextChannel.class, guildConfig.getLogChannelId())).sendMessageEmbeds(logMessage.build()).queue();
        } catch (Exception ignored) {}
    }
}