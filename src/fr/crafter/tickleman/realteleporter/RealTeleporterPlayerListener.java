package fr.crafter.tickleman.realteleporter;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.crafter.tickleman.realplugin.RealLocation;

//#################################################################### RealTeleporterPlayerListener
public class RealTeleporterPlayerListener implements Listener
{

	private final RealTeleporterPlugin plugin;
	private HashMap<Player, Long> nextCheck = new HashMap<Player, Long>();

	//------------------------------------------------------------------ RealTeleporterPlayerListener
	public RealTeleporterPlayerListener(RealTeleporterPlugin instance)
	{
		plugin = instance;
	}

	//---------------------------------------------------------------------------------- onPlayerMove
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		if (plugin.hasPermission(player, "realteleporter.teleport")) {
			long time = new Date().getTime();
			Long next = nextCheck.get(event.getPlayer());
			if (next == null) {
				nextCheck.put(player, next = (long)0);
			}
			if (time > next) {
				nextCheck.put(player, time + 100);
				String playerName = player.getName();
				RealTeleporter teleporter = plugin.teleporters.teleporterAt(player);
				if ((teleporter != null) && (teleporter.target != null)) {
					Location playerLocation = player.getLocation();
					String key = RealLocation.getId(playerLocation);
					if (!key.equals(plugin.playerLocation.get(playerName))) {
						plugin.playerLocation.put(playerName, key);
						Location location = teleporter.teleport(plugin, player);
						if (location != null) {
							event.setTo(location);
						}
					}
				} else if (plugin.playerLocation.get(playerName) != null) {
					plugin.playerLocation.remove(playerName);
				}
			}
		}
	}

	//---------------------------------------------------------------------------------- onPlayerQuit
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		nextCheck.remove(event.getPlayer());
	}

}
