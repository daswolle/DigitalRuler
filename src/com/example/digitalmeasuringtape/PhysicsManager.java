package com.example.digitalmeasuringtape;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.util.FloatMath;

public class PhysicsManager {

		MainActivity main;
		SharedPreferences settings;
	
		public PhysicsManager(MainActivity main)
		{
			this.main = main;
			settings = main.sPrefs;
		}
		
	@SuppressWarnings("unused")
	
		public float Distance(	ArrayList<Float> x_accel, 
								ArrayList<Float> y_accel,
								ArrayList<Float> t)
		{
			float distance;
			//distance = Eulers(x_accel, y_accel, t);
			//distance = Improved_Eulers(x_accel, y_accel, t);
			distance = Simpsons(x_accel, y_accel, t);
			System.out.println("Just calculated distance of: " + distance);
			System.out.println("x: "+x_accel);
			System.out.println("y: "+y_accel);
			System.out.println("t: "+t);
			return distance;
		}

		public float Eulers(	ArrayList<Float> x_accel, 
								ArrayList<Float> y_accel, 
								ArrayList<Float> t)
		{
			if(t == null) return -1;
			
			System.out.println("Entering Eulers Distance method");
			System.out.println("x: "+x_accel);
			System.out.println("y: "+y_accel);
			System.out.println("t: "+t);
			
			//This is the Euler's method.
			ArrayList<Float> dx_veloc = new ArrayList<Float>(); 
			ArrayList<Float> dy_veloc = new ArrayList<Float>();
			
			ArrayList<Float> x_veloc = new ArrayList<Float>(); x_veloc.add(0f);
			ArrayList<Float> y_veloc = new ArrayList<Float>(); y_veloc.add(0f);
			
			//compose velocity
			final int STEPS = t.size();
			float dt;
			System.out.println("Composing Velocity from Acceleration...\n");
			for( int i = 0; i < STEPS-1; i++ )
			{	
				//x'_i = x''_(i-1) * dt
				//y'_i = y''_(i-1) * dt
				dt = t.get(i+1) - t.get(i);
				dx_veloc.add(  x_accel.get(i) * dt);
				dy_veloc.add(  y_accel.get(i) * dt);
				System.out.println("Step: " + i + "\ndt: " + dt + "\n\tv_x:"+ dx_veloc.get(i) + "\n\tv_y: " + dy_veloc.get(i));
			}
			float temp = 0f;
			for(float d : dx_veloc)
				{
					temp += d;
					x_veloc.add(temp);
				}
			
			temp = 0f;
			for(float d : dy_veloc)
				{
					temp += d;
					y_veloc.add(temp);
				}

			
			ArrayList<Float> dx_disp = new ArrayList<Float>();
			ArrayList<Float> dy_disp = new ArrayList<Float>();
			
			ArrayList<Float> x_disp = new ArrayList<Float>(); x_disp.add(0f);
			ArrayList<Float> y_disp = new ArrayList<Float>(); y_disp.add(0f);
			
			//compose displacement
			System.out.println("Composing Displacement from Velocity...\n");
			for( int i = 0; i < STEPS-1; i++ )
			{	
				//x_i = x'_(i-1) * dt
				//y_i = y'_(i-1) * dt
				dt = t.get(i+1) - t.get(i);
				dx_disp.add( x_veloc.get(i) * dt);
				dy_disp.add( y_veloc.get(i) * dt);
				
				System.out.println("Step: " + i + "\ndt: " + dt + "\n\td_x:"+ dx_disp.get(i) + "\n\td_y: " + dy_disp.get(i));
			}
			
			//compose total displacement
			float distance = 0;
	
			if( true/*Euclidean_Distance_Mode */)
			{
				//vector addition, constructing R
				System.out.println("Composing R...\n");
				float r[] = new float[2]; //[x, y]
				for( int i = 0; i < STEPS-1; i++)
				{
					r[0] += dx_disp.get(i);
					r[1] += dy_disp.get(i);
					System.out.println("Step: " + i + "\n\tr_x: "+ r[0] + "\n\tr_y: " + r[1]);
				}
			
				//Distance formula, constructing D
				//D = sqrt(X^2 + Y^2 + Z^2)
				distance =  FloatMath.sqrt( 
								(float)Math.pow(r[0], 2) + 
								(float)Math.pow(r[1], 2) 
								);
				return distance;
			}
	
			else if ( false /*Path_Distance_Mode */)
			{
				//sum up individual distances, constructing D
				for( int i = 0; i < STEPS; i++)
				{
					//dD = sqrt( dx^2 + dy^2 + dz^2 )
					distance += Math.sqrt(
									Math.pow(dx_disp.get(i), 2) +
									Math.pow(dy_disp.get(i), 2) 
									);
				}		
				return distance;
			}  
		return 0; //won't get here.
	}

