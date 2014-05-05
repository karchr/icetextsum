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

package is.ru.nlp.textsum.NLPUtil;

import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Token;
import is.ru.nlp.textsum.graph.Graph;
import is.ru.nlp.textsum.graph.KeyWord;
import is.ru.nlp.textsum.graph.Node;
import is.ru.nlp.textsum.graph.SentenceNode;

import java.util.LinkedList;

/**
 * Sentence class - renamed to SentenceHolder
 * 
 * @author paco@sharethis.com
 */
public class SentenceHolder {
	
	/**
	 * Public members.
	 */
	private Node[] nodeList;
	private LinkedList<Token> tokens = new LinkedList<Token>();
	public String[] tokenList;
	public String[] tagList;
	private LanguageEnglish lang = null;
	private final String ENGLISH_LANGUAGE_RESOURCES = "/resources/en/";
	
	/**
	 * Constructor.
	 */
	public SentenceHolder(LinkedList<Token> tokens) {
		this.tokens = tokens;
		this.nodeList = null;
	}

	/**
	 * Constructor.
	 */
	public SentenceHolder(String[] tokens, String language) {
		try {
			if(language.equalsIgnoreCase("en")){
				lang = new LanguageEnglish(ENGLISH_LANGUAGE_RESOURCES);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.tokenList = tokens;
		this.nodeList = null;
	}
	
	public SentenceHolder() {

	}

	/**
	 * @return the node_list
	 */
	public Node[] getNodeList() {
		return nodeList;
	}
	/**
	 * @param node_list the node_list to set
	 */
	public void setNodeList(Node[] nodeList) {
		this.nodeList = nodeList;
	}
	/**
	 * @return the tokens
	 */
	public LinkedList<Token> getTokens() {
		return tokens;
	}
	/**
	 * @param tokens the tokens to set
	 */
	public void setTokens(LinkedList<Token> tokens) {
		this.tokens = tokens;
	}

	/**
	 * Main processing per sentence.
	 */
	public void mapTokens(final Graph graph) {
		this.nodeList = new Node[tokens.size()];
			// create nodes for the graph
			Node last_node = null;
			
			for(int i = 0; i < this.tokens.size(); i++){ 
				IceTokenTags itt = (IceTokenTags)this.tokens.get(i);
				if(itt != null){
					
				//selecting nouns, proper nouns, adjectives and numerals seems to give reasonable results
				if ( itt.isNoun() || itt.isProperNoun() || itt.isNumeral() || itt.isAdjective()) {
					//System.out.println(itt.lexeme);
					final KeyWord value = new KeyWord(itt);
					//Use first letter of word class + lemma of the word for key for the nodes in the graph
					String key = itt.getFirstTagStr().substring(0,1) + itt.getFirstTag().getLemma();
					final Node n = Node.buildNode(graph, key, value);
					// emit nodes to construct the graph
					if (last_node != null) {
						n.biConnect(last_node); //Connect two nodes with a bi-directional arc in the graph
					//	System.out.println(n.value.text + ".connect( " + last_node.value.text + " ) : " + i);
					}
					last_node = n;
					nodeList[i] = n;
					//System.out.println("node : " + n.key);
					
				}
			}
		}
	}
	
	/**
	 * Main processing per sentence.
	 */
	public void addSentenceToGraph(final Graph graph) throws Exception {
	
		// create nodes for the graph
		
		final SentenceNode value = new SentenceNode(this);
		
		final Node n = Node.buildNode(graph, this.toString(), value );
		Node last_node = graph.lastEntry().getValue();
		// emit nodes to construct the graph
		if (last_node != null) {
			n.connect(last_node);
		}
	}
	/**
	 * Main processing per sentence.
	 */
	public void addSentenceToGraphEN(final Graph graph) throws Exception {
	
		// create nodes for the graph
		final SentenceNode value = new SentenceNode(this, "en");
		final Node n = Node.buildNode(graph, this.toStringEN(), value );
		Node last_node = graph.lastEntry().getValue();
		// emit nodes to construct the graph
		if (last_node != null) {
			n.connect(last_node);
		}
	}
	
	
	/**
	 * Method for testing on an English text
	 * 
	 * @param graph
	 * @throws Exception
	 */
	public void mapTokensEN(final Graph graph) throws Exception {
		this.nodeList = new Node[tokenList.length];
			// create nodes for the graph
			Node last_node = null;
			final String[] tag_list = lang.tagTokens(tokenList);
			for(int i = 0; i < this.tokenList.length; i++){ 
				final String pos = tag_list[i];
				if(pos != null){
					//System.out.println("tag " + pos);
					if (lang.isRelevant(pos)) {
						final String key = lang.getNodeKey(tokenList[i], pos);
		
						final KeyWord value = new KeyWord(tokenList[i], pos);
						final Node n = Node.buildNode(graph, key, value);

						// emit nodes to construct the graph
						if (last_node != null) {
							n.biConnect(last_node);
						}

						last_node = n;
						nodeList[i] = n;
					}
			}
		}
	}
	
	public String toString() {
				
		StringBuilder sb = new StringBuilder();
		for(Token t : tokens){
			sb.append(t.lexeme).append(" ");
		}
		return sb.toString();
	}	
	
	public String toStringEN() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < tokenList.length; i++){
			sb.append(tokenList[i]).append(" ");
		}
		return sb.toString();
	}
}