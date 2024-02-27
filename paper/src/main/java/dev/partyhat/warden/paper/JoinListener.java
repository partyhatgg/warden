package dev.partyhat.warden.paper;

import dev.partyhat.warden.JoinHandler;
import dev.partyhat.warden.Warden;
import dev.partyhat.warden.util.player.Player;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {
        Player player = new Player(joinEvent.getPlayer().getUniqueId(), joinEvent.getPlayer().getName());
        Component component = Warden.getInstance().getJoinHandler().handleJoin(player);
        if (component != null) {
            joinEvent.getPlayer().sendMessage(component);
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent loginEvent) {
        if (Warden.isMigratoryPeriod && loginEvent.getResult() != PlayerLoginEvent.Result.KICK_WHITELIST) return;
        Player player = new Player(loginEvent.getPlayer().getUniqueId(), loginEvent.getPlayer().getName());
        JoinHandler.LoginResult result = Warden.getInstance().getJoinHandler().handleLogin(player);
        if (result.shouldAllow()) {
            loginEvent.allow();
        } else {
            assert result.reason() != null;
            loginEvent.disallow(PlayerLoginEvent.Result.KICK_OTHER, result.reason());
        }
    }
}
