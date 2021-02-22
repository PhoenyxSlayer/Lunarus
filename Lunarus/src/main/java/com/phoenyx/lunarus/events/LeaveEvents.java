package com.phoenyx.lunarus.events;

import java.util.concurrent.TimeUnit;

import com.phoenyx.lunarus.Lunarus;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LeaveEvents extends ListenerAdapter{
	public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
		String member = e.getMember().getEffectiveName(), avatar = e.getUser().getAvatarUrl(), channel = Lunarus.config.getJSONObject("channels").getString("welcome");
		String imageUrl = "https://cdn.discordapp.com/emojis/750822449330913301.png?v=1";
		//int memberCount = e.getGuild().getMemberCount();
		
		e.getGuild().retrieveAuditLogs().queueAfter(1, TimeUnit.SECONDS, logs ->{
			boolean isBan = false, isKick = false;
			
			 for (AuditLogEntry log : logs) {
	             if (log.getTargetIdLong() == e.getUser().getIdLong()) {
	                 isBan = log.getType() == ActionType.BAN;
	                 isKick = log.getType() == ActionType.KICK;
	                 break;
	             }
	         }
			 if(isBan) {
				 EmbedBuilder b = new EmbedBuilder();
					
				 b.setTitle("You won't be missed...");
				 b.setAuthor(member, imageUrl, imageUrl);
				 b.setThumbnail(avatar);
				 //b.setColor(Color.decode(embed));
				 b.setDescription(""+member+" has been banned, good riddance");
				 e.getGuild().getTextChannelById(channel).sendMessage(b.build()).queue();
			 }else if(isKick) {
				 EmbedBuilder b = new EmbedBuilder();
					
				 b.setTitle("Follow the rules next time...");
				 b.setAuthor(member, imageUrl, imageUrl);
				 b.setThumbnail(avatar);
				 //b.setColor(Color.decode(embed));
				 b.setDescription(""+member+" has been a bad boi/girl, hopefully they follow the rules next time");
				 e.getGuild().getTextChannelById(channel).sendMessage(b.build()).queue();
			 }else {
				 EmbedBuilder b = new EmbedBuilder();
				 
				 b.setTitle("Hate to see you go...");
				 b.setAuthor(member, imageUrl, imageUrl);
				 b.setThumbnail(avatar);
				 //b.setColor(Color.decode(embed));
				 b.setDescription("Hope We'll see you again someday, "+member+"");
				 e.getGuild().getTextChannelById(channel).sendMessage(b.build()).queue();
			 }
		});
	}
}
