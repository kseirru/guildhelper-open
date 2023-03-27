package com.kseirru.events.embedCommands;

import com.kseirru.core.GuildHelper;
import com.kseirru.core.Translator;
import com.kseirru.models.EmbedCached;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class embedEditAutocomplete extends ListenerAdapter {
    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if(!event.getFocusedOption().getName().equals("embed")) {return;}
        List<EmbedCached> embedCachedList = new ArrayList<>();

        try {
            Statement statement = GuildHelper.sql;
            try {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM embedCache WHERE channelId = '" + event.getChannel().getId() + "'");
                while (resultSet.next()) {
                    EmbedCached embedCached = new EmbedCached(resultSet.getString("embedTitle"), resultSet.getString("channelId"), resultSet.getString("messageId"));
                    embedCachedList.add(embedCached);
                }
            } catch (Exception ignored) {}
        } catch (Exception e) {
            GuildHelper.logger.error(e.getMessage());
        }


        Translator tr = new Translator(Objects.requireNonNull(event.getGuild()).getId());
        if(embedCachedList.toArray().length == 0) {
            String[] opt = new String[]{tr.get("messageEditAC.noEmbeds")};
            List<Command.Choice> options = Stream.of(opt)
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue()))
                    .map(word -> new Command.Choice(word, word))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
            Runtime.getRuntime().gc();
            return;
        }
        String[] embeds = new String[]{};
        for(EmbedCached embed : embedCachedList) {
            String title = embed.title;
            if(title.length() > 20) {
                title = title.substring(0, 17) + "...";
            }
            embeds = Stream.concat(Stream.of(embeds), Stream.of(title + " | " + embed.message_id)).toArray(String[]::new);
        }

        List<Command.Choice> options = Stream.of(embeds)
                .filter(word -> word.startsWith(event.getFocusedOption().getValue().toLowerCase()))
                .map(word -> new Command.Choice(word, word)).toList();
        event.replyChoices(options).queue();


        Runtime.getRuntime().gc();


    }
}