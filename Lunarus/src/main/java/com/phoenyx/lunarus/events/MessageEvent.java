package com.phoenyx.lunarus.events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageEvent extends ListenerAdapter{
	public void onMessageRecieved(MessageReceivedEvent event) {
		String message = event.getMessage().getContentDisplay();
		
		System.out.println(message);
	}
}
