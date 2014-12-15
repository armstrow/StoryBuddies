package courses.cmsc436.storybuddies;

import java.io.File;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher.ViewFactory;

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
		//backgroundView = (ImageView) findViewById(R.id.findTheRabbitBackground);
		setUpAnimation();
		next("", R.drawable.game5);
		hiddenRabbit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered hiddenRabbit OnClickListener");
				
				Log.i(TAG, "Leaving Rabbit Game Activity");
				//attempting to remove view to help with memory leak
				next(null, R.drawable.rabbit_victory);
				speech.speak("Great Job! You have found the sleeping hare!");
				
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
				
				
				//finish();							
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
	
	//Code from http://www.tutorialspoint.com/android/android_imageswitcher.htm
			private ImageSwitcher backgroundView;
			private void setUpAnimation() {
				backgroundView = (ImageSwitcher)findViewById(R.id.findTheRabbitBackground);

				backgroundView.setFactory(new ViewFactory() {
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
		      android.R.anim.fade_in);
		      Animation out = AnimationUtils.loadAnimation(this,
		      android.R.anim.fade_out);
		      backgroundView.setInAnimation(in);
		      backgroundView.setOutAnimation(out);
		      if (newImageInt != -1) {
		    	  backgroundView.setImageResource(newImageInt);
		      } else {
		    	  backgroundView.setImageURI(Uri.fromFile(new File(newImageStr)));
		      }
		   }
		  
}
