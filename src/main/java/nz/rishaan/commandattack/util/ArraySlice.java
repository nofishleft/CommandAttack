package nz.rishaan.commandattack.util;

import java.util.Iterator;

/**
 * ArraySlice allows for viewing/accessing a slice of a primitive array
 *
 * Used for running String.join on a slice of a String[]
 *
 * Example usage:
 * <code>
 *		// Player ran command "/cmd arg1 arg2 arg3 arg4";
 *		String[] args = ["arg1", "arg2", "arg3", "arg4"];
 *
 *		// Ignore the last argument
 *		ArraySlice<String> slice = new ArraySlice<String>(args, 0, args.length - 2);
 *
 *		// Join into single string
 *		// String other = "arg1 arg2 arg3";
 *		String other = String.join(" ", slice);
 * </code>
 *
 *
 * @param <T>
 */
public class ArraySlice<T extends CharSequence> implements Iterable<T>
{
	private final int from, to;
	private final T[] original;

	public ArraySlice(T[] original, int from, int to)
	{
		this.original = original;
		this.from = from;
		this.to = to;
	}

	public T get(int index)
	{
		return original[index + from];
	}

	public int size()
	{
		return to - from + 1;
	}

	@Override
	public Iterator<T> iterator()
	{
		return new Iterator<T>() {
			private int currentIndex = from;

			@Override
			public boolean hasNext() {
				return currentIndex <= to;
			}

			@Override
			public T next() {
				return original[currentIndex++];
			}
		};
	}

	//Can support setters on from/to variables
}
