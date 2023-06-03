package live.mcparty.warden.paper;

import live.mcparty.warden.VerificationHandler;
import live.mcparty.warden.Warden;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
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
        return Component.text()
                .content("You are not whitelisted!")
                .style(builder -> {
                    builder.decorate(TextDecoration.BOLD);
                    builder.color(NamedTextColor.RED);
                })
                .appendNewline()
                .append(
                        Component.text()
                                .content("Join ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text("discord.gg/offlinetv"))
                                .append(Component.text(" and run:").color(NamedTextColor.GRAY))
                )
                .appendNewline()
                .append(
                        Component.text()
                                .content("/verify " + verificationCode)
                )
                .appendNewline()
                .append(
                        Component.text()
                                .content("This code expires in ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(timeLeft.truncatedTo(ChronoUnit.SECONDS).toString()))
                                .append(Component.text(".").color(NamedTextColor.GRAY))
                )
                .build();
    }

    private Component createMigrationText(String verificationCode) {
        return Component.text()
                .content("Hey! ")
                .style(builder -> {
                    builder.decorate(TextDecoration.BOLD);
                    builder.color(NamedTextColor.AQUA);
                })
                .append(
                        Component.text()
                                .content("We're migrating to a new whitelist system and don't seem to have your name on it yet.")
                                .color(NamedTextColor.GRAY)
                )
                .appendNewline()
                .append(
                        Component.text()
                                .content("Please run ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text("/verify " + verificationCode))
                                .append(
                                        Component.text()
                                                .content(" in Discord!")
                                                .color(NamedTextColor.GRAY)
                                )
                )
                .build();
    }
}
