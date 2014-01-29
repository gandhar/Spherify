package gandhar.spherify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.Calendar;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.options.SerializeOptions;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditActivity extends Activity {
public static String TAG= "spherify";
private static final int RESULT_LOAD_IMAGE= 102;
public String src1=null;
public XMPMeta fakemeta=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit);
    ImageView imageView = (ImageView) findViewById(R.id.imageView11);
    
	
    imageView.setOnClickListener(new View.OnClickListener(){
    	//@Override
   	   public void onClick(View v) {
   		    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
   		    startActivityForResult(i, RESULT_LOAD_IMAGE);
   	   }        
    });
    
    }
    
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
    	Uri selectedImage = data.getData();
        ImageView imageView = (ImageView) findViewById(R.id.imageView11);
        TextView textView = (TextView) findViewById(R.id.textView1);
        TextView textedit1 = (TextView) findViewById(R.id.textView4);
        TextView textedit2 = (TextView) findViewById(R.id.textView5);
        
        src1=getRealPathFromURI(getBaseContext(),selectedImage);
        
        if(src1!=null){
        	try {
				createCopy();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        Bitmap d = BitmapFactory.decodeFile(src1);
        
        Log.d(TAG,src1);
        InputStream is=null;
        
        try {
			is = new FileInputStream(src1);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        XMPMeta meta = XmpUtil.extractXMPMeta(is);
        
        String namespace = "http://ns.google.com/photos/1.0/panorama/";
        String cropWidthName = "GPano:CroppedAreaImageWidthPixels";
        String fullWidthName = "GPano:FullPanoWidthPixels";
        String cropHeightName = "GPano:CroppedAreaImageHeightPixels";
        String fullHeightName = "GPano:FullPanoHeightPixels";
        
        
    	Log.d(TAG, "height"+d.getHeight()+"width"+d.getWidth());
        
        if (meta == null||!meta.doesPropertyExist(namespace, cropWidthName)||!meta.doesPropertyExist(namespace, fullWidthName)) {
        	Log.d(TAG,"well at least yoou tried");
        	textView.setText("No useful metadata found\nCalculated values were added");
        	
        	StringBuilder contents = new StringBuilder();
    	    String sep = System.getProperty("line.separator");
        	InputStream s = getBaseContext().getResources().openRawResource(R.raw.fakemeta);
        	BufferedReader input =  new BufferedReader(new InputStreamReader(s), 1024*8);
  	      	try {
  	      		String line = null; 
  	      		while (( line = input.readLine()) != null){
  	      			contents.append(line);
  	      			contents.append(sep);
  	      		}
  	      	} catch (IOException e) {
  	      		// TODO Auto-generated catch block
  	      		e.printStackTrace();
  	      	}
  	      	finally {
  	      		try {
  	      			input.close();
  	      		} catch (IOException e) {
  	      			// TODO Auto-generated catch block
  	      			e.printStackTrace();
  	      		}
  	      	}
  	      	
        	Log.d(TAG,contents.toString());
        	try {
				fakemeta=XMPMetaFactory.parseFromString(contents.toString());
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
        	if(2*d.getHeight()>d.getWidth()+5){
        		try {
        			textedit1.setText(""+(float)(360.0*d.getWidth()/(2*d.getHeight())));
        			textedit2.setText(""+180);
        			fakemeta.setProperty(namespace , cropWidthName , d.getWidth());
        			fakemeta.setProperty(namespace , fullWidthName , 2*d.getHeight());
        			fakemeta.setProperty(namespace , cropHeightName , d.getHeight());
        			fakemeta.setProperty(namespace , fullHeightName , d.getHeight());
	        	
        		} catch (XMPException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        	
        	else if (2*d.getHeight()+5<d.getWidth()){
        		try {
        			textedit1.setText(""+360.0);
        			textedit2.setText(""+(float)360.0*d.getHeight()/d.getWidth());
        			fakemeta.setProperty(namespace , cropWidthName , d.getWidth());
        			fakemeta.setProperty(namespace , fullWidthName , d.getWidth());
        			fakemeta.setProperty(namespace , cropHeightName , d.getHeight());
        			fakemeta.setProperty(namespace , fullHeightName ,2* d.getWidth());
	        	
        		} catch (XMPException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        	else{
        		try {
        			textedit1.setText(""+360.0);
        			textedit2.setText(""+180.0);
        			fakemeta.setProperty(namespace , cropWidthName , d.getWidth());
        			fakemeta.setProperty(namespace , fullWidthName , d.getWidth());
        			fakemeta.setProperty(namespace , cropHeightName , d.getHeight());
        			fakemeta.setProperty(namespace , fullHeightName , d.getHeight());
	        	
        		} catch (XMPException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        	
        	SerializeOptions options = null;
    		try {
    			Log.d(TAG,"\ndoit");
    			Log.d(TAG,XMPMetaFactory.serializeToString(fakemeta, options));
    		} catch (XMPException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
        }
        
        else{

            SerializeOptions options = null;
    		try {
    			Log.d(TAG,XMPMetaFactory.serializeToString(meta, options));
    		} catch (XMPException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
        	
        	Integer cropWidthValue = null,fullWidthValue = null, cropHeightValue = null,fullHeightValue = null;
        	
        	try {
        		cropWidthValue = meta.getPropertyInteger(namespace, cropWidthName);
        		fullWidthValue = meta.getPropertyInteger(namespace, fullWidthName);
        		cropHeightValue = meta.getPropertyInteger(namespace, cropHeightName);
        		fullHeightValue = meta.getPropertyInteger(namespace, fullHeightName);
        	} catch (XMPException e1) {
        		// TODO Auto-generated catch block
        		e1.printStackTrace();
        	}
        	
        	Log.d(TAG,"width"+cropWidthValue+"	"+fullWidthValue+"	height"+cropHeightValue+"	"+fullHeightValue);
        	
        	if (cropWidthValue != null && fullWidthValue != null && Math.abs(fullWidthValue-cropWidthValue)<5) {
        		textView.setText("Already a 360 degree image");
        		textedit1.setText(""+360.0);
        		textedit2.setText(""+(float) (180.0*cropWidthValue/fullWidthValue));
        		
        	}
        	
        	else{
        		textView.setText("Contains Valid Metadata");
        		textedit1.setText(""+(float) (360.0*cropWidthValue/fullWidthValue));
            	textedit2.setText(""+(float) (180.0*cropHeightValue/fullHeightValue));
        	}
        }
       
    	int nh = (int) ( d.getHeight() * (512.0/ d.getWidth()));
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
        		//if(src1!=null||dest1!=null){
        			//XMPMeta xmpmeta = null;
            	    //xmpmeta= XmpUtil.extractXMPMeta(src1);
        		if(fakemeta!=null){
            	    XmpUtil.writeXMPMeta(src1,fakemeta);
            	    Toast.makeText(getBaseContext(), "done, now go check the gallery", Toast.LENGTH_LONG).show();
        		}
        		else{
        			
        			Toast.makeText(getBaseContext(), "Location data was added", Toast.LENGTH_LONG).show();
        		}
        		//}
        		//else
        			//Toast.makeText(getBaseContext(), "please select both images", Toast.LENGTH_LONG).show();
        		return true;
        	
        	case R.id.action_about:
        		Intent myIntent = new Intent(EditActivity.this, AboutActivity.class);
        		myIntent.putExtra("key", 15); //Optional parameters
        		EditActivity.this.startActivity(myIntent);
        		
        		return true;

        		
        	default:
        		return super.onOptionsItemSelected(item);
    	}
    }
    
    public void createCopy() throws IOException {
    	File picFolder = new File (Environment.getExternalStorageDirectory() + "/Pictures");
    	Log.d(TAG,picFolder.toString());
    	if(!picFolder.exists()){
    		boolean success = picFolder.mkdir();
    		if(success){
    			Log.d(TAG,"/pictures folder created");
    		}
    	}
    	
    	File panoFolder = new File (Environment.getExternalStorageDirectory() + "/Pictures/Panos");
    	Log.d(TAG,picFolder.toString());
    	if(!panoFolder.exists()){
    		boolean success = panoFolder.mkdir();
    		if(success){
    			Log.d(TAG,"/pictures/panos folder created");
    		}
    	}
    	
    	FileChannel inChannel = new FileInputStream(src1).getChannel();
    	File tmp = new File(src1);
    	FileChannel outChannel =null;
        File tmp1=new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Panos/"+tmp.getName());
        Time now = new Time();
        now.setToNow();
        String timenow = ""+ now.year+now.month+now.monthDay+now.hour+now.minute+now.second;
        
        outChannel = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/Pictures/Panos/"+tmp.getName().substring(0, tmp.getName().lastIndexOf('.'))+ timenow + ".jpg").getChannel();
        src1=Environment.getExternalStorageDirectory().toString() + "/Pictures/Panos/"+tmp.getName().substring(0, tmp.getName().lastIndexOf('.'))+ timenow + ".jpg";
        Log.d(TAG,src1);
        
        try {
           inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
           if (inChannel != null)
              inChannel.close();
           if (outChannel != null)
              outChannel.close();
        }
        
    }
    
}