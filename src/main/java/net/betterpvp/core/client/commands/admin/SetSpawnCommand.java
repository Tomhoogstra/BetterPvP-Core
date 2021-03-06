package net.betterpvp.core.client.commands.admin;

import net.betterpvp.core.Core;
import net.betterpvp.core.client.Rank;
import net.betterpvp.core.command.Command;
import net.betterpvp.core.utility.UtilLocation;
import net.betterpvp.core.utility.UtilMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends Command {

    public SetSpawnCommand() {
        super("setspawn", new String[]{}, Rank.OWNER);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args == null || args.length == 0) {
            player.getWorld().setSpawnLocation((int) player.getLocation().getX(), (int) player.getLocation().getY(), (int) player.getLocation().getZ());
            UtilMessage.message(player, "Spawn", "You set Spawn at "
                    + ChatColor.YELLOW + UtilLocation.locationToString(Bukkit.getWorld("world").getSpawnLocation()) + ChatColor.GRAY + ".");

            Core.getOptions().saveLocation(new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(),
                    player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch()));
        }
    }

    @Override
    public void help(Player player) {

    }
}
