A summarizer for summarizing Icelandic texts. The summarizer runs in cmd
line mode or GUI mode. This summarizer is a prototype implementation as 
part of a masters thesis titled "Summarization of Icelandic Texts"
written at Reykjavík University 2014. 

The summarizer uses two methods for summarization, namely TextRank by 
"Mihalcea, R., & Tarau, P. (2004). Textrank: Bringing order into texts. 
In Proceedings of EMNLP 2004." This part of the code buildes on Open 
Source Code shared by Nathan Paco at http://github.com/sharethis/textrank/ 
and https://github.com/samxhuan/textrank. The second method implemented 
for the summarizer is TFxIDF.
 
Author:
  Karin Christiasen    karin12@ru.is

GitHub code repo:
  https://github.com/karchr/icetextsum


##################################################################

run summarizer GUI mode:
	java -jar summarizer.jar -gui

run summarizer in cmd line mode(default params):
	java -jar summarizer.jar -f /path/to/file/to/summarize.txt

for cmd line menu:
	java -jar summarizer.jar -h

run summarizer in cmd line mode:
	java -jar summarizer.jar "params listed below"

input params: -t, -f, -o, -l, -a, -z, -w, -p, -h, -gui
  Use -h for usage: 
  [-t : type, eigther summary or keyword : (default: summary)]
  [-f : input file]
  [-o : output file : (default value summary1.textrank.system, textrank or value specified in -a)]
  [-l : language : (default: Icelandic)]
  [-a : summarizing algorithm, textrank or tfxidf : (Default: textrank)]
  [-z : lemmatized, true or false : (Default: false)]
  [-w : minimum number of words in summary, a number between 1 and input text length (Default: 100)]
  [-p : summary minimum percentage size of fulltext : a number between 1 and 100 (Default: 20)]
    	
  [-gui : run summarizer in GUI mode, ignore all other parameters]

##################################################################

NOTE: To run the Summarizer for English texts, the summarizer.jar and
the folders /resources/en/opennlp/ must be in the same directory.

