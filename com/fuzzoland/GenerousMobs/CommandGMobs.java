package com.fuzzoland.GenerousMobs;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandGMobs implements CommandExecutor{

	private GenerousMobs plugin;
	
	public CommandGMobs(GenerousMobs plugin){
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(commandLabel.equalsIgnoreCase("GMobs")){
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("reload")){
					if(sender.hasPermission("GenerousMobs.Reload")){
						plugin.reloadConfig();
						sender.sendMessage(ChatColor.GREEN + "Configuration file successfully reloaded!");
					}else{
						sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
					}
				}else if(args[0].equalsIgnoreCase("reset")){
					if(sender.hasPermission("GenerousMobs.Reset")){
						for(String key : plugin.getConfig().getKeys(false)){
							plugin.getConfig().set(key, null);
						}
						plugin.getConfig().options().copyDefaults(true);
						plugin.saveConfig();
						plugin.reloadConfig();
						sender.sendMessage(ChatColor.GREEN + "Configuration file successfully reset!");
					}else{
						sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
					}
				}else{
					sender.sendMessage(ChatColor.RED + "Available commands: /GMobs reload, /GMobs reset");
				}
			}else{
				sender.sendMessage(ChatColor.RED + "Available commands: /GMobs reload, /GMobs reset");
			}
		}
		return false;
	}
}
