package is.ru.nlp.textsum.test;

import static org.junit.Assert.assertTrue;
import is.ru.nlp.textsum.util.StringUtil;

import org.junit.Before;
import org.junit.Test;

public class TestStringUtil {
	
	
	@Before
    public void setUp(){
		
	}
	
	@Test
	public void splitIntoSentences(){
		String s =  "\" Sumir eru ekki búnir að heyja , ætluðu sér kannski að ná seinni slætti , "
				    + "þannig að aðstæður manna eru misjafnar en menn komast nú alveg í gegnum það held ég . \""; 
		
		String s2 =  "Sumir eru ekki búnir að heyja, ætluðu sér kannski að ná seinni slætti, "
			    + "þannig að aðstæður manna eru misjafnar en menn komast nú alveg í gegnum það held ég."; 
		
		String s3 =  "\" Sumir eru ekki búnir að heyja, ætluðu sér kannski að ná seinni slætti, "
			    + "þannig að aðstæður manna eru misjafnar en menn komast nú alveg í gegnum það held ég. \" Bændur á tánum vegna illviðrisspár. "; 
		
		String[] sentence = StringUtil.splitIntoSentences(s);
		
		for(String str : sentence){
			System.out.println(str);
		}
		
		assertTrue(sentence.length == 1);
		
		String[] sentence2 = StringUtil.splitIntoSentences(s2);
		
		for(String str : sentence2){
			System.out.println(str);
		}
		
		assertTrue(sentence2.length == 1);
		
		String[] sentence3 = StringUtil.splitIntoSentences(s3);
		
		for(String str : sentence3){
			System.out.println(str);
		}
		
		assertTrue(sentence3.length == 2);
		
	}

}
