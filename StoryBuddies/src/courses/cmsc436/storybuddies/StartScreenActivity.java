package courses.cmsc436.storybuddies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class StartScreenActivity extends Activity {
	
	private final String TAG = "SB_StartScreen";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_main);
		
		ImageButton startButton = (ImageButton) this.findViewById(R.id.imageButton1);
		startButton.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.i(TAG, "Entered startButton.OnItemClickListener.onItemClick()");
				
				//StoryBuddiesUtils.playMusic(StartScreenActivity.this, "Radioactive.mp3");			
				//speech.speak("Would you like to read a story?");
				
				Intent intent = new Intent(StartScreenActivity.this, ChooseStoryActivity.class);
				startActivity(intent);
			}
		});
		
		SpeechEngine.getInstance(getApplicationContext()).speak(getString(R.string.ready));
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
	}
	
	@Override
	public void onBackPressed(){
		Log.i(TAG, "Entered onBackPressed but purposefully not doing anything");
	}
}
