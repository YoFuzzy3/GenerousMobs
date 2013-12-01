package com.fuzzoland.GenerousMobs;

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
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
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
			Boolean reward = true;
			switch(entity.getType()){
			case BAT:
				reward = plugin.giveReward(player, "Bat");
				break;
			case BLAZE:
				reward = plugin.giveReward(player, "Blaze");
				break;
			case CAVE_SPIDER:
				reward = plugin.giveReward(player, "CaveSpider");
				break;
			case CHICKEN:
				Chicken chicken = (Chicken) entity;
				if(chicken.isAdult()){
					reward = plugin.giveReward(player, "ChickenAdult");
				}else{
					reward = plugin.giveReward(player, "ChickenBaby");
				}
				break;
			case COW:
				Cow cow = (Cow) entity;
				if(cow.isAdult()){
					reward = plugin.giveReward(player, "CowAdult");
				}else{
					reward = plugin.giveReward(player, "CowBaby");
				}
				break;
			case CREEPER:
				reward = plugin.giveReward(player, "Creeper");
				break;
			case ENDER_DRAGON:
				reward = plugin.giveReward(player, "EnderDragon");
				break;
			case ENDERMAN:
				reward = plugin.giveReward(player, "Enderman");
				break;
			case GHAST:
				reward = plugin.giveReward(player, "Ghast");
				break;
			case GIANT:
				reward = plugin.giveReward(player, "Giant");
				break;
			case HORSE:
				Horse horse = (Horse) entity;
				if(horse.isTamed()){
					if(!horse.getOwner().getName().equals(player.getName())){
						reward = plugin.giveReward(player, "HorseTamed");
					}
				}else{
					reward = plugin.giveReward(player, "HorseWild");
				}
				break;
			case IRON_GOLEM:
				reward = plugin.giveReward(player, "IronGolem");
				break;
			case MAGMA_CUBE:
				reward = plugin.giveReward(player, "MagmaCube");
				break;
			case MUSHROOM_COW:
				MushroomCow mushroomcow = (MushroomCow) entity;
				if(mushroomcow.isAdult()){
					reward = plugin.giveReward(player, "MushroomCowAdult");
				}else{
					reward = plugin.giveReward(player, "MushroomCowBaby");
				}
				break;
			case OCELOT:
				Ocelot ocelot = (Ocelot) entity;
				if(ocelot.isTamed()){
					if(!ocelot.getOwner().getName().equals(player.getName())){
						if(ocelot.isAdult()){
							reward = plugin.giveReward(player, "OcelotTamedAdult");
						}else{
							reward = plugin.giveReward(player, "OcelotTamedBaby");
						}
					}
				}else{
					if(ocelot.isAdult()){
						reward = plugin.giveReward(player, "OcelotWildAdult");
					}else{
						reward = plugin.giveReward(player, "OcelotWildBaby");
					}
				}
				break;
			case PIG:
				Pig pig = (Pig) entity;
				if(pig.isAdult()){
					reward = plugin.giveReward(player, "PigAdult");
				}else{
					reward = plugin.giveReward(player, "PigBaby");
				}
				break;
			case PIG_ZOMBIE:
				PigZombie pigzombie = (PigZombie) entity;
				if(pigzombie.isBaby()){
					reward = plugin.giveReward(player, "PigZombieBaby");
				}else{
					reward = plugin.giveReward(player, "PigZombieAdult");
				}
				break;
			case PLAYER:
				reward = plugin.giveReward(player, "Player");
				break;
			case SHEEP:
				Sheep sheep = (Sheep) entity;
				if(sheep.isAdult()){
					reward = plugin.giveReward(player, "SheepAdult");
				}else{
					reward = plugin.giveReward(player, "SheepBaby");
				}
				break;
			case SILVERFISH:
				reward = plugin.giveReward(player, "Silverfish");
				break;
			case SKELETON:
				Skeleton skeleton = (Skeleton) entity;
				if(skeleton.getSkeletonType() == SkeletonType.NORMAL){
					reward = plugin.giveReward(player, "Skeleton");
				}else if(skeleton.getSkeletonType() == SkeletonType.WITHER){
					reward = plugin.giveReward(player, "WitherSkeleton");
				}
				break;
			case SLIME:
				reward = plugin.giveReward(player, "Slime");
				break;
			case SNOWMAN:
				reward = plugin.giveReward(player, "Snowman");
				break;
			case SPIDER:
				Spider spider = (Spider) entity;
				if(spider.getPassenger() instanceof Skeleton){
					reward = plugin.giveReward(player, "SpiderJockey");
				}else{
					reward = plugin.giveReward(player, "SpiderNormal");
				}
				break;
			case SQUID:
				reward = plugin.giveReward(player, "Squid");
				break;
			case VILLAGER:
				Villager villager = (Villager) entity;
				if(villager.isAdult()){
					reward = plugin.giveReward(player, "VillagerAdult");
				}else{
					reward = plugin.giveReward(player, "VillagerBaby");
				}
				break;
			case WITCH:
				reward = plugin.giveReward(player, "Witch");
				break;
			case WITHER:
				reward = plugin.giveReward(player, "Wither");
				break;
			case WOLF:
				Wolf wolf = (Wolf) entity;
				if(wolf.isTamed()){
					if(!wolf.getOwner().getName().equals(player.getName())){
						if(wolf.isAdult()){
							reward = plugin.giveReward(player, "WolfTamedAdult");
						}else{
							reward = plugin.giveReward(player, "WolfTamedBaby");
						}
					}
				}else{
					if(wolf.isAdult()){
						reward = plugin.giveReward(player, "WolfWildAdult");
					}else{
						reward = plugin.giveReward(player, "WolfWildBaby");
					}
				}
				break;
			case ZOMBIE:
				Zombie zombie = (Zombie) entity;
				if(zombie.isBaby()){
					reward = plugin.giveReward(player, "ZombieBaby");
				}else if(zombie.isVillager()){
					reward = plugin.giveReward(player, "ZombieVillager");
				}else{
					reward = plugin.giveReward(player, "ZombieNormal");
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
