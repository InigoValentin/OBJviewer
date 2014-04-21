package com.seavenois.obj;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * A view extending LinearLayout, that includes a slider menu and the 
 * model view area, and methods to display and show the menu.
 */
public class MainLayout extends LinearLayout {

	/**
	 * The duration of the sliding animation
	 */
	public static final int SLIDING_DURATION = 500;
	
	/**
	 * Parameter to wait between states
	 */
	public static final int QUERY_INTERVAL = 16;
	
	//UI elements
	private int mainLayoutWidth;
	private View menu;
	private View content;
	
	//Percent of the screen that will not be covered by the menu. Will be overridden later.
	private static int menuRightMargin = 25;
	
	//Enum with all posible states of the menu
	private enum MenuState {
		HIDING, HIDDEN, SHOWING, SHOWN,
	};

	//Useful variables
	private int contentXOffset;
	private MenuState currentMenuState = MenuState.HIDDEN;
	private Scroller menuScroller = new Scroller(this.getContext(), new EaseInInterpolator());
	private Runnable menuRunnable = new MenuRunnable();
	private Handler menuHandler = new Handler();
	private int prevX = 0;
	private boolean isDragging = false;
	private int lastDiffX = 0;

	/**
	 * Class constructor. Just calls the {@link LinearLayout} constructor.
	 * 
	 * @param context The application {@link Context}.
	 * @param attrs {@link AttributeSet} for the {@link View}
	 * 
	 * @see LinearLayout
	 * @see Context
	 * @see AttributeSet
	 */
	public MainLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Class constructor. Just calls the {@link LinearLayout} constructor.
	 * 
	 * @param context The application {@link Context}.
	 * 
	 * @see LinearLayout
	 * @see Context
	 */
	public MainLayout(Context context) {
		super(context);
	}

	/**
	 * Return the left {@link View}, the one that contains the menu.
	 * 
	 * @return the menu {@link View}
	 * @see View
	 */
	public View getMenuView(){
		return menu;
	}
	
