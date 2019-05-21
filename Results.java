/**
 * 
 * @author SirIan holds all the data for my class.
 */
public class Results implements Comparable<Results> {

	private final String path;

	private int count;

	private final int total;

	/**
	 * Constructs my Results
	 * 
	 * @param word       String The word
	 * @param path       String the file the word was found
	 * @param count      Integer the total matches for the query.
	 * @param totalwords Integer the total words in the file
	 */
	public Results(String path, int count, int totalwords) {
		this.path = path;
		this.count = count;
		this.total = totalwords;
	}

	/**
	 * Increases the total matches for the query
	 * 
	 * @param count The total matches the query found
	 */
	public void increaseCount(int count) {
		this.count += count;
	}

	/**
	 * Returns the Path for the query
	 * 
	 * @return String the filepath it found for the query
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Gets the total matches the query
	 * 
	 * @return The total matches for the query
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Gets the score for the query
	 * 
	 * @return Double the score of the query
	 */
	public double getScore() {
		return (double) count / total;
	}

	/**
	 * My compareTo method that it overrides to compare two results together.
	 * 
	 * @param r Results the other result to compare to this result
	 * @return int whether -1,0,1
	 */
	@Override
	public int compareTo(Results r) {
		int result = Double.compare(r.getScore(), this.getScore());
		if (result == 0) {
			result = Integer.compare(r.count, this.count);
		}

		if (result == 0) {
			result = this.path.compareTo(r.path);
		}
		return result;
	}

	/**
	 * The toString method to return a String of the info inside the Result class.
	 * 
	 * @return String the String of word, path, and score
	 */
	@Override
	public String toString() {
		return "\"where\"" + ": " + "\"" + this.path + "\"," + System.lineSeparator() + "\"count\"" + ": " + this.count
				+ "\"," + System.lineSeparator() + "\"score\"" + ": " + this.getScore() + System.lineSeparator();
	}
}