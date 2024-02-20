package dev.partyhat.warden.discord.commands.impl;

import dev.partyhat.warden.Warden;
import dev.partyhat.warden.discord.commands.IDiscordCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;
import java.util.UUID;

public class LookupCommand implements IDiscordCommand {
    @Override
    public CommandData createCommand() {
        return Commands.slash("lookup", "Looks up a user's information.")
                .addOption(OptionType.USER, "discord", "The user's discord account", false, false)
                .addOption(OptionType.STRING, "minecraft", "The user's minecraft username", false, true)
                .addOption(OptionType.BOOLEAN, "ephemeral", "Whether or not to be ephemereal", false, false);
    }

    @Override
    public void executeCommand(InteractionHook hook) {
        SlashCommandInteraction interaction = (SlashCommandInteraction) hook.getInteraction();
        var discordOption = interaction.getOption("discord");
        var minecraftOption = interaction.getOption("minecraft");
        if (discordOption != null) {
            var discordUser = discordOption.getAsUser();
            UUID uuid = Warden.getInstance().getWhitelistHandler().getByDiscordID(discordUser.getIdLong());
            if (uuid == null) {
                hook.sendMessage(createLookupFailEmbed("User not found in whitelist.")).queue();
            } else {
                hook.sendMessage(createLookupEmbed(uuid, null, discordUser.getIdLong())).queue();
            }
        } else if (minecraftOption != null) {
            String username = minecraftOption.getAsString();
            UUID uuid = Warden.PLAYER_UTIL.getPlayer(username).uuid();
            if (uuid.version() != 4) {
                hook.sendMessage(createLookupFailEmbed("UUID lookup failed. Is this a valid username? \n(`" + username + "`)")).queue();
            }
            Long discordId = Warden.getInstance().getWhitelistHandler().getByUUID(uuid);
            if (discordId == null) {
                hook.sendMessage("User not found in whitelist. \n(`" + username + "`)").queue();
            } else {
                hook.sendMessage(createLookupEmbed(uuid, username, discordId)).queue();
            }
        } else {
            hook.sendMessage(MessageCreateData.fromContent("You must provide one of the options!")).queue();
        }
    }

    @Override
    public boolean hasEphemeralArgument() {
        return true;
    }

    private MessageCreateData createLookupEmbed(UUID uuid, String username, long discordId) {
        if (username == null) {
            username = Warden.PLAYER_UTIL.getPlayer(uuid).username();
        }
        MessageEmbed lookupEmbed = new EmbedBuilder()
                .setAuthor("User Lookup", null, "https://cdn.discordapp.com/icons/421459800757501952/255e24acfe657af4f0a01067d58ff99d.png")
                .setColor(Color.CYAN)
                .addField(new MessageEmbed.Field("Discord", "<@" + discordId + "> `" + discordId + "`", false))
                .addField(new MessageEmbed.Field("Minecraft", "Username: " + username + " (`" + uuid + "`)", false))
                .build();
        return MessageCreateData.fromEmbeds(lookupEmbed);
    }

    private MessageCreateData createLookupFailEmbed(String reason) {
        MessageEmbed lookupFailEmbed = new EmbedBuilder()
                .setAuthor("User Lookup Failed", null, "https://cdn.discordapp.com/icons/421459800757501952/255e24acfe657af4f0a01067d58ff99d.png")
                .setColor(Color.RED)
                .addField(new MessageEmbed.Field("Reason", reason, false))
                .build();
        return MessageCreateData.fromEmbeds(lookupFailEmbed);
    }

//    @Override
//    public void handleAutocomplete(CommandAutoCompleteInteractionEvent event) {
//        var focusedOption = event.getFocusedOption();
//        if (focusedOption.getName().equals("minecraft")) {
//
//        }
//    }
}
