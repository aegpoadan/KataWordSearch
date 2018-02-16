package wordSearch;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class WordSearch {
	private ArrayList<ArrayList<Character>> boggleBoard = new ArrayList<ArrayList<Character>>();
	private ArrayList<String> wordsToSearchFor = new ArrayList<String>();
	//first int in entry is x coordinate, second is y coordinate
	private DualHashBidiMap<String, Map.Entry<Integer, Integer>[]> wordMap = new DualHashBidiMap<String, Map.Entry<Integer, Integer>[]>();
	
	public WordSearch(String path) {
		try {
			String fileContent = new String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset());
			String[] lines = fileContent.split("\n");
			int i = 0;
			
			for(String line: lines) {
				String[] words = line.split(",");
				
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
		} catch(IOException E) {
			System.err.println("Failed to read file at path: " + path);
		}
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
		return null;
	}

}
