package fr.crafter.tickleman.realteleporter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

//############################################################################# RealTeleportersFile
public class RealTeleportersFile
{

	private final RealTeleporterPlugin plugin;
	private final String fileName = "teleporters";

	/** Teleporters list : "x;y;z;world" => RealTeleporter */
	public HashMap<String, RealTeleporter> byLocation = new HashMap<String, RealTeleporter>();

	/** Teleporters list : "name" => RealTeleporter */
	public HashMap<String, RealTeleporter> byName = new HashMap<String, RealTeleporter>();

	//--------------------------------------------------------------------------- RealTeleportersFile
	public RealTeleportersFile(final RealTeleporterPlugin plugin)
	{
		this.plugin = plugin;
	}

	//--------------------------------------------------------------------------- getSortedByDistance
	public Map<Double, RealTeleporter> getSortedByDistance(Location location)
	{
		Map<Double, RealTeleporter> sortedTeleporters = new TreeMap<Double, RealTeleporter>();
		for (RealTeleporter teleporter : byLocation.values()) {
			Location teleporterLocation = teleporter.getLocation(plugin.getServer());
			if (location.getWorld().equals(teleporterLocation.getWorld())) {
				double distance = Math.sqrt(
					Math.pow(Math.abs(teleporterLocation.getX() - location.getX()), 2)
					+ Math.pow(Math.abs(teleporterLocation.getZ() - location.getZ()), 2)
				);
				sortedTeleporters.put(distance, teleporter);
			}
		}
		return sortedTeleporters;
	}

	//------------------------------------------------------------------------------------------ load
	public RealTeleportersFile load()
	{
		byLocation = new HashMap<String, RealTeleporter>();
		byName = new HashMap<String, RealTeleporter>();
		try {
			plugin.getLog().info("load " + plugin.getDataFolder().getPath() + "/" + fileName + ".txt");
			BufferedReader reader = new BufferedReader(
				new FileReader(plugin.getDataFolder().getPath() + "/" + fileName + ".txt")
			);
			String buffer;
			while ((buffer = reader.readLine()) != null) {
				String[] line = buffer.split(",");
				if ((line.length > 6) && (buffer.charAt(0) != '#')) {
					try {
						String name = line[0].trim();
						String worldName = line[1].trim();
						long x = Long.parseLong(line[2].trim());
						long y = Long.parseLong(line[3].trim());
						long z = Long.parseLong(line[4].trim());
						String targetName = line[5].trim();
						char direction = (line[6].trim().length() > 0) ? line[6].trim().charAt(0) : 'N';
						String key = x + ";" + y + ";" + z + ";" + worldName;
						RealTeleporter teleporter = new RealTeleporter(
							name, worldName, x, y, z, targetName, direction
						);
						byLocation.put(key, teleporter);
						byName.put(name, teleporter);
					} catch (Exception e) {
						// when some values are not numbers, then ignore teleporter
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			plugin.getLog().warning(
				"Needs " + plugin.getDataFolder().getPath() + "/" + fileName + ".txt file (will auto-create)"
			);
		}
		solve();
		return this;
	}

	//------------------------------------------------------------------------------------------ save
	public void save()
	{
		try {
			BufferedWriter writer = new BufferedWriter(
				new FileWriter(plugin.getDataFolder().getPath() + "/" + fileName + ".txt")
			);
			writer.write("#name,world,x,y,z,target,direction\n");
			for (RealTeleporter teleporter : byName.values()) {
				writer.write(
					teleporter.name + ","
					+ teleporter.worldName + ","
					+ teleporter.x + ","
					+ teleporter.y + ","
					+ teleporter.z + ","
					+ teleporter.targetName + ","
					+ teleporter.direction
					+ "\n"
				);
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			plugin.getLog().severe(
				"Could not save " + plugin.getDataFolder().getPath() + "/" + fileName + ".txt file"
			);
		}
	}

	//----------------------------------------------------------------------------------------- solve
	public void solve()
	{
		for (RealTeleporter teleporter : byName.values())
		{
			if (teleporter.target == null) {
				teleporter.target = byName.get(teleporter.targetName);
			}
		}
	}

	//---------------------------------------------------------------------------------- teleporterAt
	public RealTeleporter teleporterAt(Player player)
	{
		Location location = player.getLocation();
		return teleporterAt(
			player.getWorld().getName(),
			Math.round(Math.floor(location.getX())),
			Math.round(Math.floor(location.getY())),
			Math.round(Math.floor(location.getZ()))
		);
	}

	//---------------------------------------------------------------------------------- teleporterAt
	public RealTeleporter teleporterAt(String worldName, long x, long y, long z)
	{
		String key = x + ";" + y + ";" + z + ";" + worldName;
		return byLocation.get(key);
	}

}
