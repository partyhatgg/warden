package live.mcparty.warden.discord.commands;

import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface IDiscordCommand {
    CommandData createCommand();

    void executeCommand(InteractionHook event);

    default boolean hasEphemeralArgument() {
        return false;
    };

}
