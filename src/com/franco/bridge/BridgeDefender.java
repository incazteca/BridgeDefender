package com.franco.bridge;


import android.app.Activity;
import android.os.Bundle;

import com.franco.bridge.BridgeDefenderView.BridgeDefenderThread;

public class BridgeDefender extends Activity {

	private BridgeDefenderThread bDefenderThread;
	private BridgeDefenderView bDefenderView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bridge_layout);
        
        //get handle to the bridge view from the xml and the threadzors
        bDefenderView = (BridgeDefenderView) findViewById(R.id.bridge);
        bDefenderThread = bDefenderView.getThread();
        
        if (savedInstanceState == null) {
            // we were just launched: set up a new game
            bDefenderThread.setState(BridgeDefenderThread.STATE_RUNNING);
            bDefenderThread.doStart();
        }
        
 
        
    }
    
}