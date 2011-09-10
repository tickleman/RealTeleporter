package fr.crafter.tickleman.RealTeleporter;

import java.util.HashMap;

import org.bukkit.ChatColor;
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
		super("tickleman", "RealTeleporter", "0.11");
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
				if (!player.hasPermission("realteleporter.edit")) {
					player.sendMessage(ChatColor.RED + "You do not have permission to do this.");
					return true;
				}
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
				} else if (param1.equals("link")) {
					// link
					RealTeleporter teleporter1 = teleporters.byName.get(param2);
					RealTeleporter teleporter2 = teleporters.byName.get(param3);
					if (teleporter1 != null && teleporter2 != null) {
						teleporter1.setTarget(teleporter2);
						teleporters.save();
						player.sendMessage("Link teleporter " + teleporter1.name + " to " + teleporter2.name);
						return true;
					}
				} else if (param1.equals("loop") || param1.equals("l")) {
					// loop link
					RealTeleporter teleporter1 = teleporters.byName.get(param2);
					RealTeleporter teleporter2 = teleporters.byName.get(param3);
					if (teleporter1 != null && teleporter2 != null) {
						teleporter1.setTarget(teleporter2);
						teleporter2.setTarget(teleporter1);
						teleporters.save();
						player.sendMessage("Link teleporter " + teleporter1.name + " to " + teleporter2.name);
						player.sendMessage("Link teleporter " + teleporter2.name + " to " + teleporter1.name);
						return true;
					}
				} else if (param1.equals("unlink")) {
					// unlink
					RealTeleporter teleporter = teleporters.byName.get(param2);
					if (teleporter != null) {
						teleporter.setTarget(null);
						teleporters.save();
						player.sendMessage("Unlinked teleporter " + teleporter.name);
						return true;
					}
				} else if (param1.equals("unloop") || param1.equals("u")) {
					// unloop
					RealTeleporter teleporter1 = teleporters.byName.get(param2);
					RealTeleporter teleporter2 = teleporter1.target;
					if (teleporter1 != null) {
						teleporter1.setTarget(null);
						player.sendMessage("Unlinked teleporter " + teleporter1.name);
					}
					if (teleporter2 != null) {
						teleporter2.setTarget(null);
						player.sendMessage("Unlinked teleporter " + teleporter2.name);
					}
					if (teleporter1 != null || teleporter2 != null) {
						teleporters.save();
						return true;
					}
				} else if (param1.equals("list")) {
					// list teleporters
					String values = "Gates list :";
					for (RealTeleporter teleporter : teleporters.byName.values()) {
						values = values + " " + teleporter.name;
					}
					player.sendMessage(values);
					return true;
				} else if (param1.equals("orphan") || param1.equals("o")) {
					// list orphan teleporters
					String values = "Orphan gates :";
					for (RealTeleporter teleporter1 : teleporters.byName.values()) {
						if (teleporter1.target == null) {
							boolean found = false;
							for (RealTeleporter teleporter2 : teleporters.byName.values()) {
								if (teleporter2.targetName.equals(teleporter1.name)) {
									found = true;
									break;
								}
							}
							if (!found) {
								values = values + " " + teleporter1.name;
							}
						}
					}
					player.sendMessage(values);
					return true;
				} else if (param1.equals("withouttarget") || param1.equals("wt")) {
					// list teleporters without target
					String values = "Gates without target :";
					for (RealTeleporter teleporter : teleporters.byName.values()) {
						if (teleporter.target == null) {
							values = values + " " + teleporter.name;
						}
					}
					player.sendMessage(values);
					return true;
				} else if (param1.equals("withoutsource") || param1.equals("ws")) {
					// list teleporters without source
					String values = "Gates without source :";
					for (RealTeleporter teleporter1 : teleporters.byName.values()) {
						boolean found = false;
						for (RealTeleporter teleporter2 : teleporters.byName.values()) {
							if (teleporter2.targetName.equals(teleporter1.name)) {
								found = true;
								break;
							}
						}
						if (!found) {
							values = values + " " + teleporter1.name;
						}
					}
					player.sendMessage(values);
					return true;
				} else if (param1.equals("help") || param1.equals("h")) {
					player.sendMessage("/rtel create <gate>");
					player.sendMessage("/rtel remove <gate>");
					player.sendMessage("/rtel link <gate1> <gate2>");
					player.sendMessage("/rtel loop <gate1> <gate2>");
					player.sendMessage("/rtel unlink <gate1>");
					player.sendMessage("/rtel unloop <gate1>");
					player.sendMessage("/rtel list          : full gates list");
					player.sendMessage("/rtel orphan        : gates without source nor target list");
					player.sendMessage("/rtel withouttarget : gates without target list");
					player.sendMessage("/rtel withoutsource : gates without source list");
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
