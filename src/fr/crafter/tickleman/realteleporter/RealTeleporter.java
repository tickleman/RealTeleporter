package fr.crafter.tickleman.realteleporter;

import org.bukkit.Location;
import org.bukkit.Server;

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
