package is.ru.nlp.textsum.graph;

import is.ru.nlp.textsum.NLPUtil.SentenceHolder;

public class SentenceNode extends NodeValue {

	/**
	 * Public members.
	 */

	public SentenceHolder sentenceHolder = null;
	/**
	 * Constructor.
	 */

	public SentenceNode(SentenceHolder sentenceHolder) {
		this.sentenceHolder = sentenceHolder;
		this.text = sentenceHolder.toString();
	}
	
	public SentenceNode(SentenceHolder sentenceHolder, String en) {
		this.sentenceHolder = sentenceHolder;
		this.text = sentenceHolder.toStringEN();
	}

	/**
	 * Create a description text for this value.
	 */

	public String getDescription() {
		return "SENTENCE" + '\t' + ' ' + text;
	}
}
