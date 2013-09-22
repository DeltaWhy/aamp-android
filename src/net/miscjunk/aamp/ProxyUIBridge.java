package net.miscjunk.aamp;

import net.miscjunk.aamp.common.Playlist;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

public class ProxyUIBridge extends Thread implements Handler.Callback{
	public static final int PLAY = 0;
	public static final int PAUSE = 1;
	public static final int NEXT = 2;
	public static final int PREV = 3;
	public static final int SKIP_TO = 4;
	public static final int INIT_UI_WITH_THE_DATA = 5;
	public static final int GET_ALL_SONGS = 6;
	
	public Handler mHandler;
	private AAMPPlayerProxy player;
	private Handler uiHandler;
	
	public ProxyUIBridge(AAMPPlayerProxy player, Handler uiHandler) {
		this.uiHandler = uiHandler;
		this.player = player;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case ProxyUIBridge.PLAY:
			player.play();
			break;
		case ProxyUIBridge.PAUSE:
		    player.pause();
		    break;
		case ProxyUIBridge.NEXT:
			player.next();
			break;
		case ProxyUIBridge.PREV:
			player.prev();
			break;
		case ProxyUIBridge.SKIP_TO:
			player.skipTo((String) msg.obj);
			break;
		case ProxyUIBridge.GET_ALL_SONGS:
			Playlist songs = player.getAllSongs();
			Message res = Message.obtain();
			res.obj = songs;
			res.what = GET_ALL_SONGS;
			try {
				msg.replyTo.send(res);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	@Override
	public void run() {
		Looper.prepare();
		this.mHandler = new Handler(Looper.myLooper(), this);
		Looper.loop(); //blocks forever
	}

}
