package com.phoenyx.lunarus.events;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.phoenyx.lunarus.Lunarus;
import com.phoenyx.lunarus.utils.PointGen;
import com.phoenyx.lunarus.utils.TimeoutHandler;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageEvent extends ListenerAdapter{
	private JSONObject config = Lunarus.config;
	TimeoutHandler timeout = new TimeoutHandler();
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String message = e.getMessage().getContentRaw(); //prefix = config.getString("prefix");
		Member member = e.getMember();
		Guild guild = e.getGuild();
		TextChannel channel = e.getChannel();
		List<Role> roles = member.getRoles();
		String role;
		Message msg = e.getMessage();
		HashMap<Member, Message> time = new HashMap<Member, Message>();
		boolean timeoutRemoved = true;
		int toTime = 1;
		
		if(roles.size() == 0) {
			System.out.println("["+guild.getName()+"] ["+channel.getName()+"] "+member.getEffectiveName()+": "+message+"");
		}else {
			role = member.getRoles().get(0).toString();
			System.out.println("["+guild.getName()+"] ["+channel.getName()+"] ["+role.replaceAll("R:", "").substring(0, role.length() - 22)+"]"+member.getEffectiveName()+": "+message+"");
		}
		
		if(member == guild.getSelfMember()) return;
		if(member.getRoles().contains(guild.getRoleById("838621059396665384"))) return;
		if(message.startsWith(config.getString("prefix"))) return;
		time = timeout.getMap();
		if(time.containsKey(member)) timeoutRemoved = timeout.removeTimeout(member, msg, timeoutRemoved);
		if(time.containsKey(member) && !timeoutRemoved) return;
		PointGen.distribute(member, guild);
		timeout.setTime(toTime);
		timeout.timeoutCheck(member, msg, guild);
	}
}
