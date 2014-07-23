package com.example.note;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


@SuppressLint("NewApi")
public class Video extends Activity {
	
 private static final int CAPTURE_IMAGE_CAPTURE_CODE = 0;
 private static final int VIDEO_CAPTURE = 101;
	private Uri fileUri;
	
 Intent i;
 private Button ib;

  @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_video);
  

  Button recordButton = 
          (Button) findViewById(R.id.takevideo);
  ib = (Button) findViewById(R.id.takePicture);
  ib.setOnClickListener(new OnClickListener() {
	   @Override
	   public void onClick(View v) {
		   
		   
           
	    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    startActivityForResult(i, CAPTURE_IMAGE_CAPTURE_CODE);
	    
	   }
	  });
  
	
	if (!hasCamera())
		recordButton.setEnabled(false);
}

private boolean hasCamera() {
  if (getPackageManager().hasSystemFeature(
                 PackageManager.FEATURE_CAMERA_ANY)){
      return true;
  } else {
      return false;
  }

   
 }
  public void startRecording(View view)
	{
	    File mediaFile = new
      File(Environment.getExternalStorageDirectory().getAbsolutePath() 
            + "/myvideo.mp4");	
	
	    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	    fileUri = Uri.fromFile(mediaFile);
		
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
	    startActivityForResult(intent, VIDEO_CAPTURE);
	}
 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  if (requestCode == CAPTURE_IMAGE_CAPTURE_CODE) {
   if (resultCode == RESULT_OK) {
	   
    Toast.makeText(this, "Image Captured", Toast.LENGTH_LONG).show();
   } else if (resultCode == RESULT_CANCELED) {
    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
   }
  }
  else if
   (requestCode == VIDEO_CAPTURE) {
      if (resultCode == RESULT_OK) {
          Toast.makeText(this, "Video has been saved to:\n" +
             data.getData(), Toast.LENGTH_LONG).show();
     } else if (resultCode == RESULT_CANCELED) {
     	Toast.makeText(this, "Video recording cancelled.", 
               Toast.LENGTH_LONG).show();
     } else {
     	Toast.makeText(this, "Failed to record video", 
               Toast.LENGTH_LONG).show();
     }
  
  }
 }
 
 @Override
 public boolean onCreateOptionsMenu(Menu menu) {
  // Inflate the menu; this adds items to the action bar if it is present.
  getMenuInflater().inflate(R.menu.noteedit_menu, menu);
  return true;
 }

}