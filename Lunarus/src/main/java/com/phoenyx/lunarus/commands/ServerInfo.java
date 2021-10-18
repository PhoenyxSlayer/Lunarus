package com.phoenyx.lunarus.commands;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ServerInfo extends Command{
	public ServerInfo() {
		this.name = "say";
		this.aliases = new String[] {"s"};
	}
	
	protected void execute(CommandEvent e) {
		int memberCount = e.getGuild().getMembers().size();
		String members[] = new String[memberCount];
		
		for(int i = 0; i < memberCount; i ++) {
			members[i] = e.getGuild().getMembers().get(i).getEffectiveName();
		}
		e.getChannel().sendMessage("there are "+memberCount+" members "+Arrays.toString(members)+"").queue();
	}	
}
