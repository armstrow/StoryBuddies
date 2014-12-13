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
		Log.i(TAG, "Entered Game Activity");
		super.onCreate(savedInstanceState);
		speech = SpeechEngine.getInstance(getApplicationContext());
		setContentView(R.layout.activity_find_the_rabbit);
	
		ImageView hiddenRabbit = (ImageView) findViewById(R.id.hiddenRabbit);
		
		hiddenRabbit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered hiddenRabbit OnClickListener");
				speech.speak("Great Job! You have found the sleeping hare!");
				Log.i(TAG, "Leaving Game Activity");
				finish();							
			}
		});
	}
}
