package dev.partyhat.warden.fabric;

import dev.partyhat.warden.IConfig;
import dev.partyhat.warden.Warden;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class FabricServerConfig implements IConfig {
    private Properties props = new Properties();

    public FabricServerConfig(Path path) throws IOException {
        props.loadFromXML(Files.newInputStream(path, StandardOpenOption.CREATE));
    }

    @Override
    public Object getPrimitive(String path) {
        return props.get(path);
    }

    @Override
    public <T> T getObject(String path, Class<T> type) {
        return Warden.GSON.fromJson(props.getProperty(path), type);
    }
}
