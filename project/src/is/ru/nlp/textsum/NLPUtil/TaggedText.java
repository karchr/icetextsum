package is.ru.nlp.textsum.NLPUtil;

import java.io.IOException;

import is.iclt.icenlp.core.icetagger.IceTagger;
import is.iclt.icenlp.core.lemmald.LemmaResult;
import is.iclt.icenlp.core.lemmald.Lemmald;
import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Sentence;
import is.iclt.icenlp.core.tokenizer.Sentences;
import is.iclt.icenlp.core.tokenizer.Token;
import is.iclt.icenlp.facade.IceTaggerFacade;
import is.ru.nlp.textsum.NLPUtil.Language;

/**
 * Tags a text with its part-of-speech tags.
 * 
 * Supports tagging of Icelandic and English texts
 * 
 * @author Karin Christiansen, karin12@ru.is
 *
 */
public class TaggedText {
	
	
	private IceTaggerFacade iceTaggerFacade;
	private Sentences sentences;
	private String[] sentencesEN;
	private LanguageEnglish lang = null;

	private final String EN_LANGAUGE_RESOURCES = "resources/en/";
	
	public TaggedText(String text, Language language) {	
		try {
			if(Language.ENGLISH.equals(language)){
				lang = new LanguageEnglish(EN_LANGAUGE_RESOURCES);
				tagEnglishText(text);
			} else if(Language.ICELANDIC.equals(language)){
				tagIcelandicText(text);
			} else {
				throw new UnsupportedOperationException(language + " not supported");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void tagIcelandicText(String text){

    	Lemmald lemmald = Lemmald.getInstance();
    
		//none - for Icetagger only, fastest option
    	//Using the other options start, end and startend makes the tagging 
    	//very slow, further using start and startend does give proper nouns
    	//a high score in some cases
		//startend - using HMM that takes into account start and end
    	//move out not be in const. then init for every file not nessary
		try {
			iceTaggerFacade = new IceTaggerFacade(IceTagger.HmmModelType.none);
			sentences = iceTaggerFacade.tag(text);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		for(Sentence s : sentences.getSentences()){
			for(Token t : s.getTokens()){
				IceTokenTags itt = (IceTokenTags)t;
		    	
				LemmaResult lemmaResult = lemmald.lemmatize(t.lexeme, itt.getFirstTagStr());
				itt.getFirstTag().setLemma(lemmaResult.getLemma());
				//t.lexeme = lemmaResult.getLemma();
			}
		}
	}

	public void tagEnglishText(String text) {		
    	sentencesEN = lang.splitParagraph(text);
	}
	
	public String[] tokenList(String text){
		String[] tokens = lang.tokenizeSentence(text);
		return tokens;
	}
	
	public String[] tagList(String[] text){
		String[] tokens = lang.tagTokens(text);
		return tokens;
	}
	
	/**
	 * @return the sentencesEN
	 */
	public String[] getSentencesEN() {
		return sentencesEN;
	}

	/**
	 * @return the sentences
	 */
	public Sentences getSentences() {
		return sentences;
	}

	/**
	 * @param sentences the sentences to set
	 */
	public void setSentences(Sentences sentences) {
		this.sentences = sentences;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < sentences.size(); i++){
		for(int j = 0; j < sentences.getSentences().get(i).getTokens().size(); j++){
			sb.append(sentences.getSentences().get(i).getTokens().get(j).toString()).append(" ");
		}
	}
		return sb.toString();
	}
	
	public String toStringEN(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < sentencesEN.length; i++){
			sb.append(sentencesEN[i]).append(" ");
		}
		return sb.toString();
	}
}
