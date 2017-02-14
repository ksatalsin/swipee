package s.com.android.swipe;

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
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageChromaKeyBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelEdgeDetection;

import static android.R.attr.bitmap;

public class MainActivity extends AppCompatActivity {

    private GPUImage mGPUImage;
    ImageView imageView;
    private CanvasView canvasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout holder = (RelativeLayout) findViewById(R.id.activity_main);


        final GestureDetector gdt = new GestureDetector(new GestureListener());

        holder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {


                //drawFilter((int)event.getRawX());

                gdt.onTouchEvent(event);
                return true;
            }


        });

        imageView = new ImageView(getBaseContext());
        canvasView = new CanvasView(this);
        canvasView.setBackgroundColor(456578);
        holder.addView(imageView);
        holder.addView(canvasView);

        //setContentView(new CanvasView(this));
    }

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 25;
        private static final int SWIPE_VELOCITY_THRESHOLD = 5;
        boolean isLeftActive = false;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) >
                    SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {

                // change picture to
                if (distanceX > 0) {
                    // start left increment
                   isLeftActive = true;


                    if(e2.getAction()==MotionEvent.ACTION_DOWN) {
                       // drawFilter((int) e2.getX());
                    }else{
                        drawFilter((int) 540);
                    }

                } else {  // the left

                    isLeftActive = false;
                    // start right increment
                    drawFilter((int) 0);
                    // drawFilter((int)distanceX);
                }
            }
            if(e2.getAction()==MotionEvent.ACTION_DOWN) {
               // isLeftActive = false;
            }

            if(e2.getAction()==MotionEvent.ACTION_UP) {
                isLeftActive = false;

            }


           /* if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Right to left
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Left to right
            }

            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Bottom to top
            }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Top to bottom
            }*/
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX1, float distanceY) {

            if (Math.abs(distanceY) > 2 || Math.abs(distanceX1) > 2) {
                if(Math.abs(distanceX1) > Math.abs(distanceY)) {

                    if (distanceX1 < 0) {
                            drawFilter((int) e2.getX());
                    }
                }
            }


            return false;
        }

    }

    private void drawFilter(int distanceX) {
        canvasView.setCurrX(distanceX);
        canvasView.invalidate();
    }


    class CanvasView extends ViewGroup  {

        Bitmap filter,
                original;



        int currX = 0;
        Paint paint0;
        Paint paint1;

        public void setCurrX(int x) {
            this.currX = x;
        }

        public CanvasView(Context context) {
            super(context);
            paint0 = new Paint();
            paint1 = new Paint();

            paint0.setAlpha(255);
            paint1.setAlpha(128);


            paint1.setAntiAlias(true);
            paint1.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint1.setFlags(Paint.FILTER_BITMAP_FLAG);

            paint0.setAntiAlias(true);
            paint0.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint0.setFlags(Paint.FILTER_BITMAP_FLAG);
        

            Bitmap bitmapSource = BitmapFactory.decodeResource(getResources(), R.drawable.cat_puzzle3);
            Matrix matrix = new Matrix();
            original = Bitmap.createBitmap(bitmapSource, 0, 0, bitmapSource.getWidth(), bitmapSource.getHeight(), matrix, true);

            Matrix matrix1 = new Matrix();
            matrix1.postRotate(180f);

            mGPUImage = new GPUImage(context);
            mGPUImage.setFilter(new GPUImageSobelEdgeDetection());
            mGPUImage.setImage(original);

            filter = mGPUImage.getBitmapWithFilterApplied();
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        protected void onDraw(Canvas canvas) {

            drawFilter(canvas,currX);
        }

        private void drawFilter(Canvas canvas, int currX) {

            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            canvas.save();
            canvas.drawBitmap(original, 0, 0, paint0);

            Rect rectSrc = new Rect(0, 0, currX, filter.getHeight());
            Rect rectDest = new Rect(0, 0, currX, filter.getHeight());

            canvas.drawBitmap(filter, rectSrc, rectDest, paint1);
            canvas.restore();
        }
    }
}