	/*
	 * Overridden method. Calculates the menu width when the view is loaded
	 * and when it is resized. 
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		mainLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
		menuRightMargin = mainLayoutWidth * 40 / 100;
	}
	
	/*
	 * Overridden method. Called when the menu is opened. 
	 */
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		menu = this.getChildAt(0);
		content = this.getChildAt(1);
		content.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return MainLayout.this.onContentTouch(v, event);
			}
		});
		menu.setVisibility(View.GONE);
	}

	/*
	 * Overridden method. Sets measures for children. 
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		if (changed) {
			LayoutParams contentLayoutParams = (LayoutParams) content.getLayoutParams();
			contentLayoutParams.height = this.getHeight();
			contentLayoutParams.width = this.getWidth();
			LayoutParams menuLayoutParams = (LayoutParams) menu.getLayoutParams();
			menuLayoutParams.height = this.getHeight();
			menuLayoutParams.width = this.getWidth() - menuRightMargin;
		}
		this.requestDisallowInterceptTouchEvent(true);
		menu.layout(left, top, right - menuRightMargin, bottom);
		content.layout(left + contentXOffset, top, right + contentXOffset, bottom);

	}

	/**
	 * Switch the state of the slider menu, but only if its fully shown or fully hidden.
	 * It it is in the middle of a transition, it does nothing.
	 */
	public void toggleMenu() {
		if (currentMenuState == MenuState.HIDING || currentMenuState == MenuState.SHOWING)
			return;
		
		switch (currentMenuState) {
			case HIDDEN:
				currentMenuState = MenuState.SHOWING;
				menu.setVisibility(View.VISIBLE);
				menuScroller.startScroll(0, 0, menu.getLayoutParams().width, 0, SLIDING_DURATION);
				break;
			case SHOWN:
				currentMenuState = MenuState.HIDING;
				menuScroller.startScroll(contentXOffset, 0, -contentXOffset, 0, SLIDING_DURATION);
				break;
			default:
				break;
		}
		menuHandler.postDelayed(menuRunnable, QUERY_INTERVAL);
		this.invalidate();
	}
	
	/**
	 * Subclass implementing {@link Runnable} to show menu animation
	 *
	 */
	protected class MenuRunnable implements Runnable {
		
		/*
		 * Run animation
		 */
		@Override
		public void run() {
			boolean isScrolling = menuScroller.computeScrollOffset();
			adjustContentPosition(isScrolling);
		}
	}
	
	/**
	 * Adjust the layout position at each moment during the animation.
	 * @param isScrolling Indicates if the menu is being scrolled.
	 */
	private void adjustContentPosition(boolean isScrolling) {
		int scrollerXOffset = menuScroller.getCurrX();
		
		content.offsetLeftAndRight(scrollerXOffset - contentXOffset);
		
		contentXOffset = scrollerXOffset;
		this.invalidate();
		if (isScrolling)
			menuHandler.postDelayed(menuRunnable, QUERY_INTERVAL);
		else
			this.onMenuSlidingComplete();
	}
	
	/**
	 * Changes the menu state to the next one. Is called from 
	 * {@link adjustContentPosition(boolean isScrolling)} when a transition
	 * is finiehed.
	 */
	private void onMenuSlidingComplete() {
		switch (currentMenuState) {
			case SHOWING:
				currentMenuState = MenuState.SHOWN;
				break;
			case HIDING:
				currentMenuState = MenuState.HIDDEN;
				menu.setVisibility(View.GONE);
				break;
			default:
				return;
		}
	}
	
	/**
	 * Subclass implementing {@link Interpolator} to fade in and out the
	 * animation.
	 */
	protected class EaseInInterpolator implements Interpolator {
		
		/*
		 * Overridden method. Returns the imterpolation factor 
		 */
		@Override
		public float getInterpolation(float t) {
			return (float) Math.pow(t - 1, 5) + 1;
		}
	
	}
	
	/**
	 * Method to determine if the menu is being displayed.
	 * @return true if the menu is being displayed, false otherwise.
	 */
	public boolean isMenuShown() {
		return currentMenuState == MenuState.SHOWN;
	}
	
	/**
	 * Method that handles the different touch events in the menu.
	 * @param v The {@link View} being touched (Must be the menu {@link View})
	 * @param event The {@link MotionEvent} that just happened.
	 * @return true if the {@link MotionEvent} is handled by this method, false otherwise.
	 * @see {@link View}
	 * @see {@link MotionEvent}
	 */
	public boolean onContentTouch(View v, MotionEvent event) {
		if (currentMenuState == MenuState.HIDING || currentMenuState == MenuState.SHOWING)
			return false;
		int curX = (int) event.getRawX();
		int diffX = 0;
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				prevX = curX;
				return true;
			
			case MotionEvent.ACTION_MOVE:
				if (!isDragging) {
					isDragging = true;
					menu.setVisibility(View.VISIBLE);
				}
				diffX = curX - prevX;
				if (contentXOffset + diffX <= 0) {
					diffX = -contentXOffset;
				}
				else if (contentXOffset + diffX > mainLayoutWidth - menuRightMargin) {
					diffX = mainLayoutWidth - menuRightMargin - contentXOffset;
				}
				content.offsetLeftAndRight(diffX);
				contentXOffset += diffX;
				this.invalidate();
				
				prevX = curX;
				lastDiffX = diffX;
				return true;
			
			case MotionEvent.ACTION_UP:
				
				if (lastDiffX > 0) {
					currentMenuState = MenuState.SHOWING;
					menuScroller.startScroll(contentXOffset, 0,	menu.getLayoutParams().width - contentXOffset, 0, SLIDING_DURATION);
				}
				else if (lastDiffX < 0) {
					currentMenuState = MenuState.HIDING;
					menuScroller.startScroll(contentXOffset, 0, -contentXOffset, 0, SLIDING_DURATION);
				}
				menuHandler.postDelayed(menuRunnable, QUERY_INTERVAL);
				this.invalidate();
				isDragging = false;
				prevX = 0;
				lastDiffX = 0;
				return true;
				
			default:
				break;
		}
		
		return false;
	}
}

