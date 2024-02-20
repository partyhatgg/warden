package dev.partyhat.warden.discord.commands.impl;

import dev.partyhat.warden.Warden;
import dev.partyhat.warden.discord.commands.IDiscordCommand;
import dev.partyhat.warden.util.CollectionUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;
import java.util.UUID;

public class UnverifyCommand implements IDiscordCommand {
    @Override
    public CommandData createCommand() {
        return Commands.slash("unverify", "Unverifies a minecraft account from a discord account")
                .addOption(OptionType.USER, "discord", "The user's discord account", false, false)
                .addOption(OptionType.STRING, "minecraft", "The user's minecraft username", false, true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
    }

    @Override
    public void executeCommand(InteractionHook hook) {
        SlashCommandInteraction interaction = ((SlashCommandInteraction) hook.getInteraction());
        if (
                Warden.SUPERUSERS.contains(interaction.getUser().getIdLong()) ||
                        (interaction.getGuild() != null && CollectionUtil.containsAny(interaction.getMember().getRoles(), Warden.modRoles))
        ) {
            var discordOption = interaction.getOption("discord");
            var minecraftOption = interaction.getOption("minecraft");
            if (discordOption != null) {
                var discordUser = discordOption.getAsUser();
                UUID uuid = Warden.getInstance().getWhitelistHandler().unwhitelistByDiscordID(discordUser.getIdLong());
                if (uuid == null) {
                    hook.sendMessage(createUnverifyFailEmbed("User not found in whitelist.")).queue();
                } else {
                    hook.sendMessage(createUnverifyEmbed(uuid, null, discordUser.getIdLong())).queue();
                }
            } else if (minecraftOption != null) {
                String username = minecraftOption.getAsString();
                UUID uuid = Warden.PLAYER_UTIL.getPlayer(username).uuid();
                if (uuid.version() != 4) {
                    hook.sendMessage(createUnverifyFailEmbed("UUID lookup failed. Is this a valid username? \n(`" + username + "`)")).queue();
                }
                Long discordId = Warden.getInstance().getWhitelistHandler().unwhitelistByUUID(uuid);
                if (discordId == null) {
                    hook.sendMessage("User not found in whitelist. \n(`" + username + "`)").queue();
                } else {
                    hook.sendMessage(createUnverifyEmbed(uuid, username, discordId)).queue();
                }
            } else {
                hook.sendMessage(MessageCreateData.fromContent("You must provide one of the options!")).queue();
            }
        } else {
            hook.sendMessage(createPermissionFailEmbed()).queue();
        }
    }

    private MessageCreateData createUnverifyEmbed(UUID uuid, String username, long discordId) {
        if (username == null) {
            username = Warden.PLAYER_UTIL.getPlayer(uuid).username();
        }
        MessageEmbed lookupEmbed = new EmbedBuilder()
                .setAuthor("User Unverified", null, "https://cdn.discordapp.com/icons/421459800757501952/255e24acfe657af4f0a01067d58ff99d.png")
                .setColor(Color.CYAN)
                .addField(new MessageEmbed.Field("Discord", "<@" + discordId + "> `" + discordId + "`", false))
                .addField(new MessageEmbed.Field("Minecraft", "Username: " + username + " (`" + uuid + "`)", false))
                .build();
        return MessageCreateData.fromEmbeds(lookupEmbed);
    }

    private MessageCreateData createUnverifyFailEmbed(String reason) {
        MessageEmbed lookupFailEmbed = new EmbedBuilder()
                .setAuthor("Unverify Failed", null, "https://cdn.discordapp.com/icons/421459800757501952/255e24acfe657af4f0a01067d58ff99d.png")
                .setColor(Color.RED)
                .addField(new MessageEmbed.Field("Reason", reason, false))
                .build();
        return MessageCreateData.fromEmbeds(lookupFailEmbed);
    }

    private MessageCreateData createPermissionFailEmbed() {
        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("How did you get here?", null, "https://cdn.discordapp.com/icons/421459800757501952/255e24acfe657af4f0a01067d58ff99d.png")
                .setColor(Color.RED)
                .build();
        return MessageCreateData.fromEmbeds(embed);
    }
}
