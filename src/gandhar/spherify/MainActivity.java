package gandhar.spherify;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
public static String TAG= "spherify";
private static final int RESULT_LOAD_IMAGE= 100;
private static final int RESULT_LOAD_IMAGE1= 101;
public String dest=null,src=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ImageView imageView = (ImageView) findViewById(R.id.imageView1);
    ImageView imageView1 = (ImageView) findViewById(R.id.imageView2);
	
    imageView.setOnClickListener(new View.OnClickListener(){
    	//@Override
   	   public void onClick(View v) {
   		    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
   		    startActivityForResult(i, RESULT_LOAD_IMAGE);
   	   }        
    });
    
    imageView1.setOnClickListener(new View.OnClickListener(){
    	//@Override
   	   public void onClick(View v) {
   		    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
   		    startActivityForResult(i, RESULT_LOAD_IMAGE1);
   	   }        
    });

       	       
    }
    
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
    	Uri selectedImage = data.getData();
        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        src=getRealPathFromURI(getBaseContext(),selectedImage);
        
        boolean b = queryLightCycle360(src);
        if (!b){
        	Toast.makeText(getBaseContext(), "Selected Image does not contain photosphere xmp data, please choose again", Toast.LENGTH_LONG).show();
        	Log.d(TAG,"well at least yoou tried");
        }
        else{
        	Bitmap d = BitmapFactory.decodeFile(src);
        	int nh = (int) ( d.getHeight() * (512.0/ d.getWidth()));
        	Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
        	imageView.setImageBitmap(scaled);
        }
    }

    if (requestCode == RESULT_LOAD_IMAGE1 && resultCode == RESULT_OK && null != data) {
    	Uri selectedImage = data.getData();
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        dest=getRealPathFromURI(getBaseContext(),selectedImage);
        Bitmap d = BitmapFactory.decodeFile(dest);
        int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
        imageView.setImageBitmap(scaled);
    }
 
 
}


    public String getRealPathFromURI(Context context, Uri contentUri) {
    	  Cursor cursor = null;
    	  try { 
    	    String[] proj = { MediaStore.Images.Media.DATA };
    	    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
    	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    	    cursor.moveToFirst();
    	    return cursor.getString(column_index);
    	  } finally {
    	    if (cursor != null) {
    	      cursor.close();
    	    }
    	    
    	  }
    	}
  


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// Inflate the menu items for use in the action bar
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_activity_actions, menu);
    	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// Handle presses on the action bar items
    	switch (item.getItemId()) {
        	case R.id.action_doit:
        		if(src!=null||dest!=null){
        			XMPMeta xmpmeta = null;
            	    xmpmeta= XmpUtil.extractXMPMeta(src);
            	    XmpUtil.writeXMPMeta(dest,xmpmeta);
            	    Toast.makeText(getBaseContext(), "done, now go check the gallery", Toast.LENGTH_LONG).show();
        		}
        		else
        			Toast.makeText(getBaseContext(), "please select both images", Toast.LENGTH_LONG).show();
        		return true;
        		
        	case R.id.action_about:
        		Intent myIntent = new Intent(MainActivity.this, AboutActivity.class);
        		myIntent.putExtra("key", 15); //Optional parameters
        		MainActivity.this.startActivity(myIntent);
        		
        		return true;
        		
      /*  	case R.id.action_flip:
        		Intent myIntent1 = new Intent(MainActivity.this, EditActivity.class);
        		myIntent1.putExtra("key", 16); //Optional parameters
        		myIntent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
        		myIntent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		MainActivity.this.startActivity(myIntent1);
        		
        		return true;
        		*/
        	default:
        		return super.onOptionsItemSelected(item);
    	}
    }
    
        
    public static boolean queryLightCycle360(String path) {
        try {
            InputStream is = new FileInputStream(path);
            XMPMeta meta = XmpUtil.extractXMPMeta(is);
            if (meta == null) {
                return false;
            }
            String namespace = "http://ns.google.com/photos/1.0/panorama/";
            String cropWidthName = "GPano:CroppedAreaImageWidthPixels";
            String fullWidthName = "GPano:FullPanoWidthPixels";

            if (!meta.doesPropertyExist(namespace, cropWidthName)) {
                return false;
            }
            if (!meta.doesPropertyExist(namespace, fullWidthName)) {
                return false;
            }

            Integer cropValue = meta.getPropertyInteger(namespace, cropWidthName);
            Integer fullValue = meta.getPropertyInteger(namespace, fullWidthName);
            Log.d(TAG, "crflipop"+cropValue + "full"+ fullValue);
            // Definition of a 360:
            // GFullPanoWidthPixels == CroppedAreaImageWidthPixels
            if (cropValue != null && fullValue != null) {
                //return cropValue.equals(fullValue);
                return Math.abs(fullValue-cropValue)<5;
                
            }
            
            return false;
        } catch (FileNotFoundException e) {
            return false;
        } catch (XMPException e) {
            return false;
        } 
    }
    
}