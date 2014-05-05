package is.ru.nlp.textsum.util;

import is.iclt.icenlp.core.tokenizer.Segmentizer;
import is.iclt.icenlp.core.tokenizer.Sentence;
import is.iclt.icenlp.core.tokenizer.Sentences;
import is.iclt.icenlp.core.tokenizer.Tokenizer;
import is.iclt.icenlp.core.tokenizer.Token;
import is.iclt.icenlp.core.utils.Lexicon;
import is.ru.nlp.textsum.Menu;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TokenizeSentences {
	
    private Tokenizer tokenizer;
   	private Segmentizer segmentizer;
    private boolean strictTokenization = true;
    private final int DEFAULT_LINE_FOMAT = 3;
    private static final String LEXICON_PATH = "/lexicon.txt";
    //"C:/Users/Karin/Dropbox/thesis/IceNLPCore/dict/tokenizer/lexicon.txt";
    
    private Lexicon tokLexicon;// = new Lexicon(TokenizeSentences.class.getResourceAsStream(LEXICON_PATH));
    
    public TokenizeSentences(Lexicon tokenizerLexicon, int lineFormat) throws IOException
    {
        segmentizer = new Segmentizer(tokenizerLexicon, lineFormat);
        tokenizer = new Tokenizer(Tokenizer.typeToken, strictTokenization, tokenizerLexicon);
    }
    
    public TokenizeSentences(int lineFormat) throws IOException
    {
    	tokLexicon = new Lexicon(TokenizeSentences.class.getClass().getResourceAsStream(LEXICON_PATH));
        segmentizer = new Segmentizer(tokLexicon, lineFormat);
        tokenizer = new Tokenizer(Tokenizer.typeToken, strictTokenization, tokLexicon);
    }
    
    /**
     * Constructor: 
     * 		Sets Up: 
     * 			Segmentizer with lexicon
     * 			Tokenizer with type and lexicon
     * 
     * @throws IOException : Error reading Lexicon file - check file path
     */
    public TokenizeSentences() throws IOException
    {
    	tokLexicon = new Lexicon(TokenizeSentences.class.getResourceAsStream(LEXICON_PATH));
        segmentizer = new Segmentizer(tokLexicon, DEFAULT_LINE_FOMAT);
        tokenizer = new Tokenizer(Tokenizer.typeToken, strictTokenization, tokLexicon);
    }

	public Sentences tokenize( String text ) throws IOException
	    {
	        Sentence sent = null;
	        segmentizer.segmentize( text );

	        Sentences sents = new Sentences();

	        while( segmentizer.hasMoreSentences() )
	        {
	            String sentenceStr = segmentizer.getNextSentence();

	            if( !sentenceStr.equals( "" ) )
	            {
	                tokenizer.tokenize(sentenceStr);
	                if( tokenizer.tokens.size() <= 0 )
	                    continue;

	                //tokenizer.splitAbbreviations(); // don't need this?? Do need to include abbreviations??
	                sent = new Sentence(tokenizer.tokens);
	            }
	            sents.add(sent);
	        }
	        return sents;
	    }
	
	public List<String> tokenizeString( String text ) throws IOException
    {
 
        segmentizer.segmentize( text );
        List<String> sentences = new ArrayList<String>();
        while( segmentizer.hasMoreSentences() )
        {
            String sentenceStr = segmentizer.getNextSentence();

            if( !sentenceStr.equals( "" ) )
            {
                tokenizer.tokenize(sentenceStr);
                if( tokenizer.tokens.size() <= 0 )
                    continue;

                String sentence = "";
                for(Object s : tokenizer.tokens){
                	String tok = s.toString();
                
                	sentence += tok + " ";
                }
                sentences.add(sentence);
            }

        }
        return sentences;
    }
	
		public ArrayList<Token> tokenizeToToken( String text ) throws IOException
	    {
			ArrayList<Token> tokens = new ArrayList<Token>();
	        segmentizer.segmentize( text );
	
	        while( segmentizer.hasMoreSentences() )
	        {
	            String sentenceStr = segmentizer.getNextSentence();
	
	            if( !sentenceStr.equals( "" ) )
	            {
	                tokenizer.tokenize(sentenceStr);
	                if( tokenizer.tokens.size() <= 0 )
	                    continue;
	
	                tokenizer.splitAbbreviations();
	                tokens.addAll(tokenizer.tokens);
	            }
	        }
	        return tokens;
	    }
		
	    /**
		 * @return the tokenizer
		 */
		public Tokenizer getTokenizer() {
			return tokenizer;
		}
}
