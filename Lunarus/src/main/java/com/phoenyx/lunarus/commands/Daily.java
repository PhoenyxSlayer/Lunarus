package com.phoenyx.lunarus.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.TextChannel;

public class Daily extends Command{
	public Daily(){
		this.name = "daily";
	}
	
	public void execute(CommandEvent e) {
		TextChannel channel = e.getTextChannel();
		
		channel.sendMessage("You have recived you daily 500 Feathers!").queue();
	}
}
