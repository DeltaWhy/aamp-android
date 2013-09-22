package net.miscjunk.aamp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ProxyUIBridge extends Thread implements Handler.Callback{
	public static final int PLAY = 0;
	public static final int PAUSE = 1;
	
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
