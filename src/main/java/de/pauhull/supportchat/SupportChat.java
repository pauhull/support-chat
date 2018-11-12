package de.pauhull.supportchat;

import de.pauhull.supportchat.command.SupportCommand;
import de.pauhull.supportchat.listener.ChatListener;
import de.pauhull.supportchat.listener.PlayerDisconnectListener;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupportChat extends Plugin {

    public static final String PREFIX = "§8▌ §6Support §8» §f";
    public static final String SUPPORT_HOVER = "§7Hier klicken, um §f%s§7 zu supporten";
    public static final String REQUEST = PREFIX + "Der Spieler §n%s§r§f hat eine Support-Anfrage gesendet. ";
    public static final String CLICK_HERE = "§f§lKlicke hier um sie anzunehmen";
    public static final String ALREADY_PENDING = PREFIX + "§cDu hast bereits eine Support-Anfrage gesendet.";
    public static final String NO_PERMISSION = PREFIX + "§cDafür hast du keine Rechte";
    public static final String NOT_ONLINE = PREFIX + "§cDieser Spieler ist nicht online";
    public static final String ALREADY_SUPPORTED = PREFIX + "§cDieser Spieler wird bereits supportet";
    public static final String SUPPORT_ANNOUNCE = PREFIX + "%s wird nun von %s supportet";
    public static final String REQUEST_SENT = PREFIX + "§aDu hast erfolgreich eine Support-Anfrage gestellt und ein Supporter wird sich nun um dich kümmern";
    public static final String SELF_SUPPORT = PREFIX + "§cDu kannst dich nicht selbst supporten";
    public static final String CONVERSATION_CLOSED = PREFIX + "Der Chat wurde beendet";
    public static final String NOT_CHATTING = PREFIX + "§cDu bist gerade nicht im Chat";
    public static final String CHAT_QUIT = "§f§lKlicke hier um den Chat zu beenden oder nutze /support quit";

    public static final String SUPPORTER_PERMISSION = "support.supporter";
    public static final String REQUEST_PERMISSION = "support.request";

    @Getter
    private static SupportChat instance;

    @Getter
    private List<ProxiedPlayer> pending;

    @Getter
    private Map<ProxiedPlayer, ProxiedPlayer> chatting;

    @Override
    public void onEnable() {
        instance = this;
        this.pending = new ArrayList<>();
        this.chatting = new HashMap<>();

        // Register Commands
        SupportCommand.register();

        // Register Listeners
        PlayerDisconnectListener.register();
        ChatListener.register();
    }

    public boolean isChatting(ProxiedPlayer player) {
        for (ProxiedPlayer supporter : chatting.keySet()) {
            ProxiedPlayer supported = chatting.get(supporter);

            if (player == supporter || player == supported) {
                return true;
            }
        }

        return false;
    }

}
