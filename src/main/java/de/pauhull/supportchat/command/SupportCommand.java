package de.pauhull.supportchat.command;

import de.pauhull.supportchat.SupportChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Iterator;
import java.util.Map;

public class SupportCommand extends Command {

    public SupportCommand() {
        super("support");
    }

    public static void register() {
        Plugin plugin = SupportChat.getInstance();
        plugin.getProxy().getPluginManager().registerCommand(plugin, new SupportCommand());
    }

    @Override
    public void execute(CommandSender commandSender, String[] arguments) {

        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;

            if (arguments.length > 0 && arguments[0].equalsIgnoreCase("quit")) {
                Iterator<Map.Entry<ProxiedPlayer, ProxiedPlayer>> iterator = SupportChat.getInstance().getChatting().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<ProxiedPlayer, ProxiedPlayer> entry = iterator.next();
                    ProxiedPlayer supporter = entry.getKey();
                    ProxiedPlayer supported = entry.getKey();

                    if (player == supporter || player == supported) {
                        iterator.remove();
                        supporter.sendMessage(TextComponent.fromLegacyText(SupportChat.CONVERSATION_CLOSED));
                        supported.sendMessage(TextComponent.fromLegacyText(SupportChat.CONVERSATION_CLOSED));
                        return;
                    }
                }

                player.sendMessage(TextComponent.fromLegacyText(SupportChat.NOT_CHATTING));
                return;
            }

            if (player.hasPermission(SupportChat.SUPPORTER_PERMISSION) && arguments.length > 0) {
                ProxiedPlayer playerToSupport = ProxyServer.getInstance().getPlayer(arguments[0]);

                if (playerToSupport == null) {
                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.NOT_ONLINE));
                    return;
                } else if (!SupportChat.getInstance().getPending().contains(playerToSupport)) {
                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.ALREADY_SUPPORTED));
                    return;
                } else if (playerToSupport == player) {
                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.SELF_SUPPORT));
                    return;
                } else {
                    SupportChat.getInstance().getPending().remove(playerToSupport);
                    SupportChat.getInstance().getChatting().put(player, playerToSupport);

                    for (ProxiedPlayer supporter : ProxyServer.getInstance().getPlayers()) {
                        if (!supporter.hasPermission(SupportChat.SUPPORTER_PERMISSION) && supporter != playerToSupport)
                            continue;

                        supporter.sendMessage(TextComponent.fromLegacyText(String.format(SupportChat.SUPPORT_ANNOUNCE, playerToSupport.getName(), player.getName())));
                    }

                    ClickEvent onClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support quit");
                    BaseComponent[] message = new ComponentBuilder(SupportChat.PREFIX).append(SupportChat.CHAT_QUIT).event(onClick).create();

                    player.sendMessage(message);
                    playerToSupport.sendMessage(message);

                    return;
                }
            }

            if (!player.hasPermission(SupportChat.REQUEST_PERMISSION)) {
                player.sendMessage(TextComponent.fromLegacyText(SupportChat.NO_PERMISSION));
                return;
            }

            if (SupportChat.getInstance().getPending().contains(player)) {
                player.sendMessage(TextComponent.fromLegacyText(SupportChat.ALREADY_PENDING));
                return;
            }

            SupportChat.getInstance().getPending().add(player);
            HoverEvent onHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(String.format(SupportChat.SUPPORT_HOVER, player.getName())));
            ClickEvent onClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support " + player.getName());

            BaseComponent[] message = new ComponentBuilder(String.format(SupportChat.REQUEST, player.getName()))
                    .append(SupportChat.CLICK_HERE).event(onHover).event(onClick).create();

            int supporters = 0;

            for (ProxiedPlayer supporter : ProxyServer.getInstance().getPlayers()) {

                if (player == supporter || !supporter.hasPermission(SupportChat.SUPPORTER_PERMISSION))
                    continue;

                supporter.sendMessage(message);
                supporters++;
            }

            if (supporters > 0) {
                player.sendMessage(TextComponent.fromLegacyText(SupportChat.REQUEST_SENT));
            } else {
                player.sendMessage(TextComponent.fromLegacyText(SupportChat.NO_SUPPORTER));
            }

        }
    }

}
