package com.kseirru.events.configs;

import com.kseirru.core.Translator;
import com.kseirru.models.GuildConfig;
import com.kseirru.utils.JDAColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LogStatusConfig extends ListenerAdapter {
    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        if(!event.getComponentId().equals("config.log.select.messageEvents")) {
            return;
        }

        Translator tr = new Translator(Objects.requireNonNull(event.getGuild()).getId());
        GuildConfig guildConfig = new GuildConfig(event.getGuild().getId());

        Emoji TRUE = Emoji.fromUnicode("✅");
        Emoji FALSE = Emoji.fromUnicode("⛔");

        if(event.getValues().get(0).equals("messageDelete")) {
            guildConfig.setMessageDeleteEvent(!guildConfig.MessageDeleteEventStatus());
        }

        if(event.getValues().get(0).equals("messageEdit")) {
            guildConfig.setMessageEditEvent(!guildConfig.MessageEditEventStatus());
        }

        if(event.getValues().get(0).equals("modAction")) {
            guildConfig.setModActionEvent(!guildConfig.ModActionEventStatus());
        }

        if(event.getValues().get(0).equals("trafficAction")) {
            guildConfig.setTrafficEvent(!guildConfig.TrafficEventStatus());
        }

        guildConfig.update();

        Button statusButton;
        if(guildConfig.LogEnabledStatus()) {
            statusButton = Button.success("statusButton", TRUE).withLabel(tr.get("log.config.logEnabled"));
        } else {
            statusButton = Button.danger("statusButton", FALSE).withLabel(tr.get("log.config.logDisabled"));
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(tr.get("log.config.embed.title"))
                .setColor(JDAColors.DEFAULT)
                .setDescription(tr.get("log.config.embed.description"));

        StringSelectMenu messageEvents = StringSelectMenu.create("config.log.select.messageEvents")
                .setDisabled(!guildConfig.LogEnabledStatus())
                .addOption(tr.get("log.config.messageDelete.label"), "messageDelete", tr.get("log.config.messageDelete.description"), guildConfig.MessageDeleteEventStatus() ? TRUE : FALSE)
                .addOption(tr.get("log.config.messageEdit.label"), "messageEdit", tr.get("log.config.messageEdit.description"), guildConfig.MessageEditEventStatus() ? TRUE : FALSE)
                .addOption(tr.get("log.config.modAction.label"), "modAction", tr.get("log.config.modAction.description"), guildConfig.ModActionEventStatus() ? TRUE : FALSE)
                .addOption(tr.get("log.config.traffic.label"), "trafficAction", tr.get("log.config.traffic.description"), guildConfig.TrafficEventStatus() ? TRUE : FALSE)
                .build();

        EntitySelectMenu log_channel = EntitySelectMenu.create("config.log.select.log_channel", EntitySelectMenu.SelectTarget.CHANNEL)
                .setDisabled(!guildConfig.LogEnabledStatus())
                .setChannelTypes(ChannelType.TEXT)
                .setPlaceholder(tr.get("log.config.select.log_channel.placeholder"))
                .build();

        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder()
                .setEmbeds(embedBuilder.build())
                .addActionRow(statusButton)
                .addActionRow(log_channel)
                .addActionRow(messageEvents);

        event.editMessage("\\u200b").applyCreateData(messageCreateBuilder.build()).queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("statusButton")) {
            return;
        }

        Translator tr = new Translator(Objects.requireNonNull(event.getGuild()).getId());
        GuildConfig guildConfig = new GuildConfig(event.getGuild().getId());

        Emoji TRUE = Emoji.fromUnicode("✅");
        Emoji FALSE = Emoji.fromUnicode("⛔");

        guildConfig.setLogEnabled(!guildConfig.LogEnabledStatus());

        guildConfig.update();

        Button statusButton;
        if(guildConfig.LogEnabledStatus()) {
            statusButton = Button.success("statusButton", TRUE).withLabel(tr.get("log.config.logEnabled"));
        } else {
            statusButton = Button.danger("statusButton", FALSE).withLabel(tr.get("log.config.logDisabled"));
        }
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(tr.get("log.config.embed.title"))
                .setColor(JDAColors.DEFAULT)
                .setDescription(tr.get("log.config.embed.description"));

        StringSelectMenu messageEvents = StringSelectMenu.create("config.log.select.messageEvents")
                .addOption(tr.get("log.config.messageDelete.label"), "messageDelete", tr.get("log.config.messageDelete.description"), guildConfig.MessageDeleteEventStatus() ? TRUE : FALSE)
                .addOption(tr.get("log.config.messageEdit.label"), "messageEdit", tr.get("log.config.messageEdit.description"), guildConfig.MessageEditEventStatus() ? TRUE : FALSE)
                .addOption(tr.get("log.config.modAction.label"), "modAction", tr.get("log.config.modAction.description"), guildConfig.ModActionEventStatus() ? TRUE : FALSE)
                .addOption(tr.get("log.config.traffic.label"), "trafficAction", tr.get("log.config.traffic.description"), guildConfig.TrafficEventStatus() ? TRUE : FALSE)
                .build();

        EntitySelectMenu log_channel = EntitySelectMenu.create("config.log.select.log_channel", EntitySelectMenu.SelectTarget.CHANNEL)
                .setChannelTypes(ChannelType.TEXT)
                .setPlaceholder(tr.get("log.config.select.log_channel.placeholder"))
                .build();

        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder()
                .setEmbeds(embedBuilder.build())
                .addActionRow(statusButton)
                .addActionRow(log_channel)
                .addActionRow(messageEvents);

        event.editMessage("\\u200b").applyCreateData(messageCreateBuilder.build()).queue();
    }
}
