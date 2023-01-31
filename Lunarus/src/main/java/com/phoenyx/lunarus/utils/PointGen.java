package com.phoenyx.lunarus.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class PointGen {
	private static SQLConnector main = new SQLConnector();
	
	public static void distribute(Member member, Guild guild) {
		String sql;
		int points = 0;
		double	recieved = 0;
		recieved = generatePoints();
		
		try {
			Connection con = main.getConnection();
			PreparedStatement s = con.prepareStatement("SELECT * FROM points WHERE userid = '"+member.getId()+"' AND serverid = '"+guild.getId()+"'");
			ResultSet r = s.executeQuery();
			
			if(!r.next()) {
				sql = "INSERT INTO points (user, userid, server, serverid, points) VALUES ('"+member.getEffectiveName()+"', '"+member.getId()+"', '"+guild.getName()+"', '"+guild.getId()+"', "+recieved+")";
				s.executeUpdate(sql);
			}else {
				points = r.getInt("points");
				sql = "UPDATE points SET points = "+(points + recieved)+" where userid = '"+member.getId()+"' AND serverid = '"+guild.getId()+"'";
				s.executeUpdate(sql);
			}
			System.out.println(recieved);
		}catch(Exception err) {
			err.printStackTrace();
		}
	}
	
	public static double generatePoints() {
		double min = 10;
		double max = 50;
		
		return  Math.floor(Math.random() * (max - min + 1)) + min;
	}
}
