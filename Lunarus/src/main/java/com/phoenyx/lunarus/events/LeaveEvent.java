package com.phoenyx.lunarus.events;

import com.phoenyx.lunarus.Lunarus;
import com.phoenyx.lunarus.commands.modcommands.Ban;
import com.phoenyx.lunarus.commands.modcommands.Kick;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LeaveEvent extends ListenerAdapter{
	String reason = "";
	
	public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
		String member = e.getMember().getEffectiveName(), avatar = e.getUser().getAvatarUrl(), channel = Lunarus.config.getJSONObject("channels").getString("welcome");
		String imageUrl = "https://cdn.discordapp.com/emojis/750822449330913301.png?v=1";
		EmbedBuilder b = new EmbedBuilder();
		
		if(!Ban.getReason(reason).isEmpty()) return;
		if(!Kick.getReason(reason).isEmpty()) return;
				 
		b.setTitle("Hate to see you go...");
		b.setAuthor(member, imageUrl, imageUrl);
		b.setThumbnail(avatar);
		b.setDescription("Hope We'll see you again someday, "+member+"");
		e.getGuild().getTextChannelById(channel).sendMessage(b.build()).queue();
	}
}
