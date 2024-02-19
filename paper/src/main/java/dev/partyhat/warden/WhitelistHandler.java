package dev.partyhat.warden;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Implements map but delegates to an inner map.
 * Immutable!!
 */
public class WhitelistHandler {
    private final BiMap<UUID, Long> whitelist = HashBiMap.create();
    private final File file;

    public WhitelistHandler(File dataFolder) {
        this.file = new File(dataFolder, "whitelist.json");
    }

    /**
     * Reads values from file
     */
    public void readFromFile() {
        Warden instance = Warden.getInstance();
        Logger logger = instance.getSLF4JLogger();
        try {
            if (!file.exists()) {
                if (file.getParentFile().mkdirs()) {
                    file.createNewFile();
                }
            }
            whitelist.putAll(instance.getObjectMapper().readValue(file, new TypeReference<>() {}));
        } catch (StreamReadException e) {
            logger.error("Invalid content found while reading.", e);
        } catch (DatabindException e) {
            logger.error("Invalid content structure found while reading", e);
        } catch (IOException e) {
            throw new RuntimeException("Something went catastrophically wrong when reading whitelist!", e);
        }
    }

    /**
     * Writes values to file
     */
    public void saveToFile() {
        Warden instance = Warden.getInstance();
        Logger logger = instance.getSLF4JLogger();
        try {
            instance.getObjectMapper().writeValue(file, whitelist);
        } catch (StreamWriteException | DatabindException e) {
            // I think this is fine to ignore?
            logger.warn("Not sure what happened", e);
        } catch (IOException e) {
            throw new RuntimeException("Something went catastrophically wrong when writing whitelist to file!", e);
        }
    }

    public void whitelist(UUID uuid, long discordId) {
        whitelist.put(uuid, discordId);
    }

    public Long unwhitelistByUUID(UUID uuid) {
        return whitelist.remove(uuid);
    }

    public UUID unwhitelistByDiscordID(long id) {
        return whitelist.inverse().remove(id);
    }

    public boolean containsUUID(UUID uuid) {
        return whitelist.containsKey(uuid);
    }

    public boolean containsDiscordID(long id) {
        return whitelist.containsValue(id);
    }

    @Nullable
    public Long getByUUID(UUID uuid) {
        return whitelist.get(uuid);
    }

    @Nullable
    public UUID getByDiscordID(long discordId) {
        return whitelist.inverse().get(discordId);
    }

}
