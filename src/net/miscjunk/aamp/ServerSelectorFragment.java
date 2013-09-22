package net.miscjunk.aamp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

public class ServerSelectorFragment extends Fragment implements android.widget.CompoundButton.OnCheckedChangeListener {
    ViewGroup inflated;
    RadioButton localServer;
    RadioButton otherServer;
    EditText ipAddress;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        inflated = (ViewGroup) inflater.inflate(R.layout.server_selector,
                container, false);
        localServer = (RadioButton)inflated.findViewById(R.id.localServer);
        otherServer = (RadioButton)inflated.findViewById(R.id.otherServer);
        localServer.setOnCheckedChangeListener(this);
        otherServer.setOnCheckedChangeListener(this);
        ipAddress = (EditText)inflated.findViewById(R.id.ipAddress);
        return inflated;
    }	  
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.server_selector, menu);
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean checked) {
        if (v == localServer) {
            otherServer.setChecked(!checked);
            ipAddress.setEnabled(otherServer.isChecked());
        } else if (v == otherServer) {
            localServer.setChecked(!checked);
            ipAddress.setEnabled(checked);
        }
    }
}
