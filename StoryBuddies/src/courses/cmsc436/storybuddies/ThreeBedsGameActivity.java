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
		speech.speak("Find the right porridge for Goldilocks");
		speech.pauseThenSpeak(2000, "Touch the porridge bowls");
		setContentView(R.layout.activity_bear_bed_game);
	
		ImageView baby = (ImageView) findViewById(R.id.babyPorridge);
		ImageView mom = (ImageView) findViewById(R.id.momPorridge);
		ImageView dad = (ImageView) findViewById(R.id.dadPorridge);
		ImageView bg = (ImageView) findViewById(R.id.threeBearBackground);
			
		bg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered BigBed OnClickListener");
				speech.speak("Touch a porridge bowl");							
			}
		});
		
		dad.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered BigBed OnClickListener");
				speech.speak("This porridge is too cold!");							
			}
		});
		
		mom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered medBed OnClickListener");
				speech.speak("This porridge is too hot!");							
			}
		});
		
		baby.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered smallBed OnClickListener");
				speech.speak("Great job! This porridge is just right");	
				Log.i(TAG, "Leaving Three Beds Game Activity");
				//from http://pjdietz.com/android/android-delayed-execution-on-ui-thread/
				// Create a new thread inside your Actvity.
				Thread thread = new Thread() {
				    
				    @Override
				    public void run() {
				 
				        // Block this thread for 2 seconds.
				        try {
				            Thread.sleep(2000);
				        } catch (InterruptedException e) {
				        }
				 
				        // After sleep finished blocking, create a Runnable to run on the UI Thread.
				        runOnUiThread(new Runnable() {
				            @Override
				            public void run() {
				                finish();
				            }
				        });
				        
				    }
				    
				};
				 
				// Don't forget to start the thread.
				thread.start();
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
	}
	
	
}
