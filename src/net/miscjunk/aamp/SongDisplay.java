package net.miscjunk.aamp;

import net.miscjunk.aamp.common.Playlist;
import net.miscjunk.aamp.common.Song;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Messenger;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SongDisplay extends ListView implements OnScrollListener {
	private SongAdapter adapter;
    public SongDisplay(Context context, Handler handler) {
		super(context);
		adapter = new SongAdapter(handler);
		setOnScrollListener(this);
	}

    public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
        boolean loadMore = firstVisible + visibleCount >= totalCount;
        if(loadMore) {
            adapter.cycle(visibleCount / 2); // or any other amount
            adapter.notifyDataSetChanged();
        }
    }

    public void onScrollStateChanged(AbsListView v, int s) { }    

    public class SongAdapter extends BaseAdapter implements Callback  {
        int count = 40; /* starting amount */
        Playlist list;
        public Handler mHandler;
        private Handler bg;
        public SongAdapter(Handler bg) {
        	this.list = new Playlist();//init to something non null, dynamic load later
        	mHandler = new Handler(this);
        	Message giveMeData = Message.obtain(bg, ProxyUIBridge.GET_ALL_SONGS);
        	giveMeData.replyTo = new Messenger(this.mHandler);
        	bg.sendMessage(giveMeData);
        	this.bg = bg;
		}
               
        public void cycle(int i) {
        	if(count < list.size()) {
        		count += 40;
        	}
		}
        
		public Object getItem(int pos) { return list.getSong(pos); }
        public long getItemId(int pos) { return pos; }

        public View getView(int pos, View convertView, ViewGroup p) {
        		Song item = (Song)getItem(pos);
        		SongView view = new SongView(getContext(), item.getId());
                view.setText(item.getTitle());
                view.setOnClickListener(new SongView.OnClickListener() {
					@Override
					public void onClick(View v) {
						SongView song = (SongView) v;
						Message.obtain(bg, ProxyUIBridge.SKIP_TO, song.getId());
					}
				});
                view.setGravity(Gravity.CENTER);
                view.setHeight(50);
                return view;
        }

		@Override
		public int getCount() {
			return Math.min(count, list.size());
		}
		
		@Override
		public boolean handleMessage(Message msg) {
			if(msg.what == ProxyUIBridge.GET_ALL_SONGS) {//We got them back yay
				this.list.append((Playlist)msg.obj); // #WOOT
			}
			return false;
		}
    }
    
    public class SongView extends TextView {
    	private String songId;
    	public SongView(Context context, String songID) {
    		super(context);
    		this.songId = songID;
    	}
    	
    	String getSongId() {return songId; }
    	void setSongId(String id) { songId = id; }
    }
}
