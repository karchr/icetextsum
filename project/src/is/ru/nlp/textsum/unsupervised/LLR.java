package is.ru.nlp.textsum.unsupervised;

import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Sentence;
import is.iclt.icenlp.core.tokenizer.Sentences;
import is.iclt.icenlp.core.tokenizer.Token;
import is.ru.nlp.textsum.NLPUtil.Language;
import is.ru.nlp.textsum.NLPUtil.TaggedText;
import is.ru.nlp.textsum.util.FileExtensionFilter;
import is.ru.nlp.textsum.util.ReadInFile;
import is.ru.nlp.textsum.util.TextLength;
import is.ru.nlp.textsum.util.TokenizeSentences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.apache.mahout.math.stats.LogLikelihood;

/**
 * Unsupervised content selection - using log-likelihood ratio.
 * 
 * @author Karin Christiansen, karin12@ru.is
 *
 */
public class LLR {
	
	private Map<String, Integer> observedFrequency = new TreeMap<String, Integer>();
	private Map<String, Integer> backgroundFrequency = new TreeMap<String, Integer>();
	private String summary = "";
	private int wordCount = 0;
	private TaggedText taggedText;
	private ReadInFile readIn = new ReadInFile();
	private final Language DEFAULT_LANGUAGE = Language.ICELANDIC;
	private static final String[] FILE_EXT = new String[] {"txt"};
	
	public LLR(){
	}
	
	private Map<String, Integer> frequency(String fullText, Map<String, Integer> termfrequency){
		
		taggedText = new TaggedText(fullText, DEFAULT_LANGUAGE);
		wordCount = getWordCount(fullText); 

		for(Sentence sen : taggedText.getSentences().getSentences()){
			ArrayList<Token> tokenList = sen.getTokens();

			for(Token t : tokenList){	
				if(!t.isPunctuation() && !t.isQuote() && !t.isEOS()) {
					IceTokenTags itt = (IceTokenTags)t;
					
					if (itt.isNoun() || itt.isAdjective() || itt.isNumeral() || itt.isProperNoun()){
						Integer frequency = (Integer) termfrequency.get(t.lexeme.toLowerCase());
						termfrequency.put(t.lexeme.toLowerCase(), frequency == null ? 1 : frequency.intValue() + 1);
					} 
				}
			}
		}
		return termfrequency;
	}
	
public void loadBackgroundFrequencyFile(String fileName) {

		List<String> resources = readIn.readSmallFileLines(fileName);
		if(resources != null) {
			for( int i = 1; i < resources.size(); i++) {
				try {
				String key = resources.get(i).substring(0, resources.get(i).indexOf(" ")); //change to space " "
				String value = resources.get(i).substring(resources.get(i).indexOf(" ") + 1, resources.get(i).length());
				backgroundFrequency.put(key, Integer.valueOf(value));
				} catch (StringIndexOutOfBoundsException ss ) {
					System.err.println(i);
				}
			}
		}
	}
	
public void buildBackgroundFrequencyFile(String outFileName, String inFiles) {
		
		final File fullTextFiles = new File(inFiles);
		File files[] = null;
		if (fullTextFiles.isDirectory()) {
			files = fullTextFiles.listFiles(new FileExtensionFilter(FILE_EXT));
		} else {
			files = new File[1];
			files[0] = fullTextFiles;
		}
			
		for ( File f : files ) {
			ReadInFile readIn = new ReadInFile();
			List<String> lines = readIn.readSmallFileLines(f.getPath());
	    	StringBuilder fullText = new StringBuilder();
	    	
	    	for(String s : lines){
	    		fullText.append(s).append(" ");
	    	}
	    	
	    	frequency(fullText.toString(), backgroundFrequency);
		}
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, Integer> entry : backgroundFrequency.entrySet()) {
			sb.append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");
		}

