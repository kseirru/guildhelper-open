package com.kseirru.RestActions;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.kseirru.core.JDAColors;
import com.kseirru.core.Translator;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.Objects;

public class FastAnswers {
    public static void error(SlashCommandEvent event, String message, boolean ephemeral) {
        Translator translator = new Translator(Objects.requireNonNull(event.getGuild()).getId());
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(translator.get("error"))
                .setColor(JDAColors.RED)
                .setDescription(translator.get(message))
                .setTimestamp(Instant.now());
        event.replyEmbeds(embedBuilder.build()).setEphemeral(ephemeral).queue();
        translator = null;
    }

}
