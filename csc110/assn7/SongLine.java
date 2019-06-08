/**
 * CSC110 Assignment 7 : Bowie Songs.
 * SongLine.java
 * Created for use by CSC110 Summer2016
 */

/**
 * SongLine is a line from a song.
 * It is identified by:
 * <ul>
 * <li> genre which is a one word category for the song.
 * <li> lineNumber which is the number from 0 to totalLines - 1.
 * <li> The actual line of the song.
 * </ul>
 */
public class SongLine implements Comparable<SongLine> {
	private String genre;
	private int lineNumber;
	private String words;
	
	/**
	 * Creates a SongLine object.
	 * @param genre The single word genre of the song that contains this line.
	 * @param lineNumber The line number that this particular line in the song can be found, from 0 to the number of lines minus one.
	 * @param words The actual line.
	 */
	public SongLine(String genre, int lineNumber, String words) {
		this.genre = genre;
		this.lineNumber = lineNumber;
		this.words = words;
	}
	
	/**
	 * Updates the genre.
	 * @param genre The new genre.
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}
	
	/**
	 * @return The genre of the song that contains this line.
	 */
	public String getGenre() {
		return genre;
	}
	
	/**
	 * Updates the line number.
	 * @param lineNumber The new line number.
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	/**
	 * @return The line number.
	 */
	public int getLineNumber() {
		return lineNumber;
	}
	
	/**
	 * Updates the lyrics for this line.
	 * @param words The new lyric line.
	 */
	public void setWords(String words) {
		this.words = words;
	}
	
	/**
	 * @return The lyrics for this line.
	 */
	public String getWords() {
		return words;
	}

	/**
	 * Compares this song line to another.
	 * @param other The other song line.
	 * @return A number less than 0 if this lineNumber comes before the other lineNumber.
	 *	A number greater than 0 if this lineNumber comes after the other lineNumber.
	 *	0 if this lineNumber is equivalent to the other lineNumber.
	 */
	public int compareTo(SongLine other) {
		return lineNumber - other.lineNumber;
	}

	/**
	 * Determines if this songline is equivalent to the other songline.
	 * @param other The other songline.
	 * @return True if this SongLine has exactly the same genre, lineNumber and words to the other SongLine.
	 * If any of the above is not equal, then returns false.
	 */
	public boolean equals(SongLine other) {
		return lineNumber == other.lineNumber && genre.equals(other.genre) &&
			other.words.contains(words);
	}
	/**
	 * @return A string representation of the complete SongLine.
	 * The format is :<br>
	 * genre&nbsp;&nbsp;lineNumber:&nbsp;&nbsp;words
	 */
	public String toString() {
		return genre+"\t"+lineNumber+":\t"+words;		
	}

	/**
	 * @return A string representation of the SongLine, which does not include the genre.
	 */
	public String toStringLineOnly() {
		return lineNumber+":\t"+words;
	}
}
