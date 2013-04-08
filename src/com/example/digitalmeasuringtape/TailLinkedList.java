package com.example.digitalmeasuringtape;

import java.util.ArrayList;

public class TailLinkedList {
  private Node head;
  private Node tail;
  public ArrayList<Float> xData;
  public ArrayList<Float> yData;
  public ArrayList<Float> tData;

  public TailLinkedList() {
	  head = null;
	  tail = null;
	  xData = new ArrayList<Float>();
	  yData = new ArrayList<Float>();
	  tData = new ArrayList<Float>();
  }

  public TailLinkedList(Node firstNode) {
	  head = firstNode;
	  tail = firstNode;
	  xData = new ArrayList<Float>();
	  yData = new ArrayList<Float>();
	  tData = new ArrayList<Float>();
  }

  public void add(float x, float y, long time) {
   Node newNode = new Node(x, y, time);
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
	  //TODO: filter outliers
	  System.out.println("Entering Unravel");
	  Node trav = head;
	  if(trav == null) return;
	  float t0 = trav.time;
	  float t;
	  while(trav.next != null) {
		  xData.add(trav.x);
		  yData.add(trav.y);
		  t = trav.time - t0;
		  t /= 1000000000.0;
		  tData.add((float)t);
		  trav = trav.next;
	  }
	  return;
  }
  
  private class Node {
  	public float x;
  	public float y;
  	
  	public long time;
  	public Node next;

 	 public Node(float newX, float newY, long newTime) {
   		x = newX;
   		y = newY;
	   time = newTime;
	   next = null;
	  }

 }

 }
