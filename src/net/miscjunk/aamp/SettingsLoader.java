package net.miscjunk.aamp;

import android.content.Context;

public class SettingsLoader extends AAMPSettings {

	private Context context;

	public SettingsLoader(Context applicationContext) {
		this.context = applicationContext;
	}

	public AAMPSettings load() {
		AAMPSettings deser = new AAMPSettings();
		String[] settings = IOUtils.readFile(this.context, "settings.txt");
		if(settings == null) {
			return deser;
		}
		if(settings.length > 0) {
			for(String path : settings[0].split("\t")) {
				deser.addMusicPath(path);
			}
		}
		return deser;
	}
	
	public void save(AAMPSettings serialize) {
		String[] settings = new String[1];
		StringBuilder b = new StringBuilder(); boolean first = true;
		for(String path : serialize.getMusicDirectories()) {
			if(first) { //dont put tab
				first = false;
				b.append(path);
			}else {
				b.append("\t" + path);
			}
		}
		settings[0] = b.toString();
		IOUtils.writeFile(this.context, settings, "settings.txt");
	}

}
