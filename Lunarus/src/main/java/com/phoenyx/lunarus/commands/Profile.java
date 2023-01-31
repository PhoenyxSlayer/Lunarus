package com.phoenyx.lunarus.commands;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.JSONObject;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.phoenyx.lunarus.Lunarus;
import com.phoenyx.lunarus.utils.SQLConnector;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class Profile extends Command{
	private SQLConnector main = new SQLConnector();
	private JSONObject config = Lunarus.config;
	
	public Profile() {
		this.name = "profile";
		this.aliases = new String[] {"p"};
	}
	@Override
	protected void execute(CommandEvent event) {
		Member member = event.getMember();
		String userId = event.getMember().getId();
		String guildId = event.getGuild().getId();
		MessageChannel channel = event.getChannel();
		int feathers = 0;
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
				}
			roles = roles.substring(0, roles.length() - 1);
			}else roles = "none";
		
		return roles;
	}
}
