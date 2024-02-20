package dev.partyhat.warden.discord.commands.impl;

import dev.partyhat.warden.Warden;
import dev.partyhat.warden.discord.commands.IDiscordCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;
import java.util.UUID;

public class UnverifyMeCommand implements IDiscordCommand {
    @Override
    public CommandData createCommand() {
        return Commands.slash("unverifyme", "Unlinks your discord account from your minecraft account.");
    }

    @Override
    public void executeCommand(InteractionHook hook) {
        SlashCommandInteraction interaction = ((SlashCommandInteraction) hook.getInteraction());
        UUID uuid = Warden.getInstance().getWhitelistHandler().unwhitelistByDiscordID(interaction.getUser().getIdLong());
        if (uuid == null) {
            hook.sendMessage(createUnverifyFailEmbed()).queue();
        } else {
            hook.sendMessage(createUnverifyEmbed(uuid, interaction.getUser().getIdLong())).queue();
        }
    }

    private MessageCreateData createUnverifyEmbed(UUID uuid, long discordId) {
        String username = Warden.PLAYER_UTIL.getPlayer(uuid).username();
        MessageEmbed lookupEmbed = new EmbedBuilder()
                .setAuthor("User Unverified", null, "https://cdn.discordapp.com/icons/421459800757501952/255e24acfe657af4f0a01067d58ff99d.png")
                .setColor(Color.CYAN)
                .addField(new MessageEmbed.Field("Discord", "<@" + discordId + "> `" + discordId + "`", false))
                .addField(new MessageEmbed.Field("Minecraft", "Username: " + username + " (`" + uuid + "`)", false))
                .build();
        return MessageCreateData.fromEmbeds(lookupEmbed);
    }

    private MessageCreateData createUnverifyFailEmbed() {
        MessageEmbed lookupFailEmbed = new EmbedBuilder()
                .setAuthor("Unverify Failed", null, "https://cdn.discordapp.com/icons/421459800757501952/255e24acfe657af4f0a01067d58ff99d.png")
                .setColor(Color.RED)
                .addField(new MessageEmbed.Field("Reason", "Could not find you! Are you whitelisted?", false))
                .build();
        return MessageCreateData.fromEmbeds(lookupFailEmbed);
    }
}
