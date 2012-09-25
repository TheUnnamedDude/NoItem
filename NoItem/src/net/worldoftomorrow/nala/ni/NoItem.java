package net.worldoftomorrow.nala.ni;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.worldoftomorrow.nala.ni.CustomItems.CustomBlockLoader;
import net.worldoftomorrow.nala.ni.listeners.CommandListener;
import net.worldoftomorrow.nala.ni.listeners.DebugListeners;
import net.worldoftomorrow.nala.ni.listeners.EventListener;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NoItem extends JavaPlugin {

	private Config config;
	protected Vault vault;
	private static NoItem plugin;
	private Metrics metrics;
	public DebugListeners debugListener;

	private Map<String, List<ItemStack>> itemList = new HashMap<String, List<ItemStack>>();

	@Override
	public void onEnable() {
		plugin = this;
		this.config = new Config(this);
		new CustomBlockLoader(this).load();
		this.vault = new Vault(this);
		CommandListener cl = new CommandListener(this);
		this.getCommand("noitem").setExecutor(cl);

		Updater updater = new Updater(this);
		if (!updater.isLatest()) {
			String ch = Config.getString("PluginChannel");
			Log.info("------------------- No Item -------------------");
			Log.info("There is an update available to download!");
			if(ch.equalsIgnoreCase("main"))
			Log.info("Get it at: " + this.getDescription().getWebsite());
			else if (ch.equalsIgnoreCase("dev")) {
				Log.info("Get it at: http://ci.worldoftomorrow.net/job/NoItem/lastSuccessfulBuild/");
			} else {
				Log.info("Get it at: " + this.getDescription().getWebsite());
			}
			Log.info("-----------------------------------------------");
		}

		PluginManager pm = getServer().getPluginManager();

		pm.registerEvents(new EventListener(this), this);
		if (Config.getBoolean("Debugging")) {
			this.debugListener = new DebugListeners();
			pm.registerEvents(this.debugListener, this);
			Log.info("This plugin is in debugging mode!");
		}

		try {
			metrics = new Metrics(this);
			metrics.start();
		} catch (IOException ex) {
			Log.severe("Failed to start metrics!");
		}
	}

	@Override
	public void onDisable() {
	}

	public static NoItem getPlugin() {
		return plugin;
	}

	public Vault getVault() {
		return vault;
	}

	public Config getConfigClass() {
		return this.config;
	}

	public Map<String, List<ItemStack>> getItemList() {
		return this.itemList;
	}
}