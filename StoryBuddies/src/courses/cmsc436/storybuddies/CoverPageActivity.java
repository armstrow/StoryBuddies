package courses.cmsc436.storybuddies;

import java.util.ArrayList;

import courses.cmsc436.storybuddies.StoryBuddiesUtils.BitmapWorkerTask;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CoverPageActivity extends Activity implements OnGesturePerformedListener {
	
	private final String TAG = "CoverPage";
	private SpeechEngine speech;

	private GestureLibrary mLibrary;
	
	private int currStoryPos = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.title_page);
		
		speech = SpeechEngine.getInstance(getApplicationContext()); 
		
		//Get currentStory from static variable
		currStoryPos = getIntent().getIntExtra("position",0);
		StoryBook currStory = StoryBuddiesBaseActivity.stories.get(currStoryPos);
		
		speech.speak(currStory.toString());
		
		//Set title view to appropriate text
		TextView title = (TextView) findViewById(R.id.coverPageTitle);
		title.setText(currStory.getmTitle());
		
		//Set title view to appropriate image
		ImageView coverView = (ImageView) findViewById(R.id.bookCover);
		
		coverView.setImageResource(currStory.getmTitlePage());
		//BitmapWorkerTask imageLoader = new BitmapWorkerTask(coverView, getResources());
		//imageLoader.execute(currStory.getmTitlePage());
		
		coverView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered coverView OnClickListener");
				Intent storyActivity = new Intent(CoverPageActivity.this,StoryPageActivity.class);
				storyActivity.putExtra("position", currStoryPos);
				startActivity(storyActivity);
				finish();
			}
		});
		
		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		GestureOverlayView gestureOverlay = (GestureOverlayView) findViewById(R.id.gestureOverlay);
		gestureOverlay.addOnGesturePerformedListener(this);

		
		if (!mLibrary.load()) {
			finish();
		}
		
		
	}

	@Override
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
	}
	

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		// TODO Auto-generated method stub
		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);

		if (predictions.size() > 0) {

			// Get highest-ranked prediction
			Prediction prediction = predictions.get(0);

			if (prediction.name.equals("nextPage")) {
				Intent storyActivity = new Intent(CoverPageActivity.this,StoryPageActivity.class);
				storyActivity.putExtra("position", currStoryPos);
				startActivity(storyActivity);
				finish();
			}
		
		}
	}

}
