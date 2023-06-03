package live.mcparty.warden;

import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class VerificationHandler {
    public static final long EXPIRATION_LENGTH = 60L;
    private final Map<String, VerificationCode> verificationCodeMap = new HashMap<>();

    /**
     * Creates a unique verification code for a given player and puts it in the cache
     * @param playerUuid uuid of the player generated for
     * @return verification code
     */
    @NotNull
    public VerificationCode generateVerificationCodeForPlayer(@NotNull UUID playerUuid) {
        String verificationCode = RandomStringUtils.randomAlphanumeric(5).toUpperCase(Locale.ROOT);
        while (verificationCodeMap.get(verificationCode) == null) {
            verificationCode = RandomStringUtils.randomAlphanumeric(5).toUpperCase(Locale.ROOT);
        }
        VerificationCode vc = new VerificationCode(verificationCode, playerUuid, Instant.now());
        verificationCodeMap.put(verificationCode, vc);
        return vc;
    }

    /**
     * Tries to verify user. Fails if code is expired or
     * @param verificationCode code given by the user. Must not be null!
     * @param discordId id of the user
     * @return success boolean
     */
    public boolean verifyUser(@NotNull String verificationCode, long discordId) {
        VerificationCode maybeVerification = verificationCodeMap.get(verificationCode);
        if (maybeVerification != null) {
            if (maybeVerification.getExpirationInstant().isBefore(Instant.now())) {
                verificationCodeMap.remove(verificationCode);
                return false;
            }
            Warden.getInstance().getWhitelistHandler().whitelist(maybeVerification.issuedTo(), discordId);
            return true;
        }
        return false;
    }

    /**
     * Searches for the verification code of a user by UUID
     * @param uuid uuid of the player to search for
     * @return the information if found, null if not
     */
    @Nullable
    public VerificationCode getVerificationCodeByUuid(UUID uuid) {
        for (VerificationCode vc : verificationCodeMap.values()) {
            if (vc.issuedTo().equals(uuid)) {
                return vc;
            }
        }
        return null;
    }

    public record VerificationCode(String code, UUID issuedTo, Instant issueInstant) {
        public Instant getExpirationInstant() {
            return issueInstant.plusSeconds(EXPIRATION_LENGTH);
        }
    }

}
