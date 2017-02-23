package s.com.android.swipe;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by android1 on 23.02.2017.
 */
class FilterSwipe extends ViewGroup {


    private int mFilterPos = 0;
    private boolean isForward;
    private Bitmap mCurrentBitmap;
    private Bitmap mForApply;

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 25;
        private static final int SWIPE_VELOCITY_THRESHOLD = 5;


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) >
                    SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0) {
//swipe right
                    moveLeft();
                } else {
                    moveRight();

                }
                return true;
            }

            return false;
        }

        private void moveRight() {
            isForward = true;
            if (mFilterPos > 0) {

                mCurrentBitmap = mFilters.get(mFilterPos);
                mFilterPos--;
                mForApply = mFilters.get(mFilterPos);
                // currX = getWidth();
                // invalidate();


                ValueAnimator va = ValueAnimator.ofInt(getMeasuredWidth(), 0);
                va.setDuration(200);
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        isForward = true;
                        currX = (int) animation.getAnimatedValue();
                        invalidate();

                    }
                });

                va.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                va.start();
            }

        }

        private void moveLeft() {
            isForward = false;
            if (mFilterPos < mFilters.size() - 1) {
                mCurrentBitmap = mFilters.get(mFilterPos);
                mFilterPos++;
                mForApply = mFilters.get(mFilterPos);


                ValueAnimator va = ValueAnimator.ofInt(0, getMeasuredWidth());
                va.setDuration(200);
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        currX = (int) animation.getAnimatedValue();
                        invalidate();

                    }
                });
                va.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                va.start();


                // currX = getMeasuredWidth();
                //  invalidate();
            }
        }





        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX1, float distanceY) {

            if (Math.abs(distanceY) > 2 || Math.abs(distanceX1) > 2) {
                if (Math.abs(distanceX1) > Math.abs(distanceY)) {

                    // if (isLeftActive) {
                    // currX = (int) e2.getX();
                    // invalidate();
                    //  }
                }
            }
            return false;
        }


    }


    private void drawFilter(Canvas canvas) {

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.save();
        // Rect rectOrigin = new Rect(0, 0,getMeasuredWidth(),0);

        Rect rec = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
        canvas.drawBitmap(mCurrentBitmap, rec, rec, paint0);

        // canvas.drawBitmap(mOriginal, null,rectOrigin, paint0);
        Rect rectSrc = null;
        Rect rectDest = null;

        if (!isForward) {

            rectSrc = new Rect(0, 0, currX, getMeasuredHeight());
            rectDest = new Rect(0, 0, currX, getMeasuredHeight());
        } else {
            rectSrc = new Rect(currX, 0, getMeasuredWidth(), getMeasuredHeight());
            rectDest = new Rect(currX, 0, getMeasuredWidth(), getMeasuredHeight());
        }

        canvas.drawBitmap(mForApply, rectSrc, rectDest, paint1);


        canvas.restore();
    }


    private int currX = 0;
    Paint paint0;
    Paint paint1;
    private List<Bitmap> mFilters;

    public FilterSwipe(Context context) {
        super(context);
        paint0 = new Paint();
        paint1 = new Paint();

        paint0.setAlpha(255);
        paint1.setAlpha(255);

        paint1.setAntiAlias(true);
        paint1.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint1.setFlags(Paint.FILTER_BITMAP_FLAG);

        paint0.setAntiAlias(true);
        paint0.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint0.setFlags(Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawFilter(canvas);
    }



    public void init(@NonNull List<Bitmap> filters, int posToShow) {
        mFilterPos = posToShow;

        this.mFilters = filters;

        final GestureDetector gdt = new GestureDetector(new GestureListener());
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                //drawFilter((int)event.getRawX());
                gdt.onTouchEvent(event);
                return true;
            }


        });

        mForApply =  mCurrentBitmap = mFilters.get(mFilterPos);


    }
}