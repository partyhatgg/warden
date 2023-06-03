package live.mcparty.warden.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface IDiscordCommand {
    CommandData createCommand();

    void executeCommand(SlashCommandInteractionEvent event);

}
