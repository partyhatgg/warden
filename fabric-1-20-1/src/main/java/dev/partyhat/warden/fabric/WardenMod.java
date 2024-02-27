package dev.partyhat.warden.fabric;

import dev.partyhat.warden.Warden;
import net.fabricmc.api.DedicatedServerModInitializer;

import java.io.IOException;
import java.nio.file.Paths;

public class WardenMod implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        try {
            new Warden(Paths.get("assets\\warden").toFile(), new FabricServerConfig(Paths.get("warden.xml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
