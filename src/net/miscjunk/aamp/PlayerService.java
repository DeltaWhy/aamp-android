package net.miscjunk.aamp;

import net.miscjunk.aamp.common.*;

import com.google.gson.GsonBuilder;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import org.eclipse.jetty.server.Server;


public class PlayerService extends Service {
    Player player;
    Server server;
    EventServer eventServer;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("net.miscjunk.aamp.PlayerService.STOP")) {
                stopServer();
                stopSelf();
            }
        }
    };
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    public void stopServer() {
        if (server != null) {
            try {
                server.stop();
                server = null;
                System.out.println("Cleaned up server.");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Couldn't clean up server.");
            }
        }
        if (eventServer != null) {
            eventServer.sendMessage("Shutting down");
            eventServer.stop();
        }
    }

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("net.miscjunk.aamp.PlayerService.STOP");
        registerReceiver(receiver, filter);
        AAMPSettings settings = new SettingsLoader(this.getApplicationContext()).load();
        
        this.player = new Player();
        for(String path : settings.getMusicDirectories()) {
        	player.addProvider(new SdFolderProvider(this, "/sdcard" + path, true));
        }
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(Song.class, new SongAdapter());
        gb.registerTypeAdapter(Playlist.class, new PlaylistDeserializer(this.player));
        gb.registerTypeAdapter(MusicProvider.class, new MusicProviderDeserializer());
        server = new Server(13531);
        server.setHandler(new HttpPlayerHandler(player, gb.create()));
        try {
            server.start();
            System.out.println("Listening on " + 13531);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        eventServer = new EventServer(13532);
        eventServer.start();
        
        Notification n = new NotificationCompat.Builder(this)
            .setContentTitle("AAMP Server")
            .setContentText("AAMP is running in the background.")
            .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher)
            .build();
        
        startForeground(13531, n);
        super.onCreate();
    }
    
    @Override
    public void onDestroy() {
        stopServer();
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
