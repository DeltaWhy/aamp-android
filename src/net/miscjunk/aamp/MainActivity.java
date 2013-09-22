package net.miscjunk.aamp;

import java.io.File;

import javax.security.auth.SubjectDomainCombiner;

import net.miscjunk.aamp.common.MusicProvider;
import net.miscjunk.aamp.common.MusicProviderDeserializer;
import net.miscjunk.aamp.common.Playlist;
import net.miscjunk.aamp.common.PlaylistDeserializer;
import net.miscjunk.aamp.common.Song;
import net.miscjunk.aamp.common.SongAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.google.gson.GsonBuilder;

public class MainActivity extends Activity implements Callback, OnClickListener {   
        private AAMPPlayerProxy player;
	private ProxyUIBridge bridge;
	private Handler bgHandle;
	private Handler mHandler;
	private ImageButton prev_button;
	private ImageButton play_button;
	private ImageButton pause_button;
	private ImageButton next_button;
	
	private Runnable pollBridgeHandler = new Runnable() {
		@Override
		public void run() {
			if(bridge.mHandler == null) { mHandler.postDelayed(this, 50);}
			else {
				bgHandle = bridge.mHandler;
			}
		}
	};
	private Fragment nowPlayingFragment;
	private ServerSelectorFragment serverSelectorFragment;
	private AAMPSettings settings;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set up threading stuff
        mHandler = new Handler(this);
        startService(new Intent(this, PlayerService.class));
        player = new AAMPPlayerProxy("localhost", "13531");
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(Song.class, new SongAdapter());
        gb.registerTypeAdapter(Playlist.class, new PlaylistDeserializer(player));
        gb.registerTypeAdapter(MusicProvider.class, new MusicProviderDeserializer());
        player.setGson(gb.create());
        
        bridge = new ProxyUIBridge(player, mHandler);
        bridge.start();
        try {
			bridge.join(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        pollBridgeHandler.run();
        
        
        //Set up UI stuff
        setContentView(R.layout.activity_main);
        getWindowManager().getDefaultDisplay().getSize(Screen.dims);      
        prev_button = (ImageButton)findViewById(R.id.prev_button);
        play_button = (ImageButton)findViewById(R.id.play_button);
        pause_button = (ImageButton)findViewById(R.id.pause_button);
        next_button = (ImageButton)findViewById(R.id.next_button);
        prev_button.setOnClickListener(this);
        play_button.setOnClickListener(this);
        pause_button.setOnClickListener(this);
        next_button.setOnClickListener(this);
        
        folderSelected = new EditText(this);
        
        FragmentManager fm = getFragmentManager();
        nowPlayingFragment = new NowPlayingFragment();
        fm.beginTransaction().add(R.id.fragments_view, nowPlayingFragment).commit();
        checkSeekBars();
        
        settings = new SettingsLoader(this.getApplicationContext()).load();
        if(settings.getMusicDirectories().isEmpty()) {
        	AlertDialog dialog = new AlertDialog.Builder(this)
        			.setNeutralButton("OK", new DialogInterface.OnClickListener() {	
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).setTitle("No music added yet")
					.setMessage("Please select a folder for music.").create();
        	dialog.show();
        }
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
    
    
    public void play(View v) {
        Message msg = Message.obtain(bgHandle);
        v.setVisibility(View.GONE);
        pause_button.setVisibility(View.VISIBLE);
        msg.what = ProxyUIBridge.PLAY;
        msg.sendToTarget();
    }
    
    public void pause(View v) {
        Message msg = Message.obtain(bgHandle);
        v.setVisibility(View.GONE);
        play_button.setVisibility(View.VISIBLE);
        msg.what = ProxyUIBridge.PAUSE;
        msg.sendToTarget();
    }
    
    public void next(View v) {
    	Log.e("Next", "Clicked");
    	Message.obtain(bgHandle, ProxyUIBridge.NEXT).sendToTarget();
    }
    
    public void prev(View v) {
    	Log.e("Prev", "Clicked");
    	Message.obtain(bgHandle, ProxyUIBridge.PREV).sendToTarget();
    }
    
    public void volumeBar(View v) {
    	SeekBar bar = (SeekBar) v;
    	Log.e("Volume", "Clicked " + bar.getProgress());
    }
    
    public void seekBar(View v) {
    	Log.e("Seek", "Clicked");
    }
	EditText folderSelected;

    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId() == R.id.servers_choose)  {
    	    FragmentManager fm = getFragmentManager();
    	    FragmentTransaction trans = fm.beginTransaction();
    	    trans.remove(nowPlayingFragment);
    	    if (serverSelectorFragment == null) {
    	        serverSelectorFragment = new ServerSelectorFragment();
    	    }
    	    trans.add(R.id.fragments_view, serverSelectorFragment);
    	    trans.addToBackStack(null);
    	    trans.commit();
    	} else if (item.getItemId() == R.id.exit){
    	    sendBroadcast(new Intent("net.miscjunk.aamp.PlayerService.STOP"));
    	    finish();
    	} else if (item.getItemId() == R.id.changeServer) {
    	    String host = serverSelectorFragment.ipAddress.getText().toString();
    	    System.out.println(host);
    	    player.setBaseUri("http://"+host+":13531/");
    	    getFragmentManager().popBackStack();
    	}else if(item.getItemId() == R.id.folders_choose) {
			AlertDialog chooseFolderDial = new AlertDialog.Builder(this).
					setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							File selected = new File(folderSelected.getText().toString().trim());
							SettingsLoader saver = new SettingsLoader(MainActivity.this);
							AAMPSettings settings = saver.load();
							settings.addMusicPath(selected.getAbsolutePath());
							saver.save(settings);
							System.out.println("Music in " + settings.getMusicDirectories());
							dialog.dismiss();
						}
					}).setTitle("Enter music folder name").setView(folderSelected).create();
			chooseFolderDial.show();
    	}
    	return false;
    }

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		default:
			break;
		}
		return false;
	}

	@Override
    public void onClick(View v) {
        if (v == prev_button) {
            prev(v);
        } else if (v == play_button) {
            play(v);
        } else if (v == pause_button) {
            pause(v);
        } else if (v == next_button) {
            next(v);
        }
    }

	public Handler getBackgroundHandler() {
		return bgHandle;
	}
}
