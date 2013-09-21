package net.miscjunk.aamp;

import java.io.File;
import java.io.IOException;

import net.miscjunk.aamp.common.PlayableSong;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class LocalSDCardSong implements PlayableSong {
	private MediaPlayer player;
	private Runnable finished;
	
	public LocalSDCardSong(String absPath, String musicFileName, Context context) {
		try {
			Uri uri = Uri.fromFile(new File(absPath + "/" + musicFileName));
			player = new MediaPlayer();
			player.setDataSource(context, uri);
			player.prepare();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {		
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
				finished.run();
			}
		});
		player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				switch (what) {
				case MediaPlayer.MEDIA_ERROR_MALFORMED:
					Log.e("Malformed", "mal");
					break;
				case MediaPlayer.MEDIA_ERROR_IO:
					Log.e("Shit", "Media error");
					break;

				default:
					break;
				}
				return false;
			}
		});
	}
	
	@Override
	public boolean fetch() {
		return true;
	}

	@Override
	public Runnable getOnFinishedListener() {
		return this.finished;
	}

	@Override
	public double getPosition() {
		return player.getCurrentPosition() / 1000.0;
	}

	@Override
	public boolean pause() {
		player.pause();
		return false;
	}

	@Override
	public boolean play() {
		player.start();
		return false;
	}

	@Override
	public boolean seek(double time) {
		player.seekTo((int) (time * 1000));//wants millis
		return false;
	}

	@Override
	public void setOnFinishedListener(Runnable finished) {
		this.finished = finished;
	}

	@Override
	public void setVolume(double volume) {
		player.setVolume((float)volume, (float) volume);
	}

	@Override
	public void stop() {
		player.stop();
	}

}
