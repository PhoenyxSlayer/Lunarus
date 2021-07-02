package com.phoenyx.lunarus.commands.modcommands;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.phoenyx.lunarus.Lunarus;
import com.phoenyx.lunarus.utils.SQLConnector;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Kick extends Command{
	public Kick() {
		this.name = "kick";
	}
	
	public static String reason = "";
	private static SQLConnector main = new SQLConnector();
	private JSONObject config = Lunarus.config;
	
	protected void execute(CommandEvent e) {
		TextChannel channel = e.getTextChannel();
		String args[] = e.getArgs().split(" "), avatar = e.getMember().getUser().getAvatarUrl(), embedChannel = Lunarus.config.getJSONObject("channels").getString("welcome");
		String id = args[0].replaceAll("<@!", "").replaceAll(">", "");
		Member user = e.getMember(), member = null, errorAuthor = e.getGuild().getMemberById(config.getString("lunarus"));
		Guild guild = e.getGuild();

		try {
			member = guild.getMemberById(id);
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
		
		if(!e.getSelfMember().canInteract(member)) {
			channel.sendMessage("I cannot kick members with a role higher than or equal to my own").queue();
		}else if(!user.hasPermission(Permission.KICK_MEMBERS)) {
			channel.sendMessage("You do not have the proper permissions to use this command!").queue();
		}else if(member.hasPermission(Permission.KICK_MEMBERS) || member.hasPermission(Permission.BAN_MEMBERS)){
			channel.sendMessage("I cannot kick this user!").queue();
		}else {
			try {
				Connection con = main.getConnection();
				PreparedStatement s = con.prepareStatement("UPDATE punishments SET warnings = "+(0)+", kicks = "+(1)+" where userid = '"+member.getId()+"' AND guildid = '"+guild.getId()+"'");
				s.executeUpdate();
			}catch(Exception error) {
				error.printStackTrace();
				e.getGuild().getMemberById("329448703171756033").getUser().openPrivateChannel().flatMap(c -> c.sendMessage(error.toString())).queue();
			}
			embed(member, user, guild, embedChannel, avatar, reason);
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			member.kick().queue();
		}
	}
	
	public static String getReason(String a) {
		a = reason;
		return a;
	}

	public void embed(Member member, Member user, Guild guild, String channel, String avatar, String reason) {
		EmbedBuilder b = new EmbedBuilder();
		
		 b.setTitle("Follow the rules next time!");
		 b.setAuthor(member.getEffectiveName(), avatar, avatar);
		 b.setColor(Color.decode(config.getString("embed")));
		 b.setDescription(""+member.getEffectiveName()+" has been banned");
		 b.addField("Kicked By", user.getEffectiveName(), true);
		 b.addField("Reason", reason, true);
		 guild.getTextChannelById(channel).sendMessage(b.build()).queue(r -> {
			 EmbedBuilder n = new EmbedBuilder();
			 n.setTitle("Kicked");
			 n.setAuthor(member.getEffectiveName(), avatar, avatar);
			 n.setColor(Color.decode(config.getString("embed")));
			 n.setDescription("You were kicked from "+guild.getName()+"");
			 n.addField("Kicked By", user.getEffectiveName(), true);
			 n.addField("Reason", reason, true);
			 guild.getTextChannelById(config.getJSONObject("channels").getString("logs")).sendMessage(b.build()).queue();
			 guild.getMemberById(member.getId()).getUser().openPrivateChannel().flatMap(c -> c.sendMessage(n.build())).queue();
		 });
	}
	
	private void commandError(Member author, TextChannel channel) {
		EmbedBuilder b = new EmbedBuilder();
		
		b.setTitle("Command Execution Error");
		b.setAuthor(author.getEffectiveName(), author.getUser().getAvatarUrl(), author.getUser().getAvatarUrl());
		b.setColor(Color.RED);
		b.setDescription("There was an error when running this command");
		b.addField("Possible Reasons", "•\t***The user you were trying to kick does not exist in this server***\n •\t***There was no user mentioned***\n •\t***A reason for kicking this user was not specified***", false);
		b.addField("Proper Usage", ""+config.getString("prefix")+"kick [user] [reason]", false);
		channel.sendMessage(b.build()).queue();
	}
}
