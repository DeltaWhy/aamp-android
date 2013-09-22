package net.miscjunk.aamp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ServiceTestActivity extends Activity implements OnClickListener {
    Button startButton;
    Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_test);
        startButton = (Button)findViewById(R.id.startButton);
        stopButton = (Button)findViewById(R.id.stopButton);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.service_test, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == startButton) {
            startService(new Intent(this, PlayerService.class));
        } else if (v == stopButton) {
            sendBroadcast(new Intent("net.miscjunk.aamp.PlayerService.STOP"));
            stopService(new Intent(this, PlayerService.class));
        }
    }
}
