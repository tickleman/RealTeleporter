package fr.crafter.tickleman.realteleporter;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

//################################################################################## RealTeleporter
public class RealTeleporter
{

	public String name;
	public String worldName;
	public long x;
	public long y;
	public long z;
	public String targetName;
	public char direction;
	public RealTeleporter target = null;

	//-------------------------------------------------------------------------------- RealTeleporter
	public RealTeleporter(
		String name, String worldName, long x, long y, long z, float yaml
	) {
		this.name = name;
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
		this.targetName = "";
		this.direction = yawToDirection(yaml);
	}

	//-------------------------------------------------------------------------------- RealTeleporter
	public RealTeleporter(
		String name, String worldName, long x, long y, long z, String targetName, char direction
	) {
		this.name = name;
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
		this.targetName = targetName;
		this.direction = direction;
	}

	//----------------------------------------------------------------------------------- getLocation
	public Location getLocation(Server server)
	{
		return new Location(server.getWorld(worldName), x, y, z);
	}

	//-------------------------------------------------------------------------------- getLocationKey
	public String getLocationKey()
	{
		return this.x + ";" + this.y + ";" + this.z + ";" + this.worldName;
	}

	//----------------------------------------------------------------------------------- setLocation
	public void setLocation(Location location)
	{
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.direction = yawToDirection(location.getYaw());
	}

	//------------------------------------------------------------------------------------- setTarget
	public void setTarget(RealTeleporter target)
	{
		this.targetName = ((target == null) ? "" : target.name);
		this.target = target;
	}

	//---------------------------------------------------------------------------------- teleportFrom
	public Location teleportFrom(RealTeleporterPlugin plugin, Player player)
	{
		return teleportFrom(plugin, player, false);
	}

	//---------------------------------------------------------------------------------- teleportFrom
	public Location teleportFrom(RealTeleporterPlugin plugin, Player player, boolean virtual)
	{
		Location location = null;
		String playerName = player.getName();
		for (World world : plugin.getServer().getWorlds()) {
			if (world.getName().equals(target.worldName)) {
				location = target.teleportTo(plugin, player, virtual);
				if (location != null) {
					plugin.getLog().info(
						"<" + playerName + "> from "
						+ name
						+ " to " + target.name + " ("
						+ target.worldName + "," + target.x + "," + target.y + "," + target.z + ","
						+ direction
						+ ")"
					);
					if (plugin.hasPermission(player, "realteleporter.teleport.showgatename")) {
						player.sendMessage(
							plugin.tr("Teleport from +1 to +2")
							.replace("+1", name)
							.replace("+2", target.name)
						);
					}
				}
			}
		}
		return location;
	}

	//------------------------------------------------------------------------------------ teleportTo
	public Location teleportTo(RealTeleporterPlugin plugin, Player player)
	{
		return teleportTo(plugin, player, false);
	}

	//------------------------------------------------------------------------------------ teleportTo
	public Location teleportTo(RealTeleporterPlugin plugin, Player player, boolean virtual)
	{
		String playerName = player.getName();
		Location location = null;
		for (World world : plugin.getServer().getWorlds()) {
			if (world.getName().equals(worldName)) {
				float yaw;
				switch (direction) {
					case 'E': yaw = 180; break;
					case 'S': yaw = 270; break;
					case 'W': yaw = 0; break;
					default:  yaw = 90; break;
				}
				location = new Location(world, x + .5, y, z + .5, yaw, 0);
				if (!virtual) {
					player.teleport(location);
				}
				plugin.playerLocation.put(playerName, getLocationKey());
			}
		}
		return location;
	}

	//-------------------------------------------------------------------------------------- toString
	public String toString()
	{
		String targetText = ((target != null) ? (" > " + target.name) : " > -");
		return this.name + " (" + this.worldName + "," + this.x + "," + this.y + "," + this.z + ")"
		+ targetText;
	}

	//------------------------------------------------------------------------------- yawToDirection
	public static char yawToDirection(float yaw)
	{
		char direction;
		yaw = Math.round(((yaw % 360) + 360) % 360);
		if (yaw < 45) {
			direction = 'W';
		} else if (yaw < 135) {
			direction = 'N';
		} else if (yaw < 225) {
			direction = 'E';
		} else if (yaw < 315) {
			direction = 'S';
		} else {
			direction = 'W';
		}
		return direction;
	}

}
