package is.ru.nlp.gui;

import is.ru.nlp.textsum.Keywords;
import is.ru.nlp.textsum.Summarizer;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import is.ru.nlp.textsum.NLPUtil.Language;
import is.ru.nlp.textsum.unsupervised.LeadBasedBaseline;
import is.ru.nlp.textsum.unsupervised.TFxIDF;
import is.ru.nlp.textsum.unsupervised.TextRankKeywords;
import is.ru.nlp.textsum.unsupervised.TextRankSummarization;

import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import java.util.List;


/**
 * GUI for generating summaries using TextRank and TFxIDF.
 * 
 * TextRank Supports: Summaries, Keywords, Icelandic and English
 * TFxIDF Supports: Summaries and Icelandic
 * 
 *
 * @author Karin Christiansen
 *
 */

public class GUI extends JFrame implements ActionListener, DocumentListener {
    
    /**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -6719617057719304136L;
	private final String TOP_PANE_TITLE = "Generated summary";
	private final String BOTTOM_PANE_TITLE = "Paste full text here..";
	
	private JFrame frame = new JFrame("Automatic Text Summary");

	private JTextArea summaryText;
	private JTextArea fullText;
	private JTextField words;
	private JTextField percent; 
	
	private JTabbedPane jtp; 
	private JComponent jpTextRank;
	private JComponent jpTFxIDF;
	private JComponent jpBaseline;
	
    private boolean typeSummary = true;
    private boolean typeKeyword = false;
	
    private boolean languageIcelandic = true;
    private boolean languageEnglish = false;
    
    private JButton selectFile;
    private JFileChooser fc;
	
	private ChangeListener changeListener = new ChangeListener() {
		      public void stateChanged(ChangeEvent changeEvent) {
 
		      }
		    };
		    
    private ChangeListener radioChangeListener = new ChangeListener() {
        public void stateChanged(ChangeEvent changEvent) {
            AbstractButton aButton = (AbstractButton)changEvent.getSource();
            ButtonModel aModel = aButton.getModel();
            boolean selected = aModel.isSelected();
            String name = aButton.getName();
            if(name.equals("summary") && selected){
            	typeSummary = true;
            	typeKeyword = false;
            } else if(name.equals("keywords") && selected){
            	typeKeyword = true;
            	typeSummary = false;
            } else if(name.equals("icelandic") && selected){
            	languageIcelandic = true;
            	languageEnglish = false;
            } else if(name.equals("english") && selected){
            	languageIcelandic = false;
            	languageEnglish = true;
            }
            
           // System.out.println("Changed: " + name + "/" + selected);
          }
        };

	public GUI() {
       super();
    
    	summaryText = new JTextArea();
    	summaryText.getDocument().addDocumentListener(this);
    	fullText = new JTextArea();
        
        JPanel topPanelText = createTextArea( TOP_PANE_TITLE, "", summaryText, 650, 200);      
        JPanel fullTextPanel = createTextArea( BOTTOM_PANE_TITLE, "", fullText, 750, 500);
       
        JPanel topPanelInfo = new JPanel(new GridLayout(0, 1));
        JPanel topPanel = new JPanel(new BorderLayout());
              
		jtp = new JTabbedPane();
        
        jpTextRank = makeTabPanel(is.ru.nlp.textsum.unsupervised.Type.TEXTRANK); 
        jpTFxIDF = makeTabPanel(is.ru.nlp.textsum.unsupervised.Type.TFXIDF);
        jpBaseline = makeTabPanel(is.ru.nlp.textsum.unsupervised.Type.BASELINE);      
        
        jtp.addTab("TextRank", null, jpTextRank, "Summarize Using TextRank");
        jtp.addTab("TFxIDF", null, jpTFxIDF, "Summarize Using TFxIDF");
        jtp.addTab("Baseline", null, jpBaseline, "Summarize Using Leadbased Baseline");
        
        jtp.setSelectedComponent(jpTextRank);
        jtp.addChangeListener(changeListener);

        topPanelInfo.add(jtp);      
        
        topPanel.add(topPanelText, BorderLayout.CENTER);
        topPanel.add(topPanelInfo, BorderLayout.EAST);
                       
        //Put the editor pane and the text pane in a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, fullTextPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
       
        buttonPanel.add(createButton("GENERATE", "summarize", 200, 50), BorderLayout.EAST);
        buttonPanel.add(createButton("CLOSE", "close", 200, 50), BorderLayout.EAST);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder());
        
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.add(buttonPanel);
         
        add(splitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
	
	/**
	 * Create the Panel for the Tabs
	 * 
	 * @param type		Type of Method: TextRank, TFxIDF, Baseline
	 * @return			Panel
	 */
	protected JComponent makeTabPanel(is.ru.nlp.textsum.unsupervised.Type type) {
        JPanel panel = new JPanel(new GridLayout(5, 1));
        panel.setName(type.name());
  
        ButtonGroup typeGroup;
        JRadioButton jRadioButtonSummary;
        JRadioButton jRadioButtonKeywords;
        
        ButtonGroup languageGroup;
    	JRadioButton jRadioButtonIcelandic;
    	JRadioButton jRadioButtonEnglish;
        
        //Type
        JPanel panelType = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelType.setName("panelType");
        jRadioButtonSummary = createRadioButton(type.summary(), true);
        jRadioButtonSummary.addChangeListener(radioChangeListener);
        jRadioButtonKeywords = createRadioButton("keywords", false);
        jRadioButtonKeywords.addChangeListener(radioChangeListener);
        //Group the radio buttons.
        typeGroup = new ButtonGroup();
        typeGroup.add(jRadioButtonSummary);
        if (type.name().equalsIgnoreCase("TextRank")){
        	typeGroup.add(jRadioButtonKeywords);
		}
        panelType.add(jRadioButtonSummary);
        
        if (type.name().equalsIgnoreCase("TextRank")){
        	panelType.add(jRadioButtonKeywords);
		} 
        
        //Language
        JPanel panelLanguage = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelLanguage.setName("panelLanguage");
        jRadioButtonIcelandic = createRadioButton(type.icelandic().name(), true);
        jRadioButtonIcelandic.addChangeListener(radioChangeListener);
        if (!type.name().equalsIgnoreCase("Baseline")){
        	panelLanguage.add(jRadioButtonIcelandic); 
		}
        jRadioButtonEnglish = createRadioButton("English", false);
        jRadioButtonEnglish.addChangeListener(radioChangeListener);

        languageGroup = new ButtonGroup();
        languageGroup.add(jRadioButtonIcelandic);
        
        if (type.name().equalsIgnoreCase("TextRank")){
            languageGroup.add(jRadioButtonEnglish);
            panelLanguage.add(jRadioButtonEnglish); 
		}
   
        //Output
        JPanel panelOutput= new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField outputFile = new JTextField();
        panelOutput.setName("panelOutput");
        JLabel outLabel = new JLabel("Output file", SwingConstants.LEFT);
        panelOutput.add(outLabel);
        outputFile.setPreferredSize(new Dimension(175, 25));
        panelOutput.add(outputFile);
        
        //words		percent
        JPanel panelSize= new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSize.setName("panelSize");
        words = new JTextField();    
        words.setText("100");
        JLabel labelWords = new JLabel("Words", SwingConstants.LEFT);
        words.setPreferredSize(new Dimension(50, 25));
        panelSize.add(labelWords);
        panelSize.add(words);
        
     
        percent = new JTextField();
        percent.setText("20");
        JLabel labelPercent = new JLabel("Percent");
        percent.setPreferredSize(new Dimension(50, 25));
        panelSize.add(labelPercent);
        panelSize.add(percent);
        
        //Fulltext path...
        JPanel panelFull = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFull.setName("panelFull");
        JTextField fullText = new JTextField();
        fullText.setText("Select file...");
        fullText.setPreferredSize(new Dimension(180, 25));
        panelFull.add(fullText);
        
        fc = new JFileChooser();
        
        selectFile = new JButton();
        selectFile.setText("File..");
        selectFile.setName("File..");
        selectFile.addActionListener(this);
        panelFull.add(selectFile);

        panel.add(panelType);
        panel.add(panelLanguage);
       // panel.add(panelOutput);
        panel.add(panelSize);
        panel.add(panelFull);
     
        return panel;
    }
	
