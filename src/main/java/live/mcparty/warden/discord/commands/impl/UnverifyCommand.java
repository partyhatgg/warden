package live.mcparty.warden.discord.commands.impl;

import live.mcparty.warden.Warden;
import live.mcparty.warden.discord.commands.IDiscordCommand;
import live.mcparty.warden.util.MojangApiUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
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
                .addSubcommands()
                ;
    }

    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue(hook -> {
            var discordOption = event.getOption("discord");
            var minecraftOption = event.getOption("minecraft");
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
                UUID uuid = MojangApiUtil.getUUIDForUsername(username);
                if (uuid == null) {
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
        });
    }

    private MessageCreateData createUnverifyEmbed(UUID uuid, String username, long discordId) {
        if (username == null) {
            username = MojangApiUtil.getUsernameForUUID(uuid);
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
}
