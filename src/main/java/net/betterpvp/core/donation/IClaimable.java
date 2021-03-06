package net.betterpvp.core.donation;

import org.bukkit.entity.Player;

public interface IClaimable {

    void claim(Player player);

    String getClaimFailedReason();
    boolean canClaim(Player player);
}
