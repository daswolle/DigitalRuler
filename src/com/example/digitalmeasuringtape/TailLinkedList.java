package com.example.digitalmeasuringtape;

import java.util.ArrayList;

public class TailLinkedList {
  private Node head;
  private Node tail;
  public ArrayList<Float> xData;
  public ArrayList<Float> yData;
  public ArrayList<Float> zData;
  public ArrayList<Float> oxData;
  public ArrayList<Float> oyData;
  public ArrayList<Float> ozData;
  public ArrayList<Float> tData;

  public TailLinkedList() {
	  head = null;
	  tail = null;
	  xData = new ArrayList<Float>();
	  yData = new ArrayList<Float>();
	  zData = new ArrayList<Float>();
	  oxData = new ArrayList<Float>();
	  oyData = new ArrayList<Float>();
	  ozData = new ArrayList<Float>();
	  tData = new ArrayList<Float>();
  }

  public TailLinkedList(Node firstNode) {
	  head = firstNode;
	  tail = firstNode;
	  xData = new ArrayList<Float>();
	  yData = new ArrayList<Float>();
	  zData = new ArrayList<Float>();
	  oxData = new ArrayList<Float>();
	  oyData = new ArrayList<Float>();
	  ozData = new ArrayList<Float>();
	  tData = new ArrayList<Float>();
  }

  public void add(float x, float y, float z, float ox, float oy, float oz, long time) {
   Node newNode = new Node(x, y, z, ox, oy, oz, time);
   if(head==null && tail == null)
   {
	   //then this is the first node inserted ever
	   head = newNode;
	   tail = newNode;
   }
   else
   {
	   //then we should stick this after tail,
	   //and then have tail point to this
	   tail.next = newNode;
	   tail = newNode;
   }
  }
  
  public void unravel(){
	  System.out.println("Entering Unravel");
	  Node trav = head;
	  if(trav == null) return;
	  float t0 = trav.time;
	  float t;
	  while(trav.next != null) {
		  xData.add(trav.x);
		  yData.add(trav.y);
		  zData.add(trav.z);
		  oxData.add(trav.ox);
		  oyData.add(trav.oy);
		  ozData.add(trav.oz);
		  t = trav.time - t0;
		  t /= 1000000000.0;
		  System.out.printf("%f - %f = %f\n", (float)trav.time, t0, t);
		  tData.add((float)t);
		  trav = trav.next;
	  }
	  return;
  }
  
  private class Node {
  	public float x;
  	public float y;
  	public float z;
  	public float ox;
  	public float oy;
  	public float oz;
  	
  	public long time;
  	public Node next;

 	 public Node(	float newX, float newY, float newZ,
 			 		float newOX, float newOY, float newOZ,
 			 		long newTime) {
   		x = newX;
   		y = newY;
   		z = newZ;
   		ox = newOX;
   		oy = newOY;
   		oz = newOZ;
	   time = newTime;
	   next = null;
	  }

 }

 }
