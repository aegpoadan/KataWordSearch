package wordSearch;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class WordSearch {
	private ArrayList<ArrayList<Character>> boggleBoard = new ArrayList<ArrayList<Character>>();
	private ArrayList<String> wordsToSearchFor = new ArrayList<String>();
	//first int in entry is x coordinate, second is y coordinate
	private DualHashBidiMap<String, Map.Entry<Integer, Integer>[]> wordMap = new DualHashBidiMap<String, Map.Entry<Integer, Integer>[]>();
	
	public WordSearch() {
		
	}

}
