package live.mcparty.warden.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import live.mcparty.warden.Warden;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

public class MojangApiUtil {
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final Cache<UUID, String> usernameCache = CacheBuilder.newBuilder().maximumSize(1000L).build();
    private static final Cache<String, UUID> uuidCache = CacheBuilder.newBuilder().maximumSize(1000L).build();

    /**
     * Queries for a user's username given their uuid
     * @param uuid uuid of the user
     * @return username of the player, empty if not found
     */
    @NotNull
    public static String getUsernameForUUID(UUID uuid) {
        String maybeUsername = usernameCache.getIfPresent(uuid);
        if (maybeUsername != null) {
            return maybeUsername;
        }
        String uuidString = uuid.toString().replace("-", "");
        Call call = httpClient.newCall(
                new Request.Builder()
                        .url("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidString)
                        .get()
                        .build()
        );
        try (Response res = call.execute()) {
            String username = Warden.getInstance().getObjectMapper().readTree(res.body().string()).get("name").asText();
            usernameCache.put(uuid, username);
            uuidCache.put(username, uuid);
            return username;
        } catch (IOException e) {
            Warden.getInstance().getSLF4JLogger().warn("Username request failed", e);
            return "";
        }
    }

    @Nullable
    public static UUID getUUIDForUsername(String username) {
        UUID maybeUUID = uuidCache.getIfPresent(username);
        if (maybeUUID != null) {
            return maybeUUID;
        }
        Call call = httpClient.newCall(
                new Request.Builder()
                        .url("https://api.mojang.com/users/profiles/minecraft/" + username)
                        .get()
                        .build()
        );
        try (Response res = call.execute()) {
            UUID uuid = UUID.fromString(
                    Warden.getInstance().getObjectMapper().readTree(
                            res.body().string()
                    ).get("id").asText().replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
                    )
            );
            usernameCache.put(uuid, username);
            uuidCache.put(username, uuid);
            return uuid;
        } catch (IOException e) {
            Warden.getInstance().getSLF4JLogger().warn("UUID request failed", e);
            return null;
        }
    }
}
