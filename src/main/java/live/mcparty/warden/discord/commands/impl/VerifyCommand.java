package live.mcparty.warden.discord.commands.impl;

import live.mcparty.warden.Warden;
import live.mcparty.warden.discord.commands.IDiscordCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
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
    public void executeCommand(InteractionHook hook) {
        SlashCommandInteraction interaction = ((SlashCommandInteraction) hook.getInteraction());
        if (Warden.getInstance().getWhitelistHandler().containsDiscordID(interaction.getUser().getIdLong())) {
            hook.sendMessage("You've already verified!").setEphemeral(true).queue();
            return;
        }
        hook.setEphemeral(true);
        String code = interaction.getOption("code").getAsString();
        boolean success = Warden.getInstance().getVerificationHandler().verifyUser(code, interaction.getUser().getIdLong());
        if (success) {
            hook.sendMessage(createSuccessEmbed()).queue();
        } else {
            hook.sendMessage(createFailureEmbed()).queue();
        }
    }

    private MessageCreateData createSuccessEmbed() {
        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("Verified!", null, "https://cdn.discordapp.com/icons/421459800757501952/255e24acfe657af4f0a01067d58ff99d.png")
                .setColor(Color.CYAN)
                .setDescription("Thank you for verifying! You've now been whitelisted." + (Warden.isMigratoryPeriod ? "" : " Please reconnect."))
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
