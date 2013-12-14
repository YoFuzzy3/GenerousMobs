package com.fuzzoland.GenerousMobs;

import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;

public class EventListener implements Listener{

	private GenerousMobs plugin;
	
	public EventListener(GenerousMobs plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event){
		if(event.getSpawnReason() == SpawnReason.SPAWNER){
			plugin.isFromSpawner.add(event.getEntity().getUniqueId());
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event){
		LivingEntity entity = event.getEntity();
		if(!plugin.isNaturalEntity(entity)){
			plugin.isFromSpawner.remove(entity.getUniqueId());
			if(!plugin.getConfig().getBoolean("SpawnerRewards.Enabled")){
				return;
			}
		}
		if(entity.getKiller() != null){
			Player player = entity.getKiller();
			Location location = entity.getLocation();
			Boolean reward = true;
			switch(entity.getType()){
			case BAT:
				reward = plugin.giveReward(player, location, "Bat");
				break;
			case BLAZE:
				reward = plugin.giveReward(player, location, "Blaze");
				break;
			case CAVE_SPIDER:
				reward = plugin.giveReward(player, location, "CaveSpider");
				break;
			case CHICKEN:
				Chicken chicken = (Chicken) entity;
				if(chicken.isAdult()){
					reward = plugin.giveReward(player, location, "ChickenAdult");
				}else{
					reward = plugin.giveReward(player, location, "ChickenBaby");
				}
				break;
			case COW:
				Cow cow = (Cow) entity;
				if(cow.isAdult()){
					reward = plugin.giveReward(player, location, "CowAdult");
				}else{
					reward = plugin.giveReward(player, location, "CowBaby");
				}
				break;
			case CREEPER:
				reward = plugin.giveReward(player, location, "Creeper");
				break;
			case ENDER_DRAGON:
				reward = plugin.giveReward(player, location, "EnderDragon");
				break;
			case ENDERMAN:
				reward = plugin.giveReward(player, location, "Enderman");
				break;
			case GHAST:
				reward = plugin.giveReward(player, location, "Ghast");
				break;
			case GIANT:
				reward = plugin.giveReward(player, location, "Giant");
				break;
			case HORSE:
				Horse horse = (Horse) entity;
				if(horse.isTamed()){
					if(!horse.getOwner().getName().equals(player.getName())){
						reward = plugin.giveReward(player, location, "HorseTamed");
					}
				}else{
					reward = plugin.giveReward(player, location, "HorseWild");
				}
				break;
			case IRON_GOLEM:
				reward = plugin.giveReward(player, location, "IronGolem");
				break;
			case MAGMA_CUBE:
				reward = plugin.giveReward(player, location, "MagmaCube");
				break;
			case MUSHROOM_COW:
				MushroomCow mushroomcow = (MushroomCow) entity;
				if(mushroomcow.isAdult()){
					reward = plugin.giveReward(player, location, "MushroomCowAdult");
				}else{
					reward = plugin.giveReward(player, location, "MushroomCowBaby");
				}
				break;
			case OCELOT:
				Ocelot ocelot = (Ocelot) entity;
				if(ocelot.isTamed()){
					if(!ocelot.getOwner().getName().equals(player.getName())){
						if(ocelot.isAdult()){
							reward = plugin.giveReward(player, location, "OcelotTamedAdult");
						}else{
							reward = plugin.giveReward(player, location, "OcelotTamedBaby");
						}
					}
				}else{
					if(ocelot.isAdult()){
						reward = plugin.giveReward(player, location, "OcelotWildAdult");
					}else{
						reward = plugin.giveReward(player, location, "OcelotWildBaby");
					}
				}
				break;
			case PIG:
				Pig pig = (Pig) entity;
				if(pig.isAdult()){
					reward = plugin.giveReward(player, location, "PigAdult");
				}else{
					reward = plugin.giveReward(player, location, "PigBaby");
				}
				break;
			case PIG_ZOMBIE:
				PigZombie pigzombie = (PigZombie) entity;
				if(pigzombie.isBaby()){
					reward = plugin.giveReward(player, location, "PigZombieBaby");
				}else{
					reward = plugin.giveReward(player, location, "PigZombieAdult");
				}
				break;
			case PLAYER:
				reward = plugin.giveReward(player, location, "Player");
				break;
			case SHEEP:
				Sheep sheep = (Sheep) entity;
				if(sheep.isAdult()){
					reward = plugin.giveReward(player, location, "SheepAdult");
				}else{
					reward = plugin.giveReward(player, location, "SheepBaby");
				}
				break;
			case SILVERFISH:
				reward = plugin.giveReward(player, location, "Silverfish");
				break;
			case SKELETON:
				Skeleton skeleton = (Skeleton) entity;
				if(skeleton.getSkeletonType() == SkeletonType.NORMAL){
					reward = plugin.giveReward(player, location, "SkeletonNormal");
				}else if(skeleton.getSkeletonType() == SkeletonType.WITHER){
					reward = plugin.giveReward(player, location, "SkeletonWither");
				}
				break;
			case SLIME:
				reward = plugin.giveReward(player, location, "Slime");
				break;
			case SNOWMAN:
				reward = plugin.giveReward(player, location, "Snowman");
				break;
			case SPIDER:
				Spider spider = (Spider) entity;
				if(spider.getPassenger() instanceof Skeleton){
					reward = plugin.giveReward(player, location, "SpiderJockey");
				}else{
					reward = plugin.giveReward(player, location, "SpiderNormal");
				}
				break;
			case SQUID:
				reward = plugin.giveReward(player, location, "Squid");
				break;
			case VILLAGER:
				Villager villager = (Villager) entity;
				if(villager.isAdult()){
					reward = plugin.giveReward(player, location, "VillagerAdult");
				}else{
					reward = plugin.giveReward(player, location, "VillagerBaby");
				}
				break;
			case WITCH:
				reward = plugin.giveReward(player, location, "Witch");
				break;
			case WITHER:
				reward = plugin.giveReward(player, location, "Wither");
				break;
			case WOLF:
				Wolf wolf = (Wolf) entity;
				if(wolf.isTamed()){
					if(!wolf.getOwner().getName().equals(player.getName())){
						if(wolf.isAdult()){
							reward = plugin.giveReward(player, location, "WolfTamedAdult");
						}else{
							reward = plugin.giveReward(player, location, "WolfTamedBaby");
						}
					}
				}else{
					if(wolf.isAdult()){
						reward = plugin.giveReward(player, location, "WolfWildAdult");
					}else{
						reward = plugin.giveReward(player, location, "WolfWildBaby");
					}
				}
				break;
			case ZOMBIE:
				Zombie zombie = (Zombie) entity;
				if(zombie.isBaby()){
					reward = plugin.giveReward(player, location, "ZombieBaby");
				}else if(zombie.isVillager()){
					reward = plugin.giveReward(player, location, "ZombieVillager");
				}else{
					reward = plugin.giveReward(player, location, "ZombieNormal");
				}
				break;
			default:
				break;
			}
			if(!reward){
				entity.setHealth(entity.getMaxHealth());
			}
		}
	}
}
