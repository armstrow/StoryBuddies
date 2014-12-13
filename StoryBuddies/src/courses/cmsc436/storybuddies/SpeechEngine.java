package courses.cmsc436.storybuddies;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.widget.Toast;

public class SpeechEngine implements OnInitListener {
	private TextToSpeech tts;
	private boolean ttsReady = false;
	private static SpeechEngine instance = null;
	private Context mContext;
	private static final String TAG = "SB_SpeechEngine";
	public static final int AUDIO_INPUT_ACTIVITY = 37;
	private MediaPlayer mPlayer;
	
	public SpeechEngine(Context context) {
		mContext = context;
		tts = new TextToSpeech(context, this);
		mPlayer = new MediaPlayer();
		mPlayer.setVolume((float)1, (float)1);
	}
	
	public TextToSpeech getTTS() {
		return tts;
	}
	
	public static SpeechEngine getInstance(Context context) {
		if (instance == null) {
			Log.i(TAG, "Initializing Speech Engine");
			instance = new SpeechEngine(context);
		}
		return instance;
	}
	
	
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			Log.i(TAG, "Speech Engine Ready");			
			ttsReady = true;
		}
		else
			Toast.makeText(mContext, "Error initializing speech engine", Toast.LENGTH_LONG).show();
	}
	
	public void speak(String text) {
		if (ttsReady) {
			
			HashMap<String, String> hash = new HashMap<String,String>();
	        hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, 
	                String.valueOf(AudioManager.STREAM_MUSIC));
		    mPlayer.pause();
		    mPlayer.stop(); //Stop any other speech from playing
	        tts.speak(text, TextToSpeech.QUEUE_FLUSH, hash);
		}
	}
	
	public void pause(int duration) {
		if (ttsReady) {
			HashMap<String, String> hash = new HashMap<String,String>();
	        hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, 
	                String.valueOf(AudioManager.STREAM_MUSIC));
			tts.playSilence(duration, TextToSpeech.QUEUE_ADD, hash);
		}		
	}
	
	public void pauseThenSpeak(int duration, String text) {
		if (ttsReady) {
			HashMap<String, String> hash = new HashMap<String,String>();
	        hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, 
	                String.valueOf(AudioManager.STREAM_MUSIC));
			tts.playSilence(duration, TextToSpeech.QUEUE_ADD, hash);
			tts.speak(text, TextToSpeech.QUEUE_ADD, hash);
		}		
	}
	
	public boolean isReady() {
		return ttsReady;
	}
	
	
	public void listen(String prompt, final Activity curActivity) {
		//check for speaking
		Log.i(TAG, "Setting tts listener");
		tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

			@Override
			public void onDone(String arg0) {
				Log.i(TAG, "Entered onDone");
				curActivity.runOnUiThread(new Runnable() {
					public void run() {
						startListening(curActivity);
					}
				});
			}

			@Override
			public void onError(String arg0) {
				Log.i(TAG, "Entered onError");					
			}

			@Override
			public void onStart(String arg0) {
				Log.i(TAG, "Entered onStart");					
			}
			
		});
		
		HashMap<String, String> hash = new HashMap<String,String>();
        hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, 
                String.valueOf(AudioManager.STREAM_MUSIC));
        hash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "speechPrompt");
		tts.speak(prompt, TextToSpeech.QUEUE_FLUSH, hash);
	}

	public void playMusic(String filename) {
		tts.stop(); //Empty out speech queue, as if this were speech
	    mPlayer.pause();
	    mPlayer.stop(); //Stop any other speech from playing
	    mPlayer.reset();
	    try {	    	
	    	Log.i(TAG, "Playing file " + filename);
	        mPlayer.setDataSource(filename);
	        mPlayer.prepare();
	        Log.d(TAG, "start play music");
	        mPlayer.start();
	    } catch (IOException e) {
	        Log.e(TAG, "Error: " + e);
	    }
	}
	
	//From http://stackoverflow.com/questions/23047433/record-save-audio-from-voice-recognition-intent
	private void startListening(Activity activity) {
	   // Fire an intent to start the speech recognition activity.
	   Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	   // secret parameters that when added provide audio url in the result
	   intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
	   intent.putExtra("android.speech.extra.GET_AUDIO", true);
	   intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
	   activity.startActivityForResult(intent, AUDIO_INPUT_ACTIVITY);

	}
}
