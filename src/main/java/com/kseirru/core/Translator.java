package com.kseirru.core;

import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kseirru.models.GuildConfig;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class Translator {
    private String guildId;
    private String locale;

    public Translator(String guildId) {
        this.guildId = guildId;
        this.locale = "en";

        GuildConfig guildConfig = new GuildConfig(guildId);
        this.locale = guildConfig.getLocale();

    }

    public String get(String key) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(this.locale + ".json");
            if (inputStream == null) {
                inputStream = ClassLoader.getSystemResourceAsStream("en.json");
                GuildHelper.logger.error("TRANSLATOR | inputStream is NULL!");
            }
            Map<String, String> map = objectMapper.readValue(inputStream, new TypeReference<>() {
            });
            String value = map.get(key);
            if (value == null) {
                LoggerFactory.getLogger("Translator").error("Key '" + key + "' not found in '" + locale + ".json' file!");
                return key;
            }
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return key;
        }
    }
}
