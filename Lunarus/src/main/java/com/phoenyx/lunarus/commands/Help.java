package com.phoenyx.lunarus.commands;

import java.awt.Color;
import java.util.Arrays;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.phoenyx.lunarus.Lunarus;
import com.phoenyx.lunarus.commands.modcommands.Purge;
import com.phoenyx.lunarus.commands.modcommands.RemoveWarn;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Help extends Command{
	
	public Help() {
		this.name = "help";
		this.aliases = new String[] {"h", "?"};
	}
	
	Color color = Color.decode("#00FFFF");
	String prefix = Lunarus.config.getString("prefix");
	Purge purge = new Purge();
	RemoveWarn rw = new RemoveWarn();
	
	protected void execute(CommandEvent e) {
		TextChannel channel = e.getTextChannel();
		Member member = e.getGuild().getMemberById("720071761403838466");
		String args[] = e.getArgs().split(" "), help = args[0];
		
		switch(help) {
			case "fun":
				funHelp(channel, member);
				break;
			case "moderation":
				modHelp(channel, member);
				break;
			default:
				help(channel, member);
				break;
		}
	}
	
	public void help(TextChannel channel, Member member) {
		EmbedBuilder b = new EmbedBuilder();
		
		b.setAuthor(member.getEffectiveName());
		b.setThumbnail(member.getUser().getAvatarUrl());
		b.setColor(color);
		b.setTitle("Lunarus Commands");
		b.setDescription("Here's all of the commands you can use:");
		b.addField("__Help__", "**Usage:** `"+prefix+"help`\n**Description:** Shows this help message.\n**Aliases:** "+Arrays.toString(this.aliases)+"", false);
		b.addField("__Fun/Gambling Commands__", "**Usage:** `"+prefix+"help fun`\n**Description:** Shows the list of all the fun commands members can use.", false);
		b.addField("__Mod Commands__", "**Usage:** `"+prefix+"help moderation`\n**Description:** Shows the list off all the commands for moderating the server.", false);
		
		channel.sendMessage(b.build()).queue();
	}
	
	public void funHelp(TextChannel channel, Member member) {
		EmbedBuilder b = new EmbedBuilder();
		
		b.setAuthor(member.getEffectiveName());
		b.setThumbnail(member.getUser().getAvatarUrl());
		b.setColor(color);
		b.setTitle("Fun Help");
		b.setDescription("Here are all the fun commands that every member can use:");
		b.addField("Coming Soon!", "These commands are not yet available and will be coming in a future update!", false);
		
		channel.sendMessage(b.build()).queue();
	}
	
	public void modHelp(TextChannel channel, Member member) {
		EmbedBuilder b = new EmbedBuilder();
		
		b.setAuthor(member.getEffectiveName());
		b.setTitle("Mod Help");
		b.setThumbnail(member.getUser().getAvatarUrl());
		b.setColor(color);
		b.setDescription("Hare are all of the Mod/Admin only commands:");
		b.addField("__Purge__", "**Usage:** `"+prefix+"purge [# of messages]`\n**Description:** Deletes the specified amount of messages from a text channel as long as the messages are 12 weeks old or less.\n**Aliases:** "+Arrays.toString(purge.getAliases())+"", false);
		b.addField("__Ban__", "**Usage:** `"+prefix+"ban [@member] [reason]`\n**Description:** Bans the mentioned member", false);
		b.addField("__Kick__", "**Usage:** `"+prefix+"`kick [@member] [reason]\n Kicks the mentioned member", false);
		b.addField("__Warn__", "**Usage;** `"+prefix+"warn [@member] [reason]`\n**Description:** Adds a warning to the mentioned member", false);
		b.addField("__Remove Warn__", "**Usage:** `"+prefix+"removewarn [@member]`\n**Description:** Removes a warning from the mentioned user\n**Aliases:** "+Arrays.toString(rw.getAliases())+"", false);
		
		channel.sendMessage(b.build()).queue();
	}
}
