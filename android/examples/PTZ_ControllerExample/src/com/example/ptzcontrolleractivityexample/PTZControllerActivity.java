package com.example.ptzcontrolleractivityexample;


import org.crs4.most.streaming.enums.PTZ_Direction;
import org.crs4.most.visualization.PTZ_ControllerFragment;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v7.app.ActionBarActivity;

public class PTZControllerActivity extends ActionBarActivity implements PTZ_ControllerFragment.IPtzCommandReceiver  {

	private static String TAG = "PTZControllerActivity";
	private PTZ_ControllerFragment ptzControllerFragment = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.ptzControllerFragment = PTZ_ControllerFragment.newInstance();
        
        // add the first fragment to the first container
    	FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.add(R.id.container_stream_1,
				this.ptzControllerFragment);
		fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


	@Override
	public void onPTZstartMove(PTZ_Direction dir) {
		Log.d(TAG, "Called onPTZstartMove for direction:" + dir);
		Toast.makeText(this, "Start Moving to ->" + dir, Toast.LENGTH_LONG).show();
	}


	@Override
	public void onPTZstopMove(PTZ_Direction dir) {
		Log.d(TAG, "Called onPTZstoptMove for direction:" + dir);
		Toast.makeText(this, "Stop Moving from ->" + dir, Toast.LENGTH_LONG).show();
	}
}
