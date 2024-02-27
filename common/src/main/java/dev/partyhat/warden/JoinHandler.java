package dev.partyhat.warden;

import dev.partyhat.warden.util.player.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class JoinHandler {
    /**
     * Handles the player after joining.<br><br>
     * @param player player to check the status of
     * @return null if nothing should be sent, otherwise component to be shown
     */
    @Nullable
    public Component handleJoin(Player player) {
        if (!Warden.isMigratoryPeriod) return null;
        Warden warden = Warden.getInstance();
        if (!warden.getWhitelistHandler().containsUUID(player.uuid())) {
            VerificationHandler.VerificationCode maybeVc = warden.getVerificationHandler().getVerificationCodeByUuid(player.uuid());
            VerificationHandler.VerificationCode vc = (maybeVc != null) ? maybeVc : warden.getVerificationHandler().generateVerificationCodeForPlayer(player.uuid());
            Warden.LOGGER.info("Issued code `" + vc.code() + "` to " + player.username() + " (`" + player.uuid() + "`)");
            return this.createMigrationText(vc.code());
        }
        return null;
    }


    /**
     * Handles the player while logging in. <br><br>
     * Checks whether a player is whitelisted and if not, first try to get their verification code
     * If there is no existing verification code, create a new one and return the component for it
     * This component should then be shown to the player as a kick message
     * @param player player to check the status of
     * @return a [LoginResult] containing the proper information
     */
    public LoginResult handleLogin(Player player) {
        Warden warden = Warden.getInstance();
        if (!warden.getWhitelistHandler().containsUUID(player.uuid())) {
            VerificationHandler.VerificationCode maybeVc = warden.getVerificationHandler().getVerificationCodeByUuid(player.uuid());
            VerificationHandler.VerificationCode vc = (maybeVc != null) ? maybeVc : warden.getVerificationHandler().generateVerificationCodeForPlayer(player.uuid());
            return new LoginResult(false, this.createKickText(vc.code(), Duration.between(Instant.now(), vc.getExpirationInstant())));
        } else {
            return new LoginResult(true, null);
        }
    }

    public record LoginResult(boolean shouldAllow, @Nullable Component reason) {};

    private Component createKickText(String verificationCode, Duration timeLeft) {
        return Component.empty()
                .append(
                        Component.text("You are not whitelisted!", NamedTextColor.RED, TextDecoration.BOLD)
                )
                .appendNewline()
                .append(
                        Component.text("Join ", NamedTextColor.GRAY)
                                .append(Component.text("discord.gg/offlinetv", NamedTextColor.WHITE))
                                .append(Component.text(" and run:", NamedTextColor.GRAY))
                )
                .appendNewline()
                .append(
                        Component.text("/verify " + verificationCode, NamedTextColor.WHITE)
                )
                .appendNewline()
                .append(
                        Component.text("This code expires in ", NamedTextColor.GRAY)
                                .append(
                                        Component.text(
                                                timeLeft.truncatedTo(ChronoUnit.SECONDS).toString()
                                                        .substring(2)
                                                        .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                                                        .toLowerCase(),
                                                NamedTextColor.WHITE
                                        )
                                )
                                .append(Component.text(".", NamedTextColor.GRAY))
                );
    }

    private Component createMigrationText(String verificationCode) {
        return Component.empty()
                .append(
                        Component.text("Hey! ", NamedTextColor.AQUA, TextDecoration.BOLD)
                )
                .append(
                        Component.text("We're migrating to a new whitelist system and don't seem to have your name on it yet.", NamedTextColor.GRAY)
                )
                .appendNewline()
                .append(
                        Component.text("Please run ", NamedTextColor.GRAY)
                                .append(Component.text("/verify " + verificationCode, NamedTextColor.WHITE).clickEvent(ClickEvent.suggestCommand("/verify code:" + verificationCode)))
                                .append(Component.text(" in Discord!", NamedTextColor.GRAY))
                );
    }
}
