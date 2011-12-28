package uk.co.jarofgreen.JustADamnCompass;

import uk.co.jarofgreen.JustADamnCompass.AboutActivity;
import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.about:
        	Intent i = new Intent(this, AboutActivity.class);            	
        	startActivity(i);            
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
}