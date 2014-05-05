package is.ru.nlp.textsum.unsupervised;

import is.iclt.icenlp.core.lemmald.LemmaResult;
import is.iclt.icenlp.core.lemmald.Lemmald;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Sentence;
import is.iclt.icenlp.core.tokenizer.Token;
import is.ru.nlp.textsum.NLPUtil.Language;
import is.ru.nlp.textsum.util.ReadInFile;
import is.ru.nlp.textsum.NLPUtil.TaggedText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Calculate the TF score for a term i in a document j (Dj)
 * 
 * @author Karin Christiansen, karin12@ru.is
 *
 */
public class TF {
	
	private ReadInFile readIn;
	private Map<String, Integer> term_count = new TreeMap<String, Integer>();
	private TaggedText taggedText;
	private Lemmald lemmald;
	private int wordCount = 0;
	
	private final Language DEFAULT_LANGUAGE = Language.ICELANDIC;
	
	public TF() throws IOException{
		this.lemmald = Lemmald.getInstance();
	}
	 /**
	  * 
	  * @param fullText
	  * @param lemmatized
	  * @throws IOException
	  */
	public void countTermsInDocument(String fullText, boolean lemmatized) throws IOException{

		if ( lemmatized ){
			countTermsLemmatized(fullText);
		} else {
			countTerms(fullText);
		}
		//printTFCounts();
	}
	
	/**
	 * Counts the terms in a document
	 * 
	 * @return 		Term count
	 */
	public int getNumberOfTermsInDocument(){
		return term_count.size();
	}
	
	/**
	 * Count the number of words in the text.
	 * 
	 * @param text		Text
	 * @return			Number of words in text
	 */
	public int getWordCount(String text) {
		String[] words = Pattern.compile("\\s+").split(text);
		return words.length;
	}
	
	/**
	 * Counts the occurrences of terms of the word classes
	 * nouns and adjectives. All other terms are given the 
	 * count 0.
	 * 
	 * Adds all terms with counts to the set term_count
	 * 
	 * @param fullText	  	Text/Document Dj
	 */
	private void countTerms(String fullText){
			
			taggedText = new TaggedText(fullText, DEFAULT_LANGUAGE);
			wordCount = getWordCount(fullText); 

			for(Sentence sen : taggedText.getSentences().getSentences()){
				ArrayList<Token> tokenList = sen.getTokens();
	
				for(Token t : tokenList){
					if(!t.isPunctuation() && !t.isQuote() && !t.isEOS()) {
						
						IceTokenTags itt = (IceTokenTags)t;
						
						if (itt.isNoun() || itt.isAdjective()){
							Integer frequency = (Integer) term_count.get(t.lexeme.toLowerCase());
							term_count.put(t.lexeme.toLowerCase(), frequency == null ? 1 : frequency.intValue() + 1);
						} else {
							term_count.put(t.lexeme.toLowerCase(), 0);
						}
					}
				}
			}
			//printTFCounts();
	}
	
	private void countTermsLemmatized(String fullText){
			
			taggedText = new TaggedText(fullText, DEFAULT_LANGUAGE);
			wordCount = getWordCount(fullText); 
		
			for(Sentence sen : taggedText.getSentences().getSentences()){
	    		for(Token t : sen.getTokens()){
	    			if(!t.isPunctuation() && !t.isQuote() && !t.isEOS()) {
						IceTokenTags itt = (IceTokenTags)t;
						LemmaResult lemmaResult = lemmald.lemmatize(t.lexeme, itt.getFirstTagStr());
						if ( itt.isNoun() || itt.isAdjective()) {					
							Integer frequency = (Integer) term_count.get(lemmaResult.getLemma().toLowerCase());
							term_count.put(lemmaResult.getLemma().toLowerCase(), frequency == null ? 1 : frequency.intValue() + 1);
						} else {
							term_count.put(lemmaResult.getLemma().toLowerCase(), 0);
						}
					}
				}	
	    	}
	}
	
	private void printTFCounts(){
		for(Map.Entry<String, Integer> entry : term_count.entrySet()) {
			System.out.println(entry.getKey() + " => " + entry.getValue());
		}
	}
	
	private void printTFCountsToFile(String outFileName) {
		readIn = new ReadInFile();
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, Integer> entry : term_count.entrySet()) {
			sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
		}
		readIn.writeFileBytes(outFileName, sb.toString());
	}
	
	/**
	 * Returns the count of a Term Ti in the Document Dj
	 * 
	 * @param term 	Term Ti
	 * @return		The count of occurrences of the term Ti in the Document Dj
	 */
	public int getTermCountInDocument(String term) {
		return term_count.get(term) == null ? 0 : term_count.get(term);
	}
	
	/**
	 * Returns the count of a Term Ti in the Document Dj divided
	 * by the total number of terms in the Document Dj for 
	 * normalization. 
	 * 
	 * @param term    	The term Ti in Dj
	 * @return			The count of occurrences of the term Ti divided by D_terms in Dj
	 */
	public double getTF(String term) {
		if(getNumberOfTermsInDocument() == 0){
			return 0.0;
		}
		
		return (double)getTermCountInDocument(term) / (double)getNumberOfTermsInDocument();
	}

	/**
	 * @return the wordCount
	 */
	public int getWordCount() {
		return wordCount;
	}	
}
