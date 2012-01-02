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
		this.direction = yamlToDirection(yaml);
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

	//------------------------------------------------------------------------------- yamlToDirection
	public static char yamlToDirection(float yaml)
	{
		char direction;
		yaml = Math.round(((yaml % 360) + 360) % 360);
		if (yaml < 45) {
			direction = 'W';
		} else if (yaml < 135) {
			direction = 'N';
		} else if (yaml < 225) {
			direction = 'E';
		} else if (yaml < 315) {
			direction = 'S';
		} else {
			direction = 'W';
		}
		return direction;
	}

}
