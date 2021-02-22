package com.phoenyx.lunarus.events;

import com.phoenyx.lunarus.Lunarus;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinEvent extends ListenerAdapter{
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		String member = e.getMember().getEffectiveName(), avatar = e.getUser().getAvatarUrl(), channel = Lunarus.config.getJSONObject("channels").getString("welcome");
		String imageUrl = "https://cdn.discordapp.com/emojis/472492256973160463.png?v=1";
		String server = e.getGuild().getName();
		int memberCount = e.getGuild().getMemberCount();
		EmbedBuilder b = new EmbedBuilder();
		
		b.setTitle("Welcome To "+server+"");
		b.setAuthor(member, imageUrl, imageUrl);
		b.setThumbnail(avatar);
		//b.setColor(Color.decode(embed));
		b.setDescription("Make yourself at home! I hope you'll enjoy your stay and behave!");
		b.addField("Who Joined? ", ""+e.getUser().getAsMention()+"\n They are the "+memberCount+"th member", false);
		b.addField("What to do now?", "**1.** Read the <#811742689183268915>\n**2.** Pick your roles in the <#811742807492395058> (optional)\n**3.** Post an intro in <#811742849212088370> (optional)\n**4.** Make friends, have fun, be nice!", false);
		e.getGuild().getTextChannelById(channel).sendMessage(b.build()).queue();
	}
}
