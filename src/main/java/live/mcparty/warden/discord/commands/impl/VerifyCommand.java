package live.mcparty.warden.discord.commands.impl;

import live.mcparty.warden.Warden;
import live.mcparty.warden.discord.commands.IDiscordCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

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
                hook.sendMessage("You've already verified!").queue();
            });
            return;
        }
        event.deferReply(true).queue(interactionHook -> {
            String code = event.getOption("code").getAsString();
            boolean success = Warden.getInstance().getVerificationHandler().verifyUser(code, event.getUser().getIdLong());
            if (success) {
                interactionHook.sendMessage("Thank you for verifying! You've now been whitelisted. Please reconnect.").queue();
            } else {
                interactionHook.sendMessage("Your code was either invalid or expired. Please try again.").queue();
            }
        });
    }
}
