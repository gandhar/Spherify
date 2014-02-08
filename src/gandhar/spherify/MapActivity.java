package gandhar.spherify;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.readystatesoftware.systembartint.SystemBarTintManager;

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
import au.com.bytecode.opencsv.CSVReader;

public class MapActivity extends Activity {

public static String TAG= "spherify";
public Marker goa;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_map);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(128, 0, 0, 0)));
		
	    SystemBarTintManager tintManager = new SystemBarTintManager(this);
	    tintManager.setStatusBarTintEnabled(true);
	    tintManager.setNavigationBarTintEnabled(true);
	    tintManager.setTintColor(Color.parseColor("#80000000"));
		
		
        Bundle extras = getIntent().getExtras();
        boolean message = extras.getBoolean("containsdata");
        Log.d(TAG,""+message);
        LatLng mark1=null;
        if(message){
        	mark1 = new LatLng(extras.getFloat("lat"),extras.getFloat("long"));
        	Log.d(TAG, "lat"+extras.getFloat("lat")+"long"+extras.getFloat("long"));
        }
        else{
        	double gps[] = {0,0};
        	gps = getGPS();
			if(gps[0]!=0&&gps[1]!=0)
				mark1 = new LatLng(gps[0], gps[1]);
            else
            	mark1 = new LatLng(48.8567, 2.3508); //paris
        }

        
        
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setZoomControlsEnabled(false);
        goa = map.addMarker(new MarkerOptions()
                                  .position(mark1)
                                  .draggable(true));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mark1, 5));
      
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
    	 LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	 List<String> providers = lm.getProviders(true);
    	 
    	 Location l = null;
    	 for(String provider : providers){
    		 l = lm.getLastKnownLocation(provider);
    		 if(l!=null)
    			 break;
    	 }
    	 double[] gps = new double[2];
    	 if (l != null) {
    	  gps[0] = l.getLatitude();
    	  gps[1] = l.getLongitude();
    	 }
    	 else{
    		 InputStream csvStream = getBaseContext().getResources().openRawResource(R.raw.country_latlon);
    		 InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
    		 CSVReader csvReader = new CSVReader(csvStreamReader);
    		 String [] nextLine;
    		 try {
    			 String currentCountry = getBaseContext().getResources().getConfiguration().locale.getCountry();
    			 while ((nextLine = csvReader.readNext()) != null) {
    				if(nextLine[0].equals(currentCountry)){
    					gps[0]=Double.valueOf(nextLine[1]);
    					gps[1]=Double.valueOf(nextLine[2]);
    					break;
    				}
				 }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	 }

    	 return gps;
    	}
    
}
