package net.miscjunk.aamp;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import net.miscjunk.aamp.common.*;

public class SdFolderProvider implements MusicProvider {
    String rootDir;
    private Playlist playlist;
    private Map<String, String> names;
    private Context ctx;

    public SdFolderProvider(Context context, String path, boolean create) {
        this.ctx = context;
        this.rootDir = path;
        System.out.println("Root dir: "+rootDir);
        playlist = new Playlist();
        File thisDir = new File(rootDir);
        if (!thisDir.exists()) thisDir.mkdirs();
        names = new HashMap<String, String>();
        int i = 0;
        FilenameFilter filter = new Mp3FileFilter();
        if (thisDir.listFiles(filter) != null) {
            for(File songFile : thisDir.listFiles(filter)) {
                System.out.println("Detected song: " + songFile.getName());
                Song added = new Song(songFile.getName() + i + this.getId(), this);
                added.setTitle(songFile.getName()); //TODO - read ID3 tags
                playlist.addSong(added);
                names.put(added.getId(), songFile.getName());
                i++;
            }
        }
    }

    @Override
    public String getId() {
        return "sd-"+rootDir;
    }

    @Override
    public Playlist getAllSongs() {
        return playlist;
    }

    @Override
    public Playlist getSongs(SimpleQuery query) {
        Playlist p = new Playlist();
        for (Song s : playlist.getSongs()) {
            if (s.match(query)) p.addSong(s);
        }
        return p;
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public PlayableSong inflate(Song song) {
        return new LocalSDCardSong(rootDir, names.get(song.getId()), ctx);
    }
}
