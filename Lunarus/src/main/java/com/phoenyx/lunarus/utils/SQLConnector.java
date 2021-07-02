package com.phoenyx.lunarus.utils;

import java.sql.Connection;
import java.sql.DriverManager;

import org.json.JSONObject;

import com.phoenyx.lunarus.Lunarus;

public class SQLConnector {
	private JSONObject config = Lunarus.config;
	
	public Connection getConnection() throws Exception{
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String address = config.getJSONObject("SQL").getString("address");
			String database = config.getJSONObject("SQL").getString("database");
			String port = config.getJSONObject("SQL").getString("port");
			String user = config.getJSONObject("SQL").getString("username");
			String password = config.getJSONObject("SQL").getString("password");
			Class.forName(driver);
			
			Connection con = DriverManager.getConnection("jdbc:mysql://" + address + 
                    ":" + port + 
                    "/" + database + 
                    "?user=" + user + 
                    "&password=" + password + 
                    "&serverTimezone=UTC");
			//System.out.println("Connected to Database");
			return con;
		}catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return null;
	}
}
