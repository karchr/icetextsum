package is.ru.nlp.textsum.util;

import java.io.File;
import java.io.FilenameFilter;

public class FileExtensionFilter implements FilenameFilter {
	 private String[] ext;
     
     public FileExtensionFilter(String[] ext){
         this.ext = ext;
     }
    // @Override
     public boolean accept(File dir, String name) {
     	for (String element : ext) 
				 return name.toLowerCase().endsWith(element.toLowerCase());

        return false;
     } 
}
