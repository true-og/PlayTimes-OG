package me.codedred.playtimes.server;

import com.google.gson.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import me.codedred.playtimes.statistics.StatManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

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
    String new_name = Bukkit.getPlayer(uuid).getDisplayName();
    return new_name;
  }
}
