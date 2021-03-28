package nz.rishaan.commandattack;

import nz.rishaan.commandattack.util.InvalidPlaceholderException;
import nz.rishaan.commandattack.util.MismatchedBraceException;

import java.util.ArrayList;

public class ItemCommand {
	public int m_id;
	public boolean m_sudoRequired;
	private Part[] m_parts = {};
	public String template;

	public ItemCommand(int id, String template, boolean sudoRequired) throws MismatchedBraceException, InvalidPlaceholderException {
		this.template = template;

		m_id = id;
		m_sudoRequired = sudoRequired;
		ArrayList<Part> parts = new ArrayList<>();

		int s = -1, e = -1, p = 0, l = template.length();

		while (true) {
			s = template.indexOf("{", s + 1);
			e = template.indexOf("}", e + 1);

			if (s == -1 ^ e == -1 || e < s) throw new MismatchedBraceException();

			// If no more placeholders
			if (s == -1) {
				// If still some static text
				if (p < l) {
					parts.add(new StaticPart(template.substring(p, l)));
				}
				break;
			}

			// If some static text, then a placeholder
			if (s > p) {
				parts.add(new StaticPart(template.substring(p, s)));
			}

			String placeholder = template.substring(s + 1, e);

			try {
				parts.add(new DynamicPart(Placeholder.valueOf(placeholder)));
			} catch (IllegalArgumentException ex) {
				throw new InvalidPlaceholderException(placeholder);
			}

			p = e + 1;
		}
		m_parts = parts.toArray(m_parts);
	}

	public String interpolate(AttackData attackData) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Part part : m_parts) {
			if (part instanceof StaticPart) {
				stringBuilder.append(((StaticPart) part).m_part);
			} else if (part instanceof DynamicPart) {
				stringBuilder.append(((DynamicPart) part).evaluate(attackData));
			}
		}

		return stringBuilder.toString();
	}
}
