package courses.cmsc436.storybuddies;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GameFindTheRabbitActivity extends Activity {
	private final String TAG = "GameFindTheRabbitActivity";
	private SpeechEngine speech;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Entered Rabbit Game Activity");
		super.onCreate(savedInstanceState);
		speech = SpeechEngine.getInstance(getApplicationContext());
		speech.speak("Can you find the sleeping hare?");
		speech.pauseThenSpeak(2000, "Touch the hare to win!");
		setContentView(R.layout.activity_find_the_rabbit);
	
		
		final ImageView hiddenRabbit = (ImageView) findViewById(R.id.hiddenRabbit);
		final ImageView backgroundView = (ImageView) findViewById(R.id.findTheRabbitBackground);
		
		hiddenRabbit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered hiddenRabbit OnClickListener");
				speech.speak("Great Job! You have found the sleeping hare!");
				Log.i(TAG, "Leaving Rabbit Game Activity");
				//attempting to remove view to help with memory leak
				backgroundView.setImageDrawable(getResources().getDrawable(R.drawable.rabbit_victory));
				((LinearLayout)backgroundView.getParent()).removeView(backgroundView);
				
				finish();							
			}
		});
		
		backgroundView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered backgroundView OnClickListener");
				speech.speak("Try Again! Can you find the sleeping hare");						
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
	}
}
