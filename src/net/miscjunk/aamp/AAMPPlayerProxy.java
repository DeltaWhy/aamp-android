package net.miscjunk.aamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

import net.miscjunk.aamp.common.MusicProvider;
import net.miscjunk.aamp.common.MusicQueue;
import net.miscjunk.aamp.common.PlayerClient;
import net.miscjunk.aamp.common.Playlist;
import net.miscjunk.aamp.common.Query;
import net.miscjunk.aamp.common.Song;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AAMPPlayerProxy implements PlayerClient {
	private String baseUri;
	private HttpClient client = new DefaultHttpClient();
	private Gson gs;
	
	public AAMPPlayerProxy(String hostname, String port) {
		baseUri = "http://" + hostname + ":" + port + "/";
	}
	
	@Override
	public boolean next() {
		issuePost("control/", "next");		
		return false;
	}
	
	private String issuePost(String target, String entity) {
		HttpPost goNext = new HttpPost(baseUri + target);
		try {
			goNext.setEntity(new StringEntity(entity));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpResponse res = null;
		try {
			res = client.execute(goNext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return read(res);
	}

	private String read(HttpResponse res) {
		if(res == null || res.getEntity() == null) {
			return "";
		}
		try {
			BufferedReader reader = 
					new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
			StringBuilder resBuilder = new StringBuilder();
			String nextLine = reader.readLine();
			while(nextLine != null && !nextLine.isEmpty()) {
				resBuilder.append(nextLine);
				nextLine = reader.readLine();
			}
			return resBuilder.toString();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "error";
	}

	@Override
	public boolean prev() {
		issuePost("control/", "prev");
		return false;
	}

	@Override
	public boolean play() {
		issuePost("control/", "play");
		return false;
	}

	@Override
	public boolean pause() {
		issuePost("control/", "pause");
		return false;
	}

	@Override
	public boolean seek(double position) {
		issuePost("control/", "seek=" + String.valueOf(position));
		return false;
	}

	@Override
	public boolean skipTo(String id) {
		issuePost("control/", "skipTo=" + id);
		return false;
	}

	@Override
	public boolean setVolume(double volume) {
		issuePost("control/", "volume=" + String.valueOf(volume));
		return false;
	}

	@Override
	public boolean setQueue(MusicQueue queue) {
		return false;
	}

	@Override
	public double getVolume() {
		return 0;
	}

	@Override
	public double getPosition() {
		return 0;
	}

	@Override
	public MusicQueue getQueue() {
		issueSimpleGet("queue");
		return null;
	}

	private String issueSimpleGet(String target) {
		HttpGet get = new HttpGet(baseUri + target);
		try {
			HttpResponse res = client.execute(get);
			return read(res);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public Song getCurrentSong() {
		return null;
	}

	@Override
	public List<Playlist> getPlaylists() {
		String result = issueSimpleGet("playlists");
		Type listOfPlaylist = new TypeToken<List<Playlist>>(){}.getType();
		return gs.fromJson(result, listOfPlaylist);
	}

	@Override
	public Playlist getPlaylist(String id) {
		String res = issueSimpleGet("playlists/" + id);
		return gs.fromJson(res, Playlist.class);
	}

	@Override
	public boolean updatePlaylist(Playlist list) {
		String res = issuePut("playlists", gs.toJson(list));
		return false;
	}

	private String issuePut(String target, String body) {
		HttpPut goNext = new HttpPut(baseUri + target);
		try {
			goNext.setEntity(new StringEntity(body));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpResponse res = null;
		try {
			res = client.execute(goNext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return read(res);
	}

	@Override
	public boolean addPlaylist(Playlist list) {
		String json = gs.toJson(list);
		issuePost("playlists", json);
		return false;
	}

	@Override
	public boolean removePlaylist(String id) {
		issueDelete("playlists/" + id);
		return false;
	}

	private String issueDelete(String target) {
		HttpDelete del = new HttpDelete(baseUri + target);
		HttpResponse res;
		try {
			res = client.execute(del);
			return read(res);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public List<MusicProvider> getProviders() {
		String result = issueSimpleGet("providers"); //How to deserialize list?
		Type listOfMP = new TypeToken<List<MusicProvider>>(){}.getType();
		return gs.fromJson(result, listOfMP);
	}

	@Override
	public MusicProvider getProvider(String id) {
		String result = issueSimpleGet("providers/" + id);
		return gs.fromJson(result, MusicProvider.class);
	}

	@Override
	public boolean updateProvider(MusicProvider list) {
		String result = issuePut("providers", gs.toJson(list));
		return false;
	}

	@Override
	public boolean addProvider(MusicProvider provider) {
		String result = issuePost("providers", gs.toJson(provider));
		return false;
	}

	@Override
	public boolean removeProvider(String id) {
		issueDelete("providers/" + id);
		return false;
	}

	@Override
	public Playlist getAllSongs() {
		String json = issueSimpleGet("songs");
		return gs.fromJson(json, Playlist.class);
	}

	@Override
	public Playlist buildPlaylist(Query query) {
		issuePost("query", gs.toJson(query));
		return null;
	}

	public void setGson(Gson gson) {
		this.gs = gson;
	}

	public void setBaseUri(String baseUri) {
	    this.baseUri = baseUri;
	}
}
