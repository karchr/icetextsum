package is.ru.nlp.textsum;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import is.ru.nlp.textsum.NLPUtil.Language;
import is.ru.nlp.gui.GUI;
import is.ru.nlp.textsum.unsupervised.Type;
import is.ru.nlp.textsum.util.FileExtensionFilter;
import is.ru.nlp.textsum.util.ReadInFile;

/**
 * Menu class for running the Summarizer. 
 * 
 * Input params: -t, -f, -o, -l, -a, -z, -w, -p, -h, -gui
 * Use -h for usage: 
 *   		[-t : type, eigther summary or keyword : (default: summary)]
 *   		[-f : input file]
 *   		[-o : output file : (default value summary1.textrank.system, textrank or value specified in -a)]
 *   		[-l : language : (default: Icelandic)]
 *   		[-a : summarizing algorithm, textrank or tfxidf : (Default: textrank)]
 *   		[-z : lemmatized, true or false : (Default: false)]
 *   		[-w : minimum number of words in summary, a number between 1 and input text length (Default: 100)]
 *   		[-p : summary minimum percentage size of fulltext : a number between 1 and 100 (Default: 20)]
 *   	
 *   		[-gui : run summarizer in GUI mode, ignore all other parameters]
 * 
 * @author Karin Christiansen
 *
 */
public class Menu {
	
	private static final String[] FILE_EXT = new String[] {"txt"};
	
	private String type;
	private String file;
	private String outFile;
	private String language;
	private String algorithm;
	private boolean lemmatized;
	private int minNumberWords;
	private int minPercentOfFull;
	private String fileType;
	
    private static void runGUI() {
        GUI.runAndStartGUI();
    }
    
    private String getParam(String param) {
		int space = param.indexOf(' ');
		return param.substring(space + 1, param.length());
    }
    
    private int getDigit(String f) {
    	Pattern pDigits = Pattern.compile( "\\d+" );
		 int dot = f.indexOf('.');
		  String fileName = f.substring(0, dot);
		  String number = "";
		  Matcher mDigits = pDigits.matcher(fileName);
		  while ( mDigits.find()){
			  number += mDigits.group();
		  }
	   return Integer.parseInt(number);
    }
    
