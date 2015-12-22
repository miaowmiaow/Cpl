package com.example.cpl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class MaterialLayout extends RelativeLayout {

	private int radius = 0;
	private int cx = 0;
	private int cy = 0;
	private int duration = 200;

	public MaterialLayout(Context context) {
		this(context, null);
	}

	public MaterialLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MaterialLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
	}

	public void push(View view) {
		if (view != null) {
			cx = view.getLeft() + view.getWidth() / 2;
			cy = view.getTop() + view.getHeight() / 2;
		}
		int value = Math.max(cx, getWidth() - cx) + Math.max(cy, getHeight() - cy);
		ValueAnimator animator = ValueAnimator.ofInt(0, value);
		animator.setDuration(duration);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				radius = (Integer) animation.getAnimatedValue();
				invalidate(new Rect(cx - radius, cy - radius, cx + radius, cy + radius));
			}
		});
		animator.start();
	}

	public void pop() {
		ValueAnimator animator = ValueAnimator.ofInt(radius, 0);
		animator.setDuration(duration);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				radius = (Integer) animation.getAnimatedValue();
				invalidate();
			}
		});
		animator.start();
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		Path path = new Path();
		path.addCircle(cx, cy, radius, Path.Direction.CW);
		canvas.clipPath(path);
		return super.drawChild(canvas, child, drawingTime);
	}
}
