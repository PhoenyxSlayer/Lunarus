package com.phoenyx.lunarus.utils;

import java.util.HashMap;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class TimeoutHandler {
	private HashMap<Member, Message> timeout = new HashMap<Member, Message>();
	
	public void timeoutCheck(Member member, Message msg, Guild guild) {
		if(member == guild.getSelfMember()) return;
		if(member.getRoles().contains(guild.getRoleById("838621059396665384"))) return;
		
		if(timeout.containsKey(member)) {
			return;
		}else {
			timeout.put(member, msg);
		}
	}
	
	public HashMap<Member, Message> getMap() {
		return timeout;
	}
	
	public boolean removeTimeout(Member member, Message msg, boolean timeoutRemoved) {
		if(!msg.getTimeCreated().isAfter(timeout.get(member).getTimeCreated().plusMinutes(1))) {
			timeoutRemoved = false;
		}else {
			timeout.remove(member);
			timeoutRemoved = true;
		}
		
		return timeoutRemoved;
	}
}
