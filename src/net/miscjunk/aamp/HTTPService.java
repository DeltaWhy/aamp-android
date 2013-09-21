package net.miscjunk.aamp;

import net.miscjunk.aamp.common.*;

import com.google.gson.GsonBuilder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import org.eclipse.jetty.server.Server;


public class HTTPService extends Service {
    Player player;
    Server server;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("net.miscjunk.aamp.HTTPService.STOP")) {
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
    }

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("net.miscjunk.aamp.HTTPService.STOP");
        registerReceiver(receiver, filter);
        
        this.player = new Player();
        player.addProvider(new SdFolderProvider("/aamp-music", true));
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(Song.class, new SongSerializer());
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
        
        Notification n = new NotificationCompat.Builder(this)
            .setContentTitle("AAMP Server")
            .setContentText("AAMP is running in the background.")
            .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, ServiceTestActivity.class), 0))
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
