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

import is.ru.nlp.textsum.graph.Graph;
import is.ru.nlp.textsum.graph.MetricVector;
import is.ru.nlp.textsum.graph.Node;
import is.ru.nlp.textsum.NLPUtil.Language;
import is.ru.nlp.textsum.NLPUtil.NGram;
import is.ru.nlp.textsum.NLPUtil.SentenceHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Java implementation of the TextRank algorithm by Rada Mihalcea, et al.
 *    http://lit.csci.unt.edu/index.php/Graph-based_NLP
 *
 * @author paco@sharethis.com
 */
public class TextRankKeywords extends TextRank {
	
	private final static Log logger = LogFactory.getLog(TextRankKeywords.class.getName());
	
	private final static int MAX_NGRAM_LENGTH = 5;

	public TextRankKeywords() {
	}
	
	/**
	 * Run the TextRank algorithm on the given semi-structured text (e.g.,
	 * results of parsed HTML from crawled web content) to build a graph of
	 * weighted key phrases.
	 */
	public Collection<MetricVector> useTextRankKeywords(Language language) {
		// ////////////////////////////////////////////////
		// PASS 1: construct a graph from PoS tags
		
		// scan sentences to construct a graph of relevant morphemes
		if(language.equals(Language.ICELANDIC)){
			for(SentenceHolder s: getSentenceList()){
					s.mapTokens(graph);
			}
		} else if (language.equals(Language.ENGLISH)){
			for(SentenceHolder s: getSentenceList()){
				try {
					s.mapTokensEN(graph);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			throw new UnsupportedOperationException("Language not Supported");
		}
		
		// ////////////////////////////////////////////////
		// PASS 2: run TextRank to determine keywords
		final int max_results = (int) Math.round((double) graph.size() * Graph.KEYWORD_REDUCTION_FACTOR);//
		
		graph.runTextRank();
		
		//Scan results and cut of anything below max_results
		graph.sortResults(max_results);
		
		ngramSubgraph = NGram.collectNGrams(getSentenceList(), graph.getRankThreshold(), language);
		// ////////////////////////////////////////////////
		// PASS 3: lemmatize selected keywords and phrases

		// augment the graph with n-grams added as nodes
		for (Node n : ngramSubgraph.values()) {
			final NGram gram = (NGram) n.value;

			if (gram.length < MAX_NGRAM_LENGTH) {
				graph.put(n.key, n);
				for (Node keyword_node : gram.nodes) {
					n.biConnect(keyword_node);
				}
			}
		}
		// ////////////////////////////////////////////////
		// PASS 4: re-run TextRank on the augmented graph
		graph.runTextRank();
		// graph.sortResults(graph.size() / 2);

		// collect stats for metrics
		//count of the NGram that occurs most often
		//final int ngram_max_count = NGram.calcStats(ngram_subgraph); 

		
		//Comment out everything in pass 5, since the part
		//with the metric space is not covered in the paper
		// ////////////////////////////////////////////////
		// PASS 5: construct a metric space for overall ranking
		//final double link_min = ngram_subgraph.dist_stats.getMin(); //minimum rank of the all the Ngrams
		
		//get graph range
		//final double link_coeff = ngram_subgraph.dist_stats.getMax() - link_min;

	//	final double count_min = 1;
		//final double count_coeff = (double) ngram_max_count - 1;

		//Set up the results and sort by highest rank
		for (Node n : ngramSubgraph.values()) {
			final NGram gram = (NGram) n.value;
			
			if (gram.length < MAX_NGRAM_LENGTH) {
			//	final double link_rank = (n.rank - link_min) / link_coeff;
			//	final double count_rank = (gram.getCount() - count_min) / count_coeff;

				//final MetricVector mv = new MetricVector(gram, link_rank, count_rank, n.rank);
				final MetricVector mv = new MetricVector(gram, n.rank);
				//System.out.println(mv.render() + " " + gram.text);
				metricSpaceKey.put(gram, mv);
			}
		}
		
		// return results
		return metricSpaceKey.values();
	}
	
	

	public String createKeywodsExtraction(String fullText, Language language){
		String keywords = "";
		Collection<MetricVector> answer = null;
		try {
			createTaggedSentences(fullText, language);
			answer = useTextRankKeywords(language);
			List<MetricVector> sorted = new ArrayList<MetricVector>();
			for(MetricVector mv : answer){
				//    System.out.println(mv.render() + " , " + mv.value.text);
				    sorted.add(mv);
			}
				
			Collections.sort(sorted, MetricVector.SORT_ORDER);
				
			for(MetricVector mv : sorted){
				if ( logger.isDebugEnabled())
					 logger.info(mv.render() + " - " + mv.value.text);
				
				keywords += mv.value.text + ", ";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return keywords;
	}

}
