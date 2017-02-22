package s.com.android.swipe;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageChromaKeyBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDilationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelEdgeDetection;

import static android.R.attr.bitmap;

public class MainActivity extends AppCompatActivity {

    private GPUImage mGPUImage;
    //    ImageView imageView;
    private CanvasView canvasView;

    List<Bitmap> mFilters = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout holder = (RelativeLayout) findViewById(R.id.activity_main);


//        imageView = new ImageView(getBaseContext());
        canvasView = new CanvasView(this);
        canvasView.setBackgroundColor(456578);


        Bitmap bitmapSource = BitmapFactory.decodeResource(getResources(), R.drawable.cat_puzzle3);
        Matrix matrix = new Matrix();
        matrix.postScale(2f, 2f);
        Bitmap mOriginal = Bitmap.createBitmap(bitmapSource, 0, 0, bitmapSource.getWidth(), bitmapSource.getHeight(), matrix, true);


        mGPUImage = new GPUImage(getBaseContext());
        mGPUImage.setFilter(new GPUImageSobelEdgeDetection());
        mGPUImage.setImage(mOriginal);

        Bitmap filter1 = mGPUImage.getBitmapWithFilterApplied();
        mFilters.add(filter1);

        //mGPUImage.deleteImage();

        mGPUImage.setFilter(new GPUImageGammaFilter(5f));
        mGPUImage.setImage(mOriginal);

        Bitmap filter2 = mGPUImage.getBitmapWithFilterApplied();

        mFilters.add(mOriginal);
        mFilters.add(filter2);

        canvasView.init(mFilters, 1);

        //  holder.addView(imageView);
        holder.addView(canvasView);

        //setContentView(new CanvasView(this));
    }


    class CanvasView extends ViewGroup {


        private int mFilterPos = 0;
        private boolean isForward;
        private Bitmap mCurrentBitmap;

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

                        moveRight();
                    } else {

                        moveLeft();
                    }
                    return true;
                }

                return false;
            }

            private void moveRight() {
                isForward = false;
                if (mFilterPos > 0) {
                    mFilterPos--;
                    // currX = getMeasuredWidth();
                    // invalidate();


                    ValueAnimator va = ValueAnimator.ofInt(getMeasuredWidth(), 0);
                    va.setDuration(4000);
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            isForward = true;
                            currX = (Integer) animation.getAnimatedValue();
                            invalidate();

                        }
                    });

                    va.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mCurrentBitmap = mFilters.get(mFilterPos);
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
                isForward = true;
                if (mFilterPos < mFilters.size() - 1) {
                    mFilterPos++;


                    ValueAnimator va = ValueAnimator.ofInt(getMeasuredWidth(), 0);
                    va.setDuration(4000);
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            currX = (Integer) animation.getAnimatedValue();
                            invalidate();

                        }
                    });
                    va.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mCurrentBitmap = mFilters.get(mFilterPos);
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


            canvas.drawBitmap(mCurrentBitmap, 0, 0, paint0);

            // canvas.drawBitmap(mOriginal, null,rectOrigin, paint0);
            Rect rectSrc = null;
            Rect rectDest = null;

            if (isForward) {

                rectSrc = new Rect(0, 0, currX, getMeasuredHeight());
                rectDest = new Rect(0, 0, currX, getMeasuredHeight());
            } else {
                rectSrc = new Rect(currX, 0, getMeasuredWidth(), getMeasuredHeight());
                rectDest = new Rect(currX, 0, getMeasuredWidth(), getMeasuredHeight());
            }

            canvas.drawBitmap(mFilters.get(mFilterPos), rectSrc, rectDest, paint1);


            canvas.restore();
        }


        private int currX = 0;
        Paint paint0;
        Paint paint1;
        private List<Bitmap> mFilters;

        public CanvasView(Context context) {
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

            mCurrentBitmap = mFilters.get(mFilterPos);


        }
    }
}