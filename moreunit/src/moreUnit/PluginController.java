package moreUnit;


public class PluginController {

	private static PluginController controllerInstance;

	/**
	 * @deprecated
	 */
	public static PluginController getInstance() {
		if(controllerInstance == null)
			controllerInstance = new PluginController();
		 
		return controllerInstance;
	}
}
