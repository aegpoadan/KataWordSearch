package wordSearch;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;

/**
 * This class is to be used in finding a given set of words inside of a boggle board.
 * All constructors take a file path. This path should contain a csv file, where the first row
 * contains all words to search for in a boggle board. All successive rows should make up a 
 * NxN matrix of characters, representing the boggle board.
 * 
 * Parallel loading can optionally be chosen, by passing true as the second parameter to the 
 * WordSearch constructor. Be warned that parallel loading will likely result in a performance drop
 * compared to serial loading if dealing with small matrices.
 * 
 * @author Andrew Hayes
 * @version 1.0
 */
public class WordSearch {
	private ArrayList<ArrayList<Character>> boggleBoard = new ArrayList<ArrayList<Character>>();
	private ArrayList<String> wordsToSearchFor = new ArrayList<String>();
	//first int in pair is x coordinate, second is y coordinate
	private ConcurrentHashMap<String, ArrayList<Pair<Integer, Integer>>> wordMap = new ConcurrentHashMap<String, ArrayList<Pair<Integer, Integer>>>();
	private int numColumns;
	private int numRows;
	private ForkJoinPool wordSearchPool = new ForkJoinPool();
	private boolean parallel = false;
	
	/**
	 * Constructs a new WordSearch object. 
	 * Takes one String parameter, a file path, and uses the contents to create a boggle board and its
	 * associated word map. The word map will be a mapping between words and their coordinates on the 
	 * boggle board.
	 * @param path The file path containing WordSearch data
	 */
	public WordSearch(String path) {
		try {
			String fileContent = new String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset());
			String[] lines = fileContent.split("\n");
			if(lines.length < 2) {
				throw new IOException("File must be at least two lines long!");
			}
			
			numRows = lines.length - 1;
			numColumns = numRows;
			
			loadBoggleBoard(lines);
			loadWordMap();
		} catch(IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructs a new WordSearch object. 
	 * Takes two parameters, a file path as a String and a boolean.
	 * Uses the file specified at path to create a boggle board and its associated word map. 
	 * The word map will be a mapping between words and their coordinates on the boggle board.
	 * The boolean parallel can be specified to dictate whether parallel loading is used for the 
	 * word map.
	 * @param path The file path containing WordSearch data
	 * @param parallel The boolean specifying whether to use parallel loading
	 */
	public WordSearch(String path, boolean parallel) {
		this.parallel = parallel;
		try {
			String fileContent = new String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset());
			String[] lines = fileContent.split("\n");
			if(lines.length < 2) {
				throw new IOException("File must be at least two lines long!");
			}
			
			numRows = lines.length - 1;
			numColumns = numRows;
			
			loadBoggleBoard(lines);
			loadWordMap();
		} catch(IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the contents of a csv file into this WordSearch object. The first line will
	 * be loaded into wordsToSearchFor, while all successive lines will be loaded into boggleBoard.
	 * @param lines The lines of a csv file
	 * @throws IOException
	 */
	private void loadBoggleBoard(String[] lines) throws IOException {
		int i = 0;
		for(String line: lines) {
			String[] words = line.split(",");
			if(i > 0 && words.length != numColumns) {
				throw new IOException("WordSearch matrix is incorrectly formatted! Must be NxN");
			}
			
			if(i == 0) {
				for(String word: words) {
					wordsToSearchFor.add(word);
				}
			} else {
				ArrayList<Character> characters = new ArrayList<Character>();
				for(String c: words) {
					characters.add(c.charAt(0));
				}
				boggleBoard.add(characters);
			}
			
			i++;
		}
	}
	
	/**
	 * Loads all words that can be formed out of boggleBoard into wordMap.
	 * Will perform a parallel load if parallel is set to true.
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void loadWordMap() throws InterruptedException, ExecutionException {
		if(parallel) {
			wordSearchPool.submit(() -> {
				IntStream.range(0,numRows).parallel().forEach((x) -> {
					IntStream.range(0, numColumns).parallel().forEach((y) -> {
						getHorionztalWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
						getHorionztalReverseWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
						getVerticalWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
						getVerticalReverseWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
						getDiagonalAscendingWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
						getDiagonalAscendingReverseWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
						getDiagonalDescendingWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
						getDiagonalDescendingReverseWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
					});
				});
			}).get(); //wait until wordMap is done being populated
		} else {
			for(int x=0; x<numRows; x++) {
				for(int y=0; y<numColumns; y++) {
					getHorionztalWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
					getHorionztalReverseWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
					getVerticalWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
					getVerticalReverseWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
					getDiagonalAscendingWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
					getDiagonalAscendingReverseWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
					getDiagonalDescendingWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
					getDiagonalDescendingReverseWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
				}
			}
		}
	}
	
	/**
	 * Gets all diagonal ascending words that can be formed from a coordinate on this boggleBoard.
	 * @param lastCoords The ArrayList of coordinates that were used for the previous recursive call.
	 * Should be an empty ArrayList for first call.
	 * @param currentWord The StringBuilder containing the word that was formed for the previous recursive call.
	 * Should be an empty StringBuilder for first call.
	 * @param x The x coordinate to check.
	 * @param y The y coordinate to check.
	 */
	private void getDiagonalAscendingWords(ArrayList<Pair<Integer, Integer>> lastCoords,
			StringBuilder currentWord, int x, int y) {
		if(y < 0 || x >= numColumns) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getDiagonalAscendingWords(lastCoords, currentWord, x+1, y-1);
	}
	
	/**
	 * Gets all diagonal ascending reverse words that can be formed from a coordinate on this boggleBoard.
	 * @param lastCoords The ArrayList of coordinates that were used for the previous recursive call.
	 * Should be an empty ArrayList for first call.
	 * @param currentWord The StringBuilder containing the word that was formed for the previous recursive call.
	 * Should be an empty StringBuilder for first call.
	 * @param x The x coordinate to check.
	 * @param y The y coordinate to check.
	 */
	private void getDiagonalAscendingReverseWords(ArrayList<Pair<Integer, Integer>> lastCoords,
			StringBuilder currentWord, int x, int y) {
		if(y >= numRows || x < 0) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getDiagonalAscendingReverseWords(lastCoords, currentWord, x-1, y+1);
	}
	
	/**
	 * Gets all diagonal descending words that can be formed from a coordinate on this boggleBoard.
	 * @param lastCoords The ArrayList of coordinates that were used for the previous recursive call.
	 * Should be an empty ArrayList for first call.
	 * @param currentWord The StringBuilder containing the word that was formed for the previous recursive call.
	 * Should be an empty StringBuilder for first call.
	 * @param x The x coordinate to check.
	 * @param y The y coordinate to check.
	 */
	private void getDiagonalDescendingWords(ArrayList<Pair<Integer, Integer>> lastCoords,
			StringBuilder currentWord, int x, int y) {
		if(y >= numRows || x >= numColumns) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getDiagonalDescendingWords(lastCoords, currentWord, x+1, y+1);
	}
	
	/**
	 * Gets all diagonal descending reverse words that can be formed from a coordinate on this boggleBoard.
	 * @param lastCoords The ArrayList of coordinates that were used for the previous recursive call.
	 * Should be an empty ArrayList for first call.
	 * @param currentWord The StringBuilder containing the word that was formed for the previous recursive call.
	 * Should be an empty StringBuilder for first call.
	 * @param x The x coordinate to check.
	 * @param y The y coordinate to check.
	 */
	private void getDiagonalDescendingReverseWords(ArrayList<Pair<Integer, Integer>> lastCoords,
			StringBuilder currentWord, int x, int y) {
		if(y < 0 || x < 0) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getDiagonalDescendingReverseWords(lastCoords, currentWord, x-1, y-1);
	}

	/**
	 * Gets all vertical words that can be formed from a coordinate on this boggleBoard.
	 * @param lastCoords The ArrayList of coordinates that were used for the previous recursive call.
	 * Should be an empty ArrayList for first call.
	 * @param currentWord The StringBuilder containing the word that was formed for the previous recursive call.
	 * Should be an empty StringBuilder for first call.
	 * @param x The x coordinate to check.
	 * @param y The y coordinate to check.
	 */
	private void getVerticalWords(ArrayList<Pair<Integer, Integer>> lastCoords, 
			StringBuilder currentWord, int x, int y) {
		if(y >= numRows) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getVerticalWords(lastCoords, currentWord, x, y+1);
	}

	/**
	 * Gets all vertical reverse words that can be formed from a coordinate on this boggleBoard.
	 * @param lastCoords The ArrayList of coordinates that were used for the previous recursive call.
	 * Should be an empty ArrayList for first call.
	 * @param currentWord The StringBuilder containing the word that was formed for the previous recursive call.
	 * Should be an empty StringBuilder for first call.
	 * @param x The x coordinate to check.
	 * @param y The y coordinate to check.
	 */
	private void getVerticalReverseWords(ArrayList<Pair<Integer, Integer>> lastCoords, 
			StringBuilder currentWord, int x, int y) {
		if(y < 0) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getVerticalReverseWords(lastCoords, currentWord, x, y-1);
	}
	
	/**
	 * Gets all horizontal words that can be formed from a coordinate on this boggleBoard.
	 * @param lastCoords The ArrayList of coordinates that were used for the previous recursive call.
	 * Should be an empty ArrayList for first call.
	 * @param currentWord The StringBuilder containing the word that was formed for the previous recursive call.
	 * Should be an empty StringBuilder for first call.
	 * @param x The x coordinate to check.
	 * @param y The y coordinate to check.
	 */
	private void getHorionztalWords(ArrayList<Pair<Integer, Integer>> lastCoords, 
			StringBuilder currentWord, int x, int y) {
		if(x >= numColumns) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getHorionztalWords(lastCoords, currentWord, x+1, y);
	}
	
	/**
	 * Gets all horizontal reverse words that can be formed from a coordinate on this boggleBoard.
	 * @param lastCoords The ArrayList of coordinates that were used for the previous recursive call.
	 * Should be an empty ArrayList for first call.
	 * @param currentWord The StringBuilder containing the word that was formed for the previous recursive call.
	 * Should be an empty StringBuilder for first call.
	 * @param x The x coordinate to check.
	 * @param y The y coordinate to check.
	 */
	private void getHorionztalReverseWords(ArrayList<Pair<Integer, Integer>> lastCoords, 
			StringBuilder currentWord, int x, int y) {
		if(x < 0) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getHorionztalReverseWords(lastCoords, currentWord, x-1, y);
	}
	
	/**
	 * Adds a new word to wordMap. To be used along with a recursive getWords() function.
	 * @param lastCoords The ArrayList of coordinates that was last used to add a word
	 * @param currentWord The StringBuilder that was last used to add a word 
	 * @param x The x coordinate of the new character to add
	 * @param y The y coordinate of the new character to add
	 * @return A new ArrayList of coordinates representing the newly added word
	 */
	private ArrayList<Pair<Integer, Integer>> addEntryToMap(ArrayList<Pair<Integer, Integer>> lastCoords, 
			StringBuilder currentWord, int x, int y) {
		Character currentChar = boggleBoard.get(y).get(x);
		currentWord.append(currentChar);
		String key = new String(currentWord.toString());
		
		ArrayList<Pair<Integer, Integer>> value = new ArrayList<Pair<Integer, Integer>>(lastCoords);
		value.add(Pair.of(x, y));
		
		wordMap.put(key, value);
		return value;
	}
	
	/**
	 * Finds all wordsToSearchFor in this boggleBoard and return them as a formatted string.
	 * @return A formatted String of found words and their coordinates.
	 */
	public String findWordsToSearchFor() {
		StringBuilder result = new StringBuilder();
		
		for(String word: wordsToSearchFor) {
			result.append(findWord(word));
			result.append("\n");
		}
		result.deleteCharAt(result.length() - 1);
		
		return result.toString();
	}
	
	/**
	 * Gets this boggleBoard as a formatted string.
	 * @return A formatted string representing this boggleBoard, following csv format
	 */
	public String getBoggleBoardString() {
		StringBuilder result = new StringBuilder();
		
		for(ArrayList<Character> line: boggleBoard) {
			for(Character c: line) {
				result.append(c + ",");
			}
			result.deleteCharAt(result.length() - 1);
			result.append('\n');
		}
		result.deleteCharAt(result.length() - 1);
		
		return result.toString();
	}
	
	/**
	 * Gets the list of this wordsToSearchFor as a formatted string.
	 * @return A string containing all of the words from wordsToSearchFor, separated by commas.
	 */
	public String getWordsToSearchForString() {
		StringBuilder result = new StringBuilder();
		
		for(String word: wordsToSearchFor) {
			result.append(word + ",");
		}
		result.deleteCharAt(result.length() - 1);
		
		return result.toString();
	}
	
	/**
	 * Finds a given word in this boggleBoard. 
	 * Returns a formatted String of the found word and its coordinates.
	 * Will return an empty string if no word is found.
	 * @param word The word to find in this boggleBoard
	 * @return A formatted string containing the results of the word search.
	 */
	public String findWord(String word) {
		StringBuilder result = new StringBuilder();
		
		ArrayList<Pair<Integer, Integer>> coords = wordMap.get(word);
		if(coords != null) {
			result.append(word + ": ");
			for(Pair<Integer, Integer> p: coords) {
				result.append("(" + p.getKey() + "," + p.getValue() + "),");
			}
			result.deleteCharAt(result.length() - 1);
		}
		
		return result.toString();
	}

}
