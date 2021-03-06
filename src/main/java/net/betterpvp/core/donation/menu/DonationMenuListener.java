package net.betterpvp.core.donation.menu;

import net.betterpvp.core.Core;
import net.betterpvp.core.client.Client;
import net.betterpvp.core.client.ClientUtilities;
import net.betterpvp.core.donation.Donation;
import net.betterpvp.core.donation.DonationManager;
import net.betterpvp.core.donation.IClaimable;
import net.betterpvp.core.donation.IDonation;
import net.betterpvp.core.donation.mysql.DonationRepository;
import net.betterpvp.core.framework.BPVPListener;
import net.betterpvp.core.interfaces.events.ButtonClickEvent;
import net.betterpvp.core.utility.UtilMessage;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;

public class DonationMenuListener extends BPVPListener<Core> {

    public DonationMenuListener(Core instance) {
        super(instance);
    }

    @EventHandler
    public void onClick(ButtonClickEvent e){
        if(e.getMenu() instanceof DonationClaimMenu) {
            DonationClaimMenu menu = (DonationClaimMenu) e.getMenu();
            // TODO set claimed, give stuff...
            String name = ChatColor.stripColor(e.getButton().getName());
            IDonation perk = DonationManager.getDonation(name);
            if(perk != null){
                Client client = ClientUtilities.getOnlineClient(e.getPlayer());
                if(client != null){
                   Donation donation = client.getDonations().stream().filter(d -> d.getName().equalsIgnoreCase(perk.getName()) && !d.isClaimed()).findFirst().orElse(null);
                   if(donation != null){
                       IClaimable claimable = (IClaimable) perk;
                       if(claimable.canClaim(e.getPlayer())) {
                           donation.setClaimed(true);
                           DonationRepository.setClaimed(e.getPlayer().getUniqueId(), donation.getName());


                           claimable.claim(e.getPlayer());
                           e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                           menu.getButtons().clear();
                           menu.buildPage(e.getPlayer());
                           menu.construct();
                       }else{
                           UtilMessage.message(e.getPlayer(), claimable.getClaimFailedReason());
                       }
                   }
                }
            }
        }
    }
}
