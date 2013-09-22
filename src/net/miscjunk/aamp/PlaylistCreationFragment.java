package net.miscjunk.aamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.miscjunk.aamp.common.Query;
import net.miscjunk.aamp.common.SimpleQuery;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class PlaylistCreationFragment extends Fragment implements  TextWatcher, OnClickListener {
	private EditText includes;
	private TextView realTimeQuery;
	private Button go;
	private Handler bgHandler;
	
	private Map<String, String> query;
	private Map<String, String> map;
	private Handler playListHandler;
	
	public PlaylistCreationFragment() {
	    map = new HashMap<String,String>();
	}
	
	public void setBGHandler(Handler h) {
		this.bgHandler = h;
	}
	
	public void setPlaylisthandler(Handler h) {
		this.playListHandler = h;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		query = new HashMap<String, String>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup inflated = (ViewGroup) inflater.inflate(R.layout.query_layout, container, false);
		includes = (EditText) inflated.findViewById(R.id.includes);
		includes.addTextChangedListener(this);
		realTimeQuery = (TextView) inflated.findViewById(R.id.query_preview);
		go = (Button) inflated.findViewById(R.id.run_query_but);
		go.setOnClickListener(this);
		return inflated;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		map.clear();
		String input = s.toString();
		String[] toks = input.split(" ");
		String lastVal = "", lastKey = "";		
		for(int i = 0; i < toks.length; i++) {
			if(toks[i].contains(":")) {
				if(!lastKey.isEmpty()) {
					if(map.containsKey(lastKey)) {
						map.put(lastKey, map.get(lastKey) + ", " + lastVal);
					}else {
						map.put(lastKey, lastVal);
					}
				}
				String[] parsed = toks[i].split(":");
				if(parsed.length < 2) { break; }
				map.put(parsed[0], parsed[1]);
				lastVal = parsed[1];
				lastKey = parsed[0];
			}else if(!toks[i].isEmpty()) {
				lastVal += toks[i];
			}
		}
		
		StringBuilder result = new StringBuilder("Mix where ");
		for(String val : map.keySet()) {
			if(val.equalsIgnoreCase("artist")) {
				result.append("artists include (");
				result.append(map.get(val) + ") ");
			}else if(val.equalsIgnoreCase("title")) {
				result.append("songs include (");
				result.append(map.get(val) + " ) ");
			}else if(val.equalsIgnoreCase("album")) {
				result.append("albums include (");
				result.append(map.get(val) + ") "); 
			}
		}
		realTimeQuery.setText(result);
	}

	@Override
	public void onClick(View v) {
		if(v == go) {
			Query q = buildQuery(map);
			Message m = bgHandler.obtainMessage(ProxyUIBridge.QUERY, q);
			m.setTarget(playListHandler);
			m.sendToTarget();
		}
	}

	private Query buildQuery(Map<String, String> q) {
		List<SimpleQuery> res = new ArrayList<SimpleQuery>();
		for(String key : q.keySet()) {
			SimpleQuery cur = new SimpleQuery("", "", "", "", "", false);
			if(key.equalsIgnoreCase("artist")) {
				cur.setArtist(q.get(key));
				res.add(cur);
			}else if(key.equalsIgnoreCase("title")) {
				cur.setTitle(q.get(key));
				res.add(cur);
			}else if(key.equalsIgnoreCase("album")) {
				cur.setAlbum(q.get(key)); 
				res.add(cur);
			}
		}
		return new Query(res.toArray(new SimpleQuery[0]));
	}
}
