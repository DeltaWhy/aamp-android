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
		disp = new SongDisplay(getActivity());
	    inflated = (RelativeLayout) inflater.inflate(R.layout.now_playing,
	            container, false);
	    RelativeLayout controlsLayout = (RelativeLayout)inflated.findViewById(R.id.control_items);
	    RelativeLayout.LayoutParams dispParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    inflated.addView(disp, dispParams);
	    controlsLayout.setGravity(Gravity.TOP);
	    dispParams.topMargin = 120;
	    return inflated;
	  }	  
}
