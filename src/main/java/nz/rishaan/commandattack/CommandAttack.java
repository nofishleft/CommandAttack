package nz.rishaan.commandattack;

import nz.rishaan.commandattack.util.ArraySlice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Map;

public final class CommandAttack extends JavaPlugin implements Listener {

	String m_prefix = ChatColor.GOLD + "[CA] ";
	String SUDO_PERMISSION = "ca.sudo";
	CAStorage storage = new CAStorage();

	@Override
	public void onEnable() {
		// Plugin startup logic
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		storage.loadData(this);
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
		storage.saveData(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (command.getName().toLowerCase()) {
			case "ca":
				return processCommand_ca(sender, args);
			case "careload":
				return processCommand_careload(sender, args);
			case "caremove":
				return processCommand_caremove(sender, args);
			case "calist":
				return processCommand_calist(sender, args);
			default:
				return false;
		}
	}

	@EventHandler
	public void onAttackEvent(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			Player attacker = (Player) e.getDamager();
			Player target = (Player) e.getEntity();

			ItemCommand command = storage.commands.getOrDefault(
					attacker.getInventory().getItemInMainHand().getType(), null);

			if (command != null) {
				if (!command.m_sudoRequired || attacker.hasPermission(SUDO_PERMISSION)) {
					String cmd = command.interpolate(new AttackData(attacker, target, e));
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
				}
			}
		}
	}

	public void msg(CommandSender sender, String message) {
		sender.sendMessage(m_prefix + message);
	}

	public boolean processCommand_calist(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission("ca.list")) {
				if (args.length != 0) {
					msg(sender, "Found " + args.length + " arguments, but was expecting none, ignoring arguments.");
				}

				Material m = p.getInventory().getItemInMainHand().getType();
				ItemCommand cmd = storage.commands.getOrDefault(m, null);

				if (cmd == null) {
					msg(sender, "No commands for item '" + m.toString() + "'");
				} else {
					msg(sender, "Found command for item '" + m.toString() + "':\n" + "ID: " + ChatColor.DARK_GREEN + cmd.m_id + ChatColor.GOLD + "\nCommand: " + ChatColor.DARK_GREEN + cmd.template);
				}
				return true;
			} else {
				msg(sender, "You don't have permission for that command.");
				return false;
			}
		} else {
			msg(sender, "You need to be a player to use this command.");
			return false;
		}
	}

	public boolean processCommand_careload(CommandSender sender, String[] args) {
		if (sender instanceof Player && !sender.hasPermission("ca.reload")) {
			msg(sender, "You don't have permission for that command.");
			return false;
		} else {
			if (args.length != 0) {
				msg(sender, "Found " + args.length + " arguments, but was expecting none, ignoring arguments.");
			}

			msg(sender, "Not yet implemented!");
			return true;
		}
	}

	public boolean processCommand_caremove(CommandSender sender, String[] args) {
		if (sender instanceof Player && !sender.hasPermission("ca.remove")) {
			msg(sender, "You don't have permission for that command.");
			return false;
		}

		if (args.length == 1) {
			int id;
			try {
				id = Integer.parseInt(args[0]);
				System.out.println("ID: " + id);
			} catch (NumberFormatException e) {
				msg(sender, "Invalid id '" + ChatColor.DARK_GREEN + args[0] + ChatColor.GOLD + "'");
				return false;
			}

			Material material = null;
			String template = null;

			for (Map.Entry<Material, ItemCommand> entry : storage.commands.entrySet()) {
				ItemCommand cmd = entry.getValue();
				System.out.println(cmd.m_id);
				if (cmd.m_id == id) {
					// Remove
					material = entry.getKey();
					template = cmd.template;
					break;
				}
			}
			if (material == null) {
				msg(sender, "Couldn't find a command with id '" + id + "'");
				return false;
			}

			storage.commands.remove(material);
			storage.removeConfigEntry(material);
			msg(sender, "Removed successfully:\n" + ChatColor.GOLD + "ID: " + ChatColor.DARK_GREEN + id + "\n" + ChatColor.GOLD + "Command: " + ChatColor.DARK_GREEN + template);
			return true;

		} else {
			msg(sender, "Found " + args.length + " arguments, but was expecting 1.");
			return false;
		}
	}

	public boolean processCommand_ca(CommandSender sender, String[] args) {
		if (args.length < 2) {
			msg(sender, "Found " + args.length + " arguments, but was expecting at least 2.");
			return false;
		}

		String sudoRequired_s = args[args.length - 1].toLowerCase();

		boolean sudoRequired;

		switch (sudoRequired_s) {
			case "yes":
			case "y":
			case "true":
			case "t":
				sudoRequired = true;
				break;
			case "no":
			case "n":
			case "false":
			case "f":
				sudoRequired = false;
				break;
			default:
				msg(sender, "Invalid argument '" + sudoRequired_s
						+ "', was expecting one of [yes,y,true,t,no,n,false,f]");
				return false;
		}

		if (!(sender instanceof Player)) {
			msg(sender, "You need to be a player, holding an item to use this command");
			return false;
		}

		if (!sender.hasPermission("ca.create")) {
			msg(sender, "You don't have permission to use this command.");
			return false;
		}

		Player p = (Player) sender;
		String template = String.join(" ", new ArraySlice<String>(args, 0, args.length - 2));
		Material material = p.getInventory().getItemInMainHand().getType();

		try {
			storage.commands.put(material, new ItemCommand(storage.commandNum, template, sudoRequired));
			storage.commandNum++;

			storage.createConfigEntry(this, template, material, sudoRequired);
		} catch (Exception e) {
			msg(sender, "Error: '" + e.getMessage() + "'");
			return false;
		}

		return true;
	}

}
