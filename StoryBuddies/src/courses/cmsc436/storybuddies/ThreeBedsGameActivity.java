package courses.cmsc436.storybuddies;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ThreeBedsGameActivity extends Activity {
	private final String TAG = "ThreeBedsGameActivity";
	private SpeechEngine speech;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Entered ThreeBedsGame Activity");
		super.onCreate(savedInstanceState);
		speech = SpeechEngine.getInstance(getApplicationContext());
		setContentView(R.layout.activity_bear_bed_game);
	
		ImageView bigBed = (ImageView) findViewById(R.id.bigBed);
		ImageView medBed = (ImageView) findViewById(R.id.mediumBed);
		ImageView smallBed = (ImageView) findViewById(R.id.smallBed);
		
		bigBed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered BigBed OnClickListener");
				speech.speak("This Bed is to hard!");							
			}
		});
		
		medBed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered medBed OnClickListener");
				speech.speak("This Bed is to soft!");							
			}
		});
		
		smallBed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered smallBed OnClickListener");
				speech.speak("This Bed is just right");	
				Log.i(TAG, "Leaving Three Beds Game Activity");
				finish();
			}
		});
	}
	
	
	
}
