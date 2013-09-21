package net.miscjunk.aamp;

import java.io.File;
import java.io.FilenameFilter;

public class Mp3FileFilter implements FilenameFilter {

	@Override
	public boolean accept(File file, String name) {
		return name.toLowerCase().endsWith("mp3");
	}

}
