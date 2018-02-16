package test;

import java.io.IOException;

import org.junit.Test;

import wordSearch.WordSearch;

public class TestWordSearch {
	private WordSearch wordSearchTest;
	private String searchResult;
	
	public TestWordSearch() throws IOException {
		wordSearchTest = new WordSearch(TestWordSearchStrings.firstTestFile);
		searchResult = wordSearchTest.findWordsToSearchFor();
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
	}
	
	
	@Test
	public void testFindWordsToSearchForVertical() {
		String searchResult = wordSearchTest.findWord("BONES");
		assert(searchResult.equals(TestWordSearchStrings.firstVerticalCorrectResult));
	}
	
	@Test
	public void testFindWordsToSearchForDiagonalAscending() {
		String searchResult = wordSearchTest.findWord("NED");
		assert(searchResult.equals(TestWordSearchStrings.firstDiagonalAscendingCorrectResult));
	}
	
	@Test
	public void testFindWordsToSearchForDiagonalDescending() {
		String searchResult = wordSearchTest.findWord("SPOCK");
		assert(searchResult.equals(TestWordSearchStrings.firstDiagonalDescendingCorrectResult));
	}
	
	@Test
	public void testFindWordsToSearchForHorizontalReverse() {
		String searchResult = wordSearchTest.findWord("KIRK");
		assert(searchResult.equals(TestWordSearchStrings.firstHorizontalReverseCorrectResult));
	}
	
	@Test
	public void testFindWordsToSearchForVerticalReverse() {
		String searchResult = wordSearchTest.findWord("KHAN");
		assert(searchResult.equals(TestWordSearchStrings.firstVerticalReverseCorrectResult));
	}
	
	@Test
	public void testFindWordsToSearchForDiagonalAscendingReverse() {
		String searchResult = wordSearchTest.findWord("UHURA");
		assert(searchResult.equals(TestWordSearchStrings.firstDiagonalAscendingReverseCorrectResult));
	}
	/*
	@Test
	public void testFindWordsToSearchForDiagonalDescendingReverse() {
		
	}
	
	@Test
	public void testFindWordsToSearchFor() {
		assert(wordSearchTest.getWordsToSearchForString().equals(TestWordSearchStrings.firstCorrectResult));
	}
	*/
}
