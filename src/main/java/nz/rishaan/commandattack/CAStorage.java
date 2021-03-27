package nz.rishaan.commandattack;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class CAStorage {
	int commandNum = 0;
	public HashMap<Material, ItemCommand> commands = new HashMap<>();

	YamlConfiguration data;

	public void removeConfigEntry(Material material) {
		data.set(material.toString(), null);
	}

	public void createConfigEntry(CommandAttack plugin, String template, Material material, boolean sudoRequired) {
		data.set(material.toString() + ".template" , template);
		data.set(material.toString() + ".sudo" , sudoRequired);

		saveData(plugin);
	}

	public void loadData(CommandAttack plugin) {
		if (!plugin.getDataFolder().exists())
			plugin.getDataFolder().mkdirs();

		plugin.saveDefaultConfig();

		File dataFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "data.yml");

		try {
			dataFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		data = YamlConfiguration.loadConfiguration(dataFile);

		for (String key : data.getKeys(false)) {
			Material material;
			try {
				material = Material.valueOf(key);
			} catch (IllegalArgumentException e) {
				System.out.println("Invalid key in '" + dataFile.getAbsolutePath() + "', key = '" + key + "'");
				material = null;
			}

			String template = data.getString(key + ".template");
			boolean sudoRequired = data.getBoolean(key + ".sudo");

			if (template == null) {
				System.out.println("Invalid string in '" + dataFile.getAbsolutePath() + "', key = '" + key + "'");
			}

			if (material != null && template != null) {
				try {
					commands.put(material, new ItemCommand(commandNum, template, sudoRequired));
					commandNum++;
				} catch (Exception e) {
					System.out.println("Error: " + e.getMessage());
				}
			}
		}
	}

	public void saveData(CommandAttack plugin) {
		File dataFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "data.yml");
		try {
			data.save(dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
