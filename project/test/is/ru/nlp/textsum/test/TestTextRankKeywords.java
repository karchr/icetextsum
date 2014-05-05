package is.ru.nlp.textsum.test;

import static org.junit.Assert.assertTrue;
import is.ru.nlp.textsum.NLPUtil.Language;
import is.ru.nlp.textsum.unsupervised.TextRankKeywords;
import is.ru.nlp.textsum.util.ReadInFile;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestTextRankKeywords {
	
	private ReadInFile readIn;
	private final String TEST_KEYWORDS = "Birgir H. Arason sauðfjárbóndi, "
										+ "150-250 metra hæð, Veðurstofa Íslands, "
										+ "komandi helgi, mikilli rigningu, 18-25 m, "
										+ "100-200 metrum, 15-23 m, göngur, Gullbrekku, "
										+ "menn, tánum, snjókomu, sjávarmáli, ";
	
	@Before
    public void setUp(){
		readIn = new ReadInFile();
	}
	
	@Test
	public void testTextRankKeywords(){
		
		final File folder = new File("./resources/keywords.txt");
		List<String> lines = readIn.readSmallFileLines(folder.getPath());
		StringBuilder fullText = new StringBuilder();
		
		for ( String s : lines ) {
			 fullText.append(s).append(" ");
		}
    	
		TextRankKeywords tr = new TextRankKeywords();
		String keywords = tr.createKeywodsExtraction(fullText.toString(), Language.ICELANDIC);
		//System.out.println(keywords);
		
		assertTrue(keywords.equals(TEST_KEYWORDS));
		
	}

}
