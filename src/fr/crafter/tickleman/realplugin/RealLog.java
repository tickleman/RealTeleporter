package fr.crafter.tickleman.realplugin;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

//############################################################################################# Log
public class RealLog
{

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private boolean          debugMode;
	private final Logger     globalLog = Logger.getLogger("Minecraft");
	private final String     logFile;
	private final JavaPlugin plugin;
	private boolean          pluginLog;

	//------------------------------------------------------------------------------------------- Log
	public RealLog(RealPlugin plugin, boolean debugMode, boolean pluginLog)
	{
		this.plugin = plugin;
		logFile = plugin.getDataFolder().getPath() + "/"
			+ plugin.getDescription().getName().toLowerCase() + ".log";
		setDebugMode(debugMode);
		setPluginLog(pluginLog);
	}

	//------------------------------------------------------------------------------------------- Log
	public RealLog(RealPlugin plugin, boolean debugMode)
	{
		this(plugin, debugMode, false);
	}

	//------------------------------------------------------------------------------------------- Log
	public RealLog(RealPlugin plugin)
	{
		this(plugin, false, false);
	}

	//------------------------------------------------------------------------------------------ date
	private String date()
	{
		return dateFormat.format(new Date());
	}

	//----------------------------------------------------------------------------------------- debug
	public void debug(String text)
	{
		if (debugMode) {
			log("DEBUG", text);
		}
	}

	//----------------------------------------------------------------------------------------- debug
	public void debug(String text, boolean global)
	{
		if (debugMode) {
			log("DEBUG", text, global);
		}
	}

	//----------------------------------------------------------------------------------------- error
	public void error(String text)
	{
		log("ERROR", text);
	}

	//----------------------------------------------------------------------------------------- error
	public void error(String text, boolean global)
	{
		log("ERROR", text, global);
	}

	//------------------------------------------------------------------------------------------ info
	public void info(String text)
	{
		log("INFO", text);
	}

	//------------------------------------------------------------------------------------------ info
	public void info(String text, boolean global)
	{
		log("INFO", text, global);
	}

	//------------------------------------------------------------------------------------------- log
	private void log(String mark, String text)
	{
		if (pluginLog) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
				writer.write(date() + " [" + mark + "] " + text + "\n");
				writer.flush();
				writer.close();
			} catch (Exception e) {
				globalLog.severe(
					"[" + plugin.getDescription().getName() + "] Could not write into log file " + logFile
					+ " : [" + mark + "] " + text
				);
			}
		} else {
			log(mark, text, true);
		}
	}

	//------------------------------------------------------------------------------------------- log
	private void log(String mark, String text, boolean global)
	{
		if (pluginLog) {
			log(mark, text);
		}
		if (global || !pluginLog) {
			if (mark.equals("INFO")) {
				globalLog.info("[" + plugin.getDescription().getName() + "] " + text);
			} else if (mark.equals("WARNING")) {
				globalLog.warning("[" + plugin.getDescription().getName() + "] " + text);
			} else if (mark.equals("SEVERE")) {
				globalLog.severe("[" + plugin.getDescription().getName() + "] " + text);
			} else if (mark.equals("ERROR")) {
				globalLog.info("[ERROR] [" + plugin.getDescription().getName() + "] " + text);
			} else if (mark.equals("DEBUG")) {
				globalLog.info("[DEBUG] [" + plugin.getDescription().getName() + "] " + text);
			}
		}
	}

	//---------------------------------------------------------------------------------- setDebugMode
	public void setDebugMode(boolean debugMode)
	{
		this.debugMode = debugMode;
	}

	//---------------------------------------------------------------------------------- setPluginLog
	public void setPluginLog(boolean pluginLog)
	{
		this.pluginLog = pluginLog;
	}

	//---------------------------------------------------------------------------------------- severe
	public void severe(String text)
	{
		log("SEVERE", text, true);
	}

	//---------------------------------------------------------------------------------------- severe
	public void severe(String text, boolean global)
	{
		log("SEVERE", text, global);
	}

	//--------------------------------------------------------------------------------------- warning
	public void warning(String text)
	{
		log("WARNING", text);
	}

	//--------------------------------------------------------------------------------------- warning
	public void warning(String text, boolean global)
	{
		log("WARNING", text, global);
	}

}
