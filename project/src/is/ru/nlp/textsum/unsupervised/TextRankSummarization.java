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

import is.ru.nlp.textsum.graph.MetricVector;
import is.ru.nlp.textsum.graph.Node;
import is.ru.nlp.textsum.NLPUtil.Language;
import is.ru.nlp.textsum.NLPUtil.SentenceHolder;
import is.ru.nlp.textsum.util.TextLength;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Java implementation of the TextRank algorithm by Rada Mihalcea, et al.
 *    http://lit.csci.unt.edu/index.php/Graph-based_NLP
 *
 * @author paco@sharethis.com
 */
public class TextRankSummarization extends TextRank {
		
	private final static Log logger = LogFactory.getLog(TextRankSummarization.class.getName());
	
	public TextRankSummarization(){

	}
	
	/**
	 * Run the TextRank algorithm on the given semi-structured text (e.g.,
	 * results of parsed HTML from crawled web content) to build a graph of
	 * weighted key phrases.
	 */
	public Collection<MetricVector> useTextRankSummary(Language language) {
		// ////////////////////////////////////////////////
		// PASS 1: construct a graph from sentences
		try {
			if(language.equals(Language.ICELANDIC)) {
				//Connect sentences
				for (SentenceHolder s: getSentenceList()) {
						s.addSentenceToGraph(graph);
				}
			} else if (language.equals(Language.ENGLISH) ){
				//Connect sentences
				for (SentenceHolder s: getSentenceList()) {
					 s.addSentenceToGraphEN(graph);
					 //System.out.println(s.token_list.length);
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		graph.runTextRankOnSentences(language);

		//Set up the results and sort by highest rank
		for (Node n : graph.values()) {

				//final MetricVector mv = new MetricVector(gram, link_rank, count_rank, n.rank);
				final MetricVector mv = new MetricVector(n.value, n.rank);
				//System.out.println(mv.render() + " " + gram.text);
				metricSpaceSum.put(n, mv);
			
		}
		
		// return results
		return metricSpaceSum.values();
	}
	
	public int getWordCount(String text){
		String[] words = Pattern.compile("\\s+").split(text);
		return words.length;
	}

	/**
	 * Generate a summary using TextRank. Takes in a text, tags the text, 
	 * builds the graph, calculates the rank scores, sorts
	 * the list by score and returns the summary.
	 * 
	 * @param fullText				Full text
	 * @param language				Language of full text
	 * @param minSizeInPercent		Minimum summary size in percent
	 * @param minLengthInWords		Minimum summary length in words
	 * @return						A summary
	 */
	public String createSummary(String fullText, Language language, int minSizeInPercent, int minLengthInWords){
		TextLength summarySize = new TextLength(getWordCount(fullText));
		Collection<MetricVector> answer = null;
		StringBuilder summary = new StringBuilder();
		try {
				
			createTaggedSentences(fullText, language);
			answer = useTextRankSummary(language);
			List<MetricVector> sorted = new ArrayList<MetricVector>();
			for(MetricVector mv : answer){
				//    System.out.println(mv.render() + " , " + mv.value.text);
				    sorted.add(mv);
			}
				
			Collections.sort(sorted, MetricVector.SORT_ORDER);	
			int wordCount = 0;
			int percent = 0;
			
			if ( logger.isDebugEnabled())
		         logger.info("########################## Summary Generated by TextRank ##########################");
			
			for(MetricVector mv : sorted){
				if ( logger.isDebugEnabled())
						logger.info(mv.render() + " - " + mv.value.text);
				
				summary.append(mv.value.text.toString()).append("\n");
				String[] words = Pattern.compile("\\s+").split(mv.value.text);
	    		wordCount += words.length;
				percent = summarySize.percentage(wordCount);
				if(percent >= minSizeInPercent && wordCount >= minLengthInWords)
					break;
			}

		} catch (Exception e) {
			logger.error("TextRank.java: createSummary - " + e.getMessage());
			e.printStackTrace();
		}
		
		return summary.toString();
	}
}