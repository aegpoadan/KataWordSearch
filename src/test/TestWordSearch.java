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
	/*
	@Test
	public void testFindWordsToSearchForDiagonalAscending() {
		
	}
	
	@Test
	public void testFindWordsToSearchForDiagonalDescending() {
		
	}
	
	@Test
	public void testFindWordsToSearchForHorizontalReverse() {
		
	}
	
	@Test
	public void testFindWordsToSearchForVerticalReverse() {
		
	}
	
	@Test
	public void testFindWordsToSearchForDiagonalAscendingReverse() {
		
	}
	
	@Test
	public void testFindWordsToSearchForDiagonalDescendingReverse() {
		
	}
	
	@Test
	public void testFindWordsToSearchFor() {
		assert(wordSearchTest.getWordsToSearchForString().equals(TestWordSearchStrings.firstCorrectResult));
	}
	*/
}
