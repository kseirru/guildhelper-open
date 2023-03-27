package com.kseirru.events.configs;

import com.kseirru.core.Translator;
import com.kseirru.utils.JDAColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.Objects;

public class LangConfigMenu extends ListenerAdapter {
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if(!event.getValues().get(0).equals("config.language")) { return; }

        Translator tr = new Translator(Objects.requireNonNull(event.getGuild()).getId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(tr.get("lang.config.embed.title"))
                .setColor(JDAColors.DEFAULT)
                .setDescription(tr.get("lang.config.embed.description"));

        Emoji RU = Emoji.fromCustom("russian", 1035711106498428938L, false);
        Emoji EN = Emoji.fromCustom("english", 1035711105022042173L, false);

        StringSelectMenu selectMenu = StringSelectMenu.create("config.language.select")
                .addOption(tr.get("lang.config.russian"), "ru", tr.get("lang.config.russian.description"), RU)
                .addOption(tr.get("lang.config.english"), "en", tr.get("lang.config.english.description"), EN)
                .setMinValues(1)
                .setMaxValues(1)
                .setPlaceholder(tr.get("lang.config.select.placeholder"))
                .build();

        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder()
                .setEmbeds(embedBuilder.build())
                .addActionRow(selectMenu);

        event.reply(messageCreateBuilder.build()).setEphemeral(true).queue();

    }
}
