package nz.rishaan.commandattack;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AttackData {
	public Player attacker, target;
	public EntityDamageByEntityEvent e;

	public AttackData (Player attacker, Player target, EntityDamageByEntityEvent e) {
		this.attacker = attacker;
		this.target = target;
		this.e = e;
	}
}
