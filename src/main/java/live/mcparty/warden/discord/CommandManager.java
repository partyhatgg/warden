package live.mcparty.warden.discord;

import live.mcparty.warden.Warden;
import live.mcparty.warden.discord.commands.IAutocompleteDiscordCommand;
import live.mcparty.warden.discord.commands.IDiscordCommand;
import live.mcparty.warden.discord.commands.impl.LookupCommand;
import live.mcparty.warden.discord.commands.impl.UnverifyCommand;
import live.mcparty.warden.discord.commands.impl.UnverifyMeCommand;
import live.mcparty.warden.discord.commands.impl.VerifyCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
            Warden.getInstance().getSLF4JLogger().info("Successfully registered discord commands.");
        }, failure -> {
            Warden.getInstance().getSLF4JLogger().error("Failed to register discord commands.");
        });
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue(hook -> {
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
