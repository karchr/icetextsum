package is.ru.nlp.textsum.util;

import java.util.regex.Pattern;

public class StringUtil {
		
	/**
	 * Splits sentences by '.' takes simple abbreviations into account
	 * 
	 * @param 	text
	 * @return 	String array of sentences separated by '.'
	 */
	public static String[] splitIntoSentences(String text){
		return Pattern.compile( "(?<!\\.[a-zA-Z0-9])(?<![A-Z0-9]{1})\\.(?!\\.[a-zA-Z0-9]\\.) | [\\.\\\" +]" ).split( text ); // | [\\.\\\" +] (\"{0,1})(\\s*)
		//						"(?<!\\.[a-zA-Z])(?<![A-Z]{1})\\.(?!\\.[a-zA-Z]\\.) | [\\.\\\" +]"
	}
	
	
	/**
	 * Splits sentences by newline
	 * 
	 * @param 	text
	 * @return 	String array of sentences separated by newline
	 */
	public static String[] splitIntoSentencesByNewLine(String text){
		return Pattern.compile( "(\n+$)" ).split( text ); 
	}
}
