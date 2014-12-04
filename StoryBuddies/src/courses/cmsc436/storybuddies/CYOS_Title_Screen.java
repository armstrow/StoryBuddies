package courses.cmsc436.storybuddies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CYOS_Title_Screen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cyos_title_screen);
		
		//EditText mTitle = (EditText) findViewById(R.id.CYOS_Title);
		//EditText mName = (EditText) findViewById(R.id.CYOS_Name);
		
		Button CYOS_Continue_Button = (Button) findViewById(R.id.CYOS_submit_button);
		CYOS_Continue_Button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//TODO - save mTitle and mName in either an adapter or an extras to the next activity
				Intent creationActivity = new Intent(CYOS_Title_Screen.this,/*TODO.class*/ null);
				startActivity(creationActivity);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
