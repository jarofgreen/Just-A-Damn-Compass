package uk.co.jarofgreen.JustADamnCompass;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

public class MainActivity extends Activity {

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private float[] mValues;            

	private SampleView mView;

	private final SensorEventListener mListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            mValues = event.values;
            if (mView != null) mView.invalidate();
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
		
    @Override    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);        
        mView = new SampleView(this);
        setContentView(mView);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mListener, mSensor,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
    	mSensorManager.unregisterListener(mListener);
        super.onStop();
    }
    

    private class SampleView extends View {
        private Bitmap compassImage;

        public SampleView(Context context) {
            super(context);            
            compassImage = BitmapFactory.decodeResource(getResources(), R.drawable.compass);
        }

        @Override protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.BLACK);
            
            int w = canvas.getWidth();
            int h = canvas.getHeight();
            int cx = w / 2;
            int cy = h / 2;

            canvas.translate(cx, cy);
            if (mValues != null) {
                canvas.rotate(-mValues[0]);
            }
            
            canvas.drawBitmap(compassImage, -150, -150, null);

        }

    }
    
}