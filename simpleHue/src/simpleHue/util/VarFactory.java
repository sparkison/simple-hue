/**
 * @author Shaun Parkison
 * Simple driver program for turning 
 * Philips Hue light(s) on/off
 *
 * Primary variables for simple hue
 * application, namely config file and version
 */

package simpleHue.util;

import java.io.File;

public class VarFactory {
	
	// Singleton instance
	private static VarFactory instance = null;
	private final double version = 0.1;
	private final File config = new File("config");
	private final String devicetype = "simple hue"; // The hue device type
	private final String username = "simplehueuser"; // The username for this app

	// Exists only to defeat instantiation
	protected VarFactory() {}

	/**
	 * @return the devicetype
	 */
	public String getDevicetype() {
		return devicetype;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	// Get instance of VarFactory
	public static VarFactory getInstance() {
		if (instance == null) {
			instance = new VarFactory();
		}
		return instance;
	}

	/**
	 * @return the version
	 */
	public double getVersion() {
		return version;
	}

	/**
	 * @return the config
	 */
	public File getConfig() {
		return config;
	}



}