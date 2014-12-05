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

	public Bitmap getmTitlePage() {
		return mTitlePage;
	}

	public void setmTitlePage(Bitmap mTitlePage) {
		this.mTitlePage = mTitlePage;
	}

	public String getmTitle() {
		return mTitle;
	}

	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getmVoiceOver() {
		return mVoiceOver;
	}

	public void setmVoiceOver(String mVoiceOver) {
		this.mVoiceOver = mVoiceOver;
	}

	public List<StoryPage> getmPages() {
		return mPages;
	}

	public void setmPages(List<StoryPage> mPages) {
		this.mPages = mPages;
	}
	
	//TODO - Create add page method
}
