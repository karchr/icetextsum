package is.ru.nlp.textsum.util;

public class TextLength {
	
	int textLength = 0;
	
	public TextLength(int textLength){
		this.textLength = textLength;
	}
	
	public int percentage(int newTextLength){
		return (int) (((double)newTextLength/(double)textLength) * 100.0);
	}

}
