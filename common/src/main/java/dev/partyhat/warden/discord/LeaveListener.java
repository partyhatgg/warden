package dev.partyhat.warden.discord;

import dev.partyhat.warden.Warden;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class LeaveListener extends ListenerAdapter {
    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        if (event.getGuild().getIdLong() != 421459800757501952L) return;
        Warden.getInstance().getWhitelistHandler().unwhitelistByDiscordID(event.getUser().getIdLong());
    }
}
