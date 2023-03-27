package com.kseirru.events.other;

import com.kseirru.core.Translator;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class unbanAutocomplete extends ListenerAdapter {
    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if(!event.getName().equals("unban") && !event.getFocusedOption().getName().equals("user")) {
            return;
        }

        Translator tr = new Translator(Objects.requireNonNull(event.getGuild()).getId());

        try {
            Objects.requireNonNull(event.getGuild()).retrieveBanList().stream();
        } catch (Exception e) {
            String[] words = new String[]{tr.get("error.missing-permissions")};
            List<Command.Choice> options = Stream.of(words).map(word-> new Command.Choice(word, word)).toList();
            event.replyChoices(options).queue();
            return;
        }

        List<Guild.Ban> _banned_users = Objects.requireNonNull(event.getGuild()).retrieveBanList().stream().toList();

        List<Command.Choice> __banned_users = new ArrayList<>();

        for (Guild.Ban user : _banned_users) {
            __banned_users.add(new Command.Choice(user.getUser().getAsTag(), user.getUser().getIdLong()));
        }

        List<Command.Choice> banned_users = __banned_users.stream()
                .filter(word -> word.getName().toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase())).toList();

        if(banned_users.toArray().length == 0) {
            String[] words = new String[]{tr.get("unban.autocomplete.no-bans")};
            List<Command.Choice> _opt = Stream.of(words)
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue().toLowerCase()))
                    .map(word -> new Command.Choice(word, word)).toList();
            event.replyChoices(_opt).queue();
            return;
        }

        event.replyChoices(banned_users).queue();
    }
}
