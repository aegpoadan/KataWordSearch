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
 * 
 * @author Andrew Hayes
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
	
	private void getDiagonalAscendingWords(ArrayList<Pair<Integer, Integer>> lastCoords,
			StringBuilder currentWord, int x, int y) {
		if(y < 0 || x >= numColumns) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getDiagonalAscendingWords(lastCoords, currentWord, x+1, y-1);
	}
	
	private void getDiagonalAscendingReverseWords(ArrayList<Pair<Integer, Integer>> lastCoords,
			StringBuilder currentWord, int x, int y) {
		if(y >= numRows || x < 0) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getDiagonalAscendingReverseWords(lastCoords, currentWord, x-1, y+1);
	}
	
	private void getDiagonalDescendingWords(ArrayList<Pair<Integer, Integer>> lastCoords,
			StringBuilder currentWord, int x, int y) {
		if(y >= numRows || x >= numColumns) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getDiagonalDescendingWords(lastCoords, currentWord, x+1, y+1);
	}
	
	private void getDiagonalDescendingReverseWords(ArrayList<Pair<Integer, Integer>> lastCoords,
			StringBuilder currentWord, int x, int y) {
		if(y < 0 || x < 0) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getDiagonalDescendingReverseWords(lastCoords, currentWord, x-1, y-1);
	}

	private void getVerticalWords(ArrayList<Pair<Integer, Integer>> lastCoords, 
			StringBuilder currentWord, int x, int y) {
		if(y >= numRows) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getVerticalWords(lastCoords, currentWord, x, y+1);
	}

	private void getVerticalReverseWords(ArrayList<Pair<Integer, Integer>> lastCoords, 
			StringBuilder currentWord, int x, int y) {
		if(y < 0) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getVerticalReverseWords(lastCoords, currentWord, x, y-1);
	}
	
	private void getHorionztalWords(ArrayList<Pair<Integer, Integer>> lastCoords, 
			StringBuilder currentWord, int x, int y) {
		if(x >= numColumns) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getHorionztalWords(lastCoords, currentWord, x+1, y);
	}
	
	private void getHorionztalReverseWords(ArrayList<Pair<Integer, Integer>> lastCoords, 
			StringBuilder currentWord, int x, int y) {
		if(x < 0) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getHorionztalReverseWords(lastCoords, currentWord, x-1, y);
	}
	
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
	
	public String findWordsToSearchFor() {
		StringBuilder result = new StringBuilder();
		
		for(String word: wordsToSearchFor) {
			result.append(findWord(word));
			result.append("\n");
		}
		result.deleteCharAt(result.length() - 1);
		
		return result.toString();
	}
	
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
	
	public String getWordsToSearchForString() {
		StringBuilder result = new StringBuilder();
		
		for(String word: wordsToSearchFor) {
			result.append(word + ",");
		}
		result.deleteCharAt(result.length() - 1);
		
		return result.toString();
	}
	
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