	private JRadioButton createRadioButton(String text, boolean selected) {
		JRadioButton radioButton = new JRadioButton(text);
		radioButton.setName(text.toLowerCase());
		radioButton.setSelected(selected);
		return radioButton;
	}
	
	private JLabel createLabel(String text, String name, int width, int height){
		JLabel label = new JLabel();
		label.setMaximumSize(new Dimension(width, height));
		label.setCursor(Cursor.getDefaultCursor());
		label.setText(text);
		label.setName(name);
            
        return label;
	}
		
	private JButton createButton(String text, String name, int width, int height){
        JButton button = new JButton();
        button.setMaximumSize(new Dimension(width, height));
        button.setCursor(Cursor.getDefaultCursor());
        button.setMargin(new Insets(0,0,0,0)); 
        button.setText(text);
        button.setName(name);
        button.addActionListener(this);
        
        return button;
	}    
    
    private JPanel createTextArea(String title, String content, JTextArea textArea, int width, int height) {
        
		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel panel2 = new JPanel(new BorderLayout());
		textArea.setFont(new Font("Arial", Font.PLAIN, 12));
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true); 
		String text = "";
        textArea.setText(text);
        textArea.setSize(new Dimension(width, height));
        panel1.add(textArea);
        JScrollPane scrollPane = new JScrollPane(panel1);
        scrollPane.setPreferredSize(new Dimension(width, height));
        scrollPane.setMinimumSize(new Dimension(10, 10));
        
