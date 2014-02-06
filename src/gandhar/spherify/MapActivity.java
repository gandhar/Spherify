package gandhar.spherify;

import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

public class MapActivity extends Activity {

public static String TAG= "spherify";
public Marker goa;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(128, 0, 0, 0)));
		
		
        Bundle extras = getIntent().getExtras();
        boolean message = extras.getBoolean("containsdata");
        Log.d(TAG,""+message);
        LatLng mark1=null;
        if(message){
        	mark1 = new LatLng(extras.getFloat("lat"),extras.getFloat("long"));
        	Log.d(TAG, "lat"+extras.getFloat("lat")+"long"+extras.getFloat("long"));
        }
        else{
        	double gps[] = getGPS();
            if(gps[0]!=0&&gps[1]!=0)
				mark1 = new LatLng(gps[0], gps[1]);
            else
            	mark1 = new LatLng(48.8567, 2.3508); //paris
        }

        
        
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        
        goa = map.addMarker(new MarkerOptions()
                                  .position(mark1)
                                  .draggable(true));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mark1, 15));
      
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// Handle presses on the action bar items
    	
    	GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

    	switch (item.getItemId()) {
    	
    		case R.id.action_done:
    			Log.d(TAG,""+goa.getPosition());
    			EditActivity.locationdata = goa.getPosition();
    			onBackPressed();
    	    	return true;

    	    case android.R.id.home:
    	    	onBackPressed();
    	        return true;
    	        

        	case R.id.action_switch:
        		                
        		if(map.getMapType()==GoogleMap.MAP_TYPE_SATELLITE)
        	        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        		else
        	        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        		return true;

        		
        	default:
        		return super.onOptionsItemSelected(item);
    	}
    }
    
    private double[] getGPS() {
    	 LocationManager lm = (LocationManager) getSystemService(
    	  Context.LOCATION_SERVICE);
    	 List<String> providers = lm.getProviders(true);

    	 Location l = null;

    	 for (int i=providers.size()-1; i>=0; i--) {
    	  l = lm.getLastKnownLocation(providers.get(i));
    	  if (l != null) break;
    	 }

    	 double[] gps = new double[2];
    	 if (l != null) {
    	  gps[0] = l.getLatitude();
    	  gps[1] = l.getLongitude();
    	 }

    	 return gps;
    	}
    
}