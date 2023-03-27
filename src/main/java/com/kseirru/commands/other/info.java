package com.kseirru.commands.other;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.kseirru.core.Translator;
import com.kseirru.utils.JDAColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.Map;
import java.util.Objects;

public class info extends SlashCommand {
    public info() {
        this.name = "info";
        this.help = "Information about the bot";
        this.descriptionLocalization = Map.ofEntries(
                Map.entry(DiscordLocale.RUSSIAN, "Информация о боте")
        );

        this.guildOnly = true;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Translator tr = new Translator(Objects.requireNonNull(event.getGuild()).getId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(tr.get("info.embed.title") + event.getJDA().getSelfUser().getName())
                .setColor(JDAColors.DEFAULT)
                .setDescription(tr.get("info.embed.description"))
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());

        String _botInfo = """
                %s: %s
                %s: %s ms
                """;

        String botInfo = String.format(_botInfo, tr.get("info.guilds"), event.getJDA().getGuilds().toArray().length, tr.get("info.latency"), event.getJDA().getGatewayPing());

        embedBuilder.addField(tr.get("info.botInfo"), botInfo, true);

        embedBuilder.addField(tr.get("info.developer"), "[kseiru](https://discord.com/users/222392036387454978)", true);

        String SpecialThanksString = """
                
                [xhd](https://www.youtube.com/channel/UCwBwR-IwZACd_U2nyrHkMhQ)
                NeoTix
                KNTEAM <3""";

        embedBuilder.addField(tr.get("info.specialThanks"), SpecialThanksString, true);

        embedBuilder.addField("\u200b", tr.get("info.ifError"), false);

        embedBuilder.addField(tr.get("info.dataUsageTitle"), tr.get("info.dataUsage"), false);

        Button button = Button.link("https://discord.gg/w3yYwk6UhJ", "Support").withEmoji(Emoji.fromUnicode("🧑‍💻"));

        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder()
                .setEmbeds(embedBuilder.build())
                .addActionRow(button);

        event.reply(messageCreateBuilder.build()).setEphemeral(true).queue();
    }
}