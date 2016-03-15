package it.crs4.most.visualization.image_gallery;

import java.io.File;

import it.crs4.most.streaming.utils.ImageDownloader;
import it.crs4.most.visualization.R;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;



// GALLERY DEPRECATED ->>>>> http://stackoverflow.com/questions/15833889/options-for-replacing-the-deprecated-gallery

/**
 * 
 * This fragment allows you to embed in your activity an image gallery.
 *
 */
public class ImageGalleryFragment extends Fragment {
	
	private static final String TAG = "ImageGalleryFragment";
	
	private File [] pics = null;
	private ImageAdapter imageAdapter = null;
	
	LinearLayout imageView;
	private View rootView;
	
	/** Called when the activity is first created. */
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.rootView = inflater.inflate(R.layout.gallery_main, container, false);
		
			return rootView;
	}

	
	private void showDeleteMessageAlert(final int imageIndex){
		 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		 alertDialogBuilder.setTitle("Image Deletion Request");
		 alertDialogBuilder.setMessage(String.format( "Do you want to cancel the image %s ?" , this.pics[imageIndex].getName()));
		 alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				ImageDownloader.deleteInternalFile(getActivity(), pics[imageIndex].getName());
				reloadGalleryImages();
				dialog.cancel();
				
			}
			 
		 });
		 
		 alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					
				}
				 
			 });
		 
		 AlertDialog alertDialog = alertDialogBuilder.create();
		 alertDialog.show();
	}
	
	/**
	 * Reloads the images contained in to the internal storage
	 */
	public void reloadGalleryImages()
	{
		this.pics = ImageDownloader.getInternalImages(getActivity());
		if (this.imageAdapter!=null)
		{
			this.imageView.removeAllViews();
			this.imageAdapter.notifyDataSetChanged();
		}
		
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
       // load images from the internal storage of the activity attached to this fragment
        this.pics = ImageDownloader.getInternalImages(getActivity());
        
		Gallery ga = (Gallery) this.rootView.findViewById(R.id.Gallery01);
		 
		this.imageAdapter = new ImageAdapter(getActivity());
		ga.setAdapter(this.imageAdapter);

		imageView = (LinearLayout) this.rootView.findViewById(R.id.ImageView01);
		imageView.setBackgroundColor(Color.BLACK);
		
		ga.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
			   selectImage(arg2);
			}

		});
        
		// try to select the first image, if present, the first time this gallery is opened by the attached activity
		selectImage(0);
	}
	
	/**
	 * Select an image from the gallery, by index array
	 * @param imageIndex the index of the image (the index 0 is the newest image)
	 */
	public void selectImage(final int imageIndex)
	{
		try {
		imageView.removeAllViews();
		} catch (Exception e) {
			e.getMessage();
		}
		
		// if there is no image in the gallery, simply return...
		if (this.pics.length<1)
		{
			Log.d(TAG, "No image found in the gallery. Image selecting ignored");
			return;
		}
		
		
		GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

	        	@Override
	        	public boolean onDoubleTap(MotionEvent e) {
	        	    showDeleteMessageAlert(imageIndex);
	                return true;
	        	}
	        }); 
	        	 
		TouchImageView touchImageView = new TouchImageView(getActivity(), gestureDetector);
		
		//touchImageView.setImageResource(pics[arg2]);
		touchImageView.setImageDrawable(Drawable.createFromPath(pics[imageIndex].toString()));

		LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		imageView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
		touchImageView.setLayoutParams(lp);
		imageView.addView(touchImageView);
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
			// Set image resource
			//iv.setImageResource(pics[arg0]);
			iv.setImageDrawable(Drawable.createFromPath(pics[arg0].toString()));
			
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			iv.setLayoutParams(new Gallery.LayoutParams(150, 120));
			iv.setBackgroundResource(imageBackground);
			return iv;
		}
	}
}