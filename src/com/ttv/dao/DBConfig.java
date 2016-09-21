package com.ttv.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBConfig {

	public static String db_driver_Service = "com.mysql.jdbc.Driver";
	public static String db_url_Service = "jdbc:mysql://localhost:3306/account?autoReconnect=true&characterEncoding=UTF-8";
	public static String db_user_Service = "root";
	public static String db_pass_Service = "";
	
	public static String db_audio_driver_Service = "com.mysql.jdbc.Driver";
	public static String db_audio_url_Service = "jdbc:mysql://localhost:3306/account?autoReconnect=true&characterEncoding=UTF-8";
	public static String db_audio_user_Service = "root";
	public static String db_audio_pass_Service = "";
	
	public static String db_football_url_Service = "jdbc:mysql://localhost:3306/account?autoReconnect=true&characterEncoding=UTF-8";
	public static String db_football_user_Service = "root";
	public static String db_football_pass_Service = "";
	
	public static String db_swap_url_Service = "jdbc:mysql://localhost:3306/vtc_swaphub?autoReconnect=true&characterEncoding=UTF-8";
	public static String db_swap_user_Service = "root";
	public static String db_swap_pass_Service = "";
	
	public static int db_connection = 2;
	
	public static String cache_ip = "127.0.0.1";
	public static String cache_port = "11211";

	/**
	 * Contains the parameters and default values for this gateway
	 * such as system id, password, default npi, and ton of sender...
	 */
	private static Properties properties = new Properties();

	/**
	 * Loads configuration parameters from the file with the given name.
	 * Sets private variable to the loaded values.
	 */
	public static void loadProperties() throws IOException {
		FileInputStream propsFile = null;		
		propsFile = new java.io.FileInputStream("./conf/config.conf");
		properties.load(propsFile);
		propsFile.close();
		db_driver_Service = properties.getProperty("db_driver_service",	db_driver_Service);
		db_url_Service = properties.getProperty("db_url_service",db_url_Service);
		db_user_Service = properties.getProperty("db_user_service",db_user_Service);
		db_pass_Service = properties.getProperty("db_pass_service",db_pass_Service);	
		
		db_audio_driver_Service = properties.getProperty("db_driver_service",	db_audio_driver_Service);
		db_audio_url_Service = properties.getProperty("db_audio_url_service",db_audio_url_Service);
		db_audio_user_Service = properties.getProperty("db_audio_user_service",db_audio_user_Service);
		db_audio_pass_Service = properties.getProperty("db_audio_pass_service",db_audio_pass_Service);	
		
		db_football_url_Service = properties.getProperty("db_football_url_service",db_football_url_Service);
		db_football_user_Service = properties.getProperty("db_football_user_service",db_football_user_Service);
		db_football_pass_Service = properties.getProperty("db_football_pass_service",db_football_pass_Service);	
		
		cache_ip = properties.getProperty("cache_ip",	cache_ip);
		cache_port = properties.getProperty("cache_port",cache_port);
		
		db_swap_url_Service = properties.getProperty("db_swap_url_service",db_swap_url_Service);
		db_swap_user_Service = properties.getProperty("db_swap_user_service",db_swap_user_Service);
		db_swap_pass_Service = properties.getProperty("db_swap_pass_service",db_swap_pass_Service);	
		

	}

	// Gets a property and converts it into byte.
	static byte getByteProperty(String propName, byte defaultValue) {
		return Byte.parseByte(properties.getProperty(propName,
				Byte.toString(defaultValue)).trim());
	}

	// Gets a property and converts it into integer.
	static int getIntProperty(String propName, int defaultValue) {
		return Integer.parseInt(properties.getProperty(propName,
				Integer.toString(defaultValue)).trim());
	}
	
	static{
		try {
			loadProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			DBConfig.loadProperties();
			System.out.println(DBConfig.db_url_Service);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
