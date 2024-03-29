package dev.partyhat.warden.discord;

import dev.partyhat.warden.Warden;
import dev.partyhat.warden.discord.commands.IDiscordCommand;
import dev.partyhat.warden.discord.commands.impl.LookupCommand;
import dev.partyhat.warden.discord.commands.impl.UnverifyCommand;
import dev.partyhat.warden.discord.commands.impl.UnverifyMeCommand;
import dev.partyhat.warden.discord.commands.impl.VerifyCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandManager extends ListenerAdapter {
    private final Map<String, IDiscordCommand> commandMap = new HashMap<>();
    public CommandManager() {
        commandMap.put("verify", new VerifyCommand());
        commandMap.put("lookup", new LookupCommand());
        commandMap.put("unverify", new UnverifyCommand());
        commandMap.put("unverifyme", new UnverifyMeCommand());
    }

    public void registerCommands(JDA jda) {
        jda.updateCommands().addCommands(
                commandMap.values().stream().map(IDiscordCommand::createCommand).collect(Collectors.toSet())
        ).queue(success -> {
            Warden.LOGGER.info("Successfully registered discord commands.");
        }, failure -> {
            Warden.LOGGER.error("Failed to register discord commands.");
        });
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        IDiscordCommand command = commandMap.get(event.getName());
        boolean ephemeral = true;
        if (command.hasEphemeralArgument()) {
            ephemeral = event.getInteraction().getOption("ephemeral", true, OptionMapping::getAsBoolean);
        }
        event.deferReply(ephemeral).queue(hook -> {
            commandMap.get(event.getName()).executeCommand(hook);
        });
    }

//    @Override
//    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
//        IDiscordCommand command = commandMap.get(event.getName());
//        if (command instanceof IAutocompleteDiscordCommand) {
//            ((IAutocompleteDiscordCommand) command).handleAutocomplete(event);
//        }
//    }
}
