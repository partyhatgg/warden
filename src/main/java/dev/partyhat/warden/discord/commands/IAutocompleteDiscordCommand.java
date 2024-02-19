package dev.partyhat.warden.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public interface IAutocompleteDiscordCommand extends IDiscordCommand {
    public void handleAutocomplete(CommandAutoCompleteInteractionEvent event);
}
