package net.miscjunk.aamp;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SongDisplay extends ListView implements OnScrollListener {
	private QuerySelector adapter;
    public SongDisplay(Context context) {
		super(context);
		adapter = new QuerySelector();
		setAdapter(adapter);
		setOnScrollListener(this);
	}

    public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
        boolean loadMore = /* maybe add a padding */
            firstVisible + visibleCount >= totalCount;

        if(loadMore) {
            adapter.count += visibleCount; // or any other amount
            adapter.notifyDataSetChanged();
        }
    }

    public void onScrollStateChanged(AbsListView v, int s) { }    

    class QuerySelector extends BaseAdapter {
        int count = 40; /* starting amount */

        public int getCount() { return count; }
        public Object getItem(int pos) { return pos; }
        public long getItemId(int pos) { return pos; }

        public View getView(int pos, View convertView, ViewGroup p) {
                TextView view = new TextView(getContext());
                view.setText("entry " + pos);
                view.setGravity(Gravity.CENTER);
                view.setHeight(50);
                return view;
        }
    }
}
