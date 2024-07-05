package me.codedred.playtimes.utils;

import org.bukkit.command.CommandSender;

import me.codedred.playtimes.data.DataManager;
import net.kyori.adventure.text.TextComponent;

public class ChatUtil {

	private static final DataManager DATA_MANAGER = DataManager.getInstance();

	public enum ChatTypes {
		NO_PERMISSION,
		PLAYER_NOT_FOUND,
		PLAYER_NEVER_PLAYED,
	}

	public static void errno(CommandSender sender, ChatTypes type) {
		switch (type) {
		case NO_PERMISSION:
			sender.sendMessage(
					format(DATA_MANAGER.getConfig().getString("messages.noPermission"))
					);
			break;
		case PLAYER_NOT_FOUND:
			sender.sendMessage(
					formatWithPrefix(
							DATA_MANAGER.getConfig().getString("messages.player-not-found")
							)
					);
			break;
		case PLAYER_NEVER_PLAYED:
			sender.sendMessage(
					formatWithPrefix(
							DATA_MANAGER.getConfig().getString("messages.player-never-joined")
							)
					);
			break;
		}
	}

	public static TextComponent format(String msg) {
		return OGUtils.legacySerializerAnyCase(msg);
	}

	public static TextComponent formatWithPrefix(String msg) {
		return OGUtils.legacySerializerAnyCase(DATA_MANAGER.getConfig().getString("prefix") + msg);
	}

}