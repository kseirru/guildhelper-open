package com.kseirru.RestActions;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;

public class CheckActions {
    public static boolean botHasPermission(SlashCommandEvent event, Permission permission) {
        return Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getMember(event.getJDA().getSelfUser())).hasPermission(permission);
    }

    public static boolean selfPurge(SlashCommandEvent event, User violator) {
        return event.getUser().equals(violator);
    }

    public static boolean botPurge(SlashCommandEvent event, User violator) {
        return event.getJDA().getSelfUser().equals(violator);
    }

    public static boolean ownerCheck(SlashCommandEvent event, User violator) {
        return Objects.requireNonNull(event.getGuild()).getOwnerId().equals(violator.getId());
    }

    public static boolean canInteract(SlashCommandEvent event, Member violator) {
        return Objects.requireNonNull(event.getGuild()).retrieveMember(event.getJDA().getSelfUser()).complete().canInteract(violator);
    }

}