package dev.partyhat.warden.util.player;

import java.util.UUID;

public interface IPlayerUtil {

    Player getPlayer(UUID uuid);

    Player getPlayer(String username);

}