        panel2.add(scrollPane, BorderLayout.CENTER);
        return panel2;
    }  
    


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {

        //Create and set up the window.
        JFrame frame = new GUI();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        frame.pack();
        frame.setSize(950, 700);
        frame.setVisible(true);
    }
    
    public void updateTextArea(String content, JTextArea textArea){
        textArea.setText(content);
    }
    
    /**
     * Action Handler for GUI.
     * 
     * GENERATE: Generate summary or keywords
     * CLOSE: 	 Close window
     * 
     * Select Tabs : TextRank, TFxIDF, Baseline
     * Select RadioButtons
     * 
     * Open File: Open File to Summarize
     * 
     */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if ( e.getActionCommand().equalsIgnoreCase("GENERATE") ) {
			
			if(jtp.getSelectedComponent().getName().equals(jpTextRank.getName())) {	
			    
				if(typeKeyword){
					TextRankKeywords tr = new TextRankKeywords();
					Language language = Language.ICELANDIC;
					if(languageEnglish) {
						language = Language.ENGLISH;
					}
					
					Keywords keywords = new Keywords(tr.createKeywodsExtraction(fullText.getText(), language));
					updateTextArea(keywords.getKeywords(), summaryText);
					
				} else {
					TextRankSummarization tr = new TextRankSummarization();
					Language language = Language.ICELANDIC;
					if(languageEnglish) {
						language = Language.ENGLISH;
					}
					
					Summarizer summary = new Summarizer(tr.createSummary(fullText.getText(), language, 
														Integer.parseInt(percent.getText()), Integer.parseInt(words.getText())));
					updateTextArea(summary.getSummary(), summaryText);
				}
					
			} else if(jtp.getSelectedComponent().getName().equals(jpTFxIDF.getName())) {		
				
				TFxIDF tfxidf = new TFxIDF();

				if(languageEnglish) {
					languageEnglish = false;
					return;
				}
				
				Summarizer summary = new Summarizer(tfxidf.createSummary(fullText.getText(), false,  
													Integer.parseInt(percent.getText()), Integer.parseInt(words.getText())));
				updateTextArea(summary.getSummary(), summaryText);
				
			} else if(jtp.getSelectedComponent().getName().equals(jpBaseline.getName())) {		
		    
				LeadBasedBaseline llb = new LeadBasedBaseline(fullText.getText());
				Summarizer summary = new Summarizer(llb.createSummary(Integer.parseInt(percent.getText()), Integer.parseInt(words.getText())));
				updateTextArea(summary.getSummary(), summaryText);
			}
		} 
		//Handle open button action.
		else if (e.getActionCommand().equals(selectFile.getName()) ) {
            int returnVal = fc.showOpenDialog(GUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
               
                BufferedReader reader = null;
                String text = "";
                try {
                	reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
                    String s = "";
                    
                    while ((text = reader.readLine()) != null) {
                        s += text + "\n";
                    }
                    
                    fullText.setText(s);
                    
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException eio) {
                    }
                }
            } else {
               // log.append("Open command cancelled by user." + newline);
            }
           // log.setCaretPosition(log.getDocument().getLength());
 
        //Handle save button action.
        }
		else {
			exit();
		}
	}
	
	void exit() {
		//Display confirm dialog 
	    int confirmed = JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit?", "Confirm Quit", JOptionPane.YES_NO_OPTION); 
        
        //Close if user confirmed 
        if (confirmed == JOptionPane.YES_OPTION) {                             
                //Close frame 
                frame.dispose();
                System.exit(0);
        } 
	}
    
    void writeFileBytes(String filename, String content) {
        try {
        	Files.deleteIfExists(FileSystems.getDefault().getPath(".", filename));
            Files.write( FileSystems.getDefault().getPath(".", filename), content.getBytes("UTF-8"), StandardOpenOption.CREATE_NEW);
        }
        catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
	   
    public static void runAndStartGUI() {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

	@Override
	public void insertUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}
}
