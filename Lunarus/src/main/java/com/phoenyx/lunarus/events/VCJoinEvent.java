package com.phoenyx.lunarus.events;

import com.phoenyx.lunarus.Lunarus;

import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VCJoinEvent extends ListenerAdapter{
	@SuppressWarnings("unlikely-arg-type")
	public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
		TextChannel channel = e.getGuild().getTextChannelById(Lunarus.config.getJSONObject("channels").getString("general-text"));
		IPermissionHolder memberInVoice = e.getVoiceState().getMember();
		if(e.getChannelJoined().equals("General")) {
			channel.createPermissionOverride(memberInVoice);
		}
	}
}
