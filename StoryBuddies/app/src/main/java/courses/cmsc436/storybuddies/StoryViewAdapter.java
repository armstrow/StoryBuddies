package courses.cmsc436.storybuddies;

import java.io.File;
import java.util.ArrayList;

import courses.cmsc436.storybuddies.StoryBuddiesUtils.BitmapWorkerTask;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class StoryViewAdapter extends BaseAdapter {

	//private ArrayList<StoryBook> list = new ArrayList<StoryBook>();
	private static LayoutInflater inflater = null;
	private Context mContext;
	private ArrayList<StoryBook> list = StoryBuddiesBaseActivity.stories;
	private int selectedItem = -1;
	
	public StoryViewAdapter(Context context) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	

	@Override	
	public View getView(int position, View convertView, ViewGroup parent) {

		View newView = convertView;
		ViewHolder holder;

		StoryBook curr = list.get(position);

		if (null == convertView) {
			holder = new ViewHolder();
			newView = inflater.inflate(R.layout.story_list_item, parent, false);
			holder.cover = (ImageButton) newView.findViewById(R.id.cover);
			holder.title = (TextView) newView.findViewById(R.id.title_string);
			holder.author = (TextView) newView.findViewById(R.id.author_string);
			newView.setTag(holder);
			
		} else {
			holder = (ViewHolder) newView.getTag();
		}

		if (curr.getmPages().get(0).getmPicture() >= 0)
		{
			//newView.setBackgroundResource(curr.getmPages().get(0).getmPicture());
			holder.cover.setImageResource(curr.getmPages().get(0).getmPicture());	
		} else if (curr.getmPages().get(0).getmPictureFromFile() != null) {
			holder.cover.setImageURI(Uri.fromFile(new File(curr.getmPages().get(0).getmPictureFromFile())));

			//BitmapWorkerTask imageLoader = new BitmapWorkerTask(newView);
			//imageLoader.execute(curr.getmPages().get(0).getmPictureFromFile());
		}
		//newView.setBackgroundResource(curr.getmTitlePage());
		//holder.cover.setImageResource(curr.getmTitlePage());
		//
		//holder.cover.setOnClickListener(ChooseStoryActivity.getOnClickListener())
		if (position == selectedItem) {
			//newView.setBackgroundColor(mContext.getResources().getColor(R.color.selected));
			holder.cover.getBackground().setColorFilter(mContext.getResources().getColor(R.color.selected), Mode.MULTIPLY);
		} else {
			holder.cover.getBackground().setColorFilter(mContext.getResources().getColor(R.color.deselected), Mode.MULTIPLY);
			//newView.setBackgroundColor(mContext.getResources().getColor(R.color.deselected));
		}
		
		
		//newView.setBackgroundColor(Color.WHITE);
        holder.cover.setFocusable(false);
        holder.cover.setClickable(false); 
		holder.title.setText(curr.getmTitle());
		
		Log.i("SB_StoryAdapter", "Author: [" + curr.getmAuthor() + "]");
		if (curr.getmAuthor() != null && !curr.getmAuthor().trim().equals(""))
			holder.author.setText("by: " + curr.getmAuthor());

		return newView;
	}
	
	static class ViewHolder {
	
		ImageButton cover;
		TextView title;
		TextView author;
		
	}
	
	public void add(StoryBook listItem) {
		list.add(listItem);
		notifyDataSetChanged();
	}
	
	public void removeAllViews(){
		list.clear();
		this.notifyDataSetChanged();
	}

	public boolean contains(StoryBook other) {
		for (StoryBook s : list) {
			if (s.getmTitle().equals(other.getmTitle())) {
				return true;
			}
		}
		return false;
	}

	public void remove(int position) {
		list.remove(position);
		this.notifyDataSetChanged();
	}

	public int getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(int selectedItem) {
		this.selectedItem = selectedItem;
	}
}
