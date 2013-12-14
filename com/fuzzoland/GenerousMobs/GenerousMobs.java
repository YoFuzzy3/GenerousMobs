package com.fuzzoland.GenerousMobs;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class GenerousMobs extends JavaPlugin{

	private Logger logger = Bukkit.getLogger();
	private Economy econ = null;
	public Set<UUID> isFromSpawner = new HashSet<UUID>();
	
	public void onEnable(){
		getConfig().options().copyDefaults(true);
        saveConfig();
		logger.log(Level.INFO, "[GenerousMobs] Configuration file loaded!");
		getCommand("GMobs").setExecutor(new CommandGMobs(this));
		logger.log(Level.INFO, "[GenerousMobs] Commands registered!");
		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		logger.log(Level.INFO, "[GenerousMobs] Events registered!");
		setupEconomy();
		startMetrics();
	}

	private void startMetrics(){
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
	
	public Boolean giveReward(Player player, Location location, String mobName){
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
			for(ItemStack drop : getDrops(player, mobName)){
			    location.getWorld().dropItemNaturally(location, drop);
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
	
	private String getMessage(KillType type, String amount, String mobName){
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
	
    private Set<ItemStack> getDrops(Player player, String mobName){
        Set<ItemStack> drops = new HashSet<ItemStack>();
        for(String drop : getConfig().getStringList("CustomDrops." + mobName)){
            String[] data = drop.split("=");
            if((new Random().nextInt(100) + 1) < Integer.parseInt(data[1])){
                drops.add(stringToItemStack(data[0]));
                player.sendMessage(getConfig().getString("Messages.Drop").replaceAll("(&([a-f0-9l-or]))", "\u00A7$2"));
            }
        }
        return drops;
    }
	
	@SuppressWarnings("deprecation")
	private ItemStack stringToItemStack(String string){
		String[] data = string.split(";");
		Integer id = Integer.parseInt(data[0]);
		ItemStack item = new ItemStack(Material.getMaterial(id), Integer.parseInt(data[2]), (short) Integer.parseInt(data[1]));
		for(String e : data[3].split(",")){
			if(!e.equals("null")){
				String[] eData = e.split(":");
				item.addUnsafeEnchantment(Enchantment.getByName(eData[0]), Integer.parseInt(eData[1]));
			}
		}
		ItemMeta meta = item.getItemMeta();
		if(!data[4].equals("null")){
			meta.setDisplayName(data[4].replace("_", " ").replaceAll("(&([a-f0-9l-or]))", "\u00A7$2"));
		}
		List<String> lore = new ArrayList<String>();
		for(String l : data[5].split(",")){
			if(!l.equals("null")){
				lore.add(l.replace("_", " ").replaceAll("(&([a-f0-9l-or]))", "\u00A7$2"));
			}
		}
		meta.setLore(lore);
		if(!data[6].equals("null")){
			if(id >= 298 && id <= 301){
				String[] rgb = data[6].split(",");
				((LeatherArmorMeta) meta).setColor(Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
			}
		}
		item.setItemMeta(meta);
		return item;
	}
	
	private enum KillType{
		AWARD, FINE, NONE, BROKE;
	}
}
