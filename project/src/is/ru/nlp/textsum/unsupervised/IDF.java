package is.ru.nlp.textsum.unsupervised;

import is.iclt.icenlp.core.lemmald.LemmaResult;
import is.iclt.icenlp.core.lemmald.Lemmald;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Sentence;
import is.iclt.icenlp.core.tokenizer.Token;
import is.ru.nlp.textsum.NLPUtil.Language;
import is.ru.nlp.textsum.util.ReadInFile;
import is.ru.nlp.textsum.NLPUtil.TaggedText;
import is.ru.nlp.textsum.util.FileExtensionFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Calculates the IDF score and generates a IDF score file.
 * 
 * @author Karin Christiansen, karin12@ru.is
 *
 */
public class IDF {
	
	private ReadInFile readIn = new ReadInFile();
	private TaggedText taggedText;
	private Map<String, Integer> idfCounts = new TreeMap<String, Integer>();
	private final String[] FILE_EXT = new String[] {"txt"};
	private final Language DEFAULT_LANGAUGE = Language.ICELANDIC;
	private final String DEFAULT_IDF_FILE = "resources/TFxIDF/IDF.txt";
	private Lemmald lemmald;
	private int totalNumberOfDocuments;
	private String IDFFileName;

	public IDF() {
		lemmald = Lemmald.getInstance();
		this.IDFFileName = DEFAULT_IDF_FILE; 
	}
	
	public IDF(String IDFFileName) {
		lemmald = Lemmald.getInstance();
		this.IDFFileName = IDFFileName; 
	}
	
	public void loadIDFFile(String fileName, boolean lemmatized) {
		
		if ( lemmatized ) { 
			fileName = fileName.substring(0, fileName.indexOf(".")) + 
				       "_lemmatized" + fileName.substring(fileName.indexOf("."), fileName.length()); 			
		}
		
		 BufferedReader reader = null;
         String text = "";
         List<String> resources = new ArrayList<String>(); //readIn.readSmallFileLines(fileName);
         try {
         	reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName)));
             String s = "";
             
             while ((text = reader.readLine()) != null) {
            	 resources.add(text);
             }
         }catch (IOException ioex) {
        	 
         }
		
		//List<String> resources = readIn.readSmallFileLines(fileName);
		if(resources != null) {
			//Number on first line of the file shows the number of total documents the IDF is generated from
			totalNumberOfDocuments = Integer.valueOf(resources.get(0) == null ? "0" : resources.get(0));
			for( int i = 1; i < resources.size(); i++) {
				try {
				String key = resources.get(i).substring(0, resources.get(i).indexOf(" ")); //change to space " "
				String value = resources.get(i).substring(resources.get(i).indexOf(" ") + 1, resources.get(i).length());
				idfCounts.put(key, Integer.valueOf(value));
				} catch (StringIndexOutOfBoundsException ss ) {
					System.err.println(i);
				}
			}
		}
		//printIDFCounts();
	}
	
	public double getIDF(String term) {
		if ( getTermCount(term) == 0 ){
			return 0.0;
		}
		return (double)getTotalNumberOfDocuments() / (double)getTermCount(term);
	}
	
	//documents with term in it
	//menn 19
	//b�ndur 1
	//� 20
	public void setUpIDF(String sourceFileDir, String outFileName, boolean lemmatized) {
	
		final File folder = new File(sourceFileDir);
		File files[] = folder.listFiles(new FileExtensionFilter(FILE_EXT));
		totalNumberOfDocuments = files.length;
		Set<String> terms = new HashSet<String>();
		
		for (int i = 0; i < files.length; i++) {
			
			if ( lemmatized ) {
				termsLemmatized(files[i], terms);
			} else {
				terms(files[i], terms);
			}		
			
			//Update IDF counts
			updateIDFCount(terms);
			terms.clear();
	    }
		if ( lemmatized ) { 
			outFileName = outFileName.substring(0, outFileName.indexOf(".")) + 
				          "_lemmatized" + outFileName.substring(outFileName.indexOf("."), outFileName.length()); 			
		}
		writeToIDFCountFile(outFileName);
	}
	
	private void terms(File file, Set<String> terms) {

		List<String> resources = readIn.readSmallFileLines(file.getPath());
		StringBuilder fullText = new StringBuilder();
		for( String s : resources ) {
			fullText.append(s).append(" ").append("\n"); 
		}
		
		taggedText = new TaggedText(fullText.toString(), DEFAULT_LANGAUGE);
		
		//local count
		for(Sentence sen : taggedText.getSentences().getSentences()){
			ArrayList<Token> tokenList = sen.getTokens();

			for(Token t : tokenList){
				if(!t.isPunctuation() && !t.isQuote() && !t.isEOS()) {
					IceTokenTags itt = (IceTokenTags)t;
					if (itt.isNoun() || itt.isAdjective())
						terms.add(t.lexeme.toLowerCase());
				}
			}
		}
	}
	
	private void termsLemmatized(File file, Set<String> terms) {
			List<String> resources = readIn.readSmallFileLines(file.getPath());
			StringBuilder fullText = new StringBuilder();
			for( String s : resources ) {
				fullText.append(s).append(" ").append("\n"); 
			}
			
			taggedText = new TaggedText(fullText.toString(), DEFAULT_LANGAUGE);

			for(Sentence sen : taggedText.getSentences().getSentences()){
	    		for(Token t : sen.getTokens()){
	    			if(!t.isPunctuation() && !t.isQuote() && !t.isEOS() ) {
						IceTokenTags itt = (IceTokenTags)t;
						if (itt.isNoun() || itt.isAdjective() ){
							LemmaResult lemmaResult = lemmald.lemmatize(t.lexeme, itt.getFirstTagStr());
							terms.add(lemmaResult.getLemma().toLowerCase());
						}
	    			}
				}	
	    	}
	}
	
	private void printIDFCounts(){
		for(Map.Entry<String, Integer> entry : idfCounts.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}
	
	private void writeToIDFCountFile(String outFileName){
		StringBuilder sb = new StringBuilder();
		sb.append(totalNumberOfDocuments).append("\n");
		for(Map.Entry<String, Integer> entry : idfCounts.entrySet()) {
			sb.append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");
		}
		readIn.writeFileBytes(outFileName, sb.toString());
	}
	
	private void updateIDFCount(Set<String> terms){
		for(String s : terms) {
			Integer frequency = (Integer) idfCounts.get(s);
			idfCounts.put(s, frequency == null ? 1 : frequency.intValue() + 1);
		}
	}
	
	public int getTermCount(String term) {
		return idfCounts.get(term) == null ? 0 : idfCounts.get(term);
	}
	
	/**
	 * @return the totalNumberOfDocuments
	 */
	public int getTotalNumberOfDocuments() {
		return totalNumberOfDocuments;
	}

	public static void main(String[] args) {
			IDF tf = new IDF();
			tf.setUpIDF("resources/TFxIDF/IDF_SOURCE_FILES/", tf.DEFAULT_IDF_FILE, true);
	}
}