    /**
     * Load the Menu properties and set values.
     */
    public void loadMenuProperties() {
    	
		Properties prop = new Properties();
		InputStream inputMenu = null;
		InputStream inputSummary = null;
		InputStream inputKeywords = null;
    	try {

    		// load a properties file
    		prop.load(Menu.class.getResourceAsStream("/menu.properties"));
    		prop.load(Menu.class.getResourceAsStream("/summary.properties"));
    		prop.load(Menu.class.getResourceAsStream("/keywords.properties"));
     
    		// get the property values
    		type = prop.getProperty("type");
    		file = prop.getProperty("file");
    		outFile = prop.getProperty("outFile");
    		language = prop.getProperty("language");
    		algorithm = prop.getProperty("algorithm");
    		lemmatized = Boolean.parseBoolean(prop.getProperty("lemmatized"));
    		minNumberWords = Integer.parseInt(prop.getProperty("min_number_words"));
    		minPercentOfFull =  Integer.parseInt(prop.getProperty("min_percent_of_full"));

    		fileType = prop.getProperty("summary_file_type");	
     
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	} finally {
    		if (inputMenu != null) {
    			try {
    				inputMenu.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    		if (inputSummary != null) {
    			try {
    				inputSummary.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    		if (inputKeywords != null) {
    			try {
    				inputKeywords.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
    public boolean supportsLanguage(Type type) {
			if ( type.supportsLanguage(language.toUpperCase()) ) {
				return true;
			} else {
				System.err.println("Language " + language.toUpperCase() + " not supported for " + type);
				return false;
			}
    }
    
    public boolean supportsLemmatized(Type type) {
		if ( type.supportsLemmatized() ){
			return true;
		} else {
			System.err.println("Lemmatized not supported for " + type);
			return false;
		}
    }
    
    public boolean supportsKeywords(Type type) {
		try {
			type.keyword();
			return true;
		} catch ( Exception ex ) {
			System.err.println("Keywords not supported for " + type);
			return false;
		}
    }
    
    /**
     * Parse the input parameters. Generate Summary or Keyword list
     * 
     * @param input		Input parameter from cmd line
     */
    public void parseInput(String input){
    	//-t summary -f resources/full/ice/text1.txt -l icelandic -a tfxidf -s false
    	
    			//Print the Help Menu, ignore all other parameters
    			if(input.contains("-h")){
    				System.out.println(help());
    				return;
    			}
    			
    			//Run the GUI, ignore all other parameters
    			if(input.contains("-gui")){
    				runGUI();
    				return;
    			}
    					
    			Pattern p = Pattern.compile( "[-a-z]{2}\\s+((\\/)*(\\w)+(\\.)*(\\w)+)+" );
    			Matcher m = p.matcher(input);
    			
    			while(m.find()){
    				if( m.group().startsWith("-t")){
    					type = getParam(m.group().toString()).toLowerCase();
    				} else if( m.group().startsWith("-f")){
    					file = getParam(m.group().toString());
    				} else if( m.group().startsWith("-o")){
    					outFile = getParam(m.group().toString());
    				} else if( m.group().startsWith("-l")){
    					language = getParam(m.group().toString()).toLowerCase();
    				} else if( m.group().startsWith("-a")){
    					algorithm = getParam(m.group().toString()).toLowerCase();
    				} else if( m.group().startsWith("-z")){
    					lemmatized = Boolean.valueOf(getParam(m.group().toString()));
    				} else if( m.group().startsWith("-w")){
    					minNumberWords = Integer.parseInt(getParam(m.group().toString()));    					
    				} else if( m.group().startsWith("-p")){
    					minPercentOfFull = Integer.parseInt(getParam(m.group().toString()));	
    				} else {
    					System.out.println(help());
    					return;
    				}
    			}
    			
    			if (file.equals("")) { 
    				System.err.println("No file name specified" + file);
    				return;
    			}
    			if (file.startsWith("/")) { 
    				file = file.substring(1, file.length());
    			}	
    			final File fullTextFiles = new File(file);
    			
    			File files[] = null;
    			if (fullTextFiles.isDirectory()) {
    				files = fullTextFiles.listFiles(new FileExtensionFilter(FILE_EXT));
    			} else {
    				files = new File[1];
    				files[0] = fullTextFiles;
    			}
    			if(outFile == null) outFile = "";
    			final File out = new File(outFile);
    			
    			//Generate Summary
    			if(type.equals("summary")) {
    					for ( File f : files ) {
    						Type aType = Type.valueOf(algorithm.toUpperCase());
    						if (lemmatized && !supportsLemmatized(aType)) return;
    						if( supportsLanguage(aType) ) {
      						 	Summarizer summary = Summarizer.createSummary(Language.valueOf(language.toUpperCase()), aType, f.getPath(), lemmatized, minPercentOfFull, minNumberWords);
      						 	System.out.println(summary.getSummary());
      						 	writeSummaryToFile(getOutputFileName(outFile, "text"+type, out, f, algorithm), summary.getSummary());
    						}
    					}
    					
    			} //Generate Keyword list
    			else if (type.equals("keyword")) { //-t keyword -f resources/full/ice/text1.txt -l english
    					for ( File f : files ) {
    						Type aType = Type.valueOf(algorithm.toUpperCase());
    						if (lemmatized && !supportsLemmatized(aType)) return;
    						if ( supportsKeywords(aType) && supportsLanguage(aType) ) {
    							Keywords keywords = Keywords.createKeywords(Language.valueOf(language.toUpperCase()), aType, f.getPath());
    							System.out.println(keywords.getKeywords());
    							writeSummaryToFile(getOutputFileName(outFile, type, out, f, algorithm), keywords.getKeywords());
    						}
    					}
    			} else {
    				System.err.println("Invalid parameter " + type);
    				return;
    			}
    }
    
    /**
     * Print the Help menu
     * 
     * @return	The help menu
     */
    public String help(){
    	StringBuilder sb = new StringBuilder();
		
    	sb.append("Use -h for usage: ").append("\n");
    	sb.append("in params: -t, -f, -o, -l, -a, -z, -w, -p").append("\n");
    	sb.append("\n");
    	sb.append("usage: ").append("\n");
    	sb.append("\t[-t : type, eigther summary or keyword : (default: summary)]").append("\n");
    	sb.append("\t[-f : input file]").append("\n");
    	sb.append("\t[-o : output file : (default value summary1.textrank.system, textrank or value specified in -a)]").append("\n");
    	sb.append("\t[-l : language : (default: Icelandic)]").append("\n");
    	sb.append("\t[-a : summarizing algorithm, textrank or tfxidf : (Default: textrank)]").append("\n");
    	sb.append("\t[-z : lemmatized, true or false : (Default: false)]").append("\n");
    	sb.append("\t[-w : minimum number of words in summary, a number between 1 and input text length (Default: 100)]").append("\n");
    	sb.append("\t[-p : summary minimum percentage size of fulltext : a number between 1 and 100 (Default: 20)]").append("\n");
    	
    	sb.append("\t[-gui : run summarizer in GUI mode, ignore all other parameters]").append("\n");
	
    	return sb.toString();
    }
    
	public static void main(String[] args) {
		
		Menu menu = new Menu();
		menu.loadMenuProperties();
		
		String input = "";
		for(String a : args) {
			input += a + " ";
		}

		/*
    	Use -h for usage: 
    		in params: -t, -f, -o, -l, -a, -z, -w, -p, -h, -gui

    		usage: 
    		[-t : type, eigther summary or keyword : (default: summary)]
    		[-f : input file]
    		[-o : output file : (default value summary1.textrank.system, textrank or value specified in -a)]
    		[-l : language : (default: Icelandic)]
    		[-a : summarizing algorithm, textrank or tfxidf : (Default: textrank)]
    		[-z : lemmatized, true or false : (Default: false)]
    		[-w : minimum number of words in summary, a number between 1 and input text length (Default: 100)]
    		[-p : summary minimum percentage size of fulltext : a number between 1 and 100 (Default: 20)]
    	
    		[-gui : run summarizer in GUI mode, ignore all other parameters]
		 */
		menu.parseInput(input);
	}
	
	/**
	 * Create the output file for the Generated Summary
	 * 
	 * @param outFile		Name of outfile
	 * @param name			Type name
	 * @param out			Out File
	 * @param f				FullText file
	 * @param algorithm		Name of method: TextRank, TFxIDF, Baseline
	 * @return
	 */
	private String getOutputFileName(String outFile, String name, File out, File f, String algorithm) {
		  int number = getDigit(f.getName()); 
		  if(outFile.equals("") || out.isDirectory()) {					      
		      return out + "/" + name + number + "." + algorithm + fileType;
		  } else {
			  if (outFile.contains(".")){
				  int dot = outFile.lastIndexOf(".");
				  String begin = outFile.substring(0, dot);
				  String end = outFile.substring(dot, outFile.length());
				  return  begin + number + end;
			  } else {
				  return outFile;
			  }
		  }
	}
	
	/**
	 * Write Generated Summary to file
	 * 
	 * @param fileName		Name of the file the Summary is to be written to
	 * @param text			The Generated Summary
	 */
	public void writeSummaryToFile ( String fileName, String text ) {
		ReadInFile readIn = new ReadInFile();
		readIn.writeFileBytes(fileName, text);
	}
}