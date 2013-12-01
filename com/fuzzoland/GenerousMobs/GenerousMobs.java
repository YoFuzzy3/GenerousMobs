package com.fuzzoland.GenerousMobs;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class GenerousMobs extends JavaPlugin{

	private Logger logger = Bukkit.getLogger();
	private Economy econ = null;
	public List<UUID> isFromSpawner = new ArrayList<UUID>();
	
	public void onEnable(){
		startMetrics();
		getConfig().options().copyDefaults(true);
		saveConfig();
		logger.log(Level.INFO, "[GenerousMobs] Configuration file loaded!");
		getCommand("GMobs").setExecutor(new CommandGMobs(this));
		logger.log(Level.INFO, "[GenerousMobs] Commands registered!");
		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		logger.log(Level.INFO, "[GenerousMobs] Events registered!");
		setupEconomy();
	}

	public void startMetrics(){
		try{
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
			logger.log(Level.INFO, "[GenerousMobs] Metrics initiated!");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
    private void setupEconomy(){
    	logger.log(Level.INFO, "[GenerousMobs] Enabling economy support...");
    	if(getServer().getPluginManager().getPlugin("Vault") == null){
    		logger.log(Level.SEVERE, "[GenerousMobs] Failed to enable economy support - Vault not found!");
    		getPluginLoader().disablePlugin(this);
    		return;
    	}
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null){
        	logger.log(Level.SEVERE, "[GenerousMobs] Failed to enable economy support - Economy plugin not found!");
        	getPluginLoader().disablePlugin(this);
            return;
        }
        econ = rsp.getProvider();
        logger.log(Level.INFO, "[GenerousMobs] Succesfully enabled economy support!");
    }
    
	public boolean isNaturalEntity(Entity entity){
		if(isFromSpawner.contains(entity.getUniqueId())){
			return false;
		}else{
			return true;
		}
	}
	
	public Boolean giveReward(Player player, String mobName){
		if(player.hasPermission("GenerousMobs.Mob." + mobName) || player.hasPermission("GenerousMobs.Mob.*")){
			Double reward = getReward(getConfig().getString("Rewards." + mobName).split("#"));
			NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US); 
			String name = player.getName();
			if(reward > 0){
				econ.depositPlayer(name, reward);
				player.sendMessage(getMessage(KillType.AWARD, nf.format(reward), mobName));
			}else if(reward < 0){
				reward = reward - (reward * 2);
				if(econ.getBalance(name) < reward){
					if(getConfig().getBoolean("PreventKillIfBroke.Enabled")){
						player.sendMessage(getMessage(KillType.BROKE, nf.format(reward), mobName));
						return false;
					}
					econ.withdrawPlayer(name, econ.getBalance(name));
				}else{
					econ.withdrawPlayer(name, reward);
				}
				player.sendMessage(getMessage(KillType.FINE, nf.format(reward), mobName));
			}else{
				player.sendMessage(getMessage(KillType.NONE, nf.format(reward), mobName));
			}
		}
		return true;
	}
	
	private Double getReward(String[] data){
		Random random = new Random();
		Double minReward = Double.valueOf(data[0]);
		Double maxReward = Double.valueOf(data[1]);
		String rewardType = String.valueOf(data[2]);
		if(rewardType.equals("gain")){
			if(minReward.equals(maxReward)){
				return maxReward;
			}
			Integer temp1 = random.nextInt((int) ((maxReward - minReward) * 100));
			Double temp2 = ((double) temp1) / 100;
			Double reward = temp2 + minReward;
			return reward;
		}else if(rewardType.equals("loss")){
			if(minReward.equals(maxReward)){
				return maxReward - (maxReward * 2);
			}
			Integer temp1 = random.nextInt((int) ((maxReward - minReward) * 100));
			Double temp2 = ((double) temp1) / 100;
			Double reward = temp2 + minReward;
			reward = reward - (reward * 2);
			return reward;
		}else{
			return null;
		}
	}
	
	public String getMessage(KillType type, String amount, String mobName){
		String name = getConfig().getString("Messages.Names." + mobName);
		switch(type){
		case AWARD:
			return getConfig().getString("Messages.Awarded").replace("%amount%", amount).replace("%name%", name).replaceAll("(&([a-f0-9l-or]))", "\u00A7$2");
		case FINE:
			return getConfig().getString("Messages.Fined").replace("%amount%", amount).replace("%name%", name).replaceAll("(&([a-f0-9l-or]))", "\u00A7$2");
		case NONE:
			return getConfig().getString("Messages.None").replace("%name%", name).replaceAll("(&([a-f0-9l-or]))", "\u00A7$2");
		case BROKE:
			return getConfig().getString("Messages.Broke").replace("%name%", name).replaceAll("(&([a-f0-9l-or]))", "\u00A7$2");
		default:
			return "";
		}
	}
	
	private enum KillType{
		AWARD, FINE, NONE, BROKE;
	}
}
