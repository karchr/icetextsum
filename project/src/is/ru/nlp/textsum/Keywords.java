package is.ru.nlp.textsum;

import is.ru.nlp.textsum.unsupervised.TextRankKeywords;
import is.ru.nlp.textsum.unsupervised.Type;
import is.ru.nlp.textsum.NLPUtil.Language;
import is.ru.nlp.textsum.util.ReadInFile;

import java.util.List;

/**
 * @author Karin
 *
 */
public class Keywords {

	private String keywords = "";
	
	public Keywords(String keywords) {
		this.keywords = keywords;
	}

	public static Keywords createKeywords(Language language, Type type, String fileName) {
		ReadInFile readIn = new ReadInFile();
		Keywords keywords = null;
		List<String> lines = readIn.readSmallFileLines(fileName);
    	StringBuilder fullText = new StringBuilder();
    	
    	for(String s : lines){
    		fullText.append(s).append(" ");
    	}

    	if (type.equals(Type.TEXTRANK)) {
	    	TextRankKeywords tr = new TextRankKeywords();
	    	keywords = new Keywords(tr.createKeywodsExtraction(fullText.toString(), language));	
		}
    	
    	return keywords;
	}

	/**
	 * @return the keywords
	 */
	public String getKeywords() {
		return keywords;
	}
}
