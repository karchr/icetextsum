package is.ru.nlp.textsum.test;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import is.iclt.icenlp.core.tokenizer.Sentences;
import is.ru.nlp.textsum.util.ReadInFile;
import is.ru.nlp.textsum.util.TokenizeSentences;

import org.junit.Before;
import org.junit.Test;

public class TestTokenizer {
	private TokenizeSentences ts;
	private List<Sentences> sentencesList;
	private List<String> lines;

    @Before
    public void setUp() throws Exception {
    	ReadInFile readIn = new ReadInFile();
    	ts = new TokenizeSentences();
    	sentencesList = new ArrayList<Sentences>();
    	lines = readIn.readSmallFileLines("\\resources\\text1.txt");
    }
    
    @Test
    public void testSentences(){
    	try {
	    	
	    	assertFalse(lines.isEmpty());
	    	
	    	for(String s : lines){
	    		if(!s.isEmpty())
	    			sentencesList.add(ts.tokenize(s));
	    	}	
	    	assertFalse(sentencesList.isEmpty());
	    	
	    	//for(Sentences s : sentencesList)
	    			//System.out.print(s.toString());
    	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Test
    public void testSentencesGraph(){
    	try {
	    	for(String s : lines){
	    		if(!s.isEmpty())
	    			sentencesList.add(ts.tokenize(s));
	    	}
	    	
	    //	Graph graph = new Graph(sentencesList);
	    	
		//	System.out.println(graph.getV());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
