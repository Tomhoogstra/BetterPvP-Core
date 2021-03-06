package net.betterpvp.core.client.commands.admin;

import net.betterpvp.core.client.Client;
import net.betterpvp.core.client.ClientUtilities;
import net.betterpvp.core.client.Rank;
import net.betterpvp.core.client.commands.admin.events.ClientSearchEvent;
import net.betterpvp.core.client.mysql.ClientRepository;
import net.betterpvp.core.command.Command;
import net.betterpvp.core.database.Log;
import net.betterpvp.core.networking.NetworkReceiver;
import net.betterpvp.core.networking.events.NetworkMessageEvent;
import net.betterpvp.core.punish.Punish;
import net.betterpvp.core.punish.PunishManager;
import net.betterpvp.core.utility.UtilFormat;
import net.betterpvp.core.utility.UtilMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


public class ClientCommand extends Command implements Listener {

    public ClientCommand() {
        super("client", new String[]{}, Rank.MODERATOR);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args == null || args.length == 0) {
            help(player);
            return;
        }

        if (args[0].equalsIgnoreCase("search")) {
            searchCommand(player, args);
        } else if (args[0].equalsIgnoreCase("promote")) {
            if (ClientUtilities.getClient(player).hasRank(Rank.ADMIN, true)) {
                promoteCommand(player, args);
            }
        } else if (args[0].equalsIgnoreCase("demote")) {
            if (ClientUtilities.getClient(player).hasRank(Rank.ADMIN, true)) {
                demoteCommand(player, args);
            }
        } else if (args[0].equalsIgnoreCase("admin")) {
            if (ClientUtilities.getClient(player).hasRank(Rank.ADMIN, true)) {
                if (ClientUtilities.getClient(player).isAdministrating()) {
                    ClientUtilities.getClient(player).setAdministrating(false);
                    UtilMessage.message(player, "Client", "Admin Mode: " + ChatColor.RED + "Disabled");
                } else {
                    ClientUtilities.getClient(player).setAdministrating(true);
                    UtilMessage.message(player, "Client", "Admin Mode: " + ChatColor.GREEN + "Enabled");
                }
            }
        } else if (args[0].equalsIgnoreCase("allowvpn")) {
            NetworkReceiver.sendGlobalNetworkMessage("Client", "AllowVPN-!-" + args[1]);
            UtilMessage.message(player, "Client", "Updated " + ChatColor.GREEN + args[1]);
        } else if (args[0].equalsIgnoreCase("blockvpn")) {
            NetworkReceiver.sendGlobalNetworkMessage("Client", "BlockVPN-!-" + args[1]);
            UtilMessage.message(player, "Client", "Updated " + ChatColor.GREEN + args[1]);
        } else {
            help(player);
        }
    }


    @Override
    public void help(Player player) {
        UtilMessage.message(player, "Client", "Client Commands List:");
        UtilMessage.message(player, "/client search <player>", "Search client details", Rank.MODERATOR);
        UtilMessage.message(player, "/client promote <player>", "Promote a player", Rank.ADMIN);
        UtilMessage.message(player, "/client demote <player>", "Demote a player", Rank.ADMIN);
    }

    @EventHandler
    public void onNetworkMessage(NetworkMessageEvent e) {
        if (e.getChannel().equals("Client")) {
            if (e.getMessage().startsWith("AllowVPN")) {
                String[] data = e.getMessage().split("-!-");
                updateVPNStatus(data[1], true);
            } else if (e.getMessage().startsWith("BlockVPN")) {

                String[] data = e.getMessage().split("-!-");
                updateVPNStatus(data[1], false);
            }
        }
    }

    private void updateVPNStatus(String client, boolean status) {
        Client target = ClientUtilities.getClient(client);
        if (target == null) {
            return;
        }

        target.setAllowVPN(status);
        ClientRepository.updateAllowVPN(target);
    }

    public void searchCommand(Player player, String[] args) {
        if (!ClientUtilities.isClient(player.getName())) {
            UtilMessage.message(player, "Client", ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " is not a registerd Client.");
            return;
        }

        if (args.length == 1) {
            UtilMessage.message(player, "Client", "You did not input a Client to search.");
            return;
        }

        Client target = ClientUtilities.searchClient(player, args[1], false);
        if (target == null) {
            ClientUtilities.searchClient(player, args[1], true);
            return;
        }

        String punishments = "";
        for (Punish punish : PunishManager.getPunishments(target.getUUID())) {
            punishments += punish.getPunishType().name() + " (" + punish.getRemaining() + "," + ClientUtilities.getClient(punish.getPunisher()).getName() + ") " + ChatColor.WHITE + ", " + ChatColor.GRAY;
        }


        Client client = ClientUtilities.getOnlineClient(player);
        ClientSearchEvent event = new ClientSearchEvent(player);
        event.getResult().add(ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " Client Details:");
        event.getResult().add(ChatColor.YELLOW + "IP Address: "
                + (client.hasRank(Rank.ADMIN, false) ? ChatColor.GRAY + target.getIP() : ChatColor.RED + "N/A"));
        event.getResult().add(ChatColor.YELLOW + "Previous Name: " + ChatColor.GRAY + target.getOldName());
        event.getResult().add(ChatColor.YELLOW + "IP Alias: " + ChatColor.GRAY + (client.hasRank(Rank.ADMIN, false)
                ? ClientUtilities.getDetailedIPAlias(target, false) : ClientUtilities.getDetailedIPAlias(target, true)));
        event.getResult().add(ChatColor.YELLOW + "Rank: " + ChatColor.GRAY + UtilFormat.cleanString(target.getRank().toString()));
        event.getResult().add(ChatColor.YELLOW + "Discord Linked: " + ChatColor.GRAY + target.isDiscordLinked());
        event.getResult().add(ChatColor.YELLOW + "Punishments: " + ChatColor.GRAY + punishments);
        event.setTarget(target);

        Bukkit.getPluginManager().callEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClientSearch(ClientSearchEvent e) {
        for (String msg : e.getResult()) {
            UtilMessage.message(e.getPlayer(), msg);
        }
    }

    public void promoteCommand(Player player, String[] args) {
        if (args.length == 1) {
            UtilMessage.message(player, "Client", "You did not input a Client to promote.");
            return;
        }

        Client target = ClientUtilities.getClient(args[1]);
        if (target == null) {
            ClientUtilities.searchClient(player, args[1], true);
            return;
        }

        if (player == target) {
            UtilMessage.message(player, "Client", "You cannot promote yourself.");
            return;
        }

        if (target.getRank().equals(Rank.DEVELOPER)) {
            UtilMessage.message(player, "Client", ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " cannot be promoted any further.");
            return;
        }


        target.setRank(Rank.getRank(target.getRank().toInt() + 1));
        ClientUtilities.messageStaff("Client", ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " promoted " + ChatColor.YELLOW + target.getName()
                + ChatColor.GRAY + " to " + target.getRank().getTag(false) + ChatColor.GRAY + ".", player);
        UtilMessage.message(player, "Client", "You promoted " + ChatColor.YELLOW + target.getName()
                + ChatColor.GRAY + " to " + target.getRank().getTag(false) + ChatColor.GRAY + ".");
        ClientRepository.updateRank(target);
        Log.write("Client", player.getName() + " promoted " + target.getName() + " to " + UtilFormat.cleanString(target.getRank().toString()));
    }

    public void demoteCommand(Player player, String[] args) {
        if (args.length == 1) {
            UtilMessage.message(player, "Client", "You did not input a Client to demote.");
            return;
        }

        Client target = ClientUtilities.getClient(args[1]);
        if (target == null) {
            ClientUtilities.searchClient(player, args[1], true);
            return;
        }

        Client c = ClientUtilities.getClient(player);

        if (player == target) {
            UtilMessage.message(player, "Client", "You cannot demote yourself.");
            return;
        }

        if (target.getRank().equals(Rank.PLAYER)) {
            UtilMessage.message(player, "Client", ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " cannot be demoted any further.");
            return;
        }

        if (c.getRank().toInt() < target.getRank().toInt()) {
            return;
        }

        target.setRank(Rank.getRank(target.getRank().toInt() - 1));
        ClientUtilities.messageStaff("Client", ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " demoted " + ChatColor.YELLOW + target.getName()
                + ChatColor.GRAY + " to " + target.getRank().getTag(false) + ChatColor.GRAY + ".", player);
        UtilMessage.message(player, "Client", "You demoted " + ChatColor.YELLOW + target.getName()
                + ChatColor.GRAY + " to " + target.getRank().getTag(false) + ChatColor.GRAY + ".");
        ClientRepository.updateRank(target);
        Log.write("Client", player.getName() + " demoted " + target.getName() + " to " + UtilFormat.cleanString(target.getRank().toString()));
    }
}
