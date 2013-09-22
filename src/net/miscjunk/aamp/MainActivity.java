package net.miscjunk.aamp;

import net.miscjunk.aamp.common.MusicProvider;
import net.miscjunk.aamp.common.MusicProviderDeserializer;
import net.miscjunk.aamp.common.Playlist;
import net.miscjunk.aamp.common.PlaylistDeserializer;
import net.miscjunk.aamp.common.Song;
import net.miscjunk.aamp.common.SongSerializer;

import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class MainActivity extends Activity implements Callback {   
	private ProxyUIBridge bridge;
	private Handler bgHandle;
	private Handler mHandler;
	private Runnable tellMeGodHesNotNull = new Runnable() {
		@Override
		public void run() {
			if(bridge.mHandler == null) { mHandler.postDelayed(this, 200); Log.e("He's null", "jim"); }
			else {
				bgHandle = bridge.mHandler;
				Log.e("Free at last", "Free at last");
			}
		}
	};
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set up threading stuff
        mHandler = new Handler(this);
        startService(new Intent(this, HTTPService.class));
        AAMPPlayerProxy player = new AAMPPlayerProxy("localhost", "13531");
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(Song.class, new SongSerializer());
        gb.registerTypeAdapter(Playlist.class, new PlaylistDeserializer(player));
        gb.registerTypeAdapter(MusicProvider.class, new MusicProviderDeserializer());
        player.setGson(gb.create());
        
        bridge = new ProxyUIBridge(player, mHandler);
        bridge.start();
        tellMeGodHesNotNull.run();
        
        
        //Set up UI stuff
        setContentView(R.layout.activity_main);
        getWindowManager().getDefaultDisplay().getSize(Screen.dims);      
        checkSeekBars();
        startService(new Intent(this, PlayerService.class));
    }

    private void checkSeekBars() {
	    SeekBar vol = (SeekBar) findViewById(R.id.volume_bar);
	    if(vol != null) {
		    vol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					Log.e("got volume bar", "" + seekBar.getProgress());
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					
				}
			});
	    }
	    SeekBar progress = (SeekBar) findViewById(R.id.seekBar);
	    if(progress != null) {
		    progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					Log.e("got seek bar", "" + seekBar.getProgress());
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					seekBar.showContextMenu();
					
				}
			});		
	    }
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    public void playlistsClicked(View v) {
    	
    }
    
    public void nowPlayingClicked(View v) {
    	Log.e("Play", "Clicked");
    }
    
    
    private  boolean paused = true;
    public void togglePlayPause(View v) {
		Message msg = Message.obtain(bgHandle);
    	if(paused) {
    		v.setBackgroundResource(android.R.drawable.ic_media_play);
    		msg.what = ProxyUIBridge.PAUSE;
    	}else {
    		v.setBackgroundResource(android.R.drawable.ic_media_pause);
    		msg.what = ProxyUIBridge.PLAY;
    	}
    	
		msg.sendToTarget();
    	paused = !paused;
    }
    
    public void next(View v) {
    	Log.e("Next", "Clicked");
    }
    
    public void prev(View v) {
    	Log.e("Prev", "Clicked");
    }
    
    public void volumeBar(View v) {
    	SeekBar bar = (SeekBar) v;
    	Log.e("Volume", "Clicked " + bar.getProgress());
    }
    
    public void seekBar(View v) {
    	Log.e("Seek", "Clicked");
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId() == R.id.servers_choose)  {
        	Log.e("CLick", "choose servers");
    	} else if (item.getItemId() == R.id.exit){
    	    sendBroadcast(new Intent("net.miscjunk.aamp.PlayerService.STOP"));
    	    finish();
    	}
    	return false;
    }

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}
}
