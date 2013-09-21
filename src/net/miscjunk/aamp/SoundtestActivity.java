package net.miscjunk.aamp;

import net.miscjunk.aamp.common.PlayableSong;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class SoundtestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_soundtest);
		PlayableSong song = new LocalSDCardSong(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/Music/",
				"Carly Rae Jepsen - Call Me Maybe.MP3", this);
		song.setOnFinishedListener(new Runnable() {
			@Override
			public void run() {
				Log.e("Done", "playing song");
			}
		});
		song.play();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.soundtest, menu);
		return true;
	}

}
