package fr.crafter.tickleman.realplugin;

//######################################################################################## VarTools
public class RealVarTools
{

	//-------------------------------------------------------------------------------------- toString
	public static Boolean parseBoolean(String string)
	{
		string = string.toLowerCase();
		return (string.equals("null") || string.equals("default")) ? null : (
			string.equals("true")
			|| string.equals("on")
			|| string.equals("yes")
			|| string.equals("enable")
			|| string.equals("1")
		);
	}

	//----------------------------------------------------------------------------------- parseDouble
	public static Double parseDouble(String var, Double def)
	{
		try {
			return Double.parseDouble(var);
		} catch (Exception e) {
			return def;
		}
	}

	//-------------------------------------------------------------------------------------- parseInt
	public static Integer parseInt(String var, Integer def)
	{
		try {
			return Integer.parseInt(var);
		} catch (Exception e) {
			return def;
		}
	}

	//-------------------------------------------------------------------------------------- toString
	public static String toString(Boolean bool)
	{
		return (bool == null) ? "null" : (bool ? "true" : "false");
	}

}
