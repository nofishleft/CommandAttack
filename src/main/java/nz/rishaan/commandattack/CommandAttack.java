package nz.rishaan.commandattack;

import nz.rishaan.commandattack.util.ArraySlice;
import nz.rishaan.commandattack.util.InvalidPlaceholderException;
import nz.rishaan.commandattack.util.MismatchedBraceException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.MessageFormat;
import java.util.Map;

public final class CommandAttack extends JavaPlugin implements Listener {

	final String SUDO_PERMISSION = "ca.sudo";
	final String CREATE_PERMISSION = "ca.create";
	final String LIST_PERMISSION = "ca.list";
	final String REMOVE_PERMISSION = "ca.remove";
	final String RELOAD_PERMISSION = "ca.reload";

	CAStorage storage = new CAStorage();
	Config cnf = new Config();

	@Override
	public void onEnable() {
		// Plugin startup logic
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		storage.loadData(this);
		cnf.loadConfig(this.getConfig());
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
		sender.sendMessage(message);
	}

	public void msg(CommandSender sender, String format, Object ... args) {
		sender.sendMessage(MessageFormat.format(format, args));
	}

	public boolean processCommand_calist(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission(LIST_PERMISSION)) {
				if (args.length != 0) {
					msg(sender, cnf.common_argLengthMismatch_Ignoring, args.length, 0);
				}

				Material m = p.getInventory().getItemInMainHand().getType();
				ItemCommand cmd = storage.commands.getOrDefault(m, null);

				if (cmd == null) {
					msg(sender, cnf.calist_noCommandsForItem, m);
				} else {
					msg(sender, cnf.calist_foundCommandForItem, m, cmd.m_id, cmd.template);
				}
				return true;
			} else {
				msg(sender, cnf.common_noPermission);
				return false;
			}
		} else {
			msg(sender, cnf.common_notPlayer);
			return false;
		}
	}

	public boolean processCommand_careload(CommandSender sender, String[] args) {

		if (sender instanceof Player && !sender.hasPermission(RELOAD_PERMISSION)) {
			msg(sender, cnf.common_noPermission);
			return false;
		} else {
			if (args.length != 0) {
				msg(sender, cnf.common_argLengthMismatch_Ignoring, args.length, 0);
			}

			msg(sender, cnf.common_notImplemented);
			return true;
		}
	}

	public boolean processCommand_caremove(CommandSender sender, String[] args) {
		if (sender instanceof Player && !sender.hasPermission(REMOVE_PERMISSION)) {
			msg(sender, cnf.common_noPermission);
			return false;
		}

		if (args.length == 1) {
			int id;
			try {
				id = Integer.parseInt(args[0]);
				System.out.println("ID: " + id);
			} catch (NumberFormatException e) {
				msg(sender, cnf.caremove_invalidID, args[0]);
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
				msg(sender, cnf.caremove_couldntFindCommand, id);
				return false;
			}

			storage.commands.remove(material);
			storage.removeConfigEntry(material);
			msg(sender, cnf.caremove_success, material, id, template);
			return true;

		} else {
			msg(sender, cnf.common_argLengthMismatch, args.length, 1);
			return false;
		}
	}

	public boolean processCommand_ca(CommandSender sender, String[] args) {
		if (args.length < 2) {
			msg(sender, cnf.common_argLengthMin, args.length, 2);
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
				msg(sender, cnf.ca_invalidBoolean, sudoRequired_s);
				return false;
		}

		if (!(sender instanceof Player)) {
			msg(sender, cnf.common_notPlayer);
			return false;
		}

		if (!sender.hasPermission(CREATE_PERMISSION)) {
			msg(sender, cnf.common_noPermission);
			return false;
		}

		Player p = (Player) sender;
		String template = String.join(" ", new ArraySlice<String>(args, 0, args.length - 2));
		Material material = p.getInventory().getItemInMainHand().getType();

		try {
			storage.commands.put(material, new ItemCommand(storage.commandNum, template, sudoRequired));
			storage.commandNum++;

			storage.createConfigEntry(this, template, material, sudoRequired);

			msg(sender, cnf.ca_success);
		} catch (InvalidPlaceholderException e) {
			msg(sender, cnf.ca_templateInvalidPlaceholder, e.placeholder);
			return false;
		} catch (MismatchedBraceException e) {
			msg(sender, cnf.ca_templateMismatchedBraces);
			return false;
		}

		return true;
	}

}
