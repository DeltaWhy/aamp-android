package net.miscjunk.aamp;

import net.miscjunk.aamp.common.*;

import com.google.gson.GsonBuilder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import org.eclipse.jetty.server.Server;


public class HTTPService extends Service {
    private Player player;
    private Server server;
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.player = new Player();
        player.addProvider(new SdFolderProvider(this, "/Music", true));
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
        return super.onStartCommand(intent, flags, startId);
    }
}
