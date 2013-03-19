package com.example.digitalmeasuringtape;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable, SensorEventListener{

	private String pi_string;
	private TextView tv;
	private ProgressDialog pd;
	private boolean activeThread = true;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	public TailLinkedList measurements;
	public Object semaphore;
	public CountDownLatch gate; //things call gate.await(), and get blocked.
								//things become unblocked when gate.countDown()
								//is called enough times, which will be 1
	
	protected void onExit()
	{
		if(mSensorManager != null)
			mSensorManager.unregisterListener(this);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView) this.findViewById(R.id.text1);
		tv.setText("--");
		
		//setting up sensor managers
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		semaphore = new Object();
		
		//TODO tv.setText(mAccelerometer.getMinDelay());
		
		
	}
	
	//temporary to make sure this app isn't the one draining my battery...
	@Override
	protected void onStop(){
		super.onStop();
		onDestroy();
	}
	
	//connected to button's onClick
	public void start_distance_process(View view){
		//false below is for cancleable; may need to change
		//pd = ProgressDialog.show(this, "Working..", "Sucking on balls...", true, false);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("FINIZH", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				//TODO when user clicks
				activeThread = false;
				if(gate!=null)
	            	gate.countDown(); 	
			}
		});
		builder.setMessage("WERKING").setTitle("TWERKING");
		AlertDialog dialog = builder.create();
		
		dialog.show();
		
		System.out.println("Started distance process.");
		Thread thread = new Thread(this);
		thread.start();
	}
	
