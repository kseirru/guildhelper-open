package com.kseirru.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.HashMap;

public class Translator {
    private String guildId;
    private String locale;

    public Translator(String guildId) {
        this.guildId = guildId;
        this.locale = "en";

        try {
            Connection connection = DriverManager.getConnection("JDBC:sqlite:gh.db");
            String query = String.format("SELECT locale FROM guildConfig WHERE guild_id = '%s'", this.guildId);
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            while(resultSet.next()) {
                this.locale = resultSet.getString("locale");
            }
            resultSet.close();
            connection.close();
        } catch (Exception ignored) {}
    }

    public String get(String key) {
        String jsonFileName = String.format("%s.json", this.locale);
        String jsonFileUrl = String.format("/%s", jsonFileName);
        InputStream is = getClass().getResourceAsStream(jsonFileUrl);
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonContent.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
            HashMap<String, String> translations = objectMapper.readValue(jsonContent.toString(), typeRef);
            return translations.get(key);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
