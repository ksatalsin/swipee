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
    private FilterSwipe canvasView;

    List<Bitmap> mFilters = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout holder = (RelativeLayout) findViewById(R.id.activity_main);


//        imageView = new ImageView(getBaseContext());
        canvasView = new FilterSwipe(this);
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



}