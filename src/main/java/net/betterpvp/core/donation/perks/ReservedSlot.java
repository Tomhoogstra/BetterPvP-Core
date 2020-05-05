package net.betterpvp.core.donation.perks;


import net.betterpvp.core.Core;
import net.betterpvp.core.client.Client;
import net.betterpvp.core.client.ClientUtilities;
import net.betterpvp.core.client.Rank;
import net.betterpvp.core.donation.IPerk;
import net.betterpvp.core.donation.Perk;
import net.betterpvp.core.framework.BPVPListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

public class ReservedSlot extends BPVPListener<Core> implements IPerk {

    public ReservedSlot(Core instance) {
        super(instance);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent e){

        if(Bukkit.getServer().hasWhitelist()){
            return;
        }

        if(e.getPlayer() == null) return;

        Client client = ClientUtilities.getClient(e.getPlayer());
        if(client == null){
            e.setResult(PlayerLoginEvent.Result.ALLOWED);
            return;
        }

        int count = 0;
        for(Player p : Bukkit.getOnlinePlayers()){
            Client pClient = ClientUtilities.getOnlineClient(p);
            if(pClient != null){
                if(pClient.hasDonation(getPerk().getName()) || client.hasRank(Rank.TRIAL_MOD, false)){
                    count++;
                }
            }
        }

        if(Bukkit.getOnlinePlayers().size() < Bukkit.getServer().getMaxPlayers() + count){
            e.setResult(PlayerLoginEvent.Result.ALLOWED);
            return;
        }

        if(client.hasDonation(getPerk().getName()) || client.hasRank(Rank.TRIAL_MOD, false)){
            e.setResult(PlayerLoginEvent.Result.ALLOWED);
            return;
        }

        if (e.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            e.setKickMessage(ChatColor.RED + "Server - " + ChatColor.YELLOW + " Visit https://store.betterpvp.net to purchase a reserved slot\n" + ChatColor.WHITE
                    + "There are currently " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size()
                    + ChatColor.YELLOW + " / " + ChatColor.GREEN + Bukkit.getServer().getMaxPlayers() + ChatColor.WHITE + "players online");
        }

    }

    @Override
    public Perk getPerk() {return Perk.RESERVEDSLOT;}
}