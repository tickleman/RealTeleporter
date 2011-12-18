package fr.crafter.tickleman.realplugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

//####################################################################################### FileTools
public class RealFileTools
{

	// --------------------------------------------------------------------------- extractDefaultFile
	public static void extractDefaultFile(JavaPlugin plugin, String filePath)
	{
		String[] split = filePath.split("/");
		String fileName = split[split.length - 1];
		File actual = new File(filePath);
		if (!actual.exists()) {
			InputStream input = plugin.getClass().getResourceAsStream("/default/" + fileName);
			if (input != null) {
				plugin.getServer().getLogger().log(
					Level.INFO, "Create default file " + fileName + " for " + filePath
				);
				FileOutputStream output = null;
				try {
					output = new FileOutputStream(actual);
					byte[] buf = new byte[8192];
					int length = 0;
					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}
					plugin.getServer().getLogger().log(Level.INFO, "Default file written " + filePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try { input.close();  } catch (Exception e) {}
				try { output.close(); } catch (Exception e) {}
			}
		}
	}

	// ----------------------------------------------------------------------------------- fileExists
	public static boolean fileExists(String fileName)
	{
		return (new File(fileName)).exists();
	}

	// ----------------------------------------------------------------------------------- renameFile
	public static void renameFile(String fromFile, String toFile)
	{
		File from = new File(fromFile);
		File to = new File(toFile);
		if (from.exists() && !to.exists()) {
			from.renameTo(to);
		}
	}

}
