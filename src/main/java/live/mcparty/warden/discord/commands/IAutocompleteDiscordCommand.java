package live.mcparty.warden.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface IAutocompleteDiscordCommand extends IDiscordCommand {
    public void handleAutocomplete(CommandAutoCompleteInteractionEvent event);
}
