package com.phoenyx.lunarus.events;

import org.json.JSONObject;

import com.phoenyx.lunarus.Lunarus;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinEvent extends ListenerAdapter{
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		JSONObject channel = Lunarus.config.getJSONObject("channels");
		String member = e.getMember().getEffectiveName(), avatar = e.getUser().getAvatarUrl();
		String imageUrl = "https://cdn.discordapp.com/emojis/472492256973160463.png?v=1";
		String server = e.getGuild().getName();
		int memberCount = e.getGuild().getMemberCount();
		EmbedBuilder b = new EmbedBuilder();
		Member m = e.getMember();
		Role role = e.getGuild().getRoleById("848442271128092702");
		
		b.setTitle("Welcome to "+server+"");
		b.setAuthor(member, imageUrl, imageUrl);
		b.setThumbnail(avatar);
		//b.setColor(Color.decode(embed));
		b.setDescription("Make yourself at home! I hope you'll enjoy your stay and behave!");
		b.addField("Who Joined? ", e.getUser().getAsMention(), true);
		b.addField("Member #", ""+memberCount+"", true);
		b.addField("What to do now?", "**1.** Read the <#"+channel.getString("rules")+">\n**2.** Pick your roles in the <#"+channel.getString("roles")+"> (optional)\n**3.** Post an intro in <#"+channel.getString("introductions")+"> (optional)\n**4.** Make friends, have fun, be nice!", false);
		e.getGuild().getTextChannelById(channel.getString("welcome")).sendMessage(b.build()).queue();
		
		e.getGuild().addRoleToMember(m, role).queue();
	}
}
