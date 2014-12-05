package courses.cmsc436.storybuddies;

import java.util.List;

import android.graphics.Bitmap;

public class StoryPage {
	
	Bitmap mPicture;
	String mStoryText;
	String mSpeechAudio;
	String mGameActivity = null;//null will represent a story without a game link
	
	// Constructors for StoryPage ------------------------------------------------------------
		public StoryPage(Bitmap picture, String words, String speech, String gameActivity){
			mPicture = picture;
			mStoryText = words;
			mSpeechAudio = speech;
			mGameActivity = gameActivity;
		}
		
		public StoryPage(Bitmap picture, String words, String speech){
				this(picture, words, speech, null);
		}
		
		public StoryPage(Bitmap picture, String words){
			this(picture, words, null, null);
		}
		
		public StoryPage(Bitmap picture){
			this(picture, null, null, null);
		}
		
		public StoryPage(){
			this(null,null, null, null);
		}	
		// End Constructors for StoryPage -------------------------------------------------------

		//Start Getters and Setters -------------------------------------------------------------
		public Bitmap getmPicture() {
			return mPicture;
		}

		public void setmPicture(Bitmap mPicture) {
			this.mPicture = mPicture;
		}

		public String getmStoryText() {
			return mStoryText;
		}

		public void setmStoryText(String mStoryText) {
			this.mStoryText = mStoryText;
		}

		public String getmSpeechAudio() {
			return mSpeechAudio;
		}

		public void setmSpeechAudio(String mSpeechAudio) {
			this.mSpeechAudio = mSpeechAudio;
		}

		public String getmGameActivity() {
			return mGameActivity;
		}

		public void setmGameActivity(String mGameActivity) {
			this.mGameActivity = mGameActivity;
		}
		//END Getters and Setters ---------------------------------------------------------------
}
