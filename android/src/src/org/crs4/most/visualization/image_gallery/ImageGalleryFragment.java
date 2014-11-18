package org.crs4.most.visualization.image_gallery;

import org.crs4.most.visualization.R;
import org.crs4.most.visualization.R.drawable;
import org.crs4.most.visualization.R.id;
import org.crs4.most.visualization.R.layout;
import org.crs4.most.visualization.R.styleable;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


// GALLERY DEPRECATED ->>>>> http://stackoverflow.com/questions/15833889/options-for-replacing-the-deprecated-gallery

public class ImageGalleryFragment extends Fragment {
	
	Integer[] pics = { R.drawable.antartica1, R.drawable.antartica2,
			R.drawable.antartica3, R.drawable.antartica4,
			R.drawable.antartica5, R.drawable.antartica6,
			R.drawable.antartica7, R.drawable.antartica8,
			R.drawable.antartica9, R.drawable.antartica10 ,
			R.drawable.antartica3, R.drawable.antartica4,
			R.drawable.antartica5, R.drawable.antartica6,
			R.drawable.antartica7, R.drawable.antartica8,
			R.drawable.antartica9, R.drawable.antartica10 };
	
	LinearLayout imageView;
	private View rootView;
	
	/** Called when the activity is first created. */
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.rootView = inflater.inflate(R.layout.gallery_main, container, false);
		
			return rootView;
	}

	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        

		Gallery ga = (Gallery) this.rootView.findViewById(R.id.Gallery01);
		ga.setAdapter(new ImageAdapter(getActivity()));

		imageView = (LinearLayout) this.rootView.findViewById(R.id.ImageView01);
		ga.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(
						getActivity(),
						"You have selected picture " + (arg2 + 1)
								+ " of Antartica", Toast.LENGTH_SHORT).show();
				try {
				imageView.removeAllViews();
				} catch (Exception e) {
					e.getMessage();
				}
				TouchImageView touchImageView = new TouchImageView(
						getActivity());
				touchImageView.setImageResource(pics[arg2]);
				LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				imageView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
				touchImageView.setLayoutParams(lp);
				imageView.addView(touchImageView);
			}

		});
        
	}

	public class ImageAdapter extends BaseAdapter {

		private Context ctx;
		int imageBackground;

		public ImageAdapter(Context c) {
			ctx = c;
			TypedArray ta = getActivity().obtainStyledAttributes(R.styleable.Gallery1);
			imageBackground = ta.getResourceId(
					R.styleable.Gallery1_android_galleryItemBackground, 1);
			ta.recycle();
		}

		@Override
		public int getCount() {

			return pics.length;
		}

		@Override
		public Object getItem(int arg0) {

			return arg0;
		}

		@Override
		public long getItemId(int arg0) {

			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ImageView iv = new ImageView(ctx);
			iv.setImageResource(pics[arg0]);
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			iv.setLayoutParams(new Gallery.LayoutParams(150, 120));
			iv.setBackgroundResource(imageBackground);
			return iv;
		}
	}
}