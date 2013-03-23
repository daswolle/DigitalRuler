package com.example.digitalmeasuringtape;

import java.util.ArrayList;

import android.util.FloatMath;

public class Physics {

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
				System.out.println("Step: " + i + "\ndt: " + dt + "\n\tv_x:"+ dx_veloc.get(i) + "\n\tv_y: " + dy_veloc.get(i) + "\n\tv_z: " /*+ dz_veloc.get(i)*/);
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
				
				System.out.println("Step: " + i + "\ndt: " + dt + "\n\td_x:"+ dx_disp.get(i) + "\n\td_y: " + dy_disp.get(i) + "\n\td_z: " /*+ dz_disp.get(i)*/);
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
					System.out.println("Step: " + i + "\n\tr_x: "+ r[0] + "\n\tr_y: " + r[1] /*+ "\n\tr_z: " + r[2]*/);
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

		/*----Parameters-----
			Vector: an array representing a 3-component vector. [x.y.z]
			*/
		public static void Rotate(float[] vector, double psi, double theta, double phi )
		{
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
