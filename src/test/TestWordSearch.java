package test;

import org.junit.Test;

import wordSearch.WordSearch;

public class TestWordSearch {
	private WordSearch wordSearchTest;
	private WordSearch wordSearchParallelTest;
	private String searchResult;
	private String parallelSearchResult;
	
	public TestWordSearch() {
		wordSearchTest = new WordSearch(TestWordSearchStrings.firstTestFile);
		wordSearchParallelTest = new WordSearch(TestWordSearchStrings.firstTestFile, true);
		searchResult = wordSearchTest.findWordsToSearchFor();
		parallelSearchResult = wordSearchParallelTest.findWordsToSearchFor();
	}
	
	@Test
	public void testConstructionAndBoggleBoardPrint() {
		assert(wordSearchTest.getBoggleBoardString().equals(TestWordSearchStrings.firstBoggleBoardString));
	}
	
	@Test
	public void testConstructionAndWordsToSearchForPrint() {
		assert(wordSearchTest.getWordsToSearchForString().equals(TestWordSearchStrings.firstWordsToSearchForString));
	}
	
	@Test
	public void testFindWordHorizontal() {
		String searchResult = wordSearchTest.findWord("SCOTTY");
		assert(searchResult.equals(TestWordSearchStrings.firstHorizontalCorrectResult));
		
		String parallelSearchResult = wordSearchParallelTest.findWord("SCOTTY");
		assert(parallelSearchResult.equals(TestWordSearchStrings.firstHorizontalCorrectResult));
	}
	
	
	@Test
	public void testFindWordsToSearchForVertical() {
		String searchResult = wordSearchTest.findWord("BONES");
		assert(searchResult.equals(TestWordSearchStrings.firstVerticalCorrectResult));
		
		String parallelSearchResult = wordSearchParallelTest.findWord("BONES");
		assert(parallelSearchResult.equals(TestWordSearchStrings.firstVerticalCorrectResult));
	}
	
	@Test
	public void testFindWordsToSearchForDiagonalAscending() {
		String searchResult = wordSearchTest.findWord("NED");
		assert(searchResult.equals(TestWordSearchStrings.firstDiagonalAscendingCorrectResult));
		
		String parallelSearchResult = wordSearchParallelTest.findWord("NED");
		assert(parallelSearchResult.equals(TestWordSearchStrings.firstDiagonalAscendingCorrectResult));
	}
	
	@Test
	public void testFindWordsToSearchForDiagonalDescending() {
		String searchResult = wordSearchTest.findWord("SPOCK");
		assert(searchResult.equals(TestWordSearchStrings.firstDiagonalDescendingCorrectResult));
		
		String parallelSearchResult = wordSearchParallelTest.findWord("SPOCK");
		assert(parallelSearchResult.equals(TestWordSearchStrings.firstDiagonalDescendingCorrectResult));
	}
	
	@Test
	public void testFindWordsToSearchForHorizontalReverse() {
		String searchResult = wordSearchTest.findWord("KIRK");
		assert(searchResult.equals(TestWordSearchStrings.firstHorizontalReverseCorrectResult));
		
		String parallelSearchResult = wordSearchParallelTest.findWord("KIRK");
		assert(parallelSearchResult.equals(TestWordSearchStrings.firstHorizontalReverseCorrectResult));
	}
	
	@Test
	public void testFindWordsToSearchForVerticalReverse() {
		String searchResult = wordSearchTest.findWord("KHAN");
		assert(searchResult.equals(TestWordSearchStrings.firstVerticalReverseCorrectResult));
		
		String parallelSearchResult = wordSearchParallelTest.findWord("KHAN");
		assert(parallelSearchResult.equals(TestWordSearchStrings.firstVerticalReverseCorrectResult));
	}
	
	@Test
	public void testFindWordsToSearchForDiagonalAscendingReverse() {
		String searchResult = wordSearchTest.findWord("UHURA");
		assert(searchResult.equals(TestWordSearchStrings.firstDiagonalAscendingReverseCorrectResult));
		
		String parallelSearchResult = wordSearchParallelTest.findWord("UHURA");
		assert(parallelSearchResult.equals(TestWordSearchStrings.firstDiagonalAscendingReverseCorrectResult));
	}
	
	@Test
	public void testFindWordsToSearchForDiagonalDescendingReverse() {
		String searchResult = wordSearchTest.findWord("SULU");
		assert(searchResult.equals(TestWordSearchStrings.firstDiagonalDescendingReverseCorrectResult));
		
		String parallelSearchResult = wordSearchParallelTest.findWord("SULU");
		assert(parallelSearchResult.equals(TestWordSearchStrings.firstDiagonalDescendingReverseCorrectResult));
	}
	
	@Test
	public void testFindWordsToSearchFor() {
		assert(searchResult.equals(TestWordSearchStrings.firstCorrectResult));
		assert(parallelSearchResult.equals(TestWordSearchStrings.firstCorrectResult));
	}

}
