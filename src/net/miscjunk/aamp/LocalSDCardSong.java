package net.miscjunk.aamp;

import java.io.IOException;

import android.media.MediaPlayer;
import net.miscjunk.aamp.common.PlayableSong;

public class LocalSDCardSong implements PlayableSong {
	private MediaPlayer player;
	private Runnable finished;
	
	public LocalSDCardSong(String absPath, String musicFileName) {
		player = new  MediaPlayer();
		try {
			player.setDataSource(absPath + "/" + musicFileName);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			player.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {		
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.stop();
				finished.run();
			}
		});
	}
	
	@Override
	public boolean fetch() {
		try {
			player.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
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
