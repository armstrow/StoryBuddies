package courses.cmsc436.storybuddies;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class StoryBook {

	Bitmap mTitlePage;
	String mTitle;
	String mVoiceOver;
	List<StoryPage> mPages = new ArrayList<StoryPage>();
	
	// Constructors for Story Book ------------------------------------------------------------
	public StoryBook(String title, Bitmap coverImage, String voice, List<StoryPage> pages){
		mTitlePage = coverImage;
		mTitle = title;
		mVoiceOver = voice;
		mPages = pages;
	}
	
	public StoryBook(String title, Bitmap coverImage, String voice){
			this(title, coverImage, voice, null);
	}
	
	public StoryBook(String title, Bitmap coverImage){
		this(title, coverImage, null, null);
	}
	
	public StoryBook(String title){
		this(title, null, null, null);
	}
	
	public StoryBook(){
		this("Default Title",null, null, null);
	}	
	// End Constructors for StoryBook -------------------------------------------------------
	
	//TODO - Create setters and getters
	//TODO - Create add page method
}
