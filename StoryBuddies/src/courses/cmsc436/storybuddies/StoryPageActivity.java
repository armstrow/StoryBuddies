package courses.cmsc436.storybuddies;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class StoryPageActivity extends Activity implements OnGesturePerformedListener{
	
	
	private final String TAG = "StoryPage";
	private int currPage = 0;
	private SpeechEngine speech;
	private EditText storyText;
	
	private int currStoryPos;
	
	private StoryBook currStory;

	private GestureLibrary mLibrary;
	
	private GestureDetector mGestureDetector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.story_page);
		storyText = (EditText) findViewById(R.id.text);
		speech = SpeechEngine.getInstance(getApplicationContext());
		
		currStoryPos = getIntent().getIntExtra("position",0);
		currStory = StartScreenActivity.stories.get(currStoryPos);
		
		updatePage();
		
		ImageButton prevButton = (ImageButton) findViewById(R.id.lastPage);
		ImageButton nextButton = (ImageButton) findViewById(R.id.nextPage);
		

		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		GestureOverlayView gestureOverlay = (GestureOverlayView) findViewById(R.id.gestureOverlay);
		gestureOverlay.addOnGesturePerformedListener(this);
		
		prevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				prevPage();
				Log.i(TAG,"Entered prevButton OnClickListener");
				
			}
		});
		
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nextPage();
				Log.i(TAG,"Entered nextButton OnClickListener");
							
			}
		});

		gestureOverlay.setOnTouchListener( new OnTouchListener() {
		
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i(TAG, "Entered startButton.OnTouchListener.onTouch()");
				return mGestureDetector.onTouchEvent(event);

			}
		});
		
		if (!mLibrary.load()) {
			finish();
		}
	}
	
	private void updatePage(){
		String currText = currStory.getmPages().get(currPage).getmStoryText();
		storyText.setText(currText);
		speech.speak(currText);
		//SetBitmap
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
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		// TODO Auto-generated method stub
		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);

		if (predictions.size() > 0) {

			// Get highest-ranked prediction
			Prediction prediction = predictions.get(0);

			if (prediction.name.equals("nextPage")) {
				nextPage();
			} else if (prediction.name.equals("prevPage")) {
				prevPage();
			}
		
		}
	}
	
	private void nextPage() {
		if(currPage < currStory.getmPages().size()-1){
			currPage++;
			updatePage();	
		} else {
			finish();
		}
	}
	
	private void prevPage() {
		if(currPage > 0){
			currPage--;
			updatePage();
		} else {
			Intent goToCoverIntent = new Intent(StoryPageActivity.this,CoverPageActivity.class);
			goToCoverIntent.putExtra("position", currStoryPos);
			startActivity(goToCoverIntent);
			finish();
		}
	}
}