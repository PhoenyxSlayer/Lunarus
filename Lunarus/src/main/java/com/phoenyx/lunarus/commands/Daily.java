package com.phoenyx.lunarus.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class Daily extends SlashCommand{
	public Daily(){
		this.name = "daily";
		this.help = "collect points daily";
		this.guildOnly = false;
	}
	
	public void execute(CommandEvent e) {
		MessageChannel channel = e.getChannel();
		
		channel.sendMessage("You have recived you daily 500 Feathers!").queue();
	}
	
	public void execute(SlashCommandEvent e) {
		e.reply("You have recived you daily 500 Feathers!").queue();
	}
}
