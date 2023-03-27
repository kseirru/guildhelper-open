package com.kseirru;

import com.kseirru.core.GuildHelper;
import com.kseirru.models.GuildConfig;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {
    public static void main(String[] args) {
        GuildConfig guildConfig = new GuildConfig("123");
        guildConfig.setLocale("de");
        guildConfig.update();
        System.out.println(guildConfig.getLocale());
    }
}