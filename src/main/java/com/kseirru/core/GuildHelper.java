package com.kseirru.core;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
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
    private ArrayList<CachedMessage> cachedMessages;
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

            CommandClient commandClient = builder.build();

            JDA jda = JDABuilder.create(token, GatewayIntent.getIntents(130815))
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS)
                    .addEventListeners(commandClient)
                    .setEventPassthrough(true)
                    .build();

            Message.suppressContentIntentWarning();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
