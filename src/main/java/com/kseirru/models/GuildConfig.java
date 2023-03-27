package com.kseirru.models;

import com.kseirru.core.GuildHelper;

import java.sql.*;

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
            String query = String.format("SELECT * FROM guildConfig WHERE guild_id = '%s'", this.guildId);
            ResultSet resultSet = GuildHelper.sql.executeQuery(query);
            if (resultSet.next()) {
                this.locale = resultSet.getString("locale");
                this.logChannelId = resultSet.getString("logChannelId");
                this.logEnabled = resultSet.getInt("logEnabled") == 1;
                this.messageEditEvent = resultSet.getInt("messageEditEvent") == 1;
                this.messageDeleteEvent = resultSet.getInt("messageDeleteEvent") == 1;
                this.modActionEvent = resultSet.getInt("modActionEvent") == 1;
                this.trafficEvent = resultSet.getInt("trafficEvent") == 1;
            } else {
                // Insert new record
                String insertQuery = "INSERT INTO guildConfig (guild_id, locale, logChannelId, logEnabled, messageEditEvent, messageDeleteEvent, modActionEvent, trafficEvent) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = GuildHelper.db.prepareStatement(insertQuery)) {
                    statement.setString(1, this.guildId);
                    statement.setString(2, this.locale);
                    statement.setString(3, this.logChannelId);
                    statement.setInt(4, this.logEnabled ? 1 : 0);
                    statement.setInt(5, this.messageEditEvent ? 1 : 0);
                    statement.setInt(6, this.messageDeleteEvent ? 1 : 0);
                    statement.setInt(7, this.modActionEvent ? 1 : 0);
                    statement.setInt(8, this.trafficEvent ? 1 : 0);
                    statement.executeUpdate();
                    statement.close();
                }
            }
        } catch (Exception ignored) {}
    }

    public void update() {
        try {
            String updateQuery = "UPDATE guildConfig SET locale=?, logChannelId=?, logEnabled=?, messageEditEvent=?, messageDeleteEvent=?, modActionEvent=?, trafficEvent=? WHERE guild_id=?";
            try (PreparedStatement statement = GuildHelper.db.prepareStatement(updateQuery)) {
                statement.setString(1, this.locale);
                statement.setString(2, this.logChannelId);
                statement.setInt(3, this.logEnabled ? 1 : 0);
                statement.setInt(4, this.messageEditEvent ? 1 : 0);
                statement.setInt(5, this.messageDeleteEvent ? 1 : 0);
                statement.setInt(6, this.modActionEvent ? 1 : 0);
                statement.setInt(7, this.trafficEvent ? 1 : 0);
                statement.setString(8, this.guildId);
                statement.executeUpdate();
                statement.close();
            }
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