/************menu stuff**************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
//		case R.id.about_menuitem:
//			startActivity(new Intent(this, About.class));
		case R.id.settings_menuitem:
			startActivity(new Intent(this, Settings.class));
		}
		return true;
	}
	
/***********end menu stuff***********/	
	
	//put the code to be run during execution here.
	//this can be thought of as the main method of our thread.
	public void run(){
		
		System.out.println("Calling run()");
		
		//make a fresh list, set gate as closed, register listener
		measurements = new TailLinkedList();
		gate = new CountDownLatch(1);
		boolean worked = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);	
		System.out.println("Return from registerlistener: " + worked);
		List<Sensor> l = mSensorManager.getSensorList(Sensor.TYPE_ALL);
		for(Sensor s : l)
			System.out.println(s.getName());
		//Wait until the stop-measuring-signal. In the mean time,
		//onSensorChanged events should be firing and measuring.
		
		try {
			gate.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		System.out.println("Before while");
		while(activeThread)
		{}
		System.out.println("After while");
		*/
		//stop measuring
		mSensorManager.unregisterListener(this);
		
		double d = Distance(measurements.getxData(), 
						measurements.getyData(),
						measurements.getzData(),
						measurements.gettData());
		
		//d.toString(), then truncate to two decimal places
		String truncate;
		if(d == -1.0) truncate = "-1.0"; 
		else
		{
			String d_str = Double.valueOf(d).toString(); 
			truncate = d_str.substring(0, d_str.indexOf('.') + 3);
		}
		pi_string = truncate;
		handler.sendEmptyMessage(0);
		//pd.dismiss();
		System.out.println(truncate);
		System.out.println("returning from run()");
		}
	
	
	// manages user touching the screen
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("onTouchEvent fired");
    	
        if (activeThread && event.getAction() == MotionEvent.ACTION_DOWN) {
            // we set the activeThread boolean to false,
            // forcing the loop from the Thread to end
            activeThread = false;
            if(gate!=null)
            	gate.countDown(); 	//causes the thread's "run" method to contine.
            						//"opens the gate"
        }
        
        return super.onTouchEvent(event);
    }
    
	// manages user touching the screen
    public boolean stopMeasuring(MotionEvent event) {
        
        if (activeThread && event.getAction() == MotionEvent.ACTION_DOWN) {
            // we set the activeThread boolean to false,
            // forcing the loop from the Thread to end
            activeThread = false;
            gate.countDown(); //causes the thread's "run" method to contine.
            					//"opens the gate"
        }
        
        return super.onTouchEvent(event);
    }
	
	//Receive thread messages, interpret them and act as needed
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message mg){
			//pd.dismiss();
			tv.setText(pi_string);
		}
	};
	
	@SuppressWarnings("unused")
	public static float Distance(	ArrayList<Float> x_accel, 
									ArrayList<Float> y_accel, 
									ArrayList<Float> z_accel,
									ArrayList<Float> t)
	{
		if(t == null) return -1;
		
		System.out.println("Entering Distance method");
		System.out.println("x: "+x_accel);
		System.out.println("y: "+y_accel);
		System.out.println("z: "+z_accel);
		System.out.println("t: "+t);
		
		//This is the Euclid's method.
		ArrayList<Float> dx_veloc = new ArrayList<Float>(); 
		ArrayList<Float> dy_veloc = new ArrayList<Float>();
		ArrayList<Float> dz_veloc = new ArrayList<Float>();
		
		ArrayList<Float> x_veloc = new ArrayList<Float>(); x_veloc.add(0f);
		ArrayList<Float> y_veloc = new ArrayList<Float>(); y_veloc.add(0f);
		ArrayList<Float> z_veloc = new ArrayList<Float>(); z_veloc.add(0f);
		
		//compose velocity
		int I = t.size();
		float dt;
		System.out.println("Composing Velocity from Acceleration...\n");
		for( int i = 0; i < I-1; i++ )
		{	
			//x'_i = x''_(i-1) * dt
			//y'_i = y''_(i-1) * dt
			//z'_i = z''_(i-1) * dt
			dt = t.get(i+1) - t.get(i);
			dx_veloc.add(  x_accel.get(i) * dt);
			dy_veloc.add(  y_accel.get(i) * dt);
			dz_veloc.add(  z_accel.get(i) * dt);
			System.out.println("Step: " + i + "\ndt: " + dt + "\n\tv_x:"+ dx_veloc.get(i) + "\n\tv_y: " + dy_veloc.get(i) + "\n\tv_z: " + dz_veloc.get(i));
		}
		float temp = 0f;
		for(float d : dx_veloc)
			{
				temp += d;
				x_veloc.add(temp);
			}
		
		temp = 0;
		for(float d : dy_veloc)
			{
				temp += d;
				y_veloc.add(temp);
			}
		
		temp = 0;
		for(float d : dz_veloc)
			{
				temp += d;
				z_veloc.add(temp);
			}
		
		ArrayList<Float> dx_disp = new ArrayList<Float>();
		ArrayList<Float> dy_disp = new ArrayList<Float>();
		ArrayList<Float> dz_disp = new ArrayList<Float>();
		
		ArrayList<Float> x_disp = new ArrayList<Float>(); x_disp.add(0f);
		ArrayList<Float> y_disp = new ArrayList<Float>(); y_disp.add(0f);
		ArrayList<Float> z_disp = new ArrayList<Float>(); z_disp.add(0f);
		
		//compose displacement
		I = t.size();
		System.out.println("Composing Displacement from Velocity...\n");
		for( int i = 0; i < I-1; i++ )
		{	
			//x_i = x'_(i-1) * dt
			//y_i = y'_(i-1) * dt
			//z_i = z'_(i-1) * dt
			dt = t.get(i+1) - t.get(i);
			dx_disp.add( x_veloc.get(i) * dt);
			dy_disp.add( y_veloc.get(i) * dt);
			dz_disp.add( z_veloc.get(i) * dt);
			
			System.out.println("Step: " + i + "\ndt: " + dt + "\n\td_x:"+ dx_disp.get(i) + "\n\td_y: " + dy_disp.get(i) + "\n\td_z: " + dz_disp.get(i));
		}
		
		//compose total displacement
		float distance = 0;

		if( true/*Euclidean_Distance_Mode */)
		{
			//vector addition, constructing R
			System.out.println("Composing R...\n");
			float r[] = new float[3]; //[x, y, z]
			for( int i = 0; i < I-1; i++)
			{
				r[0] += dx_disp.get(i);
				r[1] += dy_disp.get(i);
				r[2] += dz_disp.get(i);
				System.out.println("Step: " + i + "\n\tr_x: "+ r[0] + "\n\tr_y: " + r[1] + "\n\tr_z: " + r[2]);
			}
		
			//Distance formula, constructing D
			//D = sqrt(X^2 + Y^2 + Z^2)
			distance =  FloatMath.sqrt( 
							(float)Math.pow(r[0], 2) + 
							(float)Math.pow(r[1], 2) +
							(float)Math.pow(r[2], 2)
							);
			return distance;
		}

		else if ( false /*Path_Distance_Mode */)
		{
			//sum up individual distances, constructing D
			for( int i = 0; i < I; i++)
			{
				//dD = sqrt( dx^2 + dy^2 + dz^2 )
				distance += Math.sqrt(
								Math.pow(dx_disp.get(i), 2) +
								Math.pow(dy_disp.get(i), 2) +
								Math.pow(dz_disp.get(i), 2)
								);
			}		
			return distance;
		}  
	return 0; //won't get here.
}
	
	public void onSensorChanged(SensorEvent event) {
		
		System.out.println("Sensor changed");
		float x = event.values[0]; 
		float y = event.values[1];
		float z = event.values[2];
		pi_string = "x = " + x + "\ny = " + y + "\nz = " + z;
		System.out.println(pi_string);
		handler.sendEmptyMessage(0);
		measurements.add(x, y, z, System.currentTimeMillis()); //record values.
		
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		System.out.println("onAccuracyChanged fired");
		
	}

}
