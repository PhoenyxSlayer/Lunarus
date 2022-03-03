package com.phoenyx.lunarus.events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageEvent extends ListenerAdapter{
	
	public void onMessageReceived(MessageReceivedEvent e) {
		String message = e.getMessage().getContentRaw();
		
		System.out.println(message);
	}

}
