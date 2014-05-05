package is.ru.nlp.textsum.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReadInFile {

	private final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");
	private final String UTF_8 = "UTF-8";

	public byte[] readSmallFileBytes(String name) {
		try {
			Path path = FileSystems.getDefault().getPath(".", name);
			return Files.readAllBytes(path);
		} catch (IOException ioe) {
			System.err.println("ReadInFile.java - readSmallFileBytes(String name)");
			ioe.printStackTrace();
		}
		return null;
	}

	public List<String> readSmallFileLines(String name) {
		try {
			return Files.readAllLines(FileSystems.getDefault().getPath(".", name), CHARSET_UTF_8);
		} catch (IOException ioe) {
			System.err.println("ReadInFile.java - readSmallFileLines(String name)");
			ioe.printStackTrace();
		} 
		return Collections.emptyList();
	}

	public List<String> readLargeFileLines(String name) {
		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(FileSystems.getDefault().getPath(".", name), CHARSET_UTF_8);

			List<String> lines = new ArrayList<String>();
			String line = null;
			
			while ((line = reader.readLine()) != null)
				    lines.add(line);

			return lines;
		} catch (IOException ioe) {
			System.err.println("ReadInFile.java - readLargeFileLines(String name)");
			ioe.printStackTrace();
		} finally {
			if (reader != null) { // may not be necessary, implements AutoClosable
				try {
					reader.close();
				} catch (IOException e) {
					System.err.println("ReadInFile.java - readLargeFileLines(String name): finally not able to close file");
					e.printStackTrace();
				}
			}
		}
		return Collections.emptyList();
	}

	public void writeFileBytes(String filename, String content) {
		try {
			Files.write(FileSystems.getDefault().getPath(".", filename),
					    content.getBytes(UTF_8), StandardOpenOption.CREATE);
		} catch (IOException ioe) {
			System.err.println("ReadInFile.java - writeFileBytes(String filename, String content)");
			ioe.printStackTrace();
		}
	}

	public void writeFileBytesBuffered(String filename, String content) {
		BufferedWriter writer = null;
		try {
			writer = Files.newBufferedWriter(
					 FileSystems.getDefault().getPath(".", filename),
					 CHARSET_UTF_8, StandardOpenOption.CREATE);

			writer.write(content, 0, content.length());
		} catch (IOException ioe) {
			System.err.println("ReadInFile.java - writeFileBytesBuffered(String name)");
			ioe.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					System.err.println("ReadInFile.java - writeFileBytesBuffered(String name): finally, not able to close file");
					e.printStackTrace();
				}
			}
		}
	}

}
