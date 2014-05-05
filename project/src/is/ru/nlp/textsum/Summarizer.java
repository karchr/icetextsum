package is.ru.nlp.textsum;

import is.ru.nlp.textsum.unsupervised.LLR;
import is.ru.nlp.textsum.unsupervised.LeadBasedBaseline;
import is.ru.nlp.textsum.unsupervised.TFxIDF;
import is.ru.nlp.textsum.unsupervised.TextRankSummarization;
import is.ru.nlp.textsum.unsupervised.Type;
import is.ru.nlp.textsum.NLPUtil.Language;
import is.ru.nlp.textsum.util.ReadInFile;

import java.util.List;

/**
 * 
 * @author Karin Christiansen
 *
 */
public class Summarizer {
	
	private String summary = "";
	
	public Summarizer ( String summary ) {
		this.summary = summary;
	}

	public static Summarizer createSummary(Language language, Type type, String fileName, boolean lemmatized, int percent, int words) {
		ReadInFile readIn = new ReadInFile();
		Summarizer summary = null;
		List<String> lines = readIn.readSmallFileLines(fileName);
    	StringBuilder fullText = new StringBuilder();
    	
    	for(String s : lines){
    		fullText.append(s).append(" ");
    	}
	
		if (type.equals(Type.TEXTRANK)) {
			TextRankSummarization tr = new TextRankSummarization();
			summary = new Summarizer(tr.createSummary(fullText.toString(), language, percent, words));
							
	    } else if (type.equals(Type.BASELINE)) {
			LeadBasedBaseline leadBasedBaseline = new LeadBasedBaseline(fullText.toString());
			summary = new Summarizer(leadBasedBaseline.createSummary(percent, words));
	
		} else if (type.equals(Type.TFXIDF)) {
	    	TFxIDF tfxidf = new TFxIDF();
	    	summary = new Summarizer(tfxidf.createSummary(fullText.toString(), lemmatized, percent, words));
		} else if (type.equals(Type.LLR)) {
	    	LLR llr = new LLR();
	    	summary = new Summarizer(llr.createSummary(fullText.toString(), percent, words));
		}
		return summary;
	}

	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}
}