package is.ru.nlp.textsum.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import is.ru.nlp.textsum.unsupervised.LLR;
import is.ru.nlp.textsum.util.ReadInFile;

import org.junit.Before;
import org.junit.Test;

public class LLRTest {

	private ReadInFile readIn;
	
	@Before
    public void setUp(){
		readIn = new ReadInFile();
	}

	@Test
	public void testLLR() {
		final File folder = new File("./resources/full/ice/text1.txt");
		List<String> lines = readIn.readSmallFileLines(folder.getPath());
		StringBuilder fullText = new StringBuilder();
		
		for ( String s : lines ) {
			  fullText.append(s).append(" ");
		}
		
		LLR llr = new LLR();
		String summary = llr.createSummary(fullText.toString(), 20, 100);
	}

}
