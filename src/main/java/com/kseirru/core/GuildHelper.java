package com.kseirru.core;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.kseirru.commands.admin.EmbedCommands;
import com.kseirru.commands.admin.config;
import com.kseirru.commands.moderation.ban;
import com.kseirru.commands.moderation.kick;
import com.kseirru.commands.moderation.timeout;
import com.kseirru.commands.moderation.timeoutCancel;
import com.kseirru.commands.other.info;
import com.kseirru.events.logger.MessageEdit;
import com.kseirru.events.logger.NewMessage;
import com.kseirru.models.CachedMessage;
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
    private Connection db;
    private Statement sql;

    public GuildHelper(String token) {
        try {
            this.db = DriverManager.getConnection("jdbc:sqlite:gh.db");
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

            builder.addSlashCommand(new info());


            CommandClient commandClient = builder.build();

            JDA jda = JDABuilder.create(token, GatewayIntent.getIntents(130815))
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS)
                    .addEventListeners(commandClient)
                    .setEventPassthrough(true)
                    .addEventListeners(new NewMessage(), new MessageEdit(), new MessageDelete())
                    .build();

            Message.suppressContentIntentWarning();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
