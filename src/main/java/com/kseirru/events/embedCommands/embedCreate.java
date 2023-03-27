package com.kseirru.events.embedCommands;

import com.kseirru.core.GuildHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.apache.commons.validator.routines.UrlValidator;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class embedCreate extends ListenerAdapter {
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(!event.getModalId().equals("create-embed")) {
            return;
        }

        String embed_title = Objects.requireNonNull(event.getValue("embed-title")).getAsString();
        int embed_color = Integer.parseInt(Objects.requireNonNull(event.getValue("embed-color")).getAsString().replace("#", ""), 16);
        String embed_description = Objects.requireNonNull(event.getValue("embed-description")).getAsString();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(embed_title)
                .setColor(embed_color)
                .setDescription(embed_description);

        if(event.getValue("embed-thumbnail") != null) {
            if(UrlValidator.getInstance().isValid(Objects.requireNonNull(event.getValue("embed-thumbnail")).getAsString())) {
                embedBuilder.setThumbnail(Objects.requireNonNull(event.getValue("embed-thumbnail")).getAsString());
            }
        }

        if(event.getValue("embed-image") != null) {
            if(UrlValidator.getInstance().isValid(Objects.requireNonNull(event.getValue("embed-image")).getAsString())) {
                embedBuilder.setImage(Objects.requireNonNull(event.getValue("embed-image")).getAsString());
            }
        }


        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder()
                .setEmbeds(embedBuilder.build());

        TextChannel textChannel = event.getChannel().asTextChannel();
        // закэшировать новый эмбед
        textChannel.sendMessage(messageCreateBuilder.build()).queue(message -> {
            try {
                GuildHelper.db.createStatement().execute(String.format("INSERT INTO embedCache (channelId, messageId, embedTitle) VALUES ('%s', '%s', '%s')", message.getChannel().getId(), message.getId(), embed_title));
            } catch (Exception e) {
                e.printStackTrace();
                GuildHelper.logger.error("embedCreate: " + e.getMessage());
            }
        });

        event.reply("✅").setEphemeral(true).queue();

    }
}