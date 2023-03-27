package com.kseirru.commands.admin;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.kseirru.core.Translator;
import com.kseirru.utils.JDAColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class config extends SlashCommand {
    public config() {
        this.name = "config";
        this.help = "Configure Guild Helper for this server";
        this.descriptionLocalization = Map.ofEntries(
                Map.entry(DiscordLocale.RUSSIAN, "Настроить бота на этом сервере")
        );
        List<Permission> permissionList = new ArrayList<>();
        permissionList.add(Permission.ADMINISTRATOR);
        this.userPermissions = permissionList.toArray(new Permission[0]);
        this.guildOnly = true;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Translator tr = new Translator(Objects.requireNonNull(event.getGuild()).getId());
        EmbedBuilder mainEmbed = new EmbedBuilder()
                .setTitle(tr.get("config.mainEmbed.title"))
                .setColor(JDAColors.DEFAULT)
                .setDescription(tr.get("config.mainEmbed.description"));

        Emoji logging_emoji = Emoji.fromCustom("log", 1034170996971810896L, false);
        Emoji language_emoji = Emoji.fromCustom("language", 1035709811964260392L, false);

        StringSelectMenu selectMenu = StringSelectMenu.create("config.select")
                .addOption(tr.get("config.language.label"), "config.language", tr.get("config.language.description"), language_emoji)
                .addOption(tr.get("config.logger.label"), "config.logging", tr.get("config.logger.description"), logging_emoji)
                .setPlaceholder(tr.get("config.select.placeholder"))
                .setMinValues(1)
                .setMaxValues(1).build();

        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder()
                .setEmbeds(mainEmbed.build())
                .setActionRow(selectMenu);

        event.reply(messageCreateBuilder.build()).setEphemeral(true).queue();
    }
}