package net.miscjunk.aamp;

import android.app.Activity;
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
        RelativeLayout fragmentLayouts = (RelativeLayout) findViewById(R.id.fragments_view);
        fragmentLayouts.getLayoutParams().height = 8 * Screen.dims.y / 10;
        RelativeLayout buttonsLayout = (RelativeLayout) findViewById(R.id.bottom_menu);
        buttonsLayout.getLayoutParams().height = Screen.dims.y - fragmentLayouts.getLayoutParams().height;
        ((RelativeLayout.LayoutParams) buttonsLayout.getLayoutParams()).topMargin = fragmentLayouts.getLayoutParams().height;
        
        View npBut = buttonsLayout.findViewById(R.id.now_playing_but);
		RelativeLayout.LayoutParams npButParams = (RelativeLayout.LayoutParams) npBut.getLayoutParams();
        npButParams.width = Screen.dims.x / 2;
        npButParams.setMargins(Screen.dims.x / 2, 0, 0, 0);
        npBut.invalidate();
        
        RelativeLayout.LayoutParams playlistParams = (RelativeLayout.LayoutParams) buttonsLayout.findViewById(R.id.playlists_but).getLayoutParams();
        playlistParams.width = Screen.dims.x / 2;
        playlistParams.leftMargin = 0;
        checkSeekBars();
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
    	}
    	return false;
    }
}
