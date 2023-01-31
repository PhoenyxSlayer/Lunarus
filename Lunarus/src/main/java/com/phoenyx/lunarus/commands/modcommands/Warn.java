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
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class Warn extends Command{
	public Warn() {
		this.name = "warn";
	}
	
	private static SQLConnector main = new SQLConnector();
	JSONObject config = Lunarus.config;
	private int warns = 0;
	
	public void execute(CommandEvent e) {
		MessageChannel channel = e.getChannel();
		Guild guild = e.getGuild();
		String args[] = e.getArgs().split(" ");
		String id = args[0].replaceAll("<@", "").replaceAll(">", ""), reason = "";
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
		

		for(int i = 1; i < args.length; i++) reason += ""+args[i]+" ";
		
		if(reason.isEmpty()) {
			commandError(errorAuthor, channel);
			return;
		}
		
		if(!user.hasPermission(Permission.MANAGE_ROLES)) {
			channel.sendMessage("You do not have permission to use this command").queue();
			return; 
		}
		
		if(member.hasPermission(Permission.MANAGE_ROLES)) {
			channel.sendMessage("You do not have the permissions to warn this user!").queue();
			return;
		}
		
		addWarns(member, guild);
		warnEmbed(user, member, reason, channel, guild);
		e.getMessage().delete().queue();
	}
	
	private void addWarns(Member member, Guild guild) {
		String sql;
		Role warning1 = guild.getRoleById("852365834731126814"), warning2 = guild.getRoleById("852366113576583208"), warning3 = guild.getRoleById("852366186162159616");
		
		try {
			Connection con = main.getConnection();
			PreparedStatement s = con.prepareStatement("SELECT * FROM punishments Where userid = '"+member.getId()+"' AND guildid = '"+guild.getId()+"'");
			ResultSet r = s.executeQuery();
			
			if(!r.next()) {
				warns = 0;
				guild.addRoleToMember(member, warning1).queue();
				sql = "INSERT INTO punishments (user, userid, guild, guildid, warnings, kicks) VALUES ('"+member.getEffectiveName()+"', '"+member.getId()+"', '"+guild.getName()+"', '"+guild.getId()+"', "+1+", "+0+")";
				s.executeUpdate(sql);
			}else {
				warns = r.getInt("warnings");
				if(warns == 1) {
					guild.addRoleToMember(member, warning2).queue();
				}else {
					guild.addRoleToMember(member, warning3).queue();
				}
				sql = "UPDATE punishments SET warnings = "+(warns+1)+" where userid = '"+member.getId()+"' AND guildid = '"+guild.getId()+"'";
				s.executeUpdate(sql);
			}
		}catch(Exception err) {
			err.printStackTrace();
		}
	}
	
	private void warnEmbed(Member user, Member member, String reason, MessageChannel channel, Guild guild) {
		EmbedBuilder b = new EmbedBuilder();
		String imageUrl = member.getUser().getAvatarUrl();
		warns++;
		
		b.setTitle("Warnings");
		b.setAuthor(member.getEffectiveName(), imageUrl, imageUrl);
		b.setColor(Color.decode("#FFFF00"));
		b.setDescription(""+member.getEffectiveName()+" ("+member.getAsMention()+") has been warned");
		b.addField("Warned by", user.getEffectiveName(), true);
		b.addField("Reason", reason, true);
		b.addField("Warning #", ""+warns+"", false);
		guild.getTextChannelById(config.getJSONObject("channels").getString("logs")).sendMessageEmbeds(b.build()).queue();
		channel.sendMessageEmbeds(b.build()).queue();
	}
	
	private void commandError(Member author, MessageChannel channel) {
		EmbedBuilder b = new EmbedBuilder();
		
		b.setTitle("Command Execution Error");
		b.setAuthor(author.getEffectiveName(), author.getUser().getAvatarUrl(), author.getUser().getAvatarUrl());
		b.setColor(Color.RED);
		b.setDescription("There was an error when running this command");
		b.addField("Possible Reasons", "•\t***The user you were trying to warn does not exist in this server***\n •\t***There was no user mentioned***\n •\t***A reason for this warning was not specified***", false);
		b.addField("Proper Usage", "`"+config.getString("prefix")+"warn [user] [reason]`", false);
		channel.sendMessageEmbeds(b.build()).queue();
	}
}
