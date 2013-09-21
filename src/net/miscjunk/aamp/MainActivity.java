package net.miscjunk.aamp;

import java.io.IOException;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.view.Menu;

import javax.jmdns.*;

public class MainActivity extends Activity {
    ServiceInfo serviceInfo;
    JmDNS jmdns;
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DEBUG - never ever do this in real life
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        serviceInfo = ServiceInfo.create("_test._tcp.local.","AndroidTest",0,"test from android");
        try {
            jmdns = JmDNS.create();
            jmdns.registerService(serviceInfo);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (jmdns != null) {
            jmdns.unregisterAllServices();
            try {
                jmdns.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
