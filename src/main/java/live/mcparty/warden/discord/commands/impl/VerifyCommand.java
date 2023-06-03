package live.mcparty.warden.discord.commands.impl;

import live.mcparty.warden.Warden;
import live.mcparty.warden.discord.commands.IDiscordCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;

public class VerifyCommand implements IDiscordCommand {
    @Override
    public CommandData createCommand() {
        return Commands.slash("verify", "Verify to play on the OTV community server.")
                .addOption(OptionType.STRING, "code", "The verification code given by the server.", true);
    }

    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        if (Warden.getInstance().getWhitelistHandler().containsDiscordID(event.getUser().getIdLong())) {
            event.deferReply(true).queue(hook -> {
                hook.sendMessage("You've already verified!").setEphemeral(true).queue();
            });
            return;
        }
        event.deferReply(true).queue(interactionHook -> {
            interactionHook.setEphemeral(true);
            String code = event.getOption("code").getAsString();
            boolean success = Warden.getInstance().getVerificationHandler().verifyUser(code, event.getUser().getIdLong());
            if (success) {
                interactionHook.sendMessage(createSuccessEmbed()).queue();
            } else {
                interactionHook.sendMessage(createFailureEmbed()).queue();
            }
        });
    }

    private MessageCreateData createSuccessEmbed() {
        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("Verified!", null, "https://cdn.discordapp.com/icons/421459800757501952/255e24acfe657af4f0a01067d58ff99d.png")
                .setColor(Color.CYAN)
                .setDescription("Thank you for verifying! You've now been whitelisted." + (Warden.isMigratoryPeriod ? "" : "Please reconnect."))
                .build();
        return MessageCreateData.fromEmbeds(embed);
    }

    private MessageCreateData createFailureEmbed() {
        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("Error", null, "https://cdn.discordapp.com/icons/421459800757501952/255e24acfe657af4f0a01067d58ff99d.png")
                .setColor(Color.RED)
                .setDescription("Your code was either invalid or expired. Please try again.")
                .build();
        return MessageCreateData.fromEmbeds(embed);
    }
}
