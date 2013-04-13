package com.example.digitalmeasuringtape;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;

public class Calibrate extends Activity implements SensorEventListener, Runnable { 
	AlertDialog dialog;
	public CountDownLatch gate;
	public SharedPreferences sPrefs;
	public TailLinkedList measurements;
	private String pi_string;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
		Thread thread = new Thread(this);
		thread.start();
        
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("00:05").setTitle("CALIBRATING");
		dialog = builder.create();
		dialog.show();
		
		new CountDownTimer(6000,1000){
			@Override
			public void onTick(long millisUntilFinished){
				dialog.setMessage("00:" + (millisUntilFinished/1000));
			}
			
			@Override
			public void onFinish(){
				dialog.dismiss();
				System.out.println("calibration finished. dismiss and countdown");
				finish();
			}
		}.start();
//        setContentView(R.layout.calibrate);
		
    }
    
	public void calibrate()
	{	
		//setting up sensor managers
		SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		System.out.println("Calibrate");
		//make a fresh list, set gate as closed, register listener
		measurements = new TailLinkedList();
		boolean worked = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);	
		System.out.println("Return from registerlistener: " + worked );
		
		try {
			Thread.sleep(4000);
			System.out.println("after sleep");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mSensorManager.unregisterListener(this, mAccelerometer);
		measurements.unravel();
		ArrayList<Float> xData = measurements.xData;
		ArrayList<Float> yData = measurements.yData;
		ArrayList<Float> zData = measurements.zData;
		
		float xAvg = 0, yAvg = 0, zAvg = 0;
		
		for(int i = 0; i < xData.size(); i ++)
		{
			xAvg += xData.get(i);
			yAvg += yData.get(i);
			zAvg += zData.get(i);
		}
		
		xAvg /= xData.size();
		yAvg /= yData.size();
		zAvg /= zData.size();
		
		System.out.println("Gravity_x: " + xAvg);
		System.out.println("Gravity_y: " + yAvg);
		System.out.println("Gravity_z: " + zAvg);
		
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putFloat("Gravity_x", xAvg);
		editor.putFloat("Gravity_y", yAvg);	
		editor.putFloat("Gravity_z", zAvg);		
		editor.commit();
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
			System.out.println("Calibrate: Accel Sensor changed");
			float x=0;
			float y=0;
			float z=0;
			
			x = event.values[0]; 
			if (sPrefs.getBoolean("MeasureY", false)) y = event.values[1];
			if (sPrefs.getBoolean("MeasureZ", false)) z = event.values[2];
			long t = event.timestamp; 
			
			if (!sPrefs.getBoolean("MeasureY", false)) measurements.add(t, 0, x);
			else if (!sPrefs.getBoolean("MeasureZ", false)) measurements.add(t, 0, x, y);
			else measurements.add(t, 0, x, y, z);
	}	

	@Override
    public void run()
    {
    	System.out.println("running thread");
		calibrate();    	
		
		//save calibrated setting
		sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putBoolean("calibrated_pref_check", true);
		editor.commit();
		
		System.out.println("after calibrate");
    }
}