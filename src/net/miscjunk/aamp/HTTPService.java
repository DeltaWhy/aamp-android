package net.miscjunk.aamp;

import net.miscjunk.aamp.common.*;

import com.google.gson.GsonBuilder;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

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
        super.onCreate();
    }
    
    @Override
    public void onDestroy() {
        stopServer();
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
