package com.phoenyx.lunarus.utils;

import java.sql.Connection;
import java.sql.DriverManager;

import org.json.JSONObject;

import com.phoenyx.lunarus.Lunarus;

public class SQLConnector {
	private JSONObject config = Lunarus.config;
	
	public Connection getConnection() throws Exception{
		try {
			String driver = config.getJSONObject("SQL").getString("driver");
			String url = config.getJSONObject("SQL").getString("url");
			String username = config.getJSONObject("SQL").getString("username");
			String password = config.getJSONObject("SQL").getString("password");
			Class.forName(driver);
			
			Connection con = DriverManager.getConnection(url, username, password);
			System.out.println("Connected to Database");
			return con;
		}catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return null;
	}
}
