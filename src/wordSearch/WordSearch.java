package wordSearch;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.tuple.Pair;

public class WordSearch {
	private ArrayList<ArrayList<Character>> boggleBoard = new ArrayList<ArrayList<Character>>();
	private ArrayList<String> wordsToSearchFor = new ArrayList<String>();
	//first int in entry is x coordinate, second is y coordinate
	private HashMap<String, ArrayList<Pair<Integer, Integer>>> wordMap = new HashMap<String, ArrayList<Pair<Integer, Integer>>>();
	private int numColumns;
	private int numRows;
	
	public WordSearch(String path) throws IOException {
		try {
			String fileContent = new String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset());
			String[] lines = fileContent.split("\n");
			numRows = lines.length - 1;
			numColumns = numRows;
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
			
			loadWordMap();
		} catch(IOException E) {
			System.err.println("Failed to read file at path: " + path);
		}
	}
	
	private void loadWordMap() {
		for(int x=0; x<numRows; x++) {
			for(int y=0; y<numColumns; y++) {
				getHorionztalWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
				getHorionztalReverseWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
				getVerticalWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
				getDiagonalAscendingWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
				getDiagonalDescendingWords(new ArrayList<Pair<Integer, Integer>>(), new StringBuilder(), x, y);
			}
		}
	}
	
	private void getDiagonalAscendingWords(ArrayList<Pair<Integer, Integer>> lastCoords,
			StringBuilder currentWord, int x, int y) {
		if(y <= 0 || x >= numColumns) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getDiagonalAscendingWords(lastCoords, currentWord, x+1, y-1);
	}
	
	private void getDiagonalDescendingWords(ArrayList<Pair<Integer, Integer>> lastCoords,
			StringBuilder currentWord, int x, int y) {
		if(y >= numRows || x >= numColumns) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getDiagonalDescendingWords(lastCoords, currentWord, x+1, y+1);
	}

	private void getVerticalWords(ArrayList<Pair<Integer, Integer>> lastCoords, StringBuilder currentWord, int x, int y) {
		if(y >= numRows) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getVerticalWords(lastCoords, currentWord, x, y+1);
	}

	private void getHorionztalWords(ArrayList<Pair<Integer, Integer>> lastCoords, StringBuilder currentWord, int x, int y) {
		if(x >= numColumns) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getHorionztalWords(lastCoords, currentWord, x+1, y);
	}
	
	private void getHorionztalReverseWords(ArrayList<Pair<Integer, Integer>> lastCoords, StringBuilder currentWord, int x, int y) {
		if(x <= 0) {
			return;
		}

		lastCoords = addEntryToMap(lastCoords, currentWord, x, y);
		getHorionztalReverseWords(lastCoords, currentWord, x-1, y);
	}
	
	private ArrayList<Pair<Integer, Integer>> addEntryToMap(ArrayList<Pair<Integer, Integer>> lastCoords, StringBuilder currentWord, int x, int y) {
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
