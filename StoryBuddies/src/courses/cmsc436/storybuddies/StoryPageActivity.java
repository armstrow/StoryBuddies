package courses.cmsc436.storybuddies;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import courses.cmsc436.storybuddies.StoryBuddiesUtils.BitmapWorkerTask;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class StoryPageActivity extends Activity implements OnGesturePerformedListener{
	
	
	private final String TAG = "StoryPage";
	private int currPage = 0;
	private SpeechEngine speech;
	private TextView storyText;
	private ImageView storyPic;
	private ImageButton gameButton;
	
	private int currStoryPos;
	
	private StoryBook currStory;

	private GestureLibrary mLibrary;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.story_page);
		storyText = (TextView) findViewById(R.id.text);
		storyText.requestFocus();
		//storyPic = (ImageView) findViewById(R.id.illustration);
		setUpAnimation();
		
		speech = SpeechEngine.getInstance(getApplicationContext());
		
		currStoryPos = getIntent().getIntExtra("position",0);
		currStory = StoryBuddiesBaseActivity.stories.get(currStoryPos);
		
		ImageButton prevButton = (ImageButton) findViewById(R.id.lastPage);  
		ImageButton nextButton = (ImageButton) findViewById(R.id.nextPage);
		ImageButton exitButton = (ImageButton) findViewById(R.id.exitButton);
		gameButton = (ImageButton) findViewById(R.id.gameButton);		
		
		updatePage(true);

		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		GestureOverlayView gestureOverlay = (GestureOverlayView) findViewById(R.id.gestureOverlay);
		gestureOverlay.addOnGesturePerformedListener(this);
		gestureOverlay.setUncertainGestureColor(Color.TRANSPARENT);
		gestureOverlay.setGestureColor(Color.TRANSPARENT);
		
		prevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				storyText.requestFocus();
				prevPage();
				Log.i(TAG,"Entered prevButton OnClickListener");
				
			}
		});
		
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				storyText.requestFocus();
				nextPage();
				Log.i(TAG,"Entered nextButton OnClickListener");
							
			}
		});
		
		exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				Log.i(TAG,"Entered nextButton OnClickListener");
							
			}
		});
		exitButton.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
					speech.speak("Are you done reading your story? Click the X again to exit");
				}				
			}
		});
		
		imageSwitcher.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				storyText.requestFocus();
			}
		});
		
		gameButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Entered StoryPage's GameButon setOnClickListener");
				storyText.requestFocus();
				String myActivity = currStory.getmPages().get(currPage).getmGameActivity();
				if(myActivity != null){
					Log.i(TAG, "Starting GameActivity");
					Intent goToGameIntent = new Intent();
					//goToGameIntent.setClass(StoryPageActivity.this,GameFindTheRabbitActivity.class);
					goToGameIntent.setClassName("courses.cmsc436.storybuddies","courses.cmsc436.storybuddies."+myActivity);
					startActivity(goToGameIntent);
				} else {
					Log.i(TAG,"Activity field was null. Cannot proceede to an activity");
				}
			}
		});
		
		if (!mLibrary.load()) {
			finish();
		}
	}
	
	private void updatePage(boolean forward){
		//sets game button to invisible and unclickable if there is not game
		Log.i(TAG,"Entered updatePage");		
		if(currStory.getmPages().get(currPage).getmGameActivity() == null){
			Log.i(TAG, "Setting GameButton to not clickable or visible");
			gameButton.setClickable(false);
			gameButton.setVisibility(ImageButton.GONE);
		} else {
			gameButton.setClickable(true);
			gameButton.setVisibility(ImageButton.VISIBLE);
		}
		String currText = currStory.getmPages().get(currPage).getmStoryText();
		String currSpeech = currStory.getmPages().get(currPage).getmSpeechAudio();
		
		Log.i(TAG, "Attemptin to load image: " + currStory.getmPages().get(currPage).getmPictureFromFile());
		if (forward) {
			next(currStory.getmPages().get(currPage).getmPictureFromFile(), currStory.getmPages().get(currPage).getmPicture());
		} else {
			previous(currStory.getmPages().get(currPage).getmPictureFromFile(), currStory.getmPages().get(currPage).getmPicture());
			
		}
		/*if(currStory.getmPages().get(currPage).getmPicture() != -1){
			//upload bitmap from resources
			storyPic.setImageResource(currStory.getmPages().get(currPage).getmPicture());
		} else if(currStory.getmPages().get(currPage).getmPictureFromFile() != null){
			//upload bitmap from file -- these will be of small enough resolution to load quickly
			storyPic.setImageURI(Uri.fromFile(new File(currStory.getmPages().get(currPage).getmPictureFromFile())));
		}*/
		
		if(currSpeech != null){
			storyText.setText(currSpeech);
		} else {
			storyText.setText(currText);
		}
		
		if (currStory.getmPages().get(currPage).getmVoiceUri() == null) {
			speech.speak(currText);
		} else {
			speech.playMusic(currStory.getmPages().get(currPage).getmVoiceUri());
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {

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
			updatePage(true);	
		} else {
			finish();
		}
	}
	
	private void prevPage() {
		if(currPage > 0){
			currPage--;
			updatePage(false);
		} else {
			Intent goToCoverIntent = new Intent(StoryPageActivity.this,CoverPageActivity.class);
			goToCoverIntent.putExtra("position", currStoryPos);
			startActivity(goToCoverIntent);
			finish();
		}
	}	
	
	//Code from http://www.tutorialspoint.com/android/android_imageswitcher.htm
		private ImageSwitcher imageSwitcher;
		private void setUpAnimation() {
		    imageSwitcher = (ImageSwitcher)findViewById(R.id.illustration);

		    imageSwitcher.setFactory(new ViewFactory() {
			   @Override
			   public View makeView() {
			      ImageView myView = new ImageView(getApplicationContext());
			      myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			      myView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.
			      FILL_PARENT,LayoutParams.FILL_PARENT));
			      return myView;
			       }

			   });
		}

	   public void next(String newImageStr, int newImageInt){
		   Log.i(TAG, "In next");
	      Animation in = AnimationUtils.loadAnimation(this,
	      R.anim.slide_in_right);
	      Animation out = AnimationUtils.loadAnimation(this,
	      R.anim.slide_out_left);
	      imageSwitcher.setInAnimation(in);
	      imageSwitcher.setOutAnimation(out);
	      if (newImageInt != -1) {
	    	  imageSwitcher.setImageResource(newImageInt);
	      } else {
	    	  imageSwitcher.setImageURI(Uri.fromFile(new File(newImageStr)));
	      }
	   }
	   public void previous(String newImageStr, int newImageInt){
	      Animation in = AnimationUtils.loadAnimation(this,
	      android.R.anim.slide_out_right);
	      Animation out = AnimationUtils.loadAnimation(this,
	      android.R.anim.slide_in_left);
	      imageSwitcher.setInAnimation(out);
	      imageSwitcher.setOutAnimation(in);
	      if (newImageInt != -1) {
	    	  imageSwitcher.setImageResource(newImageInt);
	      } else {
	    	  imageSwitcher.setImageURI(Uri.fromFile(new File(newImageStr)));
	      }
	   }
	
}
