package courses.cmsc436.storybuddies;

import java.io.File;
import java.util.Random;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher.ViewFactory;

public class GameFindTheRabbitActivity extends Activity {
	private final String TAG = "GameFindTheRabbit";
	private SpeechEngine speech;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Entered Rabbit Game");
		super.onCreate(savedInstanceState);
		speech = SpeechEngine.getInstance(getApplicationContext());
		speech.speak("Can you find the sleeping hare?");
		speech.pauseThenSpeak(2000, "Touch the hare to win!");
		setContentView(R.layout.activity_find_the_rabbit);

		
		//final ImageView hiddenRabbit = (ImageView) findViewById(R.id.findTheRabbitBackground);
		final View backgroundView = (View) findViewById(R.id.findTheRabbitBackground);
		setUpAnimation();

		final int ERROR_MARGIN = 30;
		final int[] winsX = getResources().getIntArray(R.array.rabbit_game_wins_x);
		final int[] winsY = getResources().getIntArray(R.array.rabbit_game_wins_y);
		TypedArray games = getResources().obtainTypedArray(R.array.rabbit_games);

		int idx = new Random().nextInt(games.length());

		// get resource ID by index
		final int game = games.getResourceId(idx, -1);
		final int[] win = {winsX[idx], winsY[idx]};

		next("", game);
		Log.i(TAG, "Loaded game: " + game + ", with win @ (" + win[0] + ", " + win[1] + ")");

		backgroundView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					float[] point = new float[] {event.getX(), event.getY()};

					Matrix inverse = new Matrix();
					backgroundView.getMatrix().invert(inverse);
					inverse.mapPoints(point);

					float density = getResources().getDisplayMetrics().density;
					point[0] /= density;
					point[1] /= density;

					Log.i(TAG, "Touch coordinates : " +
							String.valueOf(point[0]) + "x" + String.valueOf(point[1]));
					if (Math.abs(point[0] - win[0]) < ERROR_MARGIN && Math.abs(point[1] - win[1]) < ERROR_MARGIN) {

						Log.i(TAG, "Leaving Rabbit Game");
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
					} else {
						Log.i(TAG,"Missed the Hare");
						speech.speak("Try Again! Can you find the sleeping hare");
					}


				}
				return true;
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
