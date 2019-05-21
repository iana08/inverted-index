import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class QueryFileParser implements QueryParserInterface {

	private final TreeMap<String, ArrayList<Results>> results;
	private final InvertedIndex invertedIndex;

	/**
	 * Constructor for the QueryFileParser
	 * 
	 * @param index InvertedIndex that holds the index of words.
	 */
	public QueryFileParser(InvertedIndex index) {
		results = new TreeMap<>();
		this.invertedIndex = index;
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
		}
	}

	/**
	 * This will parse a single line and stem it and then add it to the results
	 * 
	 * @param line  The single line in the file
	 * @param exact The flag to see if must search exact word or not
	 */
	public void parseLine(String line, boolean exact) {
		TreeSet<String> queryWords = new TreeSet<>();
		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		TextFileStemmer.stemLine(line, stemmer, queryWords);
		String queryLine = String.join(" ", queryWords);
		if (!results.containsKey(queryLine) && !queryLine.equals("")) {
			results.put(String.join(" ", queryWords), this.invertedIndex.searchIndex(queryWords, exact));
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
}
