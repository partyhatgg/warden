package dev.partyhat.warden.paper;

import dev.partyhat.warden.Warden;
import org.bukkit.plugin.java.JavaPlugin;

public class WardenPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Warden.LOGGER = getSLF4JLogger();
        Warden.PLAYER_UTIL = new BukkitPlayerUtil();
        new Warden(getDataFolder(), new PaperConfig(getConfig()));
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
    }

    @Override
    public void onDisable() {
        Warden.getInstance().onShutdown();
    }
}
