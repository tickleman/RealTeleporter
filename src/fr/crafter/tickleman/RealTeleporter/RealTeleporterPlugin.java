package fr.crafter.tickleman.RealTeleporter;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;

import fr.crafter.tickleman.RealPlugin.RealPlugin;

//################################################################################## RealTeleporter
public class RealTeleporterPlugin extends RealPlugin
{

	/** Teleporters list and file link */
	public RealTeleportersFile teleporters;

	/** Player last position */
	public HashMap<String, String> playerLocation = new HashMap<String, String>(); 

	/** Player events Listener */
	private final RealTeleporterPlayerListener playerListener = new RealTeleporterPlayerListener(this);

	//-------------------------------------------------------------------------- RealTeleporterPlugin
	public RealTeleporterPlugin()
	{
		super("tickleman", "RealTeleporter", "0.02");
	}

	//------------------------------------------------------------------------------- onPlayerCommand
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (sender instanceof Player) {
			String command = cmd.getName().toLowerCase();
			Player player = (Player)sender;
			Location location = player.getLocation();
			if (command.equals("rtel") || command.equals("rt")) {
				String param1 = args.length > 0 ? args[0] : "";
				String param2 = args.length > 1 ? args[1] : "";
				String param3 = args.length > 2 ? args[2] : "";
				if (param1.equals("create") || param1.equals("c")) {
					// create
					if (!param2.equals("") && teleporters.byName.get(param2) == null) {
						RealTeleporter teleporter = new RealTeleporter(
							param2,
							location.getWorld().getName(),
							Math.round(Math.floor(location.getX())),
							Math.round(Math.floor(location.getY())),
							Math.round(Math.floor(location.getZ())),
							location.getYaw()
						);
						teleporters.byName.put(teleporter.name, teleporter);
						teleporters.byLocation.put(teleporter.getLocationKey(), teleporter);
						teleporters.save();
						player.sendMessage("Create teleporter " + teleporter.name);
						return true;
					}
				} else if (param1.equals("remove") || param1.equals("r")) {
					// remove
					RealTeleporter teleporter = teleporters.byName.get(param2);
					if (teleporter != null) {
						teleporters.byName.remove(teleporter.name);
						teleporters.byLocation.remove(teleporter.getLocationKey());
						teleporters.save();
						player.sendMessage("Remove teleporter " + teleporter.name);
						return true;
					}
				} else if (param1.equals("link") || param1.equals("l")) {
					// link
					RealTeleporter teleporter1 = teleporters.byName.get(param2);
					RealTeleporter teleporter2 = teleporters.byName.get(param3);
					if (teleporter1 != null && teleporter2 != null) {
						teleporter1.setTarget(teleporter2);
						teleporters.save();
						player.sendMessage("Link teleporter " + teleporter1.name + " to " + teleporter2.name);
						return true;
					}
				}
			}
		}
		return false;
	}

	//------------------------------------------------------------------------------------- onDisable
	@Override
	public void onDisable()
	{
		teleporters = null;
		super.onDisable();
	}

	//-------------------------------------------------------------------------------------- onEnable
	@Override
	public void onEnable()
	{
		// events listeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
		// read teleporters file
		teleporters = new RealTeleportersFile(this);
		teleporters.load();
		// enable
		super.onEnable();
	}

}
