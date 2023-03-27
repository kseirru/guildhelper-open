package com.kseirru.events.embedCommands;

import com.kseirru.core.GuildHelper;
import com.kseirru.models.EmbedCached;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.validator.routines.UrlValidator;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.util.Objects;

public class embedEdit extends ListenerAdapter {
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {

        if(!event.getModalId().contains("edit-embed")) {return;}

        String message_id;

        try {
            message_id = event.getModalId().split("-")[2];
        } catch (Exception ignored) {
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

        Message message = event.getChannel().retrieveMessageById(message_id).complete();

        message.editMessageEmbeds(embedBuilder.build()).queue(action -> {
            try {
                try (PreparedStatement statement = GuildHelper.db.prepareStatement("UPDATE embedCache SET channelId = ?, messageId = ?, embedTitle = ? WHERE messageId = ?")) {
                    statement.setString(1, message.getChannel().getId());
                    statement.setString(2, message.getId());
                    statement.setString(3, embed_title);
                    statement.setString(4, message.getId());
                    statement.execute();
                } catch (Exception ignored) {}
            } catch (Exception ignored) {}
        });

        event.reply("✅").setEphemeral(true).queue();

        Runtime.getRuntime().gc();

    }
}