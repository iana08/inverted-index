# inverted-index

This is an Inverted Index Builder.

Where it would take a file, build an Index based on where it is located in the file and print it in a json format.

to run:

javac driver.java

java driver -path <filename\path> -search<filename/path> - index -results -locations

-path = to read the file and get all the words in that file

-search = to search for these words

-index = prints the inverted index in json format

-results = prints the results in json format

-locations = prints the locations of the words in the file

-thread = runs the program in multi-threading
