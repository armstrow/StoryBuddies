package courses.cmsc436.storybuddies;


public class StoryPage {
	
	String mPictureFromFile;
	int mPicture;
	String mStoryText;
	String mSpeechAudio;
	private String mVoiceUri;
	String mGameActivity = null;//null will represent a story without a game link
	
	// Constructors for StoryPage ------------------------------------------------------------
		public StoryPage(int picture, String words, String speech, String gameActivity){
			mPicture = picture;
			mStoryText = words;
			mSpeechAudio = speech;
			mGameActivity = gameActivity;
		}
		
		public StoryPage(int picture, String words, String speech){
				this(picture, words, speech, null);
		}
		
		public StoryPage(int picture, String words){
			this(picture, words, null, null);
		}
		
		public StoryPage(int picture){
			this(picture, null, null, null);
		}
		
		public StoryPage(){
			this(-1,null, null, null);
		}	
		// End Constructors for StoryPage -------------------------------------------------------

		//Start Getters and Setters -------------------------------------------------------------
		public void setmPictureFromFile(String file){
			mPictureFromFile = file;
		}
		
		public String getmPictureFromFile(){
			return mPictureFromFile;
		}
		
		public int getmPicture() {
			return mPicture;
		}

		public void setmPicture(int mPicture) {
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

		public String getmVoiceUri() {
			return mVoiceUri;
		}

		public void setmVoiceUri(String mVoiceUri) {
			this.mVoiceUri = mVoiceUri;
		}
}
