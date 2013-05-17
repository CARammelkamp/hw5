package edu.ucsb.cs.cs185.carammelkamp.touchgestures;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class MainActivity extends SherlockFragmentActivity {

	private boolean debug = false;
	 
	//will want to select picture
    private static final int SELECT_PICTURE = 1;
    private TouchView t = null;
    private Bitmap bMap = null;

    
    //for filemanage and imagepath
    private String selectedImagePath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		t = new TouchView(this.getApplicationContext());
		setContentView(t);
		String filepath = Environment.getExternalStorageDirectory().toString() +"/ucsbmap.png";
    	bMap = BitmapFactory.decodeFile(filepath); //"/sdcard/ucsbmap.png"
    	if(bMap == null)
	    	Toast.makeText(this.getApplicationContext(), "bitmap is null!", Toast.LENGTH_SHORT).show();
    	t.setImageBitmap(bMap);
    	t.setScaleType(ScaleType.MATRIX);
		
	}

    //UPDATED
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
            	
            	if(debug) System.out.println( "IN ONACTIVITY RESULT!!!!!!");
                Uri selectedImageUri = data.getData();

                //MEDIA GALLERY
                selectedImagePath = getPath(selectedImageUri);

                //NOW WE HAVE OUR WANTED STRING
                if(selectedImagePath!=null)
                {
                 if(debug) System.out.println("selectedImagePath is the right one for you!");
           	     bMap = BitmapFactory.decodeFile(selectedImagePath); //"/sdcard/ucsbmap.png"
           	     t.setImageBitmap(bMap);
           	     t.setFocusable(true);
                }
            }
        }
    }

    
    //UPDATED!
    @SuppressWarnings("deprecation")
	public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULLs
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		  super.getSupportMenuInflater().inflate(R.menu.main, menu);
          return super.onCreateOptionsMenu(menu);
	}

	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		SettingsFragment s = new SettingsFragment();
		HelpFragment h = new HelpFragment();
		
		switch (item.getItemId()) {
		case R.id.load:
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if(debug) System.out.println("About to start new activity");
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), SELECT_PICTURE);//SELECT_PICTURE
			return true;
		case R.id.settings:
			s.show(getSupportFragmentManager(), "SETTINGS");
			return true;
		case R.id.help:
			h.show(getSupportFragmentManager(), "HELP");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	
	}
	
 
}
	
	

