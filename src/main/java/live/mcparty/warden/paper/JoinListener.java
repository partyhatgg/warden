package live.mcparty.warden.paper;

import live.mcparty.warden.VerificationHandler;
import live.mcparty.warden.Warden;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class JoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {
        Warden warden = Warden.getInstance();
        Player player = joinEvent.getPlayer();
        if (!warden.getWhitelistHandler().containsUUID(player.getUniqueId())) {
            VerificationHandler.VerificationCode maybeVc = warden.getVerificationHandler().getVerificationCodeByUuid(player.getUniqueId());
            VerificationHandler.VerificationCode vc = (maybeVc != null) ? maybeVc : warden.getVerificationHandler().generateVerificationCodeForPlayer(player.getUniqueId());
            if (Warden.isMigratoryPeriod) {
                player.sendMessage(this.createMigrationText(vc.code()));
            } else {
                player.kick(this.createKickText(vc.code(), Duration.between(Instant.now(), vc.getExpirationInstant())));
            }
            player.sendMessage(
                    Component.text()
            );
        }
    }

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
                                .append(Component.text("/verify " + verificationCode, NamedTextColor.WHITE))
                                .append(Component.text(" in Discord!", NamedTextColor.GRAY))
                );
    }
}
