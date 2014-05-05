package is.ru.nlp.textsum.unsupervised;

import is.ru.nlp.textsum.util.TextLength;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * Lead-based baseline summary.
 * 
 * Selects the n first words and n% in a text to create summary.
 * 
 * @author Karin Christiansen, karin12@ru.is
 *
 */

public class LeadBasedBaseline {

	private TextLength summarySize;
	private String[] words;
	private String summary = "";
	
	private final static Log logger = LogFactory.getLog(LeadBasedBaseline.class.getName());
	
	public LeadBasedBaseline( String fullText ) {
		words = Pattern.compile( "\\s+" ).split( fullText );
		summarySize = new TextLength( words.length );
	}
	
	public String createSummary(int minSummarySizePercent, int minWordCount){

		int calcPercent = 0;
		for ( int i = 0; i < words.length; i++ ) {			
				summary += words[i] + " ";
				calcPercent = summarySize.percentage( i );
				if( calcPercent >= minSummarySizePercent && i >= minWordCount && words[i].endsWith(".") ){
					break;
				}
		}
		
		if ( logger.isDebugEnabled())
    			logger.info(summary);
		
		return summary;
	}
}