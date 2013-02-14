package com.example.geocuratedphotolibrary.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PhotoView extends ImageView{

	public PhotoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	public PhotoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public PhotoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

}
