package com.phoenyx.lunarus.events;

import com.phoenyx.lunarus.Lunarus;
import com.phoenyx.lunarus.commands.modcommands.Ban;
import com.phoenyx.lunarus.commands.modcommands.Kick;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LeaveEvent extends ListenerAdapter{
	String reason = "";
	
	public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
		String member = e.getMember().getEffectiveName(), avatar = e.getUser().getAvatarUrl();
		MessageChannel channel = e.getGuild().getTextChannelById(Lunarus.config.getJSONObject("channels").getString("welcome"));
		String imageUrl = "https://cdn.discordapp.com/emojis/750822449330913301.png?v=1";
		
		if(Ban.getReason(reason) == null || Kick.getReason(reason) == null) {
			sendEmbed(member, avatar, channel, imageUrl);
		}else {
			return;
		}
	}
	
	private void sendEmbed(String member, String avatar, MessageChannel channel, String image) {
		EmbedBuilder b = new EmbedBuilder();
		
		b.setTitle("Hate to see you go...");
		b.setAuthor(member, image, image);
		b.setThumbnail(avatar);
		b.setDescription("Hope We'll see you again someday, "+member+"");
		channel.sendMessageEmbeds(b.build()).queue();
	}
}
