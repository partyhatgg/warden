package dev.partyhat.warden.paper;

import dev.partyhat.warden.util.player.IPlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class BukkitPlayerUtil implements IPlayerUtil {
    @Override
    public Player getPlayer(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return new Player(uuid, player.getName());
    }

    @Override
    public Player getPlayer(String username) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(username);
        return new Player(player.getUniqueId(), username);
    }
}
