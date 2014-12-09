package courses.cmsc436.storybuddies;

import java.util.ArrayList;

import courses.cmsc436.storybuddies.StoryBuddiesUtils.BitmapWorkerTask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StoryViewAdapter extends BaseAdapter {

	//private ArrayList<StoryBook> list = new ArrayList<StoryBook>();
	private static LayoutInflater inflater = null;
	private Context mContext;
	private ArrayList<StoryBook> list = StoryBuddiesBaseActivity.stories;
	
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
			holder.cover = (ImageView) newView.findViewById(R.id.cover);
			holder.title = (TextView) newView.findViewById(R.id.title_string);
			holder.author = (TextView) newView.findViewById(R.id.author_string);
			newView.setTag(holder);
			
		} else {
			holder = (ViewHolder) newView.getTag();
		}

		
		//holder.cover.setImageResource(curr.getmTitlePage());
		BitmapWorkerTask imageLoader = new BitmapWorkerTask(holder.cover, mContext.getResources());
		imageLoader.execute(curr.getmTitlePage());
		
		holder.title.setText(curr.getmTitle());
		holder.author.setText(curr.getmAuthor());

		return newView;
	}
	
	static class ViewHolder {
	
		ImageView cover;
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
}
