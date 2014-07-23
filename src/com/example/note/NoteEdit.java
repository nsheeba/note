package com.example.note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileNotFoundException;
import java.io.InputStream;
import android.net.Uri;
import android.content.Intent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Dialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;




public class NoteEdit extends Activity {
	
	public static int numTitle = 1;	
	public static String curDate = "";
	public static String curText = "";	
    private EditText mTitleText;
    private EditText mBodyText;
    private TextView mDateText;
    private Long mRowId;
    private final int SELECT_PHOTO = 1;
    private ImageView imageView;
    private Cursor note;
    
   

    private NotesDbAdapter mDbHelper;
	
      
   
		
		
 
    
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();        
        
        setContentView(R.layout.note_edit);
       
        setTitle(R.string.app_name);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mDateText = (TextView) findViewById(R.id.notelist_date);
        imageView = (ImageView)findViewById(R.id.imageView);
     
        
        
       
        
        Button pickImage = (Button) findViewById(R.id.addimgbutton);
        
        
        long msTime = System.currentTimeMillis();  
        Date curDateTime = new Date(msTime);
       
        SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y");  
        curDate = formatter.format(curDateTime);        
        
        mDateText.setText(""+curDate);
        

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                                    : null;
        }

        populateFields();
        
        
    
	pickImage.setOnClickListener(new OnClickListener() {
		 
        @Override
        public void onClick(View view) {                
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }
    });
}
	
	
	  public static class LineEditText extends EditText{
			// we need this constructor for LayoutInflater
			public LineEditText(Context context, AttributeSet attrs) {
				super(context, attrs);
					mRect = new Rect();
			        mPaint = new Paint();
			        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			        mPaint.setColor(Color.BLUE);
			}

			private Rect mRect;
		    private Paint mPaint;	    
		    
		    @Override
		    protected void onDraw(Canvas canvas) {
		  
		        int height = getHeight();
		        int line_height = getLineHeight();

		        int count = height / line_height;

		        if (getLineCount() > count)
		            count = getLineCount();

		        Rect r = mRect;
		        Paint paint = mPaint;
		        int baseline = getLineBounds(0, r);

		        for (int i = 0; i < count; i++) {

		            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
		            baseline += getLineHeight();

		        super.onDraw(canvas);
		    }

		}
	  }
	  
	  @Override
	    protected void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        saveState();
	        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
	    }
	    
	    @Override
	    protected void onPause() {
	        super.onPause();
	        saveState();
	    }
	    
	    @Override
	    protected void onResume() {
	        super.onResume();
	        populateFields();
	    }
	    
		
		
 
		
		 @Override
		    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		        super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 
		 
		        switch(requestCode) { 
		        case SELECT_PHOTO:
		            if(resultCode == RESULT_OK){
		                try {
		                    final Uri imageUri = imageReturnedIntent.getData();
		                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
		                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
		                    imageView.setImageBitmap(selectedImage);
		                } catch (FileNotFoundException e) {
		                    e.printStackTrace();
		                }
		 
		            }
		        }
		    }
		 
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    case R.id.addaudio:
		    	startActivity(new Intent(this, Audio.class));
		   finish();
		    return true;
		            
		        
		    case R.id.addvideo:
		    	 startActivity(new Intent(this, Video.class));
		    	 finish();
		    	 
		    	return true;
		    	
		    	
		    case R.id.menu_delete:
				if(note != null){
	    			note.close();
	    			note = null;
	    		}
	    		if(mRowId != null){
	    			mDbHelper.deleteNote(mRowId);
	    		}
	    		finish();
		    	
		        return true;
		 
		    case R.id.menu_save:
	    		saveState();
	    		finish();   	
		    default:
		    	return super.onOptionsItemSelected(item);
		    }
		}
	    
	    private void saveState() {
	        String title = mTitleText.getText().toString();
	        String body = mBodyText.getText().toString();
	       
	       
	        
	        
	        
	        if(mRowId == null){
	        	long id = mDbHelper.createNote(title, body, curDate);
	        	if(id > 0){
	        		mRowId = id;
	        	}else{
	        		Log.e("saveState","failed to create note");
	        	}
	        }else{
	        	if(!mDbHelper.updateNote(mRowId, title, body, curDate)){
	        		Log.e("saveState","failed to update note");
	        	}
	        }
	    }
	    
	  
	    private void populateFields() {
	        if (mRowId != null) {
	            note = mDbHelper.fetchNote(mRowId);
	            startManagingCursor(note);
	            mTitleText.setText(note.getString(
	    	            note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
	            mBodyText.setText(note.getString(
	                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
	            curText = note.getString(
	                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
	        }
	    }
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.noteedit_menu, menu);

	        return true;
	    }    
	}


	