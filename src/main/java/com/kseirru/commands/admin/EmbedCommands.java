package com.kseirru.commands.admin;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import com.kseirru.utils.JDAColors;
import com.kseirru.core.Translator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EmbedCommands extends SlashCommand {
    public EmbedCommands() {
        this.name = "embed";
        this.help = "...";
        List<Permission> permissionList = new ArrayList<>();
        permissionList.add(Permission.ADMINISTRATOR);
        this.userPermissions = permissionList.toArray(new Permission[0]);
        this.guildOnly = true;

        this.children = new SlashCommand[]{new create(), new edit()};

    }

    @Override
    protected void execute(SlashCommandEvent event) {
    }

    private static class create extends SlashCommand {
        public create() {
            this.name = "create";
            this.help = "Create a embed";
            this.descriptionLocalization = Map.ofEntries(
                    Map.entry(DiscordLocale.RUSSIAN, "Создать эмбед")
            );

        }

        @Override
        protected void execute(SlashCommandEvent event) {
            Translator tr = new Translator(Objects.requireNonNull(event.getGuild()).getId());

            TextInput textInput = TextInput.create("embed-title", tr.get("embed.title"), TextInputStyle.SHORT)
                    .setMinLength(1)
                    .setMaxLength(256)
                    .setRequired(true)
                    .setValue(tr.get("embed.title"))
                    .build();

            String _color = String.format("#%06X", (0xFFFFFF & JDAColors.DEFAULT));

            TextInput color = TextInput.create("embed-color", tr.get("embed.color"), TextInputStyle.SHORT)
                    .setMinLength(7)
                    .setMaxLength(7)
                    .setValue(_color)
                    .setRequired(true)
                    .build();

            TextInput description = TextInput.create("embed-description", tr.get("embed.description"), TextInputStyle.PARAGRAPH)
                    .setMinLength(1)
                    .setMaxLength(2048)
                    .setRequired(true)
                    .build();

            TextInput thumbnail = TextInput.create("embed-thumbnail", tr.get("embed.thumbnail"), TextInputStyle.SHORT)
                    .setMaxLength(512)
                    .setRequired(false)
                    .build();

            TextInput image = TextInput.create("embed-image", tr.get("embed.image"), TextInputStyle.SHORT)
                    .setMaxLength(512)
                    .setRequired(false)
                    .build();


            Modal modal = Modal.create("create-embed", tr.get("embed.create.modal.title"))
                    .addActionRow(textInput)
                    .addActionRow(color)
                    .addActionRow(description)
                    .addActionRow(thumbnail)
                    .addActionRow(image)
                    .build();

            event.replyModal(modal).queue();
        }
    }

    private static class edit extends SlashCommand {
        public edit() {
            this.name = "edit";
            this.help = "Edit a embed";
            this.descriptionLocalization = Map.ofEntries(
                    Map.entry(DiscordLocale.RUSSIAN, "Изменить эмбед")
            );


            OptionData embed = new OptionData(OptionType.STRING, "embed", "Embed to edit")
                    .setDescriptionLocalizations(Map.ofEntries(
                            Map.entry(DiscordLocale.RUSSIAN, "Эмбед, который нужно изменить")
                    ))
                    .setRequired(true)
                    .setAutoComplete(true);

            List<OptionData> options = new ArrayList<>();
            options.add(embed);
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            String[] message_ids = Objects.requireNonNull(event.getOption("embed")).getAsString().split(" \\| ");
            String message_id = message_ids[message_ids.length - 1];
            Translator tr = new Translator(Objects.requireNonNull(event.getGuild()).getId());

            try {
                event.getTextChannel().retrieveMessageById(message_id).queue();
            } catch (Exception e) {
                EmbedBuilder error = new EmbedBuilder()
                        .setTitle(tr.get("error"))
                        .setColor(JDAColors.RED)
                        .setDescription(tr.get("embed.edit.wrong-id"))
                        .setTimestamp(Instant.now());
                event.replyEmbeds(error.build()).setEphemeral(true).queue();
                return;
            }
            Message msg = event.getTextChannel().retrieveMessageById(message_id).complete();

            MessageEmbed embed = msg.getEmbeds().get(0);

            TextInput textInput = TextInput.create("embed-title", tr.get("embed.title"), TextInputStyle.SHORT)
                    .setMinLength(1)
                    .setMaxLength(256)
                    .setRequired(true)
                    .setValue(embed.getTitle())
                    .setPlaceholder(message_id)
                    .setValue(embed.getTitle())
                    .build();

            String _color = String.format("#%06X", (0xFFFFFF & embed.getColorRaw()));

            TextInput color = TextInput.create("embed-color", tr.get("embed.color"), TextInputStyle.SHORT)
                    .setMinLength(7)
                    .setMaxLength(7)
                    .setValue(_color)
                    .setRequired(true)
                    .build();

            TextInput description = TextInput.create("embed-description", tr.get("embed.description"), TextInputStyle.PARAGRAPH)
                    .setMinLength(1)
                    .setMaxLength(2048)
                    .setValue(embed.getDescription())
                    .setRequired(true)
                    .build();

            TextInput thumbnail = TextInput.create("embed-thumbnail", tr.get("embed.thumbnail"), TextInputStyle.SHORT)
                    .setMaxLength(512)
                    .setValue(embed.getThumbnail() == null ? null : embed.getThumbnail().getUrl())
                    .setRequired(false)
                    .build();

            TextInput image = TextInput.create("embed-image", tr.get("embed.image"), TextInputStyle.SHORT)
                    .setMaxLength(512)
                    .setValue(embed.getImage() == null? null : embed.getImage().getUrl())
                    .setRequired(false)
                    .build();


            Modal modal = Modal.create("edit-embed-" + message_id, tr.get("embed.edit.modal.title"))
                    .addActionRow(textInput)
                    .addActionRow(color)
                    .addActionRow(description)
                    .addActionRow(thumbnail)
                    .addActionRow(image)
                    .build();

            event.replyModal(modal).queue();
        }
    }

}