package com.example.digitalmeasuringtape;

import android.app.Activity;
import android.app.ProgressDialog;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView) this.findViewById(R.id.text);
		tv.setText("--");

		
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
		while (activeThread){
			pi_string = (Math.floor((Math.random()*5)+2))+ "\"";
			//signal the outside world
			handler.sendEmptyMessage(0);
		}
	}
	
	// manages user touching the screen
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // we set the activeThread boolean to false,
            // forcing the loop from the Thread to end
            activeThread = false;
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
	

}
