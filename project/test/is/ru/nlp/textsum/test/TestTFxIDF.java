package is.ru.nlp.textsum.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import is.ru.nlp.textsum.unsupervised.TFxIDF;
import is.ru.nlp.textsum.util.ReadInFile;

public class TestTFxIDF {
	
	private ReadInFile readIn;
	private final String TEST_SUMMARY = "Hann segir bændur í Höfðahverfi ætla að taka stöðuna eftir veðurspár kvöldsins, enn geti brugðið til beggja vona \n" +
			"Veðurstofa Íslands spáir norðanhríð á föstudag með slyddu eða snjókomu í 150-250 metra hæð yfir sjávarmáli og vindhraða allt að 15-23 m/s \n" +
			"Birgir er formaður Félags Sauðfjárbænda við Eyjafjörð og ætlar að heyra í mönnum í sveitinni í kvöld \n" +
			"\"Sumir eru ekki búnir að heyja, ætluðu sér kannski að ná seinni slætti, þannig að aðstæður manna eru misjafnar en menn komast nú alveg í gegnum það held ég.\" \n" +
			"Aðspurður segir Þórarinn allan gang á því hversu vel menn séu undir það búnir að taka féð heim á tún svo snemma \n";

	
	@Before
    public void setUp(){
		readIn = new ReadInFile();
	}
	
	@Test
	public void testTFxIDF() {
		final File folder = new File("./resources/full/ice/text1.txt");
		List<String> lines = readIn.readSmallFileLines(folder.getPath());
		StringBuilder fullText = new StringBuilder();
		
		for ( String s : lines ) {
			  fullText.append(s).append(" ");
		}
		
		TFxIDF tdxidf = new TFxIDF();
		
		String summary = tdxidf.createSummary(fullText.toString(), false, 20, 100);
		
		assertTrue(summary.equals(TEST_SUMMARY));
	}
	
	@Test
	public void testTFxIDFStemmed() {
		final File folder = new File("./resources/full/ice/text1.txt");
		List<String> lines = readIn.readSmallFileLines(folder.getPath());
		StringBuilder fullText = new StringBuilder();
		
		for ( String s : lines ) {
			  fullText.append(s).append(" ");
		}
		
		TFxIDF tdxidf = new TFxIDF();
		
		String summary = tdxidf.createSummary(fullText.toString(), true, 20, 100);
		
		assertTrue(summary.equals(TEST_SUMMARY));
	}
}