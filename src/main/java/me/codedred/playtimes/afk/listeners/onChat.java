package me.codedred.playtimes.afk.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.codedred.playtimes.afk.AFKManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class onChat implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {

        AFKManager.getInstance().updateActivity(event.getPlayer());

    }

}
