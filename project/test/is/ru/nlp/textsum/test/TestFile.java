/**
 * 
 */
package is.ru.nlp.textsum.test;

import is.ru.nlp.textsum.util.ReadInFile;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
/**
 * @author Karin
 *
 */
public class TestFile {
	
	private ReadInFile readIn;
	
    @Before
    public void setUp() throws Exception {
    	readIn = new ReadInFile();
    }
    
	@Test
	public void readInText() {
		final File folder = new File("./resources/full/text1.txt");
		List<String> lines = readIn.readSmallFileLines(folder.getPath());
		StringBuilder fullText = new StringBuilder();
		
		assertFalse(lines.isEmpty());
		
		for ( String s : lines ) {
			 fullText.append(s).append(" ");
		}
		assertTrue(fullText != null);
		assertTrue(fullText.length() > 0);
	}
}
