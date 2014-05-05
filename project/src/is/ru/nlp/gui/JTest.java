package is.ru.nlp.gui;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 

 
import java.awt.Component;
import javax.swing.*;
import static javax.swing.GroupLayout.Alignment.*;
 
public class JTest extends JFrame {
    public JTest() {
        JLabel label = new JLabel("Find What:");
        JTextField textField = new JTextField();
        
        JLabel label2 = new JLabel("Find What2:");
        JTextField textField2 = new JTextField();
        
        JRadioButton radioSummary = new JRadioButton("Summary");
        JRadioButton radioKeyword = new JRadioButton("Keyword");
        JRadioButton wholeCheckBox = new JRadioButton("Icelandic");
        JRadioButton backCheckBox = new JRadioButton("English");
        
        JButton findButton = new JButton("Find");
        JButton cancelButton = new JButton("Cancel");
 
        // remove redundant default border of check boxes - they would hinder
        // correct spacing and aligning (maybe not needed on some look and feels)
        radioSummary.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        radioKeyword.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        wholeCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        backCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
 
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
 
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addComponent(label)
            .addGroup(layout.createParallelGroup(LEADING)
                .addComponent(textField)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(LEADING)
                        .addComponent(radioSummary)
                        .addComponent(wholeCheckBox))
                    .addGroup(layout.createParallelGroup(LEADING)
                        .addComponent(radioKeyword)
                        .addComponent(backCheckBox))))
                        
            .addGroup(layout.createParallelGroup(LEADING)
                .addComponent(findButton)
                .addComponent(cancelButton))
        );
        
        layout.linkSize(SwingConstants.HORIZONTAL, findButton, cancelButton);
 
        layout.setVerticalGroup(layout.createSequentialGroup()
            
            .addGroup(layout.createParallelGroup(LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(radioSummary)
                        .addComponent(radioKeyword))
                    .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(wholeCheckBox)
                        .addComponent(backCheckBox)))
                .addComponent(cancelButton))
               
            .addGroup(layout.createParallelGroup(BASELINE)
                .addComponent(label)
                .addComponent(textField)
                .addComponent(findButton))
                
            .addGroup(layout.createParallelGroup(BASELINE)
                .addComponent(label)
                .addComponent(textField)
               .addComponent(findButton))
        );
 
        setTitle("Find");
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
     
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(
                                  "javax.swing.plaf.metal.MetalLookAndFeel");
                                //  "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                                //UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                new JTest().setVisible(true);
            }
        });
    }
}
