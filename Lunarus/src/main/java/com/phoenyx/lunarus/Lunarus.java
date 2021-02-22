package com.phoenyx.lunarus;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.EnumSet;

import javax.security.auth.login.LoginException;

import org.json.JSONObject;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.phoenyx.lunarus.commands.Profile;
import com.phoenyx.lunarus.commands.ServerInfo;
import com.phoenyx.lunarus.events.JoinEvent;
import com.phoenyx.lunarus.events.LeaveEvents;
import com.phoenyx.lunarus.events.MessageEvent;
import com.phoenyx.lunarus.utils.JSONUtils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Lunarus {
	private static JDA jda;
	private static File configFile = new File("rsc/config.json");
	public static JSONObject config = new JSONObject();
	
	public static void main(String[] args){
		try {
			  config = JSONUtils.readJSON(configFile);
			} catch (IOException e) {
			  e.printStackTrace();
			  System.exit(2);
			}
		
		JDABuilder b = JDABuilder.createDefault(config.getString("token"));
		b.setActivity(Activity.streaming("Suffering", "https://twitch.tv/LunarusChan"));
		b.enableIntents(EnumSet.allOf(GatewayIntent.class));
		b.setMemberCachePolicy(MemberCachePolicy.ALL);
		b.addEventListeners(new JoinEvent(), new LeaveEvents(), new MessageEvent());
		
		try {
			jda = b.build();
			jda.awaitReady();
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		CommandClientBuilder builder = new CommandClientBuilder();
		builder.setOwnerId(config.getString("owner"));
		builder.setPrefix(config.getString("prefix"));
		builder.addCommands(new ServerInfo(), new Profile());
		builder.setHelpWord(" ");
		CommandClient client = builder.build();
		jda.addEventListener(client);
	}
	
	public Connection getConnection() throws Exception{
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://24.191.244.19/Lunarus";
			String username = "Phoenyx";
			String password = "Killer224";
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
