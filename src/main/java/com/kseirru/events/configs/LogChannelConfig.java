package com.kseirru.events.configs;

import com.kseirru.core.Translator;
import com.kseirru.models.GuildConfig;
import com.kseirru.utils.JDAColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LogChannelConfig extends ListenerAdapter {
    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        if (!event.getComponentId().equals("config.log.select.log_channel")) {
            return;
        }

        Translator tr = new Translator(Objects.requireNonNull(event.getGuild()).getId());

        String logChannel = event.getValues().get(0).getId();

        GuildConfig guildConfig = new GuildConfig(event.getGuild().getId());
        guildConfig.setLogChannelId(logChannel);
        guildConfig.update();

        EmbedBuilder success = new EmbedBuilder()
                .setTitle(tr.get("success"))
                .setColor(JDAColors.DEFAULT)
                .setDescription(tr.get("log.config.logChannel.success"));
        event.replyEmbeds(success.build()).setEphemeral(true).queue();
    }
}
