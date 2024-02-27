package dev.partyhat.warden.paper;

import dev.partyhat.warden.IConfig;
import org.bukkit.configuration.file.FileConfiguration;

public class PaperConfig implements IConfig {
    FileConfiguration config;

    public PaperConfig(FileConfiguration fileConfig) {
        this.config = fileConfig;
    }

    @Override
    public Object getPrimitive(String path) {
        return this.config.get(path);
    }

    @Override
    public <T> T getObject(String path, Class<T> type) {
        return this.config.getObject(path, type);
    }
}
