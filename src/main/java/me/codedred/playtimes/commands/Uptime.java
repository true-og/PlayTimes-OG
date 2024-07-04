package me.codedred.playtimes.commands;

import java.util.List;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.utils.ChatUtil;
import me.codedred.playtimes.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Uptime implements CommandExecutor {

  @Override
  public boolean onCommand(
    @NotNull CommandSender sender,
    @NotNull Command cmd,
    String cmdL,
    String[] args
  ) {
    // command must be uptime,serveruptime,or serverupt
    if (
      !cmdL.equalsIgnoreCase("uptime") &&
      !cmdL.equalsIgnoreCase("serveruptime") &&
      !cmdL.equalsIgnoreCase("serverupt")
    ) {
      return false;
    }

    // check for permission
    if (!sender.hasPermission("pt.uptime")) {
      ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
      return true;
    }

    // get the uptime
    String uptime = StatManager.getInstance().getUptime();
    List<String> messages = DataManager
      .getInstance()
      .getConfig()
      .getStringList("uptime.message");

    // iterate through the messages and replace the placeholders
    for (String message : messages) {
      //1: replace them using placeholder api
      // TODO: use MMAPI
      /*
      if (ServerUtils.hasPAPI() && sender instanceof Player) {
        Player player = (Player) sender;
        message = PlaceholderAPI.setPlaceholders(player, message);
      }
      */
      // 2. replace them using java string methods
      if (message.contains("{\"text\":") && sender instanceof Player) {
        String consoleCommand =
          "tellraw " +
          sender.getName() +
          " " +
          message.replace("%serveruptime%", uptime);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand);
      //3. if all else fails dont send name
      } else if(sender instanceof Player) {
        sender.sendMessage(
          ChatUtil.format(message.replace("%serveruptime%", uptime))
        );
      }
    }

    return true;
  }
}
