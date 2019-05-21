import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * 
 * @author SirIan This class does multiple things: goes through files and gets
 *         the text to read, then adds it to the array and then calls writes to
 *         file at the end.
 */
public class FileReader {
	/**
	 * Checks if the filepath is a text file or not.
	 * 
	 * @param path the filepath to check if it is a text file or not.
	 * @return true if it is a text file or false if it is a directory.
	 */
	public static boolean isTextFile(Path path) {
		String filepath = path.toString().toLowerCase();
		return filepath.endsWith("txt") || filepath.endsWith("text");
	}

	/**
	 * This method goes through the entire directory to find every file.
	 * 
	 * @param path  the directory or file to read.
	 * @param index The invertedindex to add words in.
	 * @throws IOException
	 */
	public static void readFiles(Path path, InvertedIndex index) throws IOException {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> files = Files.newDirectoryStream(path)) {
				for (Path file : files) {
					readFiles(file, index);
				}
			}
		} else if (isTextFile(path)) {
			readFile(path, index);
		}
	}

	/**
	 * This method reads the text file and stems the word and adds it to the
	 * invertedIndex
	 * 
	 * @param path the text file to read
	 */
	public static void readFile(Path path, InvertedIndex index) throws IOException {
		int postion = 1;
		String filepath = path.toString();
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
				for (String token : TextFileStemmer.stemLine(line, stemmer)) {
					index.add(token, postion++, filepath);
				}
			}
		}
	}
}