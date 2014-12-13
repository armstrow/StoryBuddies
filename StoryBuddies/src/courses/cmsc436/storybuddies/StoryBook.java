package courses.cmsc436.storybuddies;

import java.util.ArrayList;
import java.util.List;

public class StoryBook  {

	int mTitlePage;
	String mTitle;
	String mVoiceOver;
	String mAuthor = null;
	private String mTitleAudio = null;
	ArrayList<StoryPage> mPages = new ArrayList<StoryPage>();
	
	// Constructors for Story Book ------------------------------------------------------------
	public StoryBook(String title, int coverImage, String voice, List<StoryPage> pages){
		mTitlePage = coverImage;
		mTitle = title;
		mVoiceOver = voice;
		if (pages != null)
			mPages = new ArrayList<StoryPage>(pages);
	}
	
	public StoryBook(String title, int coverImage, String voice){
			this(title, coverImage, voice, null);
	}
	
	public StoryBook(String title, int coverImage){
		this(title, coverImage, null, null);
	}
	
	public StoryBook(String title){
		this(title, -1, null, null);
	}
	
	public StoryBook(){
		this("Default", -1, null, null);
	}	
	// End Constructors for StoryBook -------------------------------------------------------
	
	public void addPage(StoryPage newPage){
		mPages.add(newPage);
	}
	
	//Start Getters and Setters -------------------------------------------------------------

	public void setmAuthor(String author){
		mAuthor = author;
	}
	
	public String getmAuthor(){
		return mAuthor;
	}
	
	public int getmTitlePage() {
		return mTitlePage;
	}

	public void setmTitlePage(int mTitlePage) {
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
		this.mPages = new ArrayList<StoryPage>(mPages);
	}
	//End Getters and Setters -------------------------------------------------------------

	@Override
	public String toString() {
		return mTitle;
	}

	String getmTitleAudio() {
		return mTitleAudio;
	}

	void setmTitleAudio(String mTitleAudio) {
		this.mTitleAudio = mTitleAudio;
	}


}
