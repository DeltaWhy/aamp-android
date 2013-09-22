package net.miscjunk.aamp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class MainActivity extends Activity {   

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    	if(paused) {
    		v.setBackgroundResource(android.R.drawable.ic_media_play);
    	}else {
    		v.setBackgroundResource(android.R.drawable.ic_media_pause);
    	}
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
}
