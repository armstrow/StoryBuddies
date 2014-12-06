package courses.cmsc436.storybuddies;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.widget.Toast;

public class SpeechEngine implements OnInitListener {
	private TextToSpeech tts;
	private boolean ttsReady = false;
	private static SpeechEngine instance = null;
	private Context mContext;
	private static final String TAG = "SB_SpeechEngine";
	
	public SpeechEngine(Context context) {
		mContext = context;
		tts = new TextToSpeech(context, this);
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
			Toast.makeText(mContext, "Error initializing speech engine", Toast.LENGTH_LONG);
	}
	
	public void speak(String text) {
		if (ttsReady) {
			HashMap<String, String> hash = new HashMap<String,String>();
	        hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, 
	                String.valueOf(AudioManager.STREAM_MUSIC));
			tts.speak(text, TextToSpeech.QUEUE_FLUSH, hash);
		}
	}
	
	public boolean isReady() {
		return ttsReady;
	}
}
