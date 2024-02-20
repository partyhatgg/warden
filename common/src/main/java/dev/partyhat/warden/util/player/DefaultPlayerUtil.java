package dev.partyhat.warden.util.player;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import dev.partyhat.warden.Warden;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.UUID;

public class DefaultPlayerUtil implements IPlayerUtil {
    private final OkHttpClient client = new OkHttpClient();
    private final BiMap<String, UUID> cache = HashBiMap.create();

    @Override
    public Player getPlayer(UUID uuid) {
        String username = cache.inverse().computeIfAbsent(uuid, this::getUsernameFromUUID);
        return new Player(uuid, username);
    }

    @Override
    public Player getPlayer(String username) {
        UUID uuid = cache.computeIfAbsent(username, this::getUUIDFromUsername);
        return new Player(uuid, username);
    }

    private UUID getUUIDFromUsername(String username) {
        try (
                Response res = client.newCall(
                        new Request.Builder()
                                .url("https://api.ashcon.app/mojang/v2/user/" + username)
                                .get()
                                .build()
                ).execute()
        ) {
            // This will always be non null because it was returned from `Call#execute`
            // https://github.com/square/okhttp/blob/parent-4.10.0/okhttp/src/main/kotlin/okhttp3/Response.kt#L71-L73
            assert res.body() != null;
            JsonObject json = Warden.GSON.fromJson(res.body().charStream(), JsonObject.class);
            return UUID.fromString(json.get("uuid").getAsString());
        } catch (IOException e) {
            return null;
        }
    }

    private String getUsernameFromUUID(UUID uuid) {
        try (
                Response res = client.newCall(
                        new Request.Builder()
                                .url("https://api.ashcon.app/mojang/v2/user/" + uuid)
                                .get()
                                .build()
                ).execute()
        ) {
            // This will always be non null because it was returned from `Call#execute`
            // https://github.com/square/okhttp/blob/parent-4.10.0/okhttp/src/main/kotlin/okhttp3/Response.kt#L71-L73
            assert res.body() != null;
            JsonObject json = Warden.GSON.fromJson(res.body().charStream(), JsonObject.class);
            return json.get("username").getAsString();
        } catch (IOException e) {
            return null;
        }
    }
}
