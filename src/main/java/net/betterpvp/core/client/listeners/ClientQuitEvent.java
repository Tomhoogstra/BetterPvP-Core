package net.betterpvp.core.client.listeners;

import net.betterpvp.core.client.Client;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClientQuitEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    private Client client;
    private boolean newClient;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ClientQuitEvent(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }


}
