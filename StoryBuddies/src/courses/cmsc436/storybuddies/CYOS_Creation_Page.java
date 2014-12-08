package courses.cmsc436.storybuddies;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class CYOS_Creation_Page extends Activity {
	
	public final String TAG = "CYOS_Creation_Activity";
	private SpeechEngine speech;
	
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
	
	int currPageNumber = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cyos_creation_screen);
		
		speech = SpeechEngine.getInstance(getApplicationContext());
		
		speech.speak("How does our story start?");
		
		newScreens.add(null);
		
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
		color1 = (RadioButton) findViewById(R.id.color1);
		color2 = (RadioButton) findViewById(R.id.color2);
		color3 = (RadioButton) findViewById(R.id.color3);
		color4 = (RadioButton) findViewById(R.id.color4);
		color5 = (RadioButton) findViewById(R.id.color5);
		
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
		
		prevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered prevButton OnClickListener");
				if(currPageNumber > 0){
					updatePage(-1);
				} else {
					Toast.makeText(getApplicationContext(),"TODO - Go to a title screen clone activity",Toast.LENGTH_LONG).show();
					//TODO - Create a new activity that looks like title screen so user can change title and author
					//	if they wish. Will need to use startActivityforResult
				} 
				
			}
		});
		
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered nextButton OnClickListener");
				if(currPageNumber < newStory.getmPages().size()-1){
					Log.i(TAG,"going to the next page");
					updatePage(1);	
				} else {
					Log.i(TAG,"Creating a new page");
					newStory.addPage(new StoryPage());
					newScreens.add(null);
					speech.speak("What happens next!");
					updatePage(1);
				}			
			}
		});
		
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered submitButton OnClickListener");
				//TODO - a popup asking if the user wants to continue should be implemented here
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
					Toast.makeText(getApplicationContext(), "Error saving story", Toast.LENGTH_LONG).show();
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
	}

	protected boolean saveStory(StoryBook newStory) {
		try {
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				String path = Environment.getExternalStorageDirectory() + "/" + getString(R.string.story_dir) + "/" + newStory.getmTitle().replace(" ", "_");
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
					StoryPage curPage = pages.get(i);
					//if (curPage.getmPicture() != null) { Changed to
					if(newScreens.get(i) != null) {
						outFile = new File(path, getString(R.string.page_file_name) + (i+1) + ".png");
						writeImageToMemory(outFile, newScreens.get(i));
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
			if (page.getmPictureFromFile() != null)
				w.name("PICTURE").value("page" + (i+1) + ".png");
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
}