		public float Improved_Eulers (	ArrayList<Float> x_accel, 
										ArrayList<Float> y_accel,
										ArrayList<Float> t)
		{	
			if(t == null) return -1;
			
			System.out.println("Entering Improved Eulers Distance method");
			
			ArrayList<Float> dx_veloc = new ArrayList<Float>(); 
			ArrayList<Float> dy_veloc = new ArrayList<Float>();
			
			ArrayList<Float> x_veloc = new ArrayList<Float>(); x_veloc.add(0f);
			ArrayList<Float> y_veloc = new ArrayList<Float>(); y_veloc.add(0f);
			
			int STEPS = x_accel.size();
			float dt;
			
			for(int i = 0; i < STEPS-1; i++)
			{
				//dx'_i = dx''_(i-1) * dt + (.5) * dt * (dx''(i) - dx''(i-1))
				//dy'_i = dy''_(i-1) * dt + (.5) * dt * (dy''(i) - dy''(i-1))

				dt = t.get(i+1) - t.get(i);
				dx_veloc.add(  x_accel.get(i) * dt + (.5f) * dt * (x_accel.get(i+1) - x_accel.get(i)));
				dy_veloc.add(  y_accel.get(i) * dt + (.5f) * dt * (y_accel.get(i+1) - y_accel.get(i)));
			
				System.out.println("Step: " + i + "\ndt: " + dt + "\n\tv_x:"+ dx_veloc.get(i) + "\n\tv_y: " + dy_veloc.get(i));
			}
			
			//Sum up all delta values
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
			
			ArrayList<Float> dx_disp = new ArrayList<Float>();
			ArrayList<Float> dy_disp = new ArrayList<Float>();
			
			ArrayList<Float> x_disp = new ArrayList<Float>(); x_disp.add(0f);
			ArrayList<Float> y_disp = new ArrayList<Float>(); y_disp.add(0f);
			
			//compose displacement
			STEPS = x_veloc.size();
			System.out.println("Composing Displacement from Velocity...\n");
			for( int i = 0; i < STEPS-1; i++ )
			{	
				//dx_i = dx'_(i-1) * dt + (.5) * dt * (dx'(i) - dx'(i-1))
				//dy_i = dx'_(i-1) * dt + (.5) * dt * (dy'(i) - dy'(i-1))
				dt = t.get(i+1) - t.get(i);
				dx_disp.add( x_veloc.get(i) * dt + (.5f) * dt * (x_veloc.get(i+1) - x_veloc.get(i)));
				dy_disp.add( y_veloc.get(i) * dt + (.5f) * dt * (y_veloc.get(i+1) - y_veloc.get(i)));
				
				System.out.println("Step: " + i + "\ndt: " + dt + "\n\td_x:"+ dx_disp.get(i) + "\n\td_y: " + dy_disp.get(i) );
			}
			

			//compose total displacement
			float distance = 0;
	
			if( true/*Euclidean_Distance_Mode */)
			{
				//vector addition, constructing R
				System.out.println("Composing R...\n");
				float r[] = new float[2]; //[x, y]
				for( int i = 0; i < STEPS-1; i++)
				{
					r[0] += dx_disp.get(i);
					r[1] += dy_disp.get(i);
					System.out.println("Step: " + i + "\n\tr_x: "+ r[0] + "\n\tr_y: " + r[1]);
				}
			
				//Distance formula, constructing D
				//D = sqrt(X^2 + Y^2 )
				distance =  FloatMath.sqrt( 
								(float)Math.pow(r[0], 2) + 
								(float)Math.pow(r[1], 2) 
								);
				return distance;
			}
	
			else if ( false /*Path_Distance_Mode */)
			{
				//sum up individual distances, constructing D
				for( int i = 0; i < STEPS-1; i++)
				{
					//dD = sqrt( dx^2 + dy^2 )
					distance += Math.sqrt(
									Math.pow(dx_disp.get(i), 2) +
									Math.pow(dy_disp.get(i), 2)
									);
				}		
				return distance;
			}  
			
			return 0;
		}
	
