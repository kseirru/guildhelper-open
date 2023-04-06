package com.kseirru.core;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.kseirru.commands.admin.EmbedCommands;
import com.kseirru.commands.admin.config;
import com.kseirru.commands.moderation.*;
import com.kseirru.commands.other.info;
import com.kseirru.events.configs.*;
import com.kseirru.events.embedCommands.embedCreate;
import com.kseirru.events.embedCommands.embedEdit;
import com.kseirru.events.embedCommands.embedEditAutocomplete;
import com.kseirru.events.logger.*;
import com.kseirru.events.other.unbanAutocomplete;
import com.kseirru.models.CachedMessage;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

public class GuildHelper {
    public static ArrayList<CachedMessage> cachedMessages = new ArrayList<>();
    public static Logger logger = LoggerFactory.getLogger(GuildHelper.class);
    public static Connection db;
    public static Statement sql;

    public GuildHelper(String token) {
        try {
            this.db = DriverManager.getConnection("jdbc:sqlite:" + Dotenv.load().get("database_filename"));
            this.sql = db.createStatement();

            CommandClientBuilder builder = new CommandClientBuilder()
                    .setStatus(OnlineStatus.IDLE)
                    .setActivity(Activity.streaming("best video", "https://www.youtube.com/watch?v=aZWWlqDy8nE"))
                    .setOwnerId("222392036387454978")
                    .useHelpBuilder(false);

            /* Commands */
            builder.addSlashCommand(new EmbedCommands());
            builder.addSlashCommand(new config());

            builder.addSlashCommand(new ban());
            builder.addSlashCommand(new kick());
            builder.addSlashCommand(new timeout());
            builder.addSlashCommand(new timeoutCancel());
            builder.addSlashCommand(new unban());

            builder.addSlashCommand(new info());


            CommandClient commandClient = builder.build();

            JDA jda = JDABuilder.create(token, GatewayIntent.getIntents(130815))
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS)
                    .addEventListeners(commandClient)
                    .setEventPassthrough(true)
                    .addEventListeners(
                            new NewMessage(), new MessageEdit(), new MessageDelete(),
                            new NewGuild(), new embedCreate(), new embedEditAutocomplete(),
                            new embedEdit(), new TrafficLogger(), new LangConfig(),
                            new LangConfigMenu(), new LogChannelConfig(), new LogStatusConfig(),
                            new LogConfigMenu(), new unbanAutocomplete())
                    .build();

            String query = """
                    CREATE TABLE IF NOT EXISTS guildConfig (
                      guild_id INTEGER,
                      locale TEXT,
                      logChannelId TEXT,
                      logEnabled INTEGER,
                      messageEditEvent INTEGER,
                      messageDeleteEvent INTEGER,
                      modActionEvent INTEGER,
                      trafficEvent INTEGER,
                      PRIMARY KEY (guild_id)
                    );
                                        
                    CREATE TABLE IF NOT EXISTS embedCache (
                      channelId TEXT,
                      messageId TEXT,
                      embedTitle TEXT,
                      PRIMARY KEY (channelId, messageId)
                    );
                    """;

            GuildHelper.sql.execute(query);

            Message.suppressContentIntentWarning();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
