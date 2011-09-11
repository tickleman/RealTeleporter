package fr.crafter.tickleman.RealTeleporter;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

//#################################################################### RealTeleporterPlayerListener
public class RealTeleporterPlayerListener extends PlayerListener
{

	private final RealTeleporterPlugin plugin;
	private long nextCheck = 0;

	//------------------------------------------------------------------ RealTeleporterPlayerListener
	public RealTeleporterPlayerListener(RealTeleporterPlugin instance)
	{
		plugin = instance;
	}

	//---------------------------------------------------------------------------------- onPlayerMove
	@Override
	public void onPlayerMove(PlayerMoveEvent event)
	{
		long time = new Date().getTime();
		if (time > nextCheck) {
			nextCheck = time + 100;
			Player player = event.getPlayer();
			String playerName = player.getName();
			RealTeleporter teleporter = plugin.teleporters.teleporterAt(player);
			if (teleporter != null && teleporter.target != null) {
				Location playerLocation = player.getLocation();
				String key = playerLocation.getWorld().getName() + ";"
					+ Math.round(Math.floor(playerLocation.getX())) + ";"
					+ Math.round(Math.floor(playerLocation.getY())) + ";"
					+ Math.round(Math.floor(playerLocation.getZ()));
				if (!key.equals(plugin.playerLocation.get(playerName))) {
					if (!player.hasPermission("realteleporter.use")) {
						player.sendMessage(ChatColor.RED + "You do not have permission to teleport.");
						plugin.playerLocation.put(playerName, key);
					} else {
						RealTeleporter target = teleporter.target;
						for (World world : plugin.getServer().getWorlds()) {
							if (world.getName().equals(target.worldName)) {
								float yaw;
								switch (target.direction) {
									case 'E': yaw = 180; break;
									case 'S': yaw = 270; break;
									case 'W': yaw = 0; break;
									default:  yaw = 90; break;
								}
								Location location = new Location(
									world, target.x + .5, target.y, target.z + .5, yaw, 0
								);
								plugin.log.info(
									"<" + playerName + "> from "
									+ teleporter.name
									+ " to " + target.name + " ("
									+ target.worldName + "," + target.x + "," + target.y + "," + target.z + "," + yaw
									+ ")"
								);
								player.teleport(location);
								event.setTo(location);
								plugin.playerLocation.put(
									playerName,
									target.worldName + ";" + target.x + ";" + target.y + ";" + target.z
								);
							}
						}
					}
				}
			} else if (plugin.playerLocation.get(playerName) != null) {
				plugin.playerLocation.remove(playerName);
			}
		}
	}

}
