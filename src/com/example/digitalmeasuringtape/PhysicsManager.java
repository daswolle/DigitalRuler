package com.example.digitalmeasuringtape;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.util.FloatMath;

public class PhysicsManager {

		SharedPreferences settings;
	
		public PhysicsManager(MainActivity main)
		{
			settings = main.settings;
		}
		
	@SuppressWarnings("unused")
	
		public float Distance(	ArrayList<Float> x_accel, 
								ArrayList<Float> y_accel, 
								ArrayList<Float> z_accel,
								ArrayList<Float> t)
		{
			float distance;
			//distance = Eulers(x_accel, y_accel,z_accel, t);
			distance = Improved_Eulers(x_accel, y_accel,z_accel, t);
			System.out.println("Just calculated distance of: " + distance);
			System.out.println("x: "+x_accel);
			System.out.println("y: "+y_accel);
			System.out.println("z: "+z_accel);
			System.out.println("t: "+t);
			//distace = Simpsons(x_accel, y_accel, z_accel, t);
			return distance;
		}
	
		public float Eulers(	ArrayList<Float> x_accel, 
										ArrayList<Float> y_accel, 
										ArrayList<Float> z_accel,
										ArrayList<Float> t)
		{
			if(t == null) return -1;
			
			System.out.println("Entering Eulers Distance method");
			System.out.println("x: "+x_accel);
			System.out.println("y: "+y_accel);
			System.out.println("z: "+z_accel);
			System.out.println("t: "+t);
			
			//This is the Euler's method.
			ArrayList<Float> dx_veloc = new ArrayList<Float>(); 
			ArrayList<Float> dy_veloc = new ArrayList<Float>();
			ArrayList<Float> dz_veloc = new ArrayList<Float>();
			
			ArrayList<Float> x_veloc = new ArrayList<Float>(); x_veloc.add(0f);
			ArrayList<Float> y_veloc = new ArrayList<Float>(); y_veloc.add(0f);
			ArrayList<Float> z_veloc = new ArrayList<Float>(); z_veloc.add(0f);
			
			//compose velocity
			final int STEPS = t.size();
			float dt;
			System.out.println("Composing Velocity from Acceleration...\n");
			for( int i = 0; i < STEPS-1; i++ )
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
			System.out.println("Composing Displacement from Velocity...\n");
			for( int i = 0; i < STEPS-1; i++ )
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
				for( int i = 0; i < STEPS-1; i++)
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
								(float)Math.pow(r[1], 2) //+
								//(float)Math.pow(r[2], 2)
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
									Math.pow(dy_disp.get(i), 2) +
									Math.pow(dz_disp.get(i), 2)
									);
				}		
				return distance;
			}  
		return 0; //won't get here.
	}

		public float Improved_Eulers (	ArrayList<Float> x_accel, 
										ArrayList<Float> y_accel, 
										ArrayList<Float> z_accel,
										ArrayList<Float> t)
		{	
			if(t == null) return -1;
			
			System.out.println("Entering Improved Eulers Distance method");
			
			ArrayList<Float> dx_veloc = new ArrayList<Float>(); 
			ArrayList<Float> dy_veloc = new ArrayList<Float>();
			ArrayList<Float> dz_veloc = new ArrayList<Float>();
			
			ArrayList<Float> x_veloc = new ArrayList<Float>(); x_veloc.add(0f);
			ArrayList<Float> y_veloc = new ArrayList<Float>(); y_veloc.add(0f);
			ArrayList<Float> z_veloc = new ArrayList<Float>(); z_veloc.add(0f);
			
			int STEPS = x_accel.size();
			float dt;
			
			for(int i = 0; i < STEPS-1; i++)
			{
				//dx'_i = dx''_(i-1) * dt + (.5) * dt * (dx''(i) - dx''(i-1))
				//dy'_i = dy''_(i-1) * dt + (.5) * dt * (dy''(i) - dy''(i-1))
				//dz'_i = dz''_(i-1) * dt + (.5) * dt * (dz''(i) - dz''(i-1))
				dt = t.get(i+1) - t.get(i);
				dx_veloc.add(  x_accel.get(i) * dt + (.5f) * dt * (x_accel.get(i+1) - x_accel.get(i)));
				dy_veloc.add(  y_accel.get(i) * dt + (.5f) * dt * (y_accel.get(i+1) - y_accel.get(i)));
				dz_veloc.add(  z_accel.get(i) * dt + (.5f) * dt * (z_accel.get(i+1) - z_accel.get(i)));
			
				System.out.println("Step: " + i + "\ndt: " + dt + "\n\tv_x:"+ dx_veloc.get(i) + "\n\tv_y: " + dy_veloc.get(i) + "\n\tv_z: " + dz_veloc.get(i));
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
			STEPS = x_veloc.size();
			System.out.println("Composing Displacement from Velocity...\n");
			for( int i = 0; i < STEPS-1; i++ )
			{	
				//dx_i = dx'_(i-1) * dt + (.5) * dt * (dx'(i) - dx'(i-1))
				//dx_i = dx'_(i-1) * dt + (.5) * dt * (dx'(i) - dx'(i-1))
				//dx_i = dx'_(i-1) * dt + (.5) * dt * (dx'(i) - dx'(i-1))
				dt = t.get(i+1) - t.get(i);
				dx_disp.add( x_veloc.get(i) * dt + (.5f) * dt * (x_veloc.get(i+1) - x_veloc.get(i)));
				dy_disp.add( y_veloc.get(i) * dt + (.5f) * dt * (y_veloc.get(i+1) - y_veloc.get(i)));
				dz_disp.add( z_veloc.get(i) * dt + (.5f) * dt * (z_veloc.get(i+1) - z_veloc.get(i)));
				
				System.out.println("Step: " + i + "\ndt: " + dt + "\n\td_x:"+ dx_disp.get(i) + "\n\td_y: " + dy_disp.get(i) + "\n\td_z: " + dz_disp.get(i));
			}
			

			//compose total displacement
			float distance = 0;
	
			if( true/*Euclidean_Distance_Mode */)
			{
				//vector addition, constructing R
				System.out.println("Composing R...\n");
				float r[] = new float[3]; //[x, y, z]
				for( int i = 0; i < STEPS-1; i++)
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
				for( int i = 0; i < STEPS-1; i++)
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
			
			return 0;
		}
	
		public float Simpsons (	ArrayList<Float> x_accel, 
								ArrayList<Float> y_accel, 
								ArrayList<Float> z_accel,
								ArrayList<Float> t)
		{
			
			if(t == null) return -1;
			
			System.out.println("Entering Simpsons Distance method");
			
			ArrayList<Float> dx_veloc = new ArrayList<Float>(); 
			ArrayList<Float> dy_veloc = new ArrayList<Float>();
			ArrayList<Float> dz_veloc = new ArrayList<Float>();
			
			ArrayList<Float> x_veloc = new ArrayList<Float>(); x_veloc.add(0f);
			ArrayList<Float> y_veloc = new ArrayList<Float>(); y_veloc.add(0f);
			ArrayList<Float> z_veloc = new ArrayList<Float>(); z_veloc.add(0f);
			
			//compose velocity
			int STEPS = x_accel.size();
			
		
			float 	dt, half_dt,
			
					k1_x, k2_x, k3_x,
					k1_y, k2_y, k3_y,
					k1_z, k2_z, k3_z,
			
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
				k1_z = z_accel.get(i) * dt;
				
				//Midpoint_Increment
				k2_x = (x_accel.get(i) + x_accel.get(i+1)) / 2  * dt ;
				k2_y = (y_accel.get(i) + y_accel.get(i+1)) / 2  * dt ;
				k2_z = (z_accel.get(i) + z_accel.get(i+1)) / 2  * dt ;
				
				//Right_Eulers_Increment
				k3_x = x_accel.get(i+1) * dt;
				k3_y = y_accel.get(i+1) * dt;
				k3_z = z_accel.get(i+1) * dt;
				
				dv_x = (1f/6f) * (k1_x + 4 * k2_x + k3_x );
				dv_y = (1f/6f) * (k1_y + 4 * k2_y + k3_y );
				dv_z = (1f/6f) * (k1_z + 4 * k3_z + k3_z );
						
				dx_veloc.add(  dv_x );
				dy_veloc.add(  dv_y );
				dz_veloc.add(  dv_z);
				
				
				System.out.println("Step: " + i
									+ "\ndt: " + dt
									+ "\n\tv_x:"+ dx_veloc.get(i)
									+ "\n\tv_y: " + dy_veloc.get(i)
									+ "\n\tv_z: " + dz_veloc.get(i)
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
			
			temp = 0;
			for(float d : dz_veloc)
			{
				temp += d;
				z_veloc.add(temp);
			}
			
			ArrayList<Float> dx_disp = new ArrayList<Float>(); 
			ArrayList<Float> dy_disp = new ArrayList<Float>();
			ArrayList<Float> dz_disp = new ArrayList<Float>();
			
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
				k1_z = z_veloc.get(i) * dt;
				
				//Midpoint_Increment
				k2_x = (x_veloc.get(i) + x_veloc.get(i+1)) / 2  * dt ;
				k2_y = (y_veloc.get(i) + y_veloc.get(i+1)) / 2  * dt ;
				k2_z = (z_veloc.get(i) + z_veloc.get(i+1)) / 2  * dt ;
				
				//Right_Eulers_Increment
				k3_x = x_veloc.get(i+1) * dt;
				k3_y = y_veloc.get(i+1) * dt;
				k3_z = z_veloc.get(i+1) * dt;
				
				dD_x = (1f/6f) * (k1_x + 4 * k2_x + k3_x );
				dD_y = (1f/6f) * (k1_y + 4 * k2_y + k3_y );
				dD_z = (1f/6f) * (k1_z + 4 * k3_z + k3_z );
						
				dx_disp.add(  dD_x );
				dy_disp.add(  dD_y );
				dz_disp.add(  dD_z);
				
				
				System.out.println("Step: " + i 
									+ "\ndt: " + dt
									+ "\n\td_x:"+ dx_disp.get(i)
									+ "\n\td_y: " + dy_disp.get(i)
									+ "\n\td_z: " + dz_disp.get(i)
									);
			}	
			
			//compose total displacement
			float distance = 0;
			STEPS = dx_disp.size();
			if( true/*Euclidean_Distance_Mode */)
			{
				//vector addition, constructing R
				System.out.println("Composing R...\n");
				float r[] = new float[3]; //[x, y, z]
				for( int i = 0; i < STEPS; i++)
				{
					r[0] += dx_disp.get(i);
					r[1] += dy_disp.get(i);
					r[2] += dz_disp.get(i);
				}
			
				//Distance formula, constructing D
				//D = sqrt(X^2 + Y^2 + Z^2)
				distance =  FloatMath.sqrt( 
								(float)Math.pow(r[0], 2) + 
								(float)Math.pow(r[1], 2) //+
								//(float)Math.pow(r[2], 2)
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
									Math.pow(dy_disp.get(i), 2) +
									Math.pow(dz_disp.get(i), 2)
									);
				}		
				return distance;
			}  
		return 0; //won't get here.
		}

		public void RemoveGravity( 	SensorManager mSensorManager, 
											ArrayList<Float> xData,
											ArrayList<Float> yData,
											ArrayList<Float> zData)
		{
			/*
			 * Removes gravity via high-pass filter 
			 * */
			
				
			int count = xData.size();
			float Gx = settings.getFloat("Gravity_x", 0);
			float Gy = settings.getFloat("Gravity_y", 0);
			float Gz = settings.getFloat("Gravity_z", 0);
			final float alpha = 0.9f;
			
			System.out.printf("Subtracting out a calibrated gravity vector of [%f, %f, %f]",Gx,Gy,Gz);
			
			for(int i=0; i < count; i ++)
			{
				//Gx = alpha * Gx + (1 - alpha) * xData.get(i);
				//Gy = alpha * Gy + (1 - alpha) * yData.get(i);
				//Gz = alpha * Gz + (1 - alpha) * zData.get(i);
			
				xData.set(i, xData.get(i)-Gx);
				yData.set(i, yData.get(i)-Gy);
				zData.set(i, zData.get(i)-Gz);
			}
			
			
		}
		
		public void Rotate(float[] vector, double psi, double theta, double phi )
		{
			//these very well may have the wrong angles in them. 
			//i.e., maybe the x matrix should have phi instead of theta
			double[][] arr_Rx = {	{1.0, 				0.0, 				 0.0}, 
									{0.0, 	Math.cos(theta), 	-Math.sin(theta)},
									{0.0,	Math.sin(theta), 	-Math.cos(theta)}
					};
			double[][] arr_Ry = {	{Math.cos(phi), 	0.0,		Math.sin(phi)}, 
									{0.0, 				1.0,		  		  0.0},
									{-Math.sin(phi),	0.0,		Math.cos(phi)}
					};
			double[][] arr_Rz = {	{Math.cos(psi), -Math.sin(psi), 		  0.0}, 
									{Math.sin(psi),  Math.cos(psi), 		  0.0},
									{0.0,				0.0, 				  1.0}
					};

			Matrix Rx = new Matrix(arr_Rx);
			Matrix Ry = new Matrix(arr_Ry);
			Matrix Rz = new Matrix(arr_Rz);

			//not done
		}

}
