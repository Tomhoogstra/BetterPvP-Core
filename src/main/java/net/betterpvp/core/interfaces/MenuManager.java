package net.betterpvp.core.interfaces;

import net.betterpvp.core.Core;
import net.betterpvp.core.framework.BPVPListener;
import net.betterpvp.core.framework.UpdateEvent;
import net.betterpvp.core.framework.UpdateEvent.UpdateType;
import net.betterpvp.core.interfaces.events.ButtonClickEvent;
import net.betterpvp.core.interfaces.events.MenuCloseEvent;
import net.betterpvp.core.utility.UtilTime;
import net.betterpvp.core.utility.recharge.RechargeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ListIterator;


public class MenuManager extends BPVPListener<Core> {


    public MenuManager(Core i) {
        super(i);

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType() == Material.ENCHANTING_TABLE) {
                Player p = e.getPlayer();
                //p.openInventory(new ClassSelectionPage(p).getInventory());
                e.setCancelled(true);
            } else if (e.getClickedBlock().getType() == Material.ENDER_CHEST) {

                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (e.getType() == UpdateType.SLOWEST) {
            ListIterator<Menu> it = Menu.menus.listIterator();
            while (it.hasNext()) {
                Menu next = it.next();
                if (UtilTime.elapsed(next.getOpenTime(), 120000)) {
                    Player p = next.getPlayer();

                    it.remove();
                    p.closeInventory();
                    System.out.println("Closed " + p.getName() + "'s inventory");
                }
            }
        }
    }


    @EventHandler
    public void onButtonClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getWhoClicked() instanceof Player) {
            Menu menu = Menu.getMenu(event.getInventory(), event.getView().getTitle(), player);
            if (menu != null) {

                event.setCancelled(true);
                Button button = menu.getButton(event.getCurrentItem());
                if (button != null) {

                    if (RechargeManager.getInstance().add(player, "Button Click", 0.05, false)) {
                        Bukkit.getPluginManager().callEvent(new ButtonClickEvent(player, menu, button, event.getClick(), event.getSlot()));
                    }

                }
            }
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {

            if (Menu.isMenu(event.getView().getTitle())) {
                Player player = (Player) event.getPlayer();
                Menu menu = Menu.getMenu(event.getView().getTopInventory(), event.getView().getTitle(), player);
                Bukkit.getPluginManager().callEvent(new MenuCloseEvent(player, menu));
            }
        }
    }

    @EventHandler
    public void onMenuClose(MenuCloseEvent event) {
        if (event.getMenu() != null) {
            if (!event.getMenu().keepMenu()) {
                Menu.menus.remove(event.getMenu());
            }
        }
    }


}
