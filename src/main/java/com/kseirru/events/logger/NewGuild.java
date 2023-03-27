package com.kseirru.events.logger;

import com.kseirru.models.GuildConfig;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class NewGuild extends ListenerAdapter {
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        new GuildConfig(event.getGuild().getId()).update();
    }
}
