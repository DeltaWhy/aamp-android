package net.miscjunk.aamp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class NowPlayingFragment extends Fragment{
	  SongDisplay disp;
	  RelativeLayout inflated;
	  
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
		  MainActivity act = (MainActivity) getActivity();
		  if(act.getBackgroundHandler() == null) {
			  throw new RuntimeException("Background Handler not initilized");
		}
		disp = new SongDisplay(getActivity(), act.getBackgroundHandler());
	    inflated = (RelativeLayout) inflater.inflate(R.layout.now_playing,
	            container, false);
	    RelativeLayout.LayoutParams dispParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    inflated.addView(disp, dispParams);
	    return inflated;
	  }	  
}