		public float Simpsons (	ArrayList<Float> x_accel, 
								ArrayList<Float> y_accel, 
								ArrayList<Float> t)
		{
			
			if(t == null) return -1;
			
			System.out.println("Entering Simpsons Distance method");
			
			ArrayList<Float> dx_veloc = new ArrayList<Float>(); 
			ArrayList<Float> dy_veloc = new ArrayList<Float>();
			
			ArrayList<Float> x_veloc = new ArrayList<Float>(); x_veloc.add(0f);
			ArrayList<Float> y_veloc = new ArrayList<Float>(); y_veloc.add(0f);
			
			//compose velocity
			int STEPS = x_accel.size();
			
		
			float 	dt, half_dt,
			
					k1_x, k2_x, k3_x,
					k1_y, k2_y, k3_y,
			
					dv_x, dv_y, dv_z
					;
				
			System.out.println("Composing Velocity from Acceleration...\n");
			for( int i = 0; i < STEPS-1; i++ )
			{	
				/*
				 * dF = (k1 + 4 * k2 + k3) / 6;
				 *
				 *	k1 = Left_Eulers_Increment(F) = f"(t)*(dt)
				 *	k2 = Midpoint_Increment(F) = (f"(t) + f"(t+dt)) / 2 *(dt)
				 *	k3 = Right_Eulers_Increment(F+dt) = f"(t+dt) * (dt)
				 *
				 */
				
				
				dt = t.get(i+1) - t.get(i);
				
				//Left_Eulers_Increment
				k1_x = x_accel.get(i) * dt;
				k1_y = y_accel.get(i) * dt;
				
				//Midpoint_Increment
				k2_x = (x_accel.get(i) + x_accel.get(i+1)) / 2  * dt ;
				k2_y = (y_accel.get(i) + y_accel.get(i+1)) / 2  * dt ;
				
				//Right_Eulers_Increment
				k3_x = x_accel.get(i+1) * dt;
				k3_y = y_accel.get(i+1) * dt;
				
				dv_x = (1f/6f) * (k1_x + 4 * k2_x + k3_x );
				dv_y = (1f/6f) * (k1_y + 4 * k2_y + k3_y );
						
				dx_veloc.add(  dv_x );
				dy_veloc.add(  dv_y );
				
				
				System.out.println("Step: " + i
									+ "\ndt: " + dt
									+ "\n\tv_x:"+ dx_veloc.get(i)
									+ "\n\tv_y: " + dy_veloc.get(i)
									);
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
			
			ArrayList<Float> dx_disp = new ArrayList<Float>(); 
			ArrayList<Float> dy_disp = new ArrayList<Float>();
			
			System.out.println("Composing Displacement from Velocity...\n");
			STEPS = x_veloc.size();
			
			float dD_x, dD_y, dD_z;
			for( int i = 0; i < STEPS-1; i++ )
			{	
				
				/*
				 * dF = (k1 + 4 * k2 + k3) / 6;
				 *
				 *	k1 = Left_Eulers_Increment(F) = f"(t)*(dt)
				 *	k2 = Midpoint_Increment(F) = (f"(t) + f"(t+dt)) / 2 *(dt)
				 *	k3 = Right_Eulers_Increment(F+dt) = f"(t+dt) * (dt)
				 *
				 */

				dt = t.get(i+1) - t.get(i);
				
				//Left_Eulers_Increment
				k1_x = x_veloc.get(i) * dt;
				k1_y = y_veloc.get(i) * dt;
				
				//Midpoint_Increment
				k2_x = (x_veloc.get(i) + x_veloc.get(i+1)) / 2  * dt ;
				k2_y = (y_veloc.get(i) + y_veloc.get(i+1)) / 2  * dt ;
				
				//Right_Eulers_Increment
				k3_x = x_veloc.get(i+1) * dt;
				k3_y = y_veloc.get(i+1) * dt;
				
				dD_x = (1f/6f) * (k1_x + 4 * k2_x + k3_x );
				dD_y = (1f/6f) * (k1_y + 4 * k2_y + k3_y );
						
				dx_disp.add(  dD_x );
				dy_disp.add(  dD_y );
				
				
				System.out.println("Step: " + i 
									+ "\ndt: " + dt
									+ "\n\td_x:"+ dx_disp.get(i)
									+ "\n\td_y: " + dy_disp.get(i)
									);
			}	
			
			//compose total displacement
			float distance = 0;
			STEPS = dx_disp.size();
			if( true/*Euclidean_Distance_Mode */)
			{
				//vector addition, constructing R
				System.out.println("Composing R...\n");
				float r[] = new float[2]; //[x, y]
				for( int i = 0; i < STEPS; i++)
				{
					r[0] += dx_disp.get(i);
					r[1] += dy_disp.get(i);
				}
			
				//Distance formula, constructing D
				//D = sqrt(X^2 + Y^2 + Z^2)
				distance =  FloatMath.sqrt( 
								(float)Math.pow(r[0], 2) + 
								(float)Math.pow(r[1], 2) 
								);
				return distance;
			}
	
			else if ( false /*Path_Distance_Mode */)
			{
				//sum up individual distances, constructing D
				for( int i = 0; i < STEPS; i++)
				{
					//dD = sqrt( dx^2 + dy^2 + dz^2 )
					distance += Math.sqrt(
									Math.pow(dx_disp.get(i), 2) +
									Math.pow(dy_disp.get(i), 2)
									);
				}		
				return distance;
			}  
		return 0; //won't get here.
		}

		public void RemoveGravity(  		ArrayList<Float> xData,
											ArrayList<Float> yData,
											ArrayList<Float> zData
											)
		{
			//Very basic. Simply subtracts calibrated gravity from all measurements
			
			System.out.println("Entering RemoveGravity");
				
			int count = xData.size(); //we assume all array lists are the same size because of their construction
			
			float Gx = settings.getFloat("Gravity_x", 0);
			float Gy = settings.getFloat("Gravity_y", 0);
			float Gz = settings.getFloat("Gravity_z", 0);
			
			for(int i=0; i < count; i ++)
			{
				xData.set(i, xData.get(i)-Gx);
				yData.set(i, yData.get(i)-Gy);
				zData.set(i, zData.get(i)-Gz);
			}
			
			
		}
<<<<<<< HEAD
		
		public float[] Rotate(float x, float y, float z, float theta, float phi, float psi )
		{
			double cosTheta = Math.cos(theta);
			double sinTheta = Math.sin(theta);
			double cosPsi = Math.cos(psi);
			double sinPsi = Math.sin(psi);
			double cosPhi = Math.cos(phi);
			double sinPhi = Math.sin(phi);
			
			double[][] arr_XYZ = 
				{
					{x},
					{y},
					{z}
				};
			
			double[][] arr_R =	
				{	
					{cosTheta * cosPsi,		sinPhi*sinTheta * cosPsi - cosPhi * sinPsi,		cosPhi * sinTheta * cosPsi + sinPhi *sinPsi},
					{cosTheta * sinPsi,		sinPhi * sinTheta * sinPsi + cosPhi *cosPsi,	cosPhi*sinTheta*sinPsi - sinPhi*cosPsi},
					{-sinTheta,				sinPhi * cosTheta,								cosPhi * cosTheta}	
				};
					
			Matrix XYZ = new Matrix(arr_XYZ);
			Matrix R = new Matrix(arr_R);
			//R = R.transpose();
			
			Matrix Rotated = R.times(XYZ);
			
			//not done
			float[] rotated = new float[3];
			rotated[0] = (float)Rotated.get(0,0);
			rotated[1] = (float)Rotated.get(1, 0);
			rotated[2] = (float)Rotated.get(2, 0);
			return rotated;
		}
		
		public void removeOutliers(ArrayList<Float> xData, ArrayList<Float> yData) {
			
			for(int i = 1; i < xData.size(); i++) {
				//TODO: check i-1, i, and i+1, decide if its an outlier, change it if it is.
				
			}
			
		}
=======
>>>>>>> ab8b1560066afd8305907d5a74ee8189ed58a1df

}
