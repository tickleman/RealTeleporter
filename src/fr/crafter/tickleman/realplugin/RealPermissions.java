package fr.crafter.tickleman.realplugin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

//########################################################################################### Perms
public class RealPermissions
{

	private RealPlugin plugin;
	private String permissionsPluginName;
	private PermissionHandler permissionsHandler;

	//----------------------------------------------------------------------------------------- Perms
	public RealPermissions(RealPlugin plugin, String permissionsPluginName)
	{
		this.plugin = plugin;
		this.permissionsPluginName = permissionsPluginName.toLowerCase();
	}

	//---------------------------------------------------------------------- getPermissionsPluginName
	public String getPermissionsPluginName()
	{
		return permissionsPluginName;
	}

	//--------------------------------------------------------------------------------- hasPermission
	public boolean hasPermission(Player player, String permissionString)
	{
		if (permissionsPluginName.equals("none")) {
			if (permissionString.contains(".")) {
				permissionString = permissionString.replace(
					plugin.getDescription().getName().toLowerCase() + ".", ""
				);
			}
			return player.isOp()
				? plugin.opHasPermission(permissionString)
				: plugin.playerHasPermission(permissionString);
		} else if (permissionsPluginName.equals("bukkit")) {
			boolean perm = player.hasPermission(permissionString);
			return perm;
		} else if (permissionsPluginName.equals("permissions")) {
			boolean perm = permissionsHandler.has(player, permissionString);
			return perm;
		} else {
			return false;
		}
	}

	//------------------------------------------------------------------------ initPermissionsHandler
	public void initPermissionsHandler()
	{
		if (permissionsPluginName.equals("permissions")) {
			Plugin permissions = plugin.getServer().getPluginManager().getPlugin("Permissions");
			if (permissions != null) {
				permissionsHandler = ((Permissions)permissions).getHandler();
			}
		}
	}

}
