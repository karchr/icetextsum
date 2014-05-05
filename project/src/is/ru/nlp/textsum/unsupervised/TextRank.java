/*
Copyright (c) 2009, ShareThis, Inc. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.

    * Neither the name of the ShareThis, Inc., nor the names of its
      contributors may be used to endorse or promote products derived
      from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package is.ru.nlp.textsum.unsupervised;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import is.iclt.icenlp.core.tokenizer.Sentence;
import is.iclt.icenlp.core.tokenizer.Token;
import is.ru.nlp.textsum.graph.Graph;
import is.ru.nlp.textsum.graph.MetricVector;
import is.ru.nlp.textsum.graph.Node;
import is.ru.nlp.textsum.NLPUtil.NGram;
import is.ru.nlp.textsum.NLPUtil.SentenceHolder;
import is.ru.nlp.textsum.NLPUtil.TaggedText;
import is.ru.nlp.textsum.NLPUtil.Language;

/**
 * Java implementation of the TextRank algorithm by Rada Mihalcea, et al.
 *    http://lit.csci.unt.edu/index.php/Graph-based_NLP
 *
 * @author paco@sharethis.com
 */
public class TextRank {
	
	private final static Log logger = LogFactory.getLog(TextRank.class.getName());
	
	private TaggedText taggedText = null;

	protected Graph graph = null;
	protected Graph ngramSubgraph = null;
	protected Map<NGram, MetricVector> metricSpaceKey = null;
	protected Map<Node, MetricVector> metricSpaceSum = null;
	
	
	private List<SentenceHolder> sentenceList = null;

	public TextRank(){
			graph = new Graph();
			ngramSubgraph = null;
			metricSpaceKey = new HashMap<NGram, MetricVector>();
			metricSpaceSum = new HashMap<Node, MetricVector>();
			sentenceList = new ArrayList<SentenceHolder>();
		}
	
	public void tagText(String fullText, Language language) {
		 taggedText = new TaggedText(fullText, language);
	}
	
	public void createTaggedSentences(String fullText, Language language) {
		tagText(fullText, language);
    	if ( language.equals(Language.ICELANDIC)) {
    			createTaggedSentencesICE();
    	} else if ( language.equals(Language.ENGLISH)) {
    			createTaggedSentencesEN();
    	}
	}
	
	public void createTaggedSentencesEN() {
    	
    	for(String sen : taggedText.getSentencesEN()){
    		String[] tokens = taggedText.tokenList(sen);
    		
    		SentenceHolder s = new SentenceHolder(tokens, "en");
    		s.tagList = taggedText.tagList(tokens);
    		sentenceList.add(s);
    	}
	}
	
	public void createTaggedSentencesICE() {
    	for(Sentence sen : taggedText.getSentences().getSentences()){
    		LinkedList<Token> tokenList = new LinkedList<Token>();
    		for(Token t : sen.getTokens()){
    			tokenList.add( t );    		
    		}	
    		SentenceHolder s = new SentenceHolder(tokenList);
    		sentenceList.add(s);
    	}
	}	
	
	/**
	 * @return the sentenceList
	 */
	public List<SentenceHolder> getSentenceList() {
		return sentenceList;
	}

	/**
	 * @return the taggedText
	 */
	public TaggedText getTaggedText() {
		return taggedText;
	}
}
