package com.phoenyx.lunarus.commands.modcommands;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONObject;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.phoenyx.lunarus.Lunarus;
import com.phoenyx.lunarus.utils.SQLConnector;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class RemoveWarn extends Command{
	public RemoveWarn() {
		this.name = "removewarn";
		this.aliases = new String[] {"remwarn", "rw"};
	}
	
	private JSONObject config = Lunarus.config;
	private static SQLConnector main = new SQLConnector();
	private int warns = 0;
	
	public void execute(CommandEvent e) {
		TextChannel channel = e.getTextChannel();
		Guild guild = e.getGuild();
		String args[] = e.getArgs().split(" ");
		String id = args[0].replaceAll("<@", "").replaceAll(">", "");
		Member user = e.getMember(), member = null, errorAuthor = e.getSelfMember();
		
		id = id.replaceAll("!", "");
		
		try {
			member = e.getGuild().getMemberById(id);
			if(!guild.getMembers().contains(member)) {
				commandError(errorAuthor, channel);
				return;
			}
		}catch(Exception er) {
			commandError(errorAuthor, channel);
			return;
		}
		
		if(!user.hasPermission(Permission.MANAGE_ROLES)) {
			channel.sendMessage("You do not have permission to use this command").queue();
			return; 
		}
		if(args[0].isEmpty()) {
			channel.sendMessage("You must mention a user or provide a user's Id to use this command").queue();
			return;
		}
		
		removeWarns(member, guild);
		embed(user, member, channel, guild);
		e.getMessage().delete().queue();
	}
	
	public void removeWarns(Member member, Guild guild) {
		String sql;
		Role warning1 = guild.getRoleById("852365834731126814"), warning2 = guild.getRoleById("852366113576583208"), warning3 = guild.getRoleById("852366186162159616");
		
		try {
			Connection con = main.getConnection();
			PreparedStatement s = con.prepareStatement("SELECT * FROM punishments Where userid = '"+member.getId()+"' AND guildid = '"+guild.getId()+"'");
			ResultSet r = s.executeQuery();
			if(!r.next()) {
				return;
			}else {
				warns = r.getInt("warnings");
				if(warns == 3) {
					guild.removeRoleFromMember(member, warning3).queue();
				}else if(warns == 2) {
					guild.removeRoleFromMember(member, warning2).queue();
				}else {
					guild.removeRoleFromMember(member, warning1).queue();
				}
			}
				sql = "UPDATE punishments SET warnings = "+(warns-1)+" where userid = '"+member.getId()+"' AND guildid = '"+guild.getId()+"'";
				s.executeUpdate(sql);
		}catch(Exception err) {
			err.printStackTrace();
		}
	}
	
	private void embed(Member user, Member member, TextChannel channel, Guild guild) {
		EmbedBuilder b = new EmbedBuilder();
		String imageUrl = member.getUser().getAvatarUrl();
		warns--;
		
		b.setTitle("Warnings");
		b.setAuthor(member.getEffectiveName(), imageUrl, imageUrl);
		b.setColor(Color.decode("#FFFF00"));
		b.setDescription(""+member.getEffectiveName()+" ("+member.getAsMention()+") has been warned");
		b.addField("Warning removed by", user.getEffectiveName(), true);
		b.addField("Current warnings", ""+warns+"", true);
		guild.getTextChannelById(config.getJSONObject("channels").getString("logs")).sendMessage(b.build()).queue();
		channel.sendMessage(b.build()).queue();
	}
	
	private void commandError(Member author, TextChannel channel) {
		EmbedBuilder b = new EmbedBuilder();
		
		b.setTitle("Command Execution Error");
		b.setAuthor(author.getEffectiveName(), author.getUser().getAvatarUrl(), author.getUser().getAvatarUrl());
		b.setColor(Color.RED);
		b.setDescription("There was an error when running this command");
		b.addField("Possible Reasons", "•\t***The user you were trying to remove a warn does not exist in this server***\n •\t***There was no user mentioned***", false);
		b.addField("Proper Usage", "`"+config.getString("prefix")+"removewarn [user] [reason]`", false);
		channel.sendMessage(b.build()).queue();
	}
}
