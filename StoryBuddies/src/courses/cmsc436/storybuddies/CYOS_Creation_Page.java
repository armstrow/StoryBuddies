package courses.cmsc436.storybuddies;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

public class CYOS_Creation_Page extends Activity {
	
	public final String TAG = "CYOS_Creation_Activity";
	private SpeechEngine speech;
	
	private InputMethodManager imm;
	
	StoryBook newStory = new StoryBook();
	List<Bitmap> newScreens = new ArrayList<Bitmap>();
	Button prevButton;
	Button nextButton;
	Button submitButton;
	Button undoButton;
	EditText storyText;
	PaintView drawing;
	RadioButton color1;
	RadioButton color2;
	RadioButton color3;
	RadioButton color4;
	RadioButton color5;
	RadioButton color6;
	ImageView micButton;
	
	ArrayList<byte[]> sounds = new ArrayList<byte[]>();
	
	int currPageNumber = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cyos_creation_screen);
		
		speech = SpeechEngine.getInstance(getApplicationContext());
		
		imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		
		speech.speak("How does our story start?");
		
		newScreens.add(null);
		sounds.add(null);
		
		//Get String extras from intent
		String currTitle = getIntent().getStringExtra("currTitle");
		String currName = getIntent().getStringExtra("currName");
		
		newStory.setmTitle(currTitle);
		newStory.setmAuthor(currName);
		
		//Set views to their appropriate id's
		prevButton = (Button) findViewById(R.id.cyosPrevButton);
		nextButton = (Button) findViewById(R.id.cyosNextButton);
		submitButton = (Button) findViewById(R.id.cyosSubmitButton);
		storyText = (EditText) findViewById(R.id.cyosStoryText);
		drawing = (PaintView) findViewById(R.id.paintView);
		undoButton = (Button) findViewById(R.id.undoButton);
		micButton = (ImageView) findViewById(R.id.micrphone_button);
		color1 = (RadioButton) findViewById(R.id.color1);
		color2 = (RadioButton) findViewById(R.id.color2);
		color3 = (RadioButton) findViewById(R.id.color3);
		color4 = (RadioButton) findViewById(R.id.color4);
		color5 = (RadioButton) findViewById(R.id.color5);
		color6 = (RadioButton) findViewById(R.id.color6);
		
		color1.setBackgroundColor(Color.BLACK);
		color2.setBackgroundColor(Color.WHITE);
		color3.setBackgroundColor(Color.BLUE);
		color4.setBackgroundColor(Color.RED);
		color5.setBackgroundColor(Color.YELLOW);
		color6.setBackgroundColor(Color.GREEN);
		
		//Set up the first page for editing
		newStory.addPage(new StoryPage());
		updatePage(0);
		
		color1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawing.setColor(Color.BLACK);				
			}
		});	
		color2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawing.setColor(Color.WHITE);				
			}
		});
		color3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawing.setColor(Color.BLUE);				
			}
		});
		color4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawing.setColor(Color.RED);				
			}
		});
		color5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawing.setColor(Color.YELLOW);				
			}
		});
		color6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawing.setColor(Color.GREEN);				
			}
		});
		
		prevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered prevButton OnClickListener");
				closeSoftKeyboard();
				if(currPageNumber > 0){
					updatePage(-1);
				} else {
					Toast.makeText(getApplicationContext(),getString(R.string.todo),Toast.LENGTH_LONG).show();
					//TODO - Create a new activity that looks like title screen so user can change title and author
					//	if they wish. Will need to use startActivityforResult
				} 
				
			}
		});
		
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered nextButton OnClickListener");
				closeSoftKeyboard();
				if(currPageNumber < newStory.getmPages().size()-1){
					Log.i(TAG,"going to the next page");
					updatePage(1);	
				} else {
					Log.i(TAG,"Creating a new page");
					newStory.addPage(new StoryPage());
					newScreens.add(null);
					sounds.add(null);
					speech.speak("What happens next!");
					updatePage(1);
				}
			}
		});
		
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered submitButton OnClickListener");
				closeSoftKeyboard();
				updatePage(0);
				
				if (saveStory(newStory))
				{
					Log.i(TAG,  "Story saved...returning");
					Intent data = new Intent();
					String s = newStory.getmTitle();
					data.putExtra(ChooseStoryActivity.STORY_FILE_LOC, s);
					CYOS_Creation_Page.this.setResult(RESULT_OK, data);
					CYOS_Creation_Page.this.finish(); 
				}
				else {
					Toast.makeText(getApplicationContext(), getString(R.string.error_save), Toast.LENGTH_LONG).show();
					CYOS_Creation_Page.this.setResult(RESULT_CANCELED, new Intent());
					CYOS_Creation_Page.this.finish();
				}
			}
		});
		
		undoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered undoButton OnClickListener");
				drawing.clear();
			}
		});
		
		micButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered micButton OnClickListener");
				CYOS_Creation_Page.this.listen("Tell me what you'd like this page to say");
			}
		});
	}
	
	private void closeSoftKeyboard(){
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}

	protected boolean saveStory(StoryBook newStory) {
		try {
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				//String path = Environment.getExternalStorageDirectory() + "/" + getString(R.string.story_dir
				String path = StoryBuddiesBaseActivity.storySaveLoc + "/" + newStory.getmTitle().replace(" ", "_");
				Log.i(TAG, "Saving to: " + path);
				File outFile = new File(path, getString(R.string.text_file_name) + ".txt");
				if (!(new File(path)).mkdirs()) {
					Log.e(TAG, "Could not create directory to store book");
					return false;
				}
				writeStoryText(outFile, newStory);
				
				if (newStory.getmTitlePage() != -1) {
					outFile = new File(path, getString(R.string.cover_file_name) + ".png");
					if (outFile.exists()) {
						Log.e(TAG, "Book already exists!");
						return false;
					}
					writeImageToMemory(outFile, BitmapFactory.decodeResource(getResources(), newStory.getmTitlePage()));
				}
				
				List<StoryPage> pages = newStory.getmPages();
				for (int i = 0; i < pages.size(); i++) {
					if(newScreens.get(i) != null) {
						outFile = new File(path, getString(R.string.page_file_name) + (i+1) + ".png");
						writeImageToMemory(outFile, newScreens.get(i));
					}
					if (sounds.get(i) != null) {
						String fileName = path + "/audio" + (i+1) + ".amr";
						writeAudioDataToFile(sounds.get(i), fileName);
					}
				}				
				
				return true;		
			}
			else {
				return false;
			}
		} catch (IOException e) {
			Log.e(TAG, "File write error: " + e);
			return false;
		}
	}
	
	private void writeStoryText(File out, StoryBook story) throws IOException {
		FileOutputStream os = new FileOutputStream(out);
		OutputStreamWriter writer = new OutputStreamWriter(os);
		JsonWriter w = new JsonWriter(writer);
		w.beginObject();
		w.name("TITLE").value(story.mTitle);
		w.name("AUTHOR").value(story.mAuthor);
		w.name("PAGES");
		w.beginArray();
		List<StoryPage> pages = newStory.getmPages();
		for (int i = 0; i < pages.size(); i++) {
			StoryPage page = pages.get(i);
			w.beginObject();
			w.name("TEXT").value(page.getmStoryText());
			if (newScreens.get(i) != null)
				w.name("PICTURE").value("page" + (i+1) + ".png");
			if (sounds.get(i) != null)
				w.name("AUDIO").value("audio" + (i+1) + ".amr");
			w.endObject();
		}
		w.endArray();
		w.endObject();
		w.close();
	}

	private void writeImageToMemory(File outFile, Bitmap bmp) throws FileNotFoundException {
		FileOutputStream os = new FileOutputStream(outFile);
		bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
	}


	/*
	 * updates the page to save changes to views in the correct page
	 * updates the page if page is changing
	 * updates views if page has changed
	 * delta is the change in page number -1 for previous 0 for same and 1 for next
	 */
	public void updatePage(int delta){
		List<StoryPage> pageList = newStory.getmPages();
		
		//Save changes to newStory
		pageList.get(currPageNumber).setmStoryText(storyText.getText().toString());
		Log.i(TAG, "Pretty sure it gets here");
		newScreens.set(currPageNumber, drawing.getBitmap());
		//pageList.get(currPageNumber).setmPicture(drawing.getBitmap());
		Log.i(TAG, "Hoping it gets here");
		
		//while(currPageNumber > myPaths.size()-1){
		//	myPaths.add(new Path());
		//}
		//Log.i(TAG, "Not yet setting Page: myPaths size = " + myPaths.size());
		//myPaths.set(currPageNumber, drawing.getPaths());
		//Log.i(TAG, "Setting Page");
		
		currPageNumber += delta;
		
		///while(currPageNumber > myPaths.size()-1){
		//	myPaths.add(new Path());
		//}
		
		//Upload info for newPage
		storyText.setText(pageList.get(currPageNumber).getmStoryText());
		Log.i(TAG,"Gets Here");
		drawing.clear();
		//drawing.setBitmap(pageList.get(currPageNumber).getmPicture());
		if(newScreens.get(currPageNumber) != null){
			drawing.setBitmap(newScreens.get(currPageNumber));
		}
		//drawing.setPaths(myPaths.get(currPageNumber));
		Log.i(TAG,"Gets here also?");
		
		if(currPageNumber == pageList.size()-1){
			nextButton.setText("New Page");
		} else {
			nextButton.setText("Next Page");
		}
		
		if(currPageNumber == 0){
			prevButton.setAlpha(.5f);
			prevButton.setClickable(false);
		} else {
			prevButton.setAlpha(1f);
			prevButton.setClickable(true);
		}
		
		return;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
	}

	public void listen(String prompt) {
		//check for speaking
		TextToSpeech tts = speech.getTTS();
		Log.i(TAG, "Setting tts listener");
		tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

			@Override
			public void onDone(String arg0) {
				Log.i(TAG, "Entered onDone");
				CYOS_Creation_Page.this.runOnUiThread(new Runnable() {
					public void run() {
						startListening();
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

	
	//From http://stackoverflow.com/questions/23047433/record-save-audio-from-voice-recognition-intent
	private void startListening() {
	   // Fire an intent to start the speech recognition activity.
	   Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	   // secret parameters that when added provide audio url in the result
	   intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
	   intent.putExtra("android.speech.extra.GET_AUDIO", true);
	   intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
	   startActivityForResult(intent, 123);

	}
	
	//from http://stackoverflow.com/questions/23047433/record-save-audio-from-voice-recognition-intent
	// handle result of speech recognition
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // the resulting text is in the getExtras:
	    Bundle bundle = data.getExtras();
	    ArrayList<String> matches = bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
	    storyText.setText(matches.get(0));
	    // the recording url is in getData:
	    Uri audioUri = data.getData();
	    ContentResolver contentResolver = getContentResolver();
	    try {
			InputStream filestream = contentResolver.openInputStream(audioUri);
			
			//from http://stackoverflow.com/questions/1264709/convert-inputstream-to-byte-array-in-java
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytes = new byte[16384];

			while ((nRead = filestream.read(bytes, 0, bytes.length)) != -1) {
			  buffer.write(bytes, 0, nRead);
			}

			buffer.flush();

			sounds.set(currPageNumber, buffer.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
    //From http://www.edumobile.org/android/android-development/audio-recording-in-wav-format-in-android-programming/
    private void writeAudioDataToFile(byte[] audio, String filename){
        FileOutputStream os = null;
        
        try {
                os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        
        
        if(null != os){
                
            try {
                    os.write(audio);
                    os.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }
                
        }
    }
}
