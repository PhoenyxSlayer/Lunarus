package com.phoenyx.lunarus;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

import javax.security.auth.login.LoginException;

import org.json.JSONObject;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.phoenyx.lunarus.commands.Help;
import com.phoenyx.lunarus.commands.ServerInfo;
import com.phoenyx.lunarus.commands.modcommands.Ban;
import com.phoenyx.lunarus.commands.modcommands.Kick;
import com.phoenyx.lunarus.commands.modcommands.Purge;
import com.phoenyx.lunarus.commands.modcommands.RemoveWarn;
import com.phoenyx.lunarus.commands.modcommands.Warn;
import com.phoenyx.lunarus.events.JoinEvent;
import com.phoenyx.lunarus.events.LeaveEvent;
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
		b.setActivity(Activity.playing(""+config.getString("prefix")+"help"));
		b.enableIntents(EnumSet.allOf(GatewayIntent.class));
		b.setMemberCachePolicy(MemberCachePolicy.ALL);
		b.addEventListeners(new JoinEvent(), new LeaveEvent());
		
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
		builder.addCommands(new ServerInfo(), new Kick(), new Ban(), new Help(), new Purge(), new Warn(), new RemoveWarn());
		builder.setHelpWord(" ");
		CommandClient client = builder.build();
		jda.addEventListener(client);
		
		System.out.println("Lunarus is Online!");
	}
}
