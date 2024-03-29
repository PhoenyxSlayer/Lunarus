package com.phoenyx.lunarus;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

import javax.security.auth.login.LoginException;

import org.json.JSONObject;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.phoenyx.lunarus.commands.Daily;
import com.phoenyx.lunarus.commands.Help;
import com.phoenyx.lunarus.commands.Profile;
import com.phoenyx.lunarus.commands.modcommands.Ban;
import com.phoenyx.lunarus.commands.modcommands.Kick;
import com.phoenyx.lunarus.commands.modcommands.Purge;
import com.phoenyx.lunarus.commands.modcommands.RemoveWarn;
import com.phoenyx.lunarus.commands.modcommands.Warn;
import com.phoenyx.lunarus.events.JoinEvent;
import com.phoenyx.lunarus.events.LeaveEvent;
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
			
	public static void main(String[] args) throws LoginException{
		try {
			  config = JSONUtils.readJSON(configFile);
			} catch (IOException e) {
			  e.printStackTrace();
			  System.exit(2);
			}

		JDABuilder b = JDABuilder.createDefault(config.getString("token"));
		b.setActivity(Activity.streaming(config.getString("prefix") + "help", "https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
		b.enableIntents(EnumSet.allOf(GatewayIntent.class));
		b.setMemberCachePolicy(MemberCachePolicy.ALL);
		b.addEventListeners(new JoinEvent(), new LeaveEvent(), new MessageEvent());
		
		try {
			jda = b.build();
			jda.awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		CommandClientBuilder builder = new CommandClientBuilder();
		builder.setOwnerId(config.getString("owner"));
		builder.setPrefix(config.getString("prefix"));
		builder.addCommands(new Profile(), new Kick(), new Ban(), new Help(), new Purge(), new Warn(), new RemoveWarn(), new Daily());
		builder.addSlashCommands(new Help(), new Daily(), new Ban(), new Profile(), new Purge());
		builder.setHelpWord(" ");
		CommandClient client = builder.build();
		client.upsertInteractions(jda);
		jda.addEventListener(client);
		
		System.out.println("Lunarus is Online!");
	}
}
