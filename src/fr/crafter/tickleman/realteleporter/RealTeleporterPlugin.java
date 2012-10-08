package fr.crafter.tickleman.realteleporter;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import fr.crafter.tickleman.realplugin.RealPlugin;

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
		super();
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
				param1 = paramShortCut(param1);
				if (hasPermission(player, "realteleporter." + param1)) {
					if (param1.equals("create")) {
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
							player.sendMessage(tr("Create teleporter +1").replace("+1", teleporter.name));
						} else {
							if (param2.equals("")) {
								player.sendMessage(tr("You must give the teleporter a name"));
							} else {
								player.sendMessage(tr("Teleporter +1 already exist").replace("+1", param2));
							}
						}
						return true;
					} else if (param1.equals("move")) {
						// move
						RealTeleporter teleporter = teleporters.byName.get(param2);
						if (teleporter != null) {
							teleporter.setLocation(location);
							teleporters.save();
							player.sendMessage(tr("You moved teleporter +1").replace("+1", teleporter.name));
						} else {
							player.sendMessage(tr("Teleporter +1 does not exist").replace("+1", param2));
						}
						return true;
					} else if (param1.equals("remove")) {
						// remove
						RealTeleporter teleporter = teleporters.byName.get(param2);
						if (teleporter != null) {
							teleporters.byName.remove(teleporter.name);
							teleporters.byLocation.remove(teleporter.getLocationKey());
							teleporters.save();
							player.sendMessage(tr("Remove teleporter +1").replace("+1", teleporter.name));
						} else {
							player.sendMessage(tr("Teleporter +1 does not exist").replace("+1", param2));
						}
						return true;
					} else if (param1.equals("link")) {
						// link
						RealTeleporter teleporter1 = teleporters.byName.get(param2);
						RealTeleporter teleporter2 = teleporters.byName.get(param3);
						if (teleporter1 != null && teleporter2 != null) {
							teleporter1.setTarget(teleporter2);
							teleporters.save();
							player.sendMessage(
								tr("Link teleporter +1 to +2")
								.replace("+1",teleporter1.name)
								.replace("+2", teleporter2.name)
							);
						} else {
							if (teleporter1 == null) player.sendMessage(tr("Teleporter +1 does not exist").replace("+1", param2));
							if (teleporter2 == null) player.sendMessage(tr("Teleporter +1 does not exist").replace("+2", param3));
						}
						return true;
					} else if (param1.equals("loop")) {
						// loop link
						RealTeleporter teleporter1 = teleporters.byName.get(param2);
						RealTeleporter teleporter2 = teleporters.byName.get(param3);
						if (teleporter1 != null && teleporter2 != null) {
							teleporter1.setTarget(teleporter2);
							teleporter2.setTarget(teleporter1);
							teleporters.save();
							player.sendMessage(tr("Link teleporter +1 to +2").replace("+1", teleporter1.name).replace("+2", teleporter2.name));
							player.sendMessage(tr("Link teleporter +1 to +2").replace("+1", teleporter2.name).replace("+2", teleporter1.name));
						} else {
							if (teleporter1 == null) player.sendMessage(tr("Teleporter +1 does not exist").replace("+1", param2));
							if (teleporter2 == null) player.sendMessage(tr("Teleporter +1 does not exist").replace("+1", param3));
						}
						return true;
					} else if (param1.equals("unlink")) {
						// unlink
						RealTeleporter teleporter = teleporters.byName.get(param2);
						if (teleporter != null) {
							teleporter.setTarget(null);
							teleporters.save();
							player.sendMessage(tr("Unlinked teleporter +1").replace("+1", teleporter.name));
						} else {
							player.sendMessage(tr("Teleporter +1").replace("+1", param2));
						}
						return true;
					} else if (param1.equals("unloop")) {
						// unloop
						RealTeleporter teleporter1 = teleporters.byName.get(param2);
						RealTeleporter teleporter2 = null;
						if (teleporter1 != null) {
							teleporter2 = teleporter1.target;
							teleporter1.setTarget(null);
							player.sendMessage(tr("Unlinked teleporter +1").replace("+1", teleporter1.name));
						}
						if (teleporter2 != null) {
							teleporter2.setTarget(null);
							player.sendMessage(tr("Unlinked teleporter +1").replace("+1", teleporter2.name));
						}
						if (teleporter1 != null || teleporter2 != null) {
							teleporters.save();
						} else {
							player.sendMessage(tr("Teleporter +1 does not exist").replace("+1", param2));
						}
						return true;
					} else if (param1.equals("list")) {
						// list teleporters
						String values = tr("Gates list") + " :";
						for (RealTeleporter teleporter : teleporters.byName.values()) {
							values = values + " " + teleporter.name;
						}
						player.sendMessage(values);
						return true;
					} else if (param1.equals("orphan")) {
						// list orphan teleporters
						String values = tr("Orphan gates") + " :";
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
					} else if (param1.equals("tp")) {
						// teleport to a teleporter
						RealTeleporter teleporter = teleporters.byName.get(param2);
						if (teleporter != null) {
							player.teleport(teleporter.teleport(this, player));
						} else {
							player.sendMessage(tr("Teleporter +1 does not exist").replace("+1", param2));
						}
						return true;
					} else if (param1.equals("trans")) {
						// translation teleport from source to destination
						RealTeleporter teleporter = teleporters.byName.get(param2);
						if (teleporter != null) {
							if (teleporter.target != null) {
								Location source = teleporter.getLocation(getServer());
								Location destination = teleporter.target.getLocation(getServer());
								double tx = location.getX() - source.getX();
								double ty = location.getY() - source.getY();
								double tz = location.getZ() - source.getZ();
								Location teleport = new Location(
									destination.getWorld(),
									destination.getX() + tx,
									destination.getY() + ty,
									destination.getZ() + tz,
									location.getYaw(),
									location.getPitch()
								);
								teleporter.teleport(this, player, true);
								player.teleport(teleport);
							} else {
								player.sendMessage(tr("Teleporter +1 has no destination").replace("+1", param2));
							}
						} else {
							player.sendMessage(tr("Teleporter +1 does not exist").replace("+1", param2));
						}
					} else if (param1.equals("withouttarget")) {
						// list teleporters without target
						String values = tr("Gates without target") + " :";
						for (RealTeleporter teleporter : teleporters.byName.values()) {
							if (teleporter.target == null) {
								values = values + " " + teleporter.name;
							}
						}
						player.sendMessage(values);
						return true;
					} else if (param1.equals("withoutsource")) {
						// list teleporters without source
						String values = tr("Gates without source") + " :";
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
					} else if (param1.equals("nearest")) {
						int count;
						try { count = Integer.parseInt(param2); } catch (Exception e) { count = 5; }
						for (
							RealTeleporter teleporter :
								teleporters.getSortedByDistance(player.getLocation()).values()
						) {
							player.sendMessage(teleporter.toString());
							if (count-- == 0) break;
						}
						return true;
					} else if (param1.equals("help")) {
						if (hasPermission(player, "realteleporter.create"))        player.sendMessage(tr("/rtel create <gate> : create a gate"));
						if (hasPermission(player, "realteleporter.move"))          player.sendMessage(tr("/rtel move <gate> : move a gate"));
						if (hasPermission(player, "realteleporter.remove"))        player.sendMessage(tr("/rtel remove <gate> : remove gate"));
						if (hasPermission(player, "realteleporter.link"))          player.sendMessage(tr("/rtel link <gate1> <gate2> : link a gate to another"));
						if (hasPermission(player, "realteleporter.loop"))          player.sendMessage(tr("/rtel loop <gate1> <gate2> : loop two gates"));
						if (hasPermission(player, "realteleporter.unlink"))        player.sendMessage(tr("/rtel unlink <gate1> : remove link from gate"));
						if (hasPermission(player, "realteleporter.unloop"))        player.sendMessage(tr("/rtel unloop <gate1> : remove link from and its destination"));
						if (hasPermission(player, "realteleporter.list"))          player.sendMessage(tr("/rtel list : full gates list"));
						if (hasPermission(player, "realteleporter.orphan"))        player.sendMessage(tr("/rtel orphan : gates without source nor target list"));
						if (hasPermission(player, "realteleporter.tp"))            player.sendMessage(tr("/rtel tp <gate2> : teleport to gate"));
						if (hasPermission(player, "realteleporter.trans"))         player.sendMessage(tr("/rtel trans <gate1> : translation teleport from gate"));
						if (hasPermission(player, "realteleporter.withouttarget")) player.sendMessage(tr("/rtel withouttarget : gates without target list"));
						if (hasPermission(player, "realteleporter.withoutsource")) player.sendMessage(tr("/rtel withoutsource : gates without source list"));
						if (hasPermission(player, "realteleporter.nearest"))       player.sendMessage(tr("/rtel nearest : gates near from you"));
						return true;
					}
				} else {
					player.sendMessage(tr("Unknown teleporter command"));
					return true;
				}
			} else {
				player.sendMessage(tr("You don't have access to this command"));
				return true;
			}
		}
		return false;
	}

	//--------------------------------------------------------------------------------- paramShortCut
	public String paramShortCut(String param)
	{
		if (param.length() > 2) return param;
		if (param.equals("c")) return "create";
		if (param.equals("r")) return "remove";
		if (param.equals("lk")) return "link";
		if (param.equals("l") || param.equals("lp")) return "loop";
		if (param.equals("m")) return "move";
		if (param.equals("uk")) return "unlink";
		if (param.equals("u")) return "unloop";
		if (param.equals("o")) return "orphan";
		if (param.equals("wt")) return "withouttarget";
		if (param.equals("ws")) return "withoutsource";
		if (param.equals("n")) return "nearest";
		if (param.equals("h")) return "help";
		return param;
	}

	//--------------------------------------------------------------------------- playerHasPermission
	/**
	 * Plugin developer can override this to set default non-op players some permissions
	 * if permission system is "none"
	 */
	@Override
	public boolean playerHasPermission(String permissionString)
	{
		return permissionString.equals("realteleporter.teleport");
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
		// enable
		super.onEnable();
		// events listeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		// read teleporters file
		teleporters = new RealTeleportersFile(this);
		teleporters.load();
	}

}
