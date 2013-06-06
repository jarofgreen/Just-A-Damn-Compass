package uk.co.jarofgreen.JustADamnCompass;

import uk.co.jarofgreen.JustADamnCompass.AboutActivity;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private float[] mValues;            
	private float lastDirection = -360;
	private float secondLastDirection = -360;
	private SampleView mView;
	
	private final int MIN_ROTATION = 2;

	private final SensorEventListener mListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
        	
            mValues = event.values;
        	// check for min rotation or jitter

            if(Math.abs(lastDirection - mValues[0]) < MIN_ROTATION || secondLastDirection == mValues[0])
        		return;

            secondLastDirection = lastDirection;
            lastDirection = mValues[0];
        			
        	if (mView != null) mView.invalidate();
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
	private int w;
	private int h;
	private String TAG = "Compass";
	private SharedPreferences prefs;
	private static boolean invert;
		
    @Override    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);        
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        invert = prefs.getBoolean("invert", false);
        
        mView = new SampleView(this);
        mView.setBackgroundColor(invert?Color.WHITE:Color.BLACK);
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
		private Bitmap backgroundImage;
        private Bitmap compassImage;
		private int nWidth;
		private int nHeight;
		private Matrix matrix = new Matrix();
		private Matrix rotator = new Matrix();

        public SampleView(Context context) {
            super(context);            
            
            // variables
            
            Display display = getWindowManager().getDefaultDisplay();
            w = display.getWidth();
            h = display.getHeight();

            Log.d(TAG,"width: "+w+" height: "+h);
            
    		Bitmap a;
    		Bitmap b;
			if(invert) { 
    			a = BitmapFactory.decodeResource(getResources(), R.drawable.background);
    			b = BitmapFactory.decodeResource(getResources(), R.drawable.compass_black);
			}
    		else {
    			a = BitmapFactory.decodeResource(getResources(), R.drawable.background_black);
    			b = BitmapFactory.decodeResource(getResources(), R.drawable.compass_white);
    		}
            int mHeight = b.getHeight();
            int mWidth = b.getWidth();
            
    		nWidth = w;
    		nHeight = h;
    		
    		if(mHeight/mWidth > h/w) { // image skinnier than canvas
    			nWidth = (int) (mWidth*((float)h/(float)mHeight));
    		}
    		else { // image fatter than or equal to canvas
    			nHeight = (int) (mHeight*((float)w/(float)mWidth));
    		}

    		Log.d(TAG,"image width: "+nWidth+" image height: "+nHeight);

    		backgroundImage = Bitmap.createScaledBitmap(a, nWidth, nHeight, false);
    		compassImage = Bitmap.createScaledBitmap(b, nWidth, nHeight, false);
            
        }

        @Override protected void onDraw(Canvas canvas) {
    		// center
    		
            canvas.translate(0, h/2-nHeight/2);
            
            // rotate
            
            if (mValues != null) {
                rotator.setRotate(-mValues[0], nWidth/2, nHeight/2);
            }
            
            canvas.drawBitmap(backgroundImage, matrix, null);
            canvas.drawBitmap(compassImage, rotator, null);
           

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
        Intent i;
		switch (item.getItemId()) {
        case R.id.about:
        	i = new Intent(this, AboutActivity.class);            	
        	startActivity(i);            
            return true;
        case R.id.invert:
        	// invert then restart
        	Editor e = prefs.edit();
        	e.putBoolean("invert", invert?false:true);
        	e.commit();
        	
        	i = new Intent(this, MainActivity.class);
        	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(i);            
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
}