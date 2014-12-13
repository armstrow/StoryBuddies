package courses.cmsc436.storybuddies;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class GameFindTheRabbitActivity extends Activity {
	private final String TAG = "GameFindTheRabbitActivity";
	private SpeechEngine speech;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Entered Rabbit Game Activity");
		super.onCreate(savedInstanceState);
		speech = SpeechEngine.getInstance(getApplicationContext());
		setContentView(R.layout.activity_find_the_rabbit);
	
		ImageView hiddenRabbit = (ImageView) findViewById(R.id.hiddenRabbit);
		ImageView backgroundView = (ImageView) findViewById(R.id.findTheRabbitBackground);
		
		hiddenRabbit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered hiddenRabbit OnClickListener");
				speech.speak("Great Job! You have found the sleeping hare!");
				Log.i(TAG, "Leaving Rabbit Game Activity");
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
}
