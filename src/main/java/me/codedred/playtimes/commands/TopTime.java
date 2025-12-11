package me.codedred.playtimes.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.models.Leaderboard;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.time.TimeManager;
import me.codedred.playtimes.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class TopTime implements CommandExecutor {

  @Override
  public boolean onCommand(
    CommandSender sender,
    @NotNull Command cmd,
    @NotNull String label,
    String[] args
  ) {
    if (!sender.hasPermission("pt.top")) {
      ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
      return true;
    }

    DataManager data = DataManager.getInstance();

    if (
      data.getConfig().getBoolean("top-playtime.enable-cooldown") &&
      !sender.hasPermission("pt.block-cooldown") &&
      sender instanceof Player
    ) {
      Player player = (Player) sender;
      if (CoolDownUtil.contains(player.getUniqueId())) {
        String cooldownMessage = Objects
          .requireNonNull(data.getConfig().getString("messages.cooldown"))
          .replace(
            "%timeleft%",
            Integer.toString(CoolDownUtil.left(player.getUniqueId()))
          );
        sender.sendMessage(ChatUtil.formatWithPrefix(cooldownMessage));
        return true;
      }
      CoolDownUtil.add(
        player.getUniqueId(),
        System.currentTimeMillis() +
        (data.getConfig().getInt("top-playtime.cooldown-seconds") * 1000L)
      );
    }

    generateAndSendLeaderboard(sender);
    return true;
  }

  private void generateAndSendLeaderboard(CommandSender sender) {
    CompletableFuture.supplyAsync(() -> {
      Leaderboard board = new Leaderboard();
      Map<String, Integer> map = board.getTopTen();
      
      if (map.isEmpty()) {
        return null;
      }

      return generateLeaderboardMessages(map, sender);
    }).thenAccept(messages -> {
      Bukkit.getScheduler().runTask(getPlugin(), () -> {
        if (messages == null) {
          sender.sendMessage(
            ChatUtil.format("&cRejoin the server to fill the leaderboard!")
          );
          return;
        }

        for (String message : messages) {
          sender.sendMessage(message);
        }
      });
    }).exceptionally(throwable -> {
      throwable.printStackTrace();
      Bukkit.getScheduler().runTask(getPlugin(), () -> {
        sender.sendMessage(ChatUtil.format("&cError generating leaderboard!"));
      });
      return null;
    });
  }

  private List<String> generateLeaderboardMessages(Map<String, Integer> map, CommandSender sender) {
    List<String> messages = new ArrayList<>();
    DataManager data = DataManager.getInstance();
    StatManager statManager = StatManager.getInstance();
    TimeManager timeManager = TimeManager.getInstance();

    String header = ChatUtil.format(
      data.getConfig().getString("top-playtime.header")
    );
    String footer = ChatUtil.format(
      data.getConfig().getString("top-playtime.footer")
    );
    String contentTemplate = data.getConfig().getString("top-playtime.content");

    if (ServerUtils.hasPAPI() && sender instanceof Player) {
      header = PAPIHolders.getHolders((Player) sender, header);
      footer = PAPIHolders.getHolders((Player) sender, footer);
    }

    messages.add(header);

    int place = 1;
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      UUID uuid = UUID.fromString(entry.getKey());
      org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

      String content = contentTemplate;
      if (ServerUtils.hasPAPI()) {
        content = PAPIHolders.getHolders(offlinePlayer, content);
      }

      String defaultPlayerName = "Unknown";
      String defaultJoinDate = "N/A";
      String defaultTime = "0h 0m 0s";

      String offlinePlayerName = offlinePlayer.getName() != null
        ? offlinePlayer.getName()
        : defaultPlayerName;
      String time = timeManager.buildFormat(entry.getValue() / 20);
      String joinDate = statManager.getJoinDate(uuid) != null
        ? statManager.getJoinDate(uuid)
        : defaultJoinDate;

      if (time == null) {
        time = defaultTime;
      }

      String formattedContent = content
        .replace("%player%", offlinePlayerName)
        .replace("%place%", String.valueOf(place))
        .replace("%time%", time)
        .replace("%joindate%", joinDate);

      messages.add(ChatUtil.format(formattedContent));
      place++;
    }

    messages.add(footer);
    return messages;
  }

  private Plugin getPlugin() {
    return Bukkit.getPluginManager().getPlugin("PlayTimes");
  }
}
