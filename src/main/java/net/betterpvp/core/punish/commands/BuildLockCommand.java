package net.betterpvp.core.punish.commands;

import net.betterpvp.core.client.Client;
import net.betterpvp.core.client.ClientUtilities;
import net.betterpvp.core.client.Rank;
import net.betterpvp.core.command.Command;
import net.betterpvp.core.database.Log;
import net.betterpvp.core.punish.Punish;
import net.betterpvp.core.punish.Punish.PunishType;
import net.betterpvp.core.punish.PunishManager;
import net.betterpvp.core.punish.PunishUtilities;
import net.betterpvp.core.punish.mysql.PunishRepository;
import net.betterpvp.core.utility.UtilMessage;
import net.betterpvp.core.utility.UtilTime;
import net.betterpvp.core.utility.UtilTime.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BuildLockCommand extends Command {

    public BuildLockCommand() {
        super("b-lock", new String[]{}, Rank.ADMIN);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args == null || args.length == 0) {
            help(player);
            return;
        }

        Client target = ClientUtilities.searchClient(player, args[0], false);
        if (target == null) {
            ClientUtilities.searchClient(player, args[0], true);
            return;
        }

        if (args.length >= 3) {

            if (target.getRank().toInt() >= ClientUtilities.getClient(player).getRank().toInt()) {
                UtilMessage.message(player, "Client", "You must outrank " + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " to Punish.");
                return;
            }


            long time = Integer.parseInt(args[1]);
            String unit = args[2].toLowerCase();
            String msg = "";
            for (String str : args) {
                if (str.equals(args[0]) || str.equals(args[1]) || str.equals(args[2])) {
                    continue;
                }

                msg += str + " ";
            }


            if (!target.getName().equals("Mykindos")) {


                Punish punish = new Punish(target.getUUID(), player.getUniqueId(), PunishType.BuildLock,
                        PunishUtilities.getProperLength(time, unit) + System.currentTimeMillis(), msg);
                PunishManager.addPunishment(punish);
                PunishRepository.savePunishment(punish);


                UtilMessage.broadcast("Punish", ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " Build Locked " + ChatColor.YELLOW
                        + target.getName() + ChatColor.GRAY + " for " + ChatColor.GREEN +
                        UtilTime.convert(PunishUtilities.getProperLength(time, unit), TimeUnit.BEST, 1)
                        + " " + UtilTime.getTimeUnit(unit) + (msg.equals("") ? "." : ChatColor.GRAY + " for "
                        + ChatColor.YELLOW + msg));
                Log.write("Punish", player.getName() + " Build Locked " +
                        target.getName() + " for " +
                        UtilTime.convert(PunishUtilities.getProperLength(time, unit), TimeUnit.BEST, 1)
                        + " " + UtilTime.getTimeUnit(unit) + " for "
                        + msg);

            }



        } else {
            UtilMessage.message(player, "Punish", "Correct Usage: /b-lock {name} {time} {unit} {reason}");
            return;
        }


    }

    @Override
    public void help(Player player) {
        UtilMessage.message(player, "Client", "Punish Commands List:");
        UtilMessage.message(player, "/ban <player> <time> <unit> <reason>", "Ban a player.", Rank.MODERATOR);
        UtilMessage.message(player, "/mute <player> <time> <unit> <reason>", "Mute a player.", Rank.MODERATOR);
        UtilMessage.message(player, "/unban <player>", "Unban a player.", Rank.MODERATOR);
        UtilMessage.message(player, "/unmute <player>", "Unmute a player.", Rank.MODERATOR);
    }
}
