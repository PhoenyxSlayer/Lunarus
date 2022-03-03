package com.phoenyx.lunarus.commands.modcommands;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
//import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.phoenyx.lunarus.Lunarus;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;

public class Purge extends Command{
	public Purge() {
		this.name = "purge";
		this.aliases = new String[] {"clean", "clear", "c", "pr"};
	}
	
	JSONObject config = Lunarus.config;
	
	public void execute(CommandEvent e) {
		String args[] = e.getArgs().split(" ");
		int num = 0;
		Member member = e.getMember(), errorAuthor = e.getSelfMember();
		TextChannel channel = e.getTextChannel();
		
		if(!member.hasPermission(Permission.MESSAGE_MANAGE)) return;

		e.getMessage().delete().queue();
		try {
			TimeUnit.MILLISECONDS.sleep(150);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		try {
			num = Integer.parseInt(args[0]);
		}catch(NumberFormatException err) {
			commandError(errorAuthor, channel);
			return;
		}
		clearMessages(num, channel, errorAuthor);
	}
	
	public void clearMessages(int num, TextChannel channel, Member author) {
		MessageHistory history = new MessageHistory(channel);
		List<Message> msgs;
		OffsetDateTime time = OffsetDateTime.now();
		ArrayList<Message> toDelete = new ArrayList<>();

		if(num < 2 || num > 100) {
			commandError(author, channel);
			return;
		}
		
		msgs = history.retrievePast(num).complete();
		
		for(int i = 0; i < msgs.size(); i++) {
			if(msgs.isEmpty()) {
				return;
			}else if(msgs.get(i).getTimeCreated().isBefore(time.minusWeeks(2))) {
				toDelete.add(msgs.get(i));
			}
		}
		
		for(int i = 0; i < toDelete.size(); i++) {
			msgs.remove(toDelete.get(i));
		}
		if(msgs.isEmpty() || msgs.size() < 2) {
			channel.sendMessage("``Not enough messages to clean this channel (2 or more messages that are less than or equal to 2 weeks old are required for me to clear this channel)``").queue();
			return;
		}
		channel.deleteMessages(msgs).queue();
		channel.sendMessage("Cleared "+msgs.size()+" messages!").queue(m ->{
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			m.delete().queue();
		});
	}
	
	private void commandError(Member author, TextChannel channel) {
		EmbedBuilder b = new EmbedBuilder();
		String aliases = "";
		
		for(int i = 0; i < this.aliases.length; i++) aliases += ""+this.aliases[i]+", ";
		aliases = aliases.substring(0, aliases.length()-2);
		
		b.setTitle("Command Execution Error");
		b.setAuthor(author.getEffectiveName(), author.getUser().getAvatarUrl(), author.getUser().getAvatarUrl());
		b.setColor(Color.RED);
		b.setDescription("There was an error when running this command");
		b.addField("Possible Reasons", "•\t***Argument entered is not a number or no number was entered***\n •\t***The number entered is outside the range of deleteable messages***", false);
		b.addField("Example Usage", ""+config.getString("prefix")+"purge [# of messages] (2-100)", true);
		b.addField("Aliases", aliases.toString(), true);
		channel.sendMessage(b.build()).queue();
	}
}
