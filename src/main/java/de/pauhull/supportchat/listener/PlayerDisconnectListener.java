package de.pauhull.supportchat.listener;

import de.pauhull.supportchat.SupportChat;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Iterator;
import java.util.Map;

public class PlayerDisconnectListener implements Listener {

    private SupportChat supportChat;

    private PlayerDisconnectListener(SupportChat supportChat) {
        this.supportChat = supportChat;

        ProxyServer.getInstance().getPluginManager().registerListener(supportChat, this);
    }

    public static void register() {
        new PlayerDisconnectListener(SupportChat.getInstance());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (supportChat.getPending().contains(player)) {
            supportChat.getPending().remove(player);
        }

        Iterator<Map.Entry<ProxiedPlayer, ProxiedPlayer>> iterator = supportChat.getChatting().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ProxiedPlayer, ProxiedPlayer> entry = iterator.next();
            ProxiedPlayer supporter = entry.getKey();
            ProxiedPlayer supported = entry.getKey();

            if (player == supporter || player == supported) {
                if (player == supporter) {
                    supported.sendMessage(TextComponent.fromLegacyText(SupportChat.CONVERSATION_CLOSED));
                } else {
                    supporter.sendMessage(TextComponent.fromLegacyText(SupportChat.CONVERSATION_CLOSED));
                }

                iterator.remove();
            }
        }
    }

}
