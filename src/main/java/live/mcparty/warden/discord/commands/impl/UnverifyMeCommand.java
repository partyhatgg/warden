package live.mcparty.warden.discord.commands.impl;

import live.mcparty.warden.Warden;
import live.mcparty.warden.discord.commands.IDiscordCommand;
import live.mcparty.warden.util.MojangApiUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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
    public void executeCommand(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue(hook -> {
            hook.setEphemeral(true);
            UUID uuid = Warden.getInstance().getWhitelistHandler().unwhitelistByDiscordID(event.getUser().getIdLong());
            if (uuid == null) {
                hook.sendMessage(createUnverifyFailEmbed()).queue();
            } else {
                hook.sendMessage(createUnverifyEmbed(uuid, event.getUser().getIdLong())).queue();
            }
        });
    }

    private MessageCreateData createUnverifyEmbed(UUID uuid, long discordId) {
        String username = MojangApiUtil.getUsernameForUUID(uuid);
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
