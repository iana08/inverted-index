import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

/**
 * The Great Creator Of This File!
 * 
 * @author SirIan
 *
 */
public class Driver {
	/**
	 * Parses the command-line arguments to build and use an in-memory search engine
	 * from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 */

	public static void main(String[] args) {
		ArgumentMap argumentMap = new ArgumentMap(args);
		if (argumentMap.hasFlag("-threads")) {
			ThreadSafeInvertedIndex index = new ThreadSafeInvertedIndex();
			WorkQueue queue = new WorkQueue(Integer.parseInt(argumentMap.getString("-threads", "5")));
			MultiThreadQueryParser query = new MultiThreadQueryParser(index, queue);
			if (argumentMap.hasFlag("-path") && argumentMap.getPath("-path") != null) {
				try {
					ThreadSafeFileReader.readFiles(argumentMap.getPath("-path"), index, queue);
				} catch (InvalidPathException | IOException e) {
					System.err.println(
							"Unable to access the path to make an Inverted Index: " + argumentMap.getPath("-path"));
				}
			}

			if (argumentMap.hasFlag("-search") && argumentMap.hasValue("-search")) {
				try {
					query.readQueries(argumentMap.getPath("-search"), argumentMap.hasFlag("-exact"));
				} catch (InvalidPathException | IOException e) {
					System.err.println(
							"Unable to access the path to make an Inverted Index: " + argumentMap.getPath("-exact"));
				}
			}
			if (argumentMap.hasFlag("-index")) {
				try {
					index.toJSON(argumentMap.getPath("-index", Paths.get("index.json")));

				} catch (IOException e) {
					System.err.println(
							"Unable to access the path to write a JSON files: " + argumentMap.getPath("-index"));
				}
			}

			if (argumentMap.hasFlag("-results")) {
				try {
					query.toJSON(argumentMap.getPath("-results", Paths.get("results.json")));
				} catch (InvalidPathException | IOException e) {
					System.err.println("Unable to read the queries from the file: " + argumentMap.getPath("-results"));
				}
			}

			if (argumentMap.hasFlag("-locations")) {
				try {
					index.toJsonAsLocation(argumentMap.getPath("-locations", Paths.get("locations.json")));
				} catch (IOException e) {
					System.err.println("Unable to open file : " + argumentMap.getPath("-locations"));
				}
			}
			queue.shutdown();
		} else {
			InvertedIndex invertedIndex = new InvertedIndex();
			QueryFileParser query = new QueryFileParser(invertedIndex);
			if (argumentMap.hasFlag("-path") && argumentMap.getPath("-path") != null) {
				try {
					FileReader.readFiles(argumentMap.getPath("-path"), invertedIndex);
				} catch (InvalidPathException | IOException e) {
					System.err.println(
							"Unable to access the path to make an Inverted Index: " + argumentMap.getPath("-path"));
				}
			}

			if (argumentMap.hasFlag("-search") && argumentMap.hasValue("-search")) {
				try {
					query.readQueries(argumentMap.getPath("-search"), argumentMap.hasFlag("-exact"));
				} catch (InvalidPathException | IOException e) {
					System.err.println(
							"Unable to access the path to make an Inverted Index: " + argumentMap.getPath("-exact"));
				}
			}
			if (argumentMap.hasFlag("-index")) {
				try {
					invertedIndex.toJSON(argumentMap.getPath("-index", Paths.get("index.json")));

				} catch (IOException e) {
					System.err.println(
							"Unable to access the path to write a JSON files: " + argumentMap.getPath("-index"));
				}
			}

			if (argumentMap.hasFlag("-results")) {
				try {
					query.toJSON(argumentMap.getPath("-results", Paths.get("results.json")));
				} catch (InvalidPathException | IOException e) {
					System.err.println("Unable to read the queries from the file: " + argumentMap.getPath("-results"));
				}
			}

			if (argumentMap.hasFlag("-locations")) {
				try {
					invertedIndex.toJsonAsLocation(argumentMap.getPath("-locations", Paths.get("locations.json")));
				} catch (IOException e) {
					System.err.println("Unable to open file : " + argumentMap.getPath("-locations"));
				}
			}
		}
		
		/*
		 * TODO Can be significantly simplified using upcasting.
		 */
	}

}