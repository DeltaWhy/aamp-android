package net.miscjunk.aamp;

import java.util.ArrayList;
import java.util.List;

public class AAMPSettings {
	private List<String> musicDirectories;
	
	public AAMPSettings() {
		musicDirectories = new ArrayList<String>();
	}
	
	
	public List<String> getMusicDirectories() {
		return musicDirectories;
	}



	public void addMusicPath(String path) {
		musicDirectories.add(path);
	}

}
