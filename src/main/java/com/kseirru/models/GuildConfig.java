package com.kseirru.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class GuildConfig {
    private String guildId = "0";
    private String locale = "en";
    private String logChannelId = "0";
    private boolean logEnabled = false;
    private boolean messageEditEvent = false;
    private boolean messageDeleteEvent = false;
    private boolean modActionEvent = false;
    private boolean trafficEvent = false;

    public GuildConfig(String guildId) {
        this.guildId = guildId;
        try {
            Connection connection = DriverManager.getConnection("JDBC:sqlite:gh.db");
            String query = String.format("SELECT * FROM guildConfig WHERE guild_id = '%s'", this.guildId);
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            while (resultSet.next()) {
                this.locale = resultSet.getString("locale");
                this.logChannelId = resultSet.getString("logChannelId");
                this.logEnabled = resultSet.getInt("logEnabled") == 1;
                this.messageEditEvent = resultSet.getInt("messageEditEvent") == 1;
                this.modActionEvent = resultSet.getInt("modActionEvent") == 1;
                this.trafficEvent = resultSet.getInt("trafficEvent") == 1;
            }
        } catch (Exception ignored) {}
    }

    public void update() {
        try {
            Connection connection = DriverManager.getConnection("JDBC:sqlite:gh.db");
            Statement statement = connection.createStatement();

            if(statement.executeQuery("SELECT * FROM guildConfig WHERE guild_id = '" + this.guildId + "'").next() == false) {
                String query = String.format("""
                        INSERT INTO guildConfig (guild_id, locale, logChannelId, logEnabled, messageEditEvent, messageDeleteEvent, modActionEvent, trafficEvent) VALUES ('%s', '%s', '%s', %s, %s, %s, %s, %s)""",
                        this.locale, this.logChannelId,
                        this.logEnabled ? 1 : 0,
                        this.messageEditEvent ? 1 : 0,
                        this.messageDeleteEvent ? 1 : 0,
                        this.modActionEvent ? 1 : 0,
                        this.trafficEvent ? 1 : 0);
                statement.execute(query);
            } else {

                String query = String.format("""
                                UPDATE guildConfig SET locale = '%s', logChannelId = '%s', logEnabled = %s, messageEditEvent = %s, messageDeleteEvent = %s, modActionEvent = %s, trafficEvent = %s WHERE guild_id = '%s'""",
                        this.locale, this.logChannelId,
                        this.logEnabled ? 1 : 0,
                        this.messageEditEvent ? 1 : 0,
                        this.messageDeleteEvent ? 1 : 0,
                        this.modActionEvent ? 1 : 0,
                        this.trafficEvent ? 1 : 0);
                statement.execute(query);
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getGuildId() {
        return guildId;
    }

    public String getLocale() {
        return locale;
    }

    public String getLogChannelId() {
        return logChannelId;
    }

    public boolean LogEnabledStatus() {
        return logEnabled;
    }

    public boolean MessageDeleteEventStatus() {
        return messageDeleteEvent;
    }

    public boolean MessageEditEventStatus() {
        return messageEditEvent;
    }

    public boolean ModActionEventStatus() {
        return modActionEvent;
    }

    public boolean TrafficEventStatus() {
        return trafficEvent;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    public void setMessageDeleteEvent(boolean messageDeleteEvent) {
        this.messageDeleteEvent = messageDeleteEvent;
    }

    public void setMessageEditEvent(boolean messageEditEvent) {
        this.messageEditEvent = messageEditEvent;
    }

    public void setLogChannelId(String logChannelId) {
        this.logChannelId = logChannelId;
    }

    public void setModActionEvent(boolean modActionEvent) {
        this.modActionEvent = modActionEvent;
    }

    public void setTrafficEvent(boolean trafficEvent) {
        this.trafficEvent = trafficEvent;
    }
}
