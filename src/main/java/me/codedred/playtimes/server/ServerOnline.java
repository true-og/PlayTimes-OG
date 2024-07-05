package me.codedred.playtimes.server;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import me.codedred.playtimes.statistics.StatManager;

public class ServerOnline implements ServerStatus {

	@Override
	public UUID getUUID(@NotNull String name) {
		UUID uuid = Bukkit.getServer().getPlayer(name).getUniqueId();
		if (!StatManager.getInstance().hasJoinedBefore(uuid)) {
			return null;
		}
		return uuid;
	}

	@Override
	public boolean isOnline() {
		return true;
	}

	@Override
	public String getName(UUID uuid) throws IOException {
		String new_name = Bukkit.getPlayer(uuid).getName();
		return new_name;
	}
}
