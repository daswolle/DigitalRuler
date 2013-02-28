package com.example.digitalmeasuringtape;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable{

	private String pi_string;
	private TextView tv;
	private ProgressDialog pd;
	private boolean activeThread = true;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	public myLL measurements;
	public Object semaphore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView) this.findViewById(R.id.text);
		tv.setText("--");
		
		//setting up sensor managers
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		semaphore = new Object();
		
	}
	
	//connected to button's onClick
	public void record_measurements(View view){
		//false below is for cancleable; may need to change
		pd = ProgressDialog.show(this, "Working..", "Calculating", true, false);
		Thread thread = new Thread(this);
		thread.start();
	}
	
	//put the code to be run during execution here
	public void run(){
		
		//make a fresh list, register listener
		measurements = new myLL();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);	
		
		//Wait until measuring is stopped. In the mean time,
		//onSensorChanged events should be firing and measuring.
		while(activeThread)
			semaphore.wait();
		
		//stop measuring
		mSensorManager.unregisterListener(this);
		
		return Distance(measurements.getxData(), 
						measurements.getyData(),
						measurements.getzData(),
						measurements.gettData());
		
	}
	
	// manages user touching the screen
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (activeThread && event.getAction() == MotionEvent.ACTION_DOWN) {
            // we set the activeThread boolean to false,
            // forcing the loop from the Thread to end
            activeThread = false;
            semaphore.notify();
        }
        return super.onTouchEvent(event);
    }
	
	//Receive thread messages, interpret them and act as needed
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message mg){
			pd.dismiss();
			tv.setText(pi_string);
		}
	};
	
	public static double Distance(	ArrayList<Double> x_accel, 
									ArrayList<Double> y_accel, 
									ArrayList<Double> z_accel,
									ArrayList<Double> t)
	{
		//This is the Euclid's method.
		ArrayList<Double> dx_veloc = new ArrayList<Double>(); dx_veloc.add(0.0);
		ArrayList<Double> dy_veloc = new ArrayList<Double>(); dy_veloc.add(0.0);
		ArrayList<Double> dz_veloc = new ArrayList<Double>(); dz_veloc.add(0.0);
		
		ArrayList<Double> x_veloc = new ArrayList<Double>(); x_veloc.add(0.0);
		ArrayList<Double> y_veloc = new ArrayList<Double>(); y_veloc.add(0.0);
		ArrayList<Double> z_veloc = new ArrayList<Double>(); z_veloc.add(0.0);
		
		//compose velocity
		int I = x_accel.size();
		double dt;
		System.out.println("Composing Velocity...\n");
		for( int i = 1; i < I; i++ )
		{	
			//x'_i = x''_(i-1) * dt
			//y'_i = y''_(i-1) * dt
			//z'_i = z''_(i-1) * dt
			dt = t.get(i) - t.get(i-1);
			dx_veloc.add(  x_accel.get(i-1) * dt);
			dy_veloc.add(  y_accel.get(i-1) * dt);
			dz_veloc.add(  z_accel.get(i-1) * dt);
			System.out.println("Step: " + i + "\n\tv_x: "+ dx_veloc.get(i) + "\n\tv_y: " + dy_veloc.get(i) + "\n\tv_z: " + dz_veloc.get(i));
		}
		double temp = 0;
		for(double d : dx_veloc)
			{
				temp += d;
				x_veloc.add(temp);
			}
		
		temp = 0;
		for(double d : dy_veloc)
			{
				temp += d;
				y_veloc.add(temp);
			}
		
		temp = 0;
		for(double d : dz_veloc)
			{
				temp += d;
				z_veloc.add(temp);
			}
		
		ArrayList<Double> dx_disp = new ArrayList<Double>(); dx_disp.add(0.0); dx_disp.add(0.0);
		ArrayList<Double> dy_disp = new ArrayList<Double>(); dy_disp.add(0.0); dy_disp.add(0.0);
		ArrayList<Double> dz_disp = new ArrayList<Double>(); dz_disp.add(0.0); dz_disp.add(0.0);
		
		ArrayList<Double> x_disp = new ArrayList<Double>(); x_disp.add(0.0); x_disp.add(0.0);
		ArrayList<Double> y_disp = new ArrayList<Double>(); y_disp.add(0.0); y_disp.add(0.0);
		ArrayList<Double> z_disp = new ArrayList<Double>(); z_disp.add(0.0); z_disp.add(0.0);
		
		//compose displacement
		I = t.size();
		for( int i = 2; i < I; i++ )
		{	
			//x_i = x'_(i-1) * dt
			//y_i = y'_(i-1) * dt
			//z_i = z'_(i-1) * dt
			dt = t.get(i) - t.get(i-1);
			dx_disp.add( x_veloc.get(i-1) * dt);
			dy_disp.add( y_veloc.get(i-1) * dt);
			dz_disp.add( z_veloc.get(i-1) * dt);
		}
		
		//compose total displacement
		double distance = 0;

		if( true/*Euclidean_Distance_Mode */)
		{
			//vector addition, constructing R
			System.out.println("Composing R...\n");
			double r[] = new double[3]; //[x, y, z]
			for( int i = 0; i < I; i++)
			{
				r[0] += dx_disp.get(i);
				r[1] += dy_disp.get(i);
				r[2] += dz_disp.get(i);
				System.out.println("Step: " + i + "\n\tr_x: "+ r[0] + "\n\tr_y: " + r[1] + "\n\tr_z: " + r[2]);
			}
		
			//Distance formula, constructing D
			//D = sqrt(X^2 + Y^2 + Z^2)
			distance = Math.sqrt( 
							Math.pow(r[0], 2) + 
							Math.pow(r[1], 2) +
							Math.pow(r[2], 2)
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
		
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		measurements.add(x, y, z, System.currentTimeMillis()); //record values.
		
	}
	
	public class myLL {
		  public node head;
		  public node tail;
		  
		  public myLL() {
			  node temp = new node(0,0,0,0);
			  head = temp;
			  tail = temp;
		  }
		  
		  public myLL(node firstNode) {
		   head = firstNode;
		   tail = firstNode;
		  }
		  
		  public void add(float x, float y, float z, long time) {
		   node newNode = new node(x, y, z, time);
		   tail.next = newNode;
		   tail = newNode;
		  }
		  	  
		  public ArrayList<Float> getxData() {
		   ArrayList<Float> xData = new ArrayList<Float>();
		   node trav = head;
		   while(trav.next != null) {
		    xData.add(trav.x);
		    trav = trav.next;
		   }
		   return xData;
		  }
		  
		  public ArrayList<Float> getyData() {
		   ArrayList<Float> yData = new ArrayList<Float>();
		   node trav = head;
		   while(trav.next != null) {
		    yData.add(trav.y);
		    trav = trav.next;
		   }
		   return yData;
		  }
		  
		  public ArrayList<Float> getzData() {
		   ArrayList<Float> zData = new ArrayList<Float>();
		   node trav = head;
		   while(trav.next != null) {
			   zData.add(trav.z);
			   trav = trav.next;
		   }
		   return zData;
		  	}
		  
		  public ArrayList<Float> gettData() {
			ArrayList<Float> tData = new ArrayList<Float>();
			node trav = head;
			while(trav.next != null) {
				tData.add((float) trav.time); //i dont know if its ok to cast a long as a float...
			}
			return tData;
		  }
		  
		  private class node {
		  	public float x;
		  	public float y;
		  	public float z;
		  	public long time;
		  	public node next;
		  
		 	 public node(float newX, float newY, float newZ, long newTime) {
		   		x = newX;
		   		y = newY;
		   		z = newZ;
			   time = newTime;
			   next = null;
			  }
		  
		 }
		  
		 }

}
