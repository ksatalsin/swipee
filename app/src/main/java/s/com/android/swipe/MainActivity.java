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
    Bitmap mOriginal;

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
        matrix.postScale(2f,2f);
        mOriginal = Bitmap.createBitmap(bitmapSource, 0, 0, bitmapSource.getWidth(), bitmapSource.getHeight(), matrix, true);



        mGPUImage = new GPUImage(getBaseContext());
        mGPUImage.setFilter(new GPUImageSobelEdgeDetection());
        mGPUImage.setImage(mOriginal);

        Bitmap filter1 = mGPUImage.getBitmapWithFilterApplied();
        mFilters.add(filter1);

        //mGPUImage.deleteImage();

        mGPUImage.setFilter(new GPUImageGammaFilter(5f));
        mGPUImage.setImage(mOriginal);

        Bitmap filter2 = mGPUImage.getBitmapWithFilterApplied();

        mFilters.add(filter2);

        canvasView.init(mFilters, mOriginal);

      //  holder.addView(imageView);
        holder.addView(canvasView);

        //setContentView(new CanvasView(this));
    }





    class CanvasView extends ViewGroup  {


        private int mFilterPos = 0;

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
                    if (distanceX > 0) {
                        // start left increment
                        isLeftActive = false;


                        if (e2.getAction() == MotionEvent.ACTION_DOWN) {

                        } else {

                            currX = getMeasuredWidth();
                            invalidate();
                            mFilterPos = 1;
                        }

                    } else {

                        isLeftActive = true;
                        currX = 0;
                        invalidate();
                    }
                }
                if (e2.getAction() == MotionEvent.ACTION_DOWN) {

                }

                if (e2.getAction() == MotionEvent.ACTION_UP) {
                    isLeftActive = false;

                }

                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX1, float distanceY) {

                if (Math.abs(distanceY) > 2 || Math.abs(distanceX1) > 2) {
                    if (Math.abs(distanceX1) > Math.abs(distanceY)) {

                       // if (isLeftActive) {
                            currX = (int) e2.getX();
                            invalidate();
                      //  }
                    }
                }
                    return false;
            }
        }


        private int currX = 0;
        Paint paint0;
        Paint paint1;
        private List<Bitmap> mFilters;
        private Bitmap mOriginal;

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
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        protected void onDraw(Canvas canvas) {

            if(mOriginal!=null)
            drawFilter(canvas,currX);
        }

        private void drawFilter(Canvas canvas, int currX) {

            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.save();
           // Rect rectOrigin = new Rect(0, 0,getMeasuredWidth(),0);
            canvas.drawBitmap(mOriginal, 0, 0, paint0);
           // canvas.drawBitmap(mOriginal, null,rectOrigin, paint0);
            Rect rectSrc = new Rect(0, 0, currX, getMeasuredHeight());
            Rect rectDest = new Rect(0, 0, currX, getMeasuredHeight());

            canvas.drawBitmap(mFilters.get(mFilterPos), rectSrc, rectDest, paint1);
            canvas.restore();
        }

        public void init(@NonNull List<Bitmap> filters, @NonNull Bitmap original) {

            this.mFilters = filters;
            this.mOriginal = original;

            final GestureDetector gdt = new GestureDetector(new GestureListener());
            this.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View view, final MotionEvent event) {
                    //drawFilter((int)event.getRawX());
                    gdt.onTouchEvent(event);
                    return true;
                }


            });


        }
    }
}