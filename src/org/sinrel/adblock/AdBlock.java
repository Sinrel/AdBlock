package org.sinrel.adblock;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdBlock extends JavaPlugin implements Listener{
	
	List<String> regexps = new ArrayList<>();
	List<Pattern> patterns = new ArrayList<Pattern>();
	boolean banEnabled = false;
	boolean kickEnabled = false;
	boolean replaceEnabled = false;
	String kickMessage = "";
	String replaceText = "";
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		for(Pattern p : patterns){
			Matcher m = p.matcher(event.getMessage());
			if(m.matches()){		
				if(replaceEnabled){
					event.setMessage(replaceText);
				}
				
				if(banEnabled){
					player.setBanned(true);
					player.kickPlayer(kickMessage);
					return;
				}else if(kickEnabled){
					player.kickPlayer(kickMessage);
					return;
				}
			}
		}
	}

	@Override
	public void onEnable() {
		loadConfig();
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	private void loadConfig()
	{
		FileConfiguration config = this.getConfig();
		regexps = config.getStringList("regulars");
		banEnabled = config.getBoolean("banEnabled", false);
		kickEnabled = config.getBoolean("kickEnabled", false);
		kickMessage = config.getString("kickMessage", "");
		replaceEnabled = config.getBoolean("replaceEnabled", false);
		replaceText = config.getString("replaceText", "");
		
		if(regexps == null) return;
		for(String s : regexps){
			System.out.println(s);
			patterns.add(Pattern.compile(s));
		}
	}
}
