import java.io.IOException;
import java.nio.file.Path;

/**
 * This is the interface for QueryParser
 * 
 * @author SirIan
 *
 */
public interface QueryParserInterface {
	/**
	 * Reads from the path and gets all queries
	 * 
	 * @param path      The path to read from
	 * @param exactFlag The flag to either read exact words or partial
	 * @throws IOException
	 */
	void readQueries(Path path, boolean exactFlag) throws IOException;

	/**
	 * Writes the search Queries to a JSON file
	 * 
	 * @param path The path to write to
	 * @throws IOException
	 */
	void toJSON(Path path) throws IOException;

	/**
	 * This will parse the line
	 * 
	 * @param line  the text to parse
	 * @param exact the flag to search exact or partial
	 * @throws IOException
	 */
	void parseLine(String line, boolean exact) throws IOException;
}