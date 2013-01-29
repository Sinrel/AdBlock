package org.sinrel.adblock;

import org.bukkit.Bukkit;
import org.bukkit.command.defaults.BanCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdBlock extends JavaPlugin implements Listener{
	
	private List<String> regexps = new ArrayList<>();
	private List<Pattern> patterns = new ArrayList<Pattern>();
	private boolean banEnabled = false;
	private boolean kickEnabled = false;
	private boolean replaceEnabled = false;
	private String kickMessage = "";
	private String replaceText = "";
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		if (player.isOp() || player.hasPermission("adBlock.admin")) return;
		
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
		this.getServer().getPluginManager().registerEvents(this, this);
		if(!new File(this.getDataFolder(),"config.yml").exists()) createConfig();
		loadConfig();
	}
	
	private void createConfig() {
		FileConfiguration config = this.getConfig();
		List<String> regs = new ArrayList<String>();
		regs.add(".*[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}:[0-9]{1,5}.*");
		regs.add(".*[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.*");
		regs.add(".*[a-zA-z]+\\.(com|net|ru|ua|by|info|us|uk|so|org|su|tv|kz).*");
		regs.add(".*[a-zA-z]+\\.(com|net|ru|ua|by|info|us|uk|so|org|su|tv|kz):[0-9]{1,5}.*");
		
		config.set("regulars", regs);
		config.set("banEnabled", false);
		config.set("kickEnabled", true);
		config.set("kickMessage", "commercial is not allowed in this server");
		config.set("replaceEnabled", true);
		config.set("replaceText", "commercial");
		
		saveConfig();
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
			patterns.add(Pattern.compile(s));
		}
	}
}
