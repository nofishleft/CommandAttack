package nz.rishaan.commandattack;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;

public class Config {
	public void loadConfig(FileConfiguration config) {
		message_prefix = config.getString("message_prefix", message_prefix);

		Class<Config> cls = Config.class;

		ConfigurationSection messages = config.getConfigurationSection("messages");

		if (messages == null) {
			System.out.println("Null messages");
			return;
		}

		for (String key : messages.getKeys(false)) {
			ConfigurationSection options = messages.getConfigurationSection(key);

			if (options == null) {
				System.out.println("Null options");
				continue;
			}

			for (String option : options.getKeys(false)) {
				String fullOption = key + "_" + option;
				String value = options.getString(option);
				if (value == null) {
					System.out.println("Value null for key: " + key);
					continue;
				}
				value = message_prefix + " " + value;
				value = ChatColor.translateAlternateColorCodes('&', value);

				try {
					Field fld = cls.getField(fullOption);
					try {
						fld.set(this, value);
					} catch (IllegalAccessException ex) {
						ex.printStackTrace();
						return;
					}
				} catch (NoSuchFieldException ex) {
					ex.printStackTrace();
					return;
				}

			}
		}
	}

	public String message_prefix = "[CA]";

	// common
	public String common_noPermission = "You don not have permission to use this command.";
	public String common_notPlayer = "You need to be a player to use this command.";
	public String common_notImplemented = "Not yet implemented!.";
	public String common_argLengthMismatch_Ignoring = "Found {0} arguments, but was expecting {1}, ignoring arguments.";
	public String common_argLengthMismatch = "Found {0} arguments, but was expecting {1}.";
	public String common_argLengthMin = "Found {0} arguments, but was expecting at least {1}.";

	// calist
	public String calist_noCommandsForItem = "No commands for item {0}.";
	public String calist_foundCommandForItem = "Found command for item {0}:\nID: {1}\nCommand: {2}";

	//caremove
	public String caremove_invalidID = "Invalid id {0}.";
	public String caremove_couldntFindCommand = "Could not find command with id {0}.";
	public String caremove_success = "Removed successfully\nMaterial: {0}\nID: {1}\nCommand: {2}";

	// careload
	public String careload_success = "Successfully reloaded config.";
	public String careload_invalidConfigOption = "Invalid configuration value at {0}.";

	// ca
	public String ca_invalidBoolean = "Invalid argument {0}.\nWas expecting one of [yes,y,true,t,no,n,false,f].";
	public String ca_success = "Successfully bound command.";
	public String ca_templateMismatchedBraces = "Mismatched braces.";
	public String ca_templateInvalidPlaceholder = "Invalid placeholder {0}.";
}
