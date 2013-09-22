package net.miscjunk.aamp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class IOUtils {
	
	public static String[] readFile(Context context, String name) {
		List<String> contents = new ArrayList<String>();
		FileInputStream stream;
		try {
			stream = context.openFileInput(name);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = reader.readLine();
			while(line != null) {
				contents.add(line);
				line = reader.readLine();
			}
			stream.close();
			reader.close();
			return contents.toArray(new String[] {});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean writeFile(Context context, String[] contents, String name) {
		try {
			FileOutputStream out = context.openFileOutput(name, Context.MODE_PRIVATE);
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(out));
			for(String line : contents) {
				wr.write(line + "\n");
			}
			wr.flush();
			wr.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
		
	}

}