		readIn.writeFileBytes(outFileName, sb.toString());
	}
	
	public int getWordCount(String text) {
		String[] words = Pattern.compile("\\s+").split(text);
		return words.length;
	}
	   
	/**
	 * @return the wordCount
	 */
	public int getWordCount() {
		return observedFrequency.size();
	}
	
	public String createSummary(String fullText, int minSizeInPercent, int minLengthInWords) {
		  
		try {
			
			loadBackgroundFrequencyFile("resources/LLR/background.txt");

	    	
	    	frequency(fullText, observedFrequency);
			
			Map<String, Double> scoreMap = new HashMap<String, Double>();
			TokenizeSentences tokenizeSentences = new TokenizeSentences(3); //3 for splitting into sentences within paragraph
	    	//String[] sentences = StringUtil.splitIntoSentences( fullText );
	    	
	    	Sentences sentences = tokenizeSentences.tokenize(fullText);
	    	
	    	TextLength summaryLength = new TextLength(getWordCount());
	    	
			for ( Sentence s : sentences.getSentences() ) {
				  double score = 0;
				  ArrayList<Token> tokenList = s.getTokens(); //tokenizeSentences.tokenizeToToken(s);
				  if(tokenList.size() < 5 ) continue;
				  for ( Token t : tokenList ) {
					  	if(isIncludeWeight(t.lexeme)) {
					  		score += 1/tokenList.size();
					  	}
				  }
				scoreMap.put(s.toString(), score);
			}
			
			Map<String, Double> sortedScoreMap = sortByValues(scoreMap);
			int wordCount = 0;
			int percent = 0;
			
		//	if ( logger.isInfoEnabled()) {
		//	        logger.info("########################## Summary Generated by LLR " ##########################");
		//	}
			
			for(Map.Entry<String, Double> entry : sortedScoreMap.entrySet()) {

				//if ( logger.isInfoEnabled())
				//	    logger.info(entry.getKey());
			
				summary += entry.getKey() + "\n";
				String[] words = Pattern.compile("\\s+").split(entry.getKey());
	    		wordCount += words.length;
				percent = summaryLength.percentage(wordCount);
				if(percent >= minSizeInPercent && wordCount >= minLengthInWords)
					break;
			}
	
		} catch (IOException e) {
		//	logger.error("LLR.java: createSummary - IOException");
			e.printStackTrace();
		}
	return summary;
}

public static <K extends Comparable<String>,V extends Comparable<Double>> Map<K,V> sortByValues(Map<K,V> map){
    List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
  
    Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

       // @Override
        public int compare(Entry<K, V> o1, Entry<K, V> o2) {
            return o2.getValue().compareTo((Double) o1.getValue());
        }
    });
  
    //LinkedHashMap will keep the keys in the order they are inserted
    //which is currently sorted on natural ordering
    Map<K,V> sortedMap = new LinkedHashMap<K,V>();
  
    for(Map.Entry<K,V> entry: entries){
        sortedMap.put(entry.getKey(), entry.getValue());
    }
  
    return sortedMap;
}
	public boolean isIncludeWeight(String t) {
		if(Math.abs(calculateLLR(t)) > 10 ) return true;
		return false;
	}
	
	public double calculateLLR(String t) {
		int not_A = 0;
    	int not_B = 0;
    	for(Map.Entry<String, Integer> entry : backgroundFrequency.entrySet()) {
			not_B += entry.getKey().equalsIgnoreCase(t) ? 0 : entry.getValue();
		}
    	for(Map.Entry<String, Integer> entry : observedFrequency.entrySet()) {
    		not_A += entry.getKey().equalsIgnoreCase(t) ? 0 : entry.getValue();
		}
    	int not_AB = not_A + not_B;
		int A = observedFrequency.get(t) == null ? 0 : observedFrequency.get(t); 
		int B = backgroundFrequency.get(t) == null ? 0 : backgroundFrequency.get(t); 
		int AB = A + B;
		// A+B, A+!B, !A+B, !A+!B
		double llrTest = LogLikelihood.rootLogLikelihoodRatio(AB, not_A + B, not_B + A, not_AB);
    	//System.out.println("A: " + A + " B: " + B + " AB: " + AB + " !A+!B: " + not_AB);
		//System.out.println("llrTest : " + llrTest);
		
		return llrTest;
	}
	public static void main(String[] args) {
		LLR llr = new LLR();
		
		
		ReadInFile readIn = new ReadInFile();
		List<String> lines = readIn.readSmallFileLines("resources/full/ice/text1.txt");
    	StringBuilder fullText = new StringBuilder();
    	
    	for(String s : lines){
    		fullText.append(s).append(" ");
    	}
    	
    	String summary = llr.createSummary(fullText.toString(), 20, 100);
    	System.out.println(summary);
		//llr.createSummary(fullText.toString(), 0, 0);
		//llr.buildBackgroundFrequencyFile("resources/LLR/background.txt", "resources/background");	
	}
}
