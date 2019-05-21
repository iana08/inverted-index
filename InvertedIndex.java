import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data structure to store strings and their positions.
 */
public class InvertedIndex {

	/**
	 * Stores a mapping of words to the positions the words were found.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	private final TreeMap<String, Integer> totalwords;

	/**
	 * Initializes the index.
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();
		this.totalwords = new TreeMap<>();
	}

	/**
	 * Adds the word and the position it was found to the index.
	 *
	 * @param word     word to clean and add to index
	 * @param position position word was found
	 * @param path     The file the word was from
	 * @return true if this index did not already contain this word and position
	 */
	public boolean add(String word, int position, String path) {
		index.putIfAbsent(word, new TreeMap<>());
		index.get(word).putIfAbsent(path, new TreeSet<>());
		boolean result = index.get(word).get(path).add(position);

		if (result) {
			totalwords.put(path, totalwords.getOrDefault(path, 0) + 1);
		}
		return result;
	}

	/**
	 * Adds the array of words at once, assuming the first word in the array is at
	 * position 1.
	 *
	 * @param words array of words to add
	 * @return true if this index is changed as a result of the call (i.e. if one or
	 *         more words or positions were added to the index)
	 *
	 * @see #addAll(String[], int)
	 */
	public boolean addAll(String[] words, String path) {
		return addAll(words, path, 1);
	}

	/**
	 * Adds the array of words at once, assuming the first word in the array is at
	 * the provided starting position
	 *
	 * @param words array of words to add
	 * @param start starting position
	 * @return true if this index is changed as a result of the call (i.e. if one or
	 *         more words or positions were added to the index)
	 */
	public boolean addAll(String[] text, String path, int start) {
		boolean changed = false;
		for (String word : text) {
			if (add(word, start, path) == true) {
				changed = true;
			}
			start++;
		}
		return changed;
	}

	/**
	 * This will write the locations in a JSON format to the specific path
	 * 
	 * @param path The path to write to
	 * @throws IOException
	 */
	public void toJsonAsLocation(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			TreeJSONWriter.writeLocations(totalwords, writer, 0);
		}
	}

	/**
	 * This will write a json at the path it was given.
	 * 
	 * @param path the path it writes to
	 * @throws IOException
	 */
	public void toJSON(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			TreeJSONWriter.asDoubleNestedObject(index, writer, 0);
		}
	}

	/**
	 * Returns a string representation of this index.
	 */
	@Override
	public String toString() {
		return this.index.toString();
	}

	/**
	 * Checks if the word is in the index
	 * 
	 * @param word String The word to see if it is in the index
	 * @return true or false
	 */
	public boolean contains(String word) {
		return index.containsKey(word);
	}

	/**
	 * Checks if the word and location is in the index
	 * 
	 * @param word     the word to see if it is in the index
	 * @param location the location to see if the location is in the word
	 * @return true if the word is in the index false if otherwise
	 * @throws NullPointerException
	 */
	public boolean contains(String word, String location) throws NullPointerException {
		return contains(word) == true ? null : index.get(word).containsKey(location);
	}

	/**
	 * Checks if the word and location and position is in the index
	 * 
	 * @param word     the word to see if it is in the index
	 * @param location the location to see if the location is in the word
	 * @param position int to see if the position is in the TreeSet
	 * @return true if the word is in the index false if otherwise
	 * @throws NullPointerException
	 */
	public boolean contains(String word, String location, int position) throws NullPointerException {
		return contains(word, location) == true ? null : index.get(word).get(location).contains(position);
	}

	/**
	 * Gets the size of the Index
	 * 
	 * @return the size of the index
	 */
	public int getSize() {
		return index.size();
	}

	/**
	 * This will search through the invertedIndex and finds the query for it and
	 * returns all the results for it
	 * 
	 * @param query       The word to look for
	 * @param exactSearch a boolean to see if the exact flag is passed in
	 * @return an ArrayList of all the results for the word
	 */
	public ArrayList<Results> searchIndex(Collection<String> queryWords, boolean exactSearch) {
		if (exactSearch) {
			return exactSearch(queryWords);
		} else {
			return partialSearch(queryWords);
		}
	}

	/**
	 * The exact search will go through the index and returns words that is the
	 * exact query
	 * 
	 * @param queryWords the collection of different query words
	 * @return
	 */
	public ArrayList<Results> exactSearch(Collection<String> queryWords) {
		HashMap<String, Results> lookup = new HashMap<>();
		ArrayList<Results> results = new ArrayList<>();
		for (String query : queryWords) {
			if (index.containsKey(query)) {
				searchHelper(query, results, lookup);
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * The search Helper for both exact and partical
	 * 
	 * @param query   The query to get from the index
	 * @param results The arrayList to add the results found from the invertedIndex
	 * @param lookup  The hashmap to see if we have a result from before
	 */
	private void searchHelper(String query, ArrayList<Results> results, HashMap<String, Results> lookup) {
		TreeMap<String, TreeSet<Integer>> pathMap = index.get(query);
		for (String path : pathMap.keySet()) {
			if (lookup.containsKey(path)) {
				Results result = lookup.get(path);
				result.increaseCount(pathMap.get(path).size());
			} else {
				Results new_Result = new Results(path, pathMap.get(path).size(), totalwords.get(path));
				lookup.put(path, new_Result);
				results.add(new_Result);
			}
		}
	}

	/**
	 * The partical search will go through the index and returns words that starts
	 * with this query
	 * 
	 * @param lookup The treeMap to hold all the results
	 * @param query  the word to look for.
	 */
	public ArrayList<Results> partialSearch(Collection<String> queryWords) {
		HashMap<String, Results> lookup = new HashMap<>();
		ArrayList<Results> results = new ArrayList<>();
		for (String query : queryWords) {
			SortedMap<String, TreeMap<String, TreeSet<Integer>>> headMap = index.tailMap(query);
			for (String word : headMap.keySet()) {
				if (word.startsWith(query)) {
					searchHelper(word, results, lookup);
				} else {
					break;
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * This will add all the elements in the other invertedIndex and adds it to this
	 * index
	 * 
	 * @param other the intvertedIndex to add to the current index
	 */
	public void addAll(InvertedIndex other) {
		for (String key : other.index.keySet()) {
			if (this.index.containsKey(key) == false) {
				this.index.put(key, other.index.get(key));
			} else {
				for (String path : other.index.get(key).keySet()) {
					if (this.index.get(key).containsKey(path) == false) {
						this.index.get(key).put(path, other.index.get(key).get(path));
					}
					/*
					 * TODO else {
					 *   you have to use the set addAll method to combine togethr the position sets
					 * }
					 */
				}

			}
		}

		for (String key : other.totalwords.keySet()) {
			if (this.totalwords.containsKey(key) == false) {
				this.totalwords.put(key, other.totalwords.get(key));
			}
			/*
			 * TODO
			 * Also not a generally functional approach
			 */
		}
	}
}