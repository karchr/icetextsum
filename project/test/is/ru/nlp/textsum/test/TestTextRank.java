package is.ru.nlp.textsum.test;

import static org.junit.Assert.assertTrue;
import is.ru.nlp.textsum.unsupervised.TextRankSummarization;
import is.ru.nlp.textsum.NLPUtil.Language;
import is.ru.nlp.textsum.util.ReadInFile;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestTextRank {
	
	private ReadInFile readIn;
	private String summary = "";
	private final String TEST_SUMMARY = "B�ndur um allt Nor�urland eru � t�num vegna illvi�rissp�r um komandi helgi . \n" +
											"��rarinn segir a� �egar s� byrja� a� tala vi� �ann mannskap sem fari� hefur � g�ngur s��ustu haust �annig a� menn ver�i � startholunum ef �urfa �ykir � fyrram�li� . \n" +
											"\" Sumir eru ekki b�nir a� heyja , �tlu�u s�r kannski a� n� seinni sl�tti , �annig a� a�st��ur manna eru misjafnar en menn komast n� alveg � gegnum �a� held �g . \" \n" +
											"\uFEFF B�ndur � t�num vegna illvi�rissp�r . \n" +
											"G�ngur g�tu hafist strax � morgun . \n" +
											"V��a er funda� � kv�ld og svo g�ti fari� a� g�ngur hefjist sumsta�ar strax � morgun . \n";
	
	@Before
    public void setUp(){
		readIn = new ReadInFile();
	}
	
	@Test
	public void testTextRank(){
		
		final File folder = new File("resources/full/ice/text1.txt");
		List<String> lines = readIn.readSmallFileLines(folder.getPath());
		StringBuilder fullText = new StringBuilder();
		
		for ( String s : lines ) {
			 fullText.append(s).append(" ");
		}
    	
		TextRankSummarization tr = new TextRankSummarization();
		summary = tr.createSummary(fullText.toString(), Language.ICELANDIC, 20, 100);
		System.out.println();
		System.out.println(summary);

		assertTrue(summary.equals(TEST_SUMMARY));
	}

}
