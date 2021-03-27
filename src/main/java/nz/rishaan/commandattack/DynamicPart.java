package nz.rishaan.commandattack;

public class DynamicPart implements Part {
	public Placeholder m_placeholder;

	public DynamicPart(Placeholder placeholder) {
		m_placeholder = placeholder;
	}

	public String evaluate(AttackData attackData) {
		switch (m_placeholder) {
			case attacker:
				return attackData.attacker.getDisplayName();
			case target:
				return attackData.target.getDisplayName();
		}

		return "";
	}
}
