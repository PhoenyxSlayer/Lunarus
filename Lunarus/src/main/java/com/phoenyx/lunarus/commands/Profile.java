package com.phoenyx.lunarus.commands;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.phoenyx.lunarus.Lunarus;
import com.phoenyx.lunarus.utils.SQLConnector;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class Profile extends SlashCommand{
	private SQLConnector main = new SQLConnector();
	private JSONObject config = Lunarus.config;
	
	public Profile() {
		this.name = "profile";
		this.aliases = new String[] {"p"};
		this.help = "Check your server profile";
		this.guildOnly = false;
		this.options = Collections.singletonList(new OptionData(OptionType.USER, "user", "select a user to see their server profile").setRequired(false));
	}

	protected void execute(CommandEvent event) {
		String args[] = event.getArgs().split(" ");
		Member member = event.getMember();
		String userId = event.getMember().getId();
		String guildId = event.getGuild().getId();
		Guild guild = event.getGuild();
		MessageChannel channel = event.getChannel();
		int feathers = 0;
		
		if(args[0] != null) {
			String id = args[0].replaceAll("<@", "").replaceAll(">", "");
			id = id.replaceAll("!", "");
			try {
				member = guild.getMemberById(id);
				if(!guild.getMembers().contains(member)) {
					event.reply("User does not exist");
					return;
				}
				Connection con = main.getConnection();
				PreparedStatement s = con.prepareStatement("SELECT * FROM points WHERE userid = '"+member.getId()+"' AND serverid = '"+guildId+"'");
				ResultSet r = s.executeQuery();
				
				if(r.next()) {
					feathers = r.getInt("points");
					
					sendEmbed(channel, member, feathers);
				}else {
					sendEmbed(channel, member, feathers);
				}
			}catch(Exception e) {
				System.out.println(e);
			}
			return;
		}
		try {
			Connection con = main.getConnection();
			PreparedStatement s = con.prepareStatement("SELECT * FROM points WHERE userid = '"+userId+"' AND serverid = '"+guildId+"'");
			ResultSet r = s.executeQuery();
			
			if(r.next()) {
				feathers = r.getInt("points");
				
				sendEmbed(channel, member, feathers);
			}else {
				sendEmbed(channel, member, feathers);
			}
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	protected void execute(SlashCommandEvent event) {
		Member member = event.getMember();
		Member user = event.optMember("user");
		String userId = event.getMember().getId();
		String guildId = event.getGuild().getId();
		int feathers = 0;
		
		if(user != null) {
			try {
				Connection con = main.getConnection();
				PreparedStatement s = con.prepareStatement("SELECT * FROM points WHERE userid = '"+user.getId()+"' AND serverid = '"+guildId+"'");
				ResultSet r = s.executeQuery();
				
				if(r.next()) {
					feathers = r.getInt("points");
					
					sendEmbed(user, feathers, event);
				}else {
					sendEmbed(user, feathers, event);
				}
			}catch(Exception e) {
				System.out.println(e);
			}
		}
		try {
			Connection con = main.getConnection();
			PreparedStatement s = con.prepareStatement("SELECT * FROM points WHERE userid = '"+userId+"' AND serverid = '"+guildId+"'");
			ResultSet r = s.executeQuery();
			
			if(r.next()) {
				feathers = r.getInt("points");
				
				sendEmbed(member, feathers, event);
			}else {
				sendEmbed(member, feathers, event);
			}
		}catch(Exception e) {
			System.out.println(e);
		}	
	}
	
	public void sendEmbed(MessageChannel channel, Member member, int feathers) {
		EmbedBuilder b = new EmbedBuilder();
		b.setTitle(""+member.getEffectiveName()+"'s Profile");
		b.setAuthor(member.getUser().getAsTag(), member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl());
		b.setThumbnail(member.getUser().getAvatarUrl());
		b.setColor(Color.decode(config.getString("embed")));
		b.addField("Joined Discord", ""+member.getTimeCreated().format(DateTimeFormatter.ISO_LOCAL_DATE)+" at "+member.getTimeCreated().format(DateTimeFormatter.ISO_LOCAL_TIME)+"", true);
		b.addField("Joined Server", ""+member.getTimeJoined().format(DateTimeFormatter.ISO_LOCAL_DATE)+" at "+member.getTimeJoined().format(DateTimeFormatter.ISO_LOCAL_TIME)+"", true);
		b.addField("\t"+member.getEffectiveName()+"'s roles", ""+roles(member)+"", false);
		b.addField("Points", ""+feathers+" Feathers", false);
		channel.sendMessageEmbeds(b.build()).queue();
	}
	
	public void sendEmbed(Member member, int feathers, SlashCommandEvent e) {
		EmbedBuilder b = new EmbedBuilder();
		b.setTitle(""+member.getEffectiveName()+"'s Profile");
		b.setAuthor(member.getUser().getAsTag(), member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl());
		b.setThumbnail(member.getUser().getAvatarUrl());
		b.setColor(Color.decode(config.getString("embed")));
		b.addField("Joined Discord", ""+member.getTimeCreated().format(DateTimeFormatter.ISO_LOCAL_DATE)+" at "+member.getTimeCreated().format(DateTimeFormatter.ISO_LOCAL_TIME)+"", true);
		b.addField("Joined Server", ""+member.getTimeJoined().format(DateTimeFormatter.ISO_LOCAL_DATE)+" at "+member.getTimeJoined().format(DateTimeFormatter.ISO_LOCAL_TIME)+"", true);
		b.addField("\t"+member.getEffectiveName()+"'s roles", ""+roles(member)+"", false);
		b.addField("Points", ""+feathers+" Feathers", false);
		e.replyEmbeds(b.build()).queue();
	}
	
	public String roles(Member member) {
		List<Role> roleInput = member.getRoles();
		String roles = "";
		int openP = 0, closedP = 0;
		
		if(!roleInput.isEmpty()) {
			for(int i = 0; i < roleInput.size(); i++) {
				for(int j = 0; j < roleInput.get(i).toString().length(); j++) {
					openP = roleInput.get(i).toString().indexOf('(');
					closedP = roleInput.get(i).toString().indexOf(')');
					}
				roles += ("<@&"+roleInput.get(i).toString().substring(openP + 1, closedP)+"> ");
				roles = roles.replaceAll("id=", "");
				}
			roles = roles.substring(0, roles.length() - 1);
			}else roles = "none";
		
		return roles;
	}
}
