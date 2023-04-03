package com.phoenyx.lunarus.commands.modcommands;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.phoenyx.lunarus.Lunarus;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class Purge extends SlashCommand{
	public Purge() {
		this.name = "clear";
		this.help = "Clear last number of messages from chat.";
		this.guildOnly = false;
		this.aliases = new String[] {"clean", "purge", "c"};
		ArrayList<OptionData> options = new ArrayList<OptionData>();
		options.add(new OptionData(OptionType.INTEGER, "num", "Number of messages to delete").setRequired(true));
		this.options = options;
	}
	
	JSONObject config = Lunarus.config;
	
	public void execute(CommandEvent e) {
		String args[] = e.getArgs().split(" ");
		int num = 0;
		Member member = e.getMember(), errorAuthor = e.getSelfMember();
		MessageChannel channel = e.getChannel();
		
		if(!member.hasPermission(Permission.MESSAGE_MANAGE)) return;

		e.getMessage().delete().queue();
		try {
			TimeUnit.MILLISECONDS.sleep(200);
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
	
	public void execute(SlashCommandEvent e) {
		int num = (int)e.optDouble("num");
		Member member = e.getMember();
		MessageChannel channel = e.getChannel();
		
		if(!member.hasPermission(Permission.MESSAGE_MANAGE)) return;
		
		clearMessages(num, channel, e);
	}
	
	public void clearMessages(int num, MessageChannel channel, Member author) {
		MessageHistory history = new MessageHistory(channel);
		List<Message> msgs;
		OffsetDateTime time = OffsetDateTime.now();
		ArrayList<Message> toDelete = new ArrayList<>();

		if(num > 100) {
			commandError(author, channel);
			return;
		}
		
		msgs = history.retrievePast(num).complete();
		
		for(int i = 0; i < msgs.size(); i++) {
			if(msgs.isEmpty()) {
				return;
			}else if(msgs.get(i).getTimeCreated().getDayOfYear() <= time.minusWeeks(2).getDayOfYear()) {
				toDelete.add(msgs.get(i));
			}
		}
		
		for(int i = 0; i < toDelete.size(); i++) {
			msgs.remove(toDelete.get(i));
		}
		channel.purgeMessages(msgs);
		channel.sendMessage("Cleared "+msgs.size()+" messages!").queue(m ->{
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			m.delete().queue();
		});
	}
	
	public void clearMessages(int num, MessageChannel channel, SlashCommandEvent ev) {
		MessageHistory history = new MessageHistory(channel);
		List<Message> msgs;
		OffsetDateTime time = OffsetDateTime.now();
		ArrayList<Message> toDelete = new ArrayList<>();

		if(num > 100) {
			commandError(ev);
			return;
		}
		
		msgs = history.retrievePast(num).complete();
		
		for(int i = 0; i < msgs.size(); i++) {
			if(msgs.isEmpty()) {
				return;
			}else if(msgs.get(i).getTimeCreated().getDayOfYear() <= time.minusWeeks(2).getDayOfYear()) {
				toDelete.add(msgs.get(i));
			}
		}
		
		for(int i = 0; i < toDelete.size(); i++) {
			msgs.remove(toDelete.get(i));
		}
		channel.purgeMessages(msgs);
		ev.reply("Cleared "+msgs.size()+" messages!").setEphemeral(true).queue(hook -> hook.deleteOriginal().queueAfter(5, TimeUnit.SECONDS));
	}
	
	private void commandError(Member author, MessageChannel channel) {
		EmbedBuilder b = new EmbedBuilder();
		String aliases = "";
		
		for(int i = 0; i < this.aliases.length; i++) aliases += ""+this.aliases[i]+", ";
		aliases = aliases.substring(0, aliases.length()-2);
		
		b.setTitle("Command Execution Error");
		b.setAuthor(author.getEffectiveName(), author.getUser().getAvatarUrl(), author.getUser().getAvatarUrl());
		b.setColor(Color.RED);
		b.setDescription("There was an error when running this command");
		b.addField("Possible Reasons", "•\t***Argument entered is not a number or no number was entered***\n •\t***The number entered is outside the range of deleteable messages***", false);
		b.addField("Example Usage", ""+config.getString("prefix")+"clear [# of messages] (1-100)", true);
		b.addField("Aliases", aliases.toString(), true);
		channel.sendMessageEmbeds(b.build()).queue();
	}
	
	private void commandError(SlashCommandEvent e) {
		EmbedBuilder b = new EmbedBuilder();
		String aliases = "";
		
		for(int i = 0; i < this.aliases.length; i++) aliases += ""+this.aliases[i]+", ";
		aliases = aliases.substring(0, aliases.length()-2);
		
		b.setTitle("Command Execution Error");
		b.setColor(Color.RED);
		b.setDescription("There was an error when running this command");
		b.addField("Possible Reasons", "•\t***Argument entered is not a number or no number was entered***\n •\t***The number entered is outside the range of deleteable messages***", false);
		b.addField("Example Usage", "/purge [# of messages] (1-100)", true);
		e.replyEmbeds(b.build()).setEphemeral(true).queue();
	}
}
