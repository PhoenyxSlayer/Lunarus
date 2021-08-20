package com.phoenyx.lunarus.utils;

import java.util.HashMap;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class TimeoutHandler {
	private HashMap<Member, Message> timeout = new HashMap<Member, Message>();
	private HashMap<Member, Message> dailyTimeout = new HashMap<Member, Message>();
	private int time;
	
	public void setTime(int timeout) {
		time = timeout;
		return;
	}
	
	public void timeoutCheck(Member member, Message msg, Guild guild) {
		if(member == guild.getSelfMember()) return;
		if(member.getRoles().contains(guild.getRoleById("838621059396665384"))) return;
		
		if(time == 1 && timeout.containsKey(member)) {
			return;
		}else if (time == 1 && !timeout.containsKey(member)){
			timeout.put(member, msg);
			System.out.println(timeout.toString());
		}
		
		if(time == 24 && dailyTimeout.containsKey(member)) {
			return;
		}else if(time == 24 && !dailyTimeout.containsKey(member)){
			dailyTimeout.put(member, msg);
		}
	}
	
	public HashMap<Member, Message> getMap() {
		return timeout;
	}
	
	public boolean removeTimeout(Member member, Message msg, boolean timeoutRemoved) {
		if(time == 1 && !msg.getTimeCreated().isAfter(timeout.get(member).getTimeCreated().plusMinutes(1))) {
			timeoutRemoved = false;
		}else if(time == 1 && msg.getTimeCreated().isAfter(timeout.get(member).getTimeCreated().plusMinutes(1))) {
			timeout.remove(member);
			timeoutRemoved = true;
		}

		if(time == 24 && !msg.getTimeCreated().isAfter(dailyTimeout.get(member).getTimeCreated().plusHours(24))) {
			timeoutRemoved = false;
		}else if(time == 24 && msg.getTimeCreated().isAfter(dailyTimeout.get(member).getTimeCreated().plusHours(24))) {
			timeout.remove(member);
			timeoutRemoved = true;
		}
		
		return timeoutRemoved;
	}
}