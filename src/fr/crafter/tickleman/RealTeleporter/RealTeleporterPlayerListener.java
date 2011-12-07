package fr.crafter.tickleman.RealTeleporter;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.crafter.tickleman.RealPlugin.RealLocation;

//#################################################################### RealTeleporterPlayerListener
public class RealTeleporterPlayerListener extends PlayerListener
{

	private final RealTeleporterPlugin plugin;
	private HashMap<Player, Long> nextCheck = new HashMap<Player, Long>();

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
		Long next = nextCheck.get(event.getPlayer());
		if (next == null) {
			nextCheck.put(event.getPlayer(), next = (long)0);
		}
		if (time > next) {
			nextCheck.put(event.getPlayer(), time + 100);
			Player player = event.getPlayer();
			String playerName = player.getName();
			RealTeleporter teleporter = plugin.teleporters.teleporterAt(player);
			if ((teleporter != null) && (teleporter.target != null)) {
				Location playerLocation = player.getLocation();
				String key = RealLocation.getId(playerLocation);
				if (!key.equals(plugin.playerLocation.get(playerName))) {
					plugin.playerLocation.put(playerName, key);
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
							plugin.getLog().info(
								"<" + playerName + "> from "
								+ teleporter.name
								+ " to " + target.name + " ("
								+ target.worldName + "," + target.x + "," + target.y + "," + target.z + "," + yaw
								+ ")"
							);
							player.teleport(location);
							event.setTo(location);
							plugin.playerLocation.put(playerName, target.getLocationKey());
						}
					}
				}
			} else if (plugin.playerLocation.get(playerName) != null) {
				plugin.playerLocation.remove(playerName);
			}
		}
	}

	//---------------------------------------------------------------------------------- onPlayerQuit
	@Override
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		nextCheck.remove(event.getPlayer());
	}

}
