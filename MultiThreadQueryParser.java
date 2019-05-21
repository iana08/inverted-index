import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

// TODO Don't need both this and the ThreadSafeInvertedQueryParser class 
// TODO implements QueryParserInterface
public class MultiThreadQueryParser {

	private final TreeMap<String, ArrayList<Results>> results;
	private final InvertedIndex invertedIndex;
	private final WorkQueue queue;

	/**
	 * Constructor for the QueryFileParser
	 * 
	 * @param index InvertedIndex that holds the index of words.
	 */
	public MultiThreadQueryParser(InvertedIndex index, WorkQueue queue) {
		results = new TreeMap<>();
		this.invertedIndex = index;
		this.queue = queue;
	}

	/**
	 * This will read the Queries and saves them to a search query index
	 * 
	 * @param path      The path of the queries
	 * @param exactFlag to see if exact flag was passed too
	 * @throws IOException
	 */
	public void readQueries(Path path, boolean exactFlag) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line = "";
			while ((line = reader.readLine()) != null) {
				parseLine(line, exactFlag);
			}
			// TODO Put in finally block
			this.queue.finish();
		}
	}

	/**
	 * This will parse a single line and stem it and then add it to the results
	 * 
	 * @param line  The single line in the file
	 * @param exact The flag to see if must search exact word or not
	 */
	public void parseLine(String line, boolean exact) {
		// TODO Move ALL of the work into the task
		TreeSet<String> queryWords = new TreeSet<>();
		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		TextFileStemmer.stemLine(line, stemmer, queryWords);
		String queryLine = String.join(" ", queryWords);
		// TODO Need to protect the access to results below
		if (!results.containsKey(queryLine) && !queryLine.equals("")) {
			this.queue.execute(new Task(queryWords, exact, results, this.invertedIndex));
		}
	}

	/**
	 * This will write a json at the path it was given.
	 * 
	 * @param path    the path it writes to
	 * @param isIndex boolean that checks if this writer should write for the index
	 *                or the search queries.
	 * @throws IOException
	 */
	public void toJSON(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			TreeJSONWriter.asResults(this.results, writer, 0);
		}
	}

	/**
	 * An inner class for a runnable method to search in the invertedIndex
	 * 
	 * @author SirIan
	 *
	 */
	public class Task implements Runnable {
		// TODO Only need the line and exactFlag
		// TODO Everything else can either be a local variable or accessed directly
		private final boolean exactFlag;
		private final Collection<String> queryWords;
		private final TreeMap<String, ArrayList<Results>> results;
		private final InvertedIndex index;

		/**
		 * This is the constructor for the runnable class
		 * 
		 * @param queryWords The collection of words to search for
		 * @param exact      Boolean to do either exact or partial
		 * @param results    the results dataStructure to add query words.
		 */
		public Task(Collection<String> queryWords, boolean exact, TreeMap<String, ArrayList<Results>> results,
				InvertedIndex index) {
			this.exactFlag = exact;
			this.queryWords = queryWords;
			this.results = results;
			this.index = index;
		}

		@Override
		public void run() {
			synchronized (results) {
				results.put(String.join(" ", queryWords), index.searchIndex(queryWords, exactFlag));
			}
			
			/*
			 * 
			 * List<...> current = index.searchIndex(queryWords, exactFlag);
			 * String queryLine = String.join(" ", queryWords);
			 * synchronized (results) {
				results.put(queryLine, current);
			} 
			 * 
			 */
		}
	}

}