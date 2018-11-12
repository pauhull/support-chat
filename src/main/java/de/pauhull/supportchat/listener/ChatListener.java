package de.pauhull.supportchat.listener;

import de.pauhull.supportchat.SupportChat;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {

    private SupportChat supportChat;

    public ChatListener(SupportChat supportChat) {
        this.supportChat = supportChat;

        ProxyServer.getInstance().getPluginManager().registerListener(supportChat, this);
    }

    public static void register() {
        new ChatListener(SupportChat.getInstance());
    }

    @EventHandler
    public void onChat(ChatEvent event) {

        if (event.isCommand() || !(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        BaseComponent[] message = TextComponent.fromLegacyText(SupportChat.PREFIX + player.getName() + ": " + event.getMessage());

        for (ProxiedPlayer supporter : SupportChat.getInstance().getChatting().keySet()) {
            ProxiedPlayer supported = SupportChat.getInstance().getChatting().get(supporter);

            if (player == supporter || player == supported) {
                supporter.sendMessage(message);
                supported.sendMessage(message);
                event.setCancelled(true);
                return;
            }
        }
    }

}
