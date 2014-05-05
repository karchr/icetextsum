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

package is.ru.nlp.textsum.graph;

import is.iclt.icenlp.core.tokenizer.IceTokenTags;
import is.iclt.icenlp.core.tokenizer.Token;

import java.util.HashSet;

/**
 * Implements a node in the TextRank graph, denoting some noun or adjective
 * morpheme.
 * 
 * @author paco@sharethis.com
 */

public class Node implements Comparable<Node> {
	// logging
	//private final static Log log_ = LogFactory.getLog(Node.class.getName());

	/**
	 * Public members.
	 */
	//Bi-directional edges for Keyword Extraction
	public HashSet<Node> edges = new HashSet<Node>();
	
	//In and Out Edges for Text Summarization
	public HashSet<Node> edgesIN = new HashSet<Node>();
	public HashSet<Node> edgesOUT = new HashSet<Node>();
	
	public double rank = 0.0D;
	public double weight = 0.0D;
	public String key = null;
	public boolean marked = false;
	public NodeValue value = null;

	/**
	 * Private constructor.
	 */
	private Node( final String key, final NodeValue value ) {
		this.rank = 1.0D;
		this.key = key;
		this.value = value;
	}
	
	/**
	 * Calculate the similarity between two nodes.
	 * Uses IceToken and only works for texts tagged with IceTagger.
	 * 
	 * @param n	
	 * @return	The similarity ratio between the two sentences
	 */
	public double similarity( Node n ) {
		
		/*
		Similarity(Si, Sj) = _|_{Wk_|_Wk_in_Si_&_Wk_in_Sj}_|_
							      log(|Si|) + log(|Sj|)
							 
		*/
		
			SentenceNode sn = ( SentenceNode ) this.value;
			SentenceNode sn2 = ( SentenceNode ) n.value;
	        double ratio = 0.0;
			for( Token t : sn.sentenceHolder.getTokens() ){
				IceTokenTags itt = ( IceTokenTags )t;
			
				for( Token t1 : sn2.sentenceHolder.getTokens() ){
					if(!t.isPunctuation() && !t.isQuote() && !t.isEOS()) {
						IceTokenTags itt2 = ( IceTokenTags )t1;
						if ( isIncludeToken( itt, itt2 ) ) {
								ratio += itt.getFirstTagStr().equalsIgnoreCase( itt2.getFirstTagStr() ) ? 1.0 : 0.0;
						}
					}
				}
			}
			ratio = Math.abs( ratio ) / ( Math.log(Math.abs(sn.sentenceHolder.getTokens().size())) + 
					          Math.log(Math.abs(sn2.sentenceHolder.getTokens().size())) );
			return ratio;
	}
	
	
	
	/**
	 * Calculate the similarity between two nodes.
	 * Uses OpenNLP and only works for tagged with OpenNLP.
	 * 
	 * @param n	
	 * @return	The similarity ratio between the two sentences
	 */
	public double similarityEN( Node n ){
		
		/*
		Similarity(Si, Sj) = _|_{Wk_|_Wk_in_Si_&_Wk_in_Sj}_|_
							      log(|Si|) + log(|Sj|)
							 
		*/
		
		SentenceNode sn = ( SentenceNode ) this.value;
		SentenceNode sn2 = ( SentenceNode ) n.value;
        double ratio = 0.0;
		for( int i = 0; i < sn.sentenceHolder.tagList.length; i++ ){
			String itt = sn.sentenceHolder.tagList[i];
		
			for( int j = 0; j < sn2.sentenceHolder.tagList.length; j++ ){
				String itt2 = sn2.sentenceHolder.tagList[j];
				if ( ( itt.startsWith("NN") && itt2.startsWith("NN") ) || 
					 ( itt.startsWith("JJ") && itt2.startsWith("JJ") ) ) {
						ratio += itt.equalsIgnoreCase(itt2) ? 1.0 : 0.0;
				}
			}
		}
		ratio = Math.abs( ratio ) / ( Math.log( Math.abs(sn.sentenceHolder.tagList.length) ) + 
				                      Math.log( Math.abs(sn2.sentenceHolder.tagList.length) ) );
		return ratio;
	}
	
	public boolean isIncludeToken( IceTokenTags itt, IceTokenTags itt2 ) {
		if( itt.isNoun() && itt2.isNoun() ) return true;
		else if ( itt.isNumeral() && itt2.isNumeral() ) return true;
		else if ( itt.isProperNoun() && itt2.isProperNoun() ) return true;
		else if ( itt.isAdjective() && itt2.isAdjective() ) return true;
		else return false;
	}

	/**
	 * Compare method for sort ordering.
	 */
	public int compareTo(final Node that) {
		if (this.rank > that.rank) {
			return -1;
		} else if (this.rank < that.rank) {
			return 1;
		} else {
			return this.value.text.compareTo(that.value.text);
		}
	}

	/**
	 * Connect two nodes with a bi-directional arc in the graph.
	 */
	public void biConnect(final Node that) {
		this.edges.add(that);
		that.edges.add(this);
	}
	
	/**
	 * Connect two nodes with a directional arc in the graph.
	 */
	public void connect(final Node that) {
		this.edgesOUT.add(that);
		this.edgesIN.add(that);
	}

	/**
	 * Create a unique identifier for this node, returned as a hex string.
	 */
	public String getId() {
		return Integer.toString(hashCode(), 16);
	}

	/**
	 * Factory method.
	 */
	public static Node buildNode(final Graph graph, final String key, final NodeValue value) {
		Node n = graph.get(key);

		if (n == null) {
			n = new Node(key, value);
			graph.put(key, n);
		}
		return n;
	}
}
