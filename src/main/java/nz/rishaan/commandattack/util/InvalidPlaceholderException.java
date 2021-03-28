package nz.rishaan.commandattack.util;

public class InvalidPlaceholderException extends Exception {
	public InvalidPlaceholderException(String placeholder) {
		this.placeholder = placeholder;
	}

	public String placeholder;
}
