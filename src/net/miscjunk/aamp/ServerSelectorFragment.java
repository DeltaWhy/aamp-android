package net.miscjunk.aamp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ServerSelectorFragment extends Fragment {
    ViewGroup inflated;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        inflated = (ViewGroup) inflater.inflate(R.layout.server_selector,
                container, false);

        return inflated;
    }	  
}
