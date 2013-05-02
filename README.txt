README.txt
The code in this .zip represents the work done by coders:
Peter Beissinger,
Matthew Wollerman,
Beau Reddington
on the Android application "Slide Ruler." This code was written between January and May of 2013, and excepting the following citation, is the sole intellectual property of these individuals.


A third-party class has been included in this project as permitted by the Creative Commons Attribution 3.0 license. The file "ProgressWheel.java" is authored by Todd Davies, as cited on lines 17-22 of this file. Please see lines 17-22 of "ProgressWheel.java" for details relating to the Creative Commons Attribution 3.0 license that permits free use of this class.
________________


TO UNDERSTAND THIS CODE, READ THE FOLLOWING DESCRIPTION OF CODE FLOW


Qualitative description:
1. When the user begins the application, a popup describes to them the method of using this app for best results. The reader may turn this popup off. In the future, the app begins at step 2.


2. The user touches and holds the button labeled "start," and the app begins calibrating. The user continues to hold the button.


3. A ring fills around the "start" button, and when it is full, the app displays a message saying “GO!” and begins measuring. The user continues to hold the button.


4. The user slides the phone across the object they desire to measure. The user then releases the button after the desired, unknown distance has been traversed.


5. The app calculates the distance between starting and ending locations, and prints this distance to screen.


6.The user closes the app. OR The user presses holds down the start button again (Goto step 2).


If the user removes their finger from the button at any point during steps 2-3 above, the app returns to step 2.


Technical description:
1. Android app creation occurs. MainActivity is created. MainActivity.onCreate is called (or MainActivity.onResume depending on the previous state). This initializes objects and variables needed for execution. If this is the first time this app has run on this phone, or the user has turned on the "help" setting, an AlertDialogue is created to walk the user through use.


2. The button is pressed, calling the onTouch method of the OnTouchListener named myListener. This launches the thread that performs the calibration and measurement process. This method returns, and the thread continues in 3. Although this is a thread, and separate from the UI thread, due to design constraints imposed by the programmers, no meaningful code should be run until this thread is complete. It is almost as though program flow simply hops over to the other thread.


3. The method “run” in MainActivity is the main method for the thread mentioned in 2. It is assumed that the user continues to hold the button through the life of the thread, and after measurement begins, release of the button determines the end of the thread. First, Calibrate() is called, which calibrates the app. Then, Measure() is called, which collects data, then sends the data to the selected distance-calculation algorithm, which is found in the PhysicsManager class. At this time, only one algorithm is selected--or may be selected.


3b. If the user releases the button during Calibrate(), the method will terminate early, and the rest of the thread’s run() method will be skipped.


4. At the end of Measure(), the calculated distance moved is sent to the TextView tv, which displays near the bottom of the screen
________________


Measure() and Calibrate()
Measure() and Calibrate() are similar methods with similar behavior.
        Calibrate()
First, the sensor manager is instantiated, variables to hold maximum values are  
reset, a fresh TailLinkedList is created to hold measurements, the boolean to 
indicate that calibration is taking place is set, and a listener is registered with the 
accelerometer. Then the thread sleeps for two seconds.


During these two seconds, onSensorChanged events will be firing. They fire via the method onSensorChanged(SensorEvent event), found at the bottom of MainActivity.java. Whenever a change is detected in the accelerometer, the measurement is recorded in a TailLinkedList node, along with a timestamp. Greatest measurements are recorded.


After the two seconds of sleep, the thread wakes up and immediately unregisters the accelerometer listener. onSensorChanged events will stop firing. All measurements recorded during the two second interval are now in the TailLinkedList measurements.
Next, unravel() is called to split the TailLinkedList into several ArrayLists, then each dimension’s data is averaged and saved as the calibrated gravity for this measurement. The method then returns


Measure()
        First, Collect() is called. This method is nearly identical to the first part of 
Callibrate(). Variables and lists are initialized, then a CountDownLatch named 
gate is instantiated with an initial CountDown value of 1. Instead of registering an accelerometer listener and sleeping for two seconds, Collect() registers an accelerometer listener and await()’s on gate, blocking the thread. When gate.countDown() is called in another thread, this thread will be unblocked. gate.countDown() will be called by the onTouchListener of the start button in the event of an onButtonUp. While the thread is blocked, onSensorChanged events will be firing in the onSensorChanged(SensorEvent event) method found at the bottom of MainActivity.java.


When gate.await() is called, the thread wakes up and immediately unregisters its accelerometer listener. All distance measurements are now recorded in the TailLinkedList measurements. trim() is called, cutting extraneous data from the left-end and right-end of the measurements. Then Collect() returns, to Measure().


Depending on the number of axes being measured, and which distance algorithm is selected, Measure() then calls RemoveGravity(), and Distance(). RemoveGravity(), as defined in the PhysicsManager class, subtracts from each measurement the effect of gravity, as measured during Calibrate(). Distance(), as defined in PhysicsManager, sends measurements to the correct distance calculation algorithm. By default (and there is currently no way to change this), the algorithm is Simpson’s, for only the x-dimension.


The value of distance calculated is returned from Distance(), and Measure() formats this into a human-readable string. It is then sent to the TextView tv via a call to handler.sendEmptyMessage(), which prints whatever is currently inside of “pi_string” to the screen.


Measure() finishes.
________________


FOLLOWING IS A CLASS-BY-CLASS EXPLANATION OF THIS APPLICATION


Help.class:
Contains the simple initialization code to launch the Help page which is structured by res/layout/help.xml.


MainActivity.class:
        Contains the methods:
1. onExit()
1. unregisters listeners
1. onCreate()
1. initializes variables, the sensor manager, and the UI. also calls needHelp()
1. onRestart()
1. calls needHelp()
1. needHelp()
1. creates a popup dialog if the user has the help setting enabled
1. Calibrate()
1. this method registers a listener, collects data for two seconds, averages the data points, and saves it as gravity data to be subtracted later
1. run()
1. if the user is still holding the button, this method calls Calibrate() and Measure(). It also outputs messages to the screen. This is background thread as described above.
1. Measure()
1. calls Collect(), unravel(), removeGravity(), lowPassFilter(), and Distance(). Then it formats the value and outputs it to the screen.
1. Collect()
1. registers a listener and waits to allow data to be collected. Then stores greatest values, calls trim, and ends.
1. stopMeasuring(MotionEvent)
1. kills the thread if a user releases the button and unregisters the accelerometer listener
1. onSensorChanged(SensorEvent)
1. required by implementing SensorEventListener
2. records readings from the accelerometer and adds them to the TailLinkedList
1. onAccuracyChanged(Sensor int)
1. required by implementing SensorEventListener


PhysicsManager.class:
1. Distance(ArrayList<Float> …)
1. Switches based on number of axes measured, and on which distance algorithm is selected. Sends measurements to proper distance algorithm, the returns calculated distance
1. EulersX(ArrayList<Float>, ArrayList<Float>)
1. Eulers for 1 axis
1. EulersXY(ArrayList<Float>, ArrayList<Float>, ArrayList<Float>)
1. Eulers for 2 axes
1. EulersXYZ(ArrayList<Float>, ArrayList<Float>, ArrayList<Float>, ArrayList<Float>)
1. Eulers for 3 axes
1. Improved_EulersX(ArrayList<Float>, ArrayList<Float>)
1. Improved_Eulers for 1 axes
1. Improved_EulersXY(ArrayList<Float>, ArrayList<Float>, ArrayList<Float>)
1. Improved_Eulers for 2 axes
1. Improved_EulersXYZ(ArrayList<Float>, ArrayList<Float>, ArrayList<Float>, ArrayList<Float>)
1. Improved_Eulers for 3 axes
1. SimpsonsX(ArrayList<Float>, ArrayList<Float>)
1. Simpsons method for 1 axis
1. SimpsonsXY(ArrayList<Float>, ArrayList<Float>, ArrayList<Float>)
1. Simpsons method for 2 axes
1. SimpsonsXYZ(ArrayList<Float>, ArrayList<Float>, ArrayList<Float>, ArrayList<Float>)
1. Simpsons method for all 3 axes
1. RemoveGravity(ArrayList<Float>...)
1. subtracts the calculated gravity value (during calibrate) and removes it from each data point
1. Straighten(ArrayList<Float>, ArrayList<Float>)
1. DEPRECATED
2. shifts values according to a reading generated from geomagnetic sensor to straighten values in the XY plane
1. LowPassFilter(ArrayList<Float>)
1. filters out high frequency noise via a low-pass filter with alpha=15%


TailLinkedList.class:
1. trim(float)
1. If a value is within 5% of the peak, on the left and the right, these values are removed from the LinkedList
1. add(long, float...)
1. Adds a value to the TailLinkedList
1. unravel()
1. Takes each node and turns each coordinate into an Array List as opposed to a LinkedList of nodes
1. smooth(ArrayList<Float>, String)
1. Basic moving average smoothing with m = 2. Averages 5 values, 2 on either side of each data point, and stores this as the new value
1. listToString(ArrayList<Float>, String)
1. Outputs an ArrayList as a comma seperated string. Used for development.
1. writeGraph(String, String, String, String)
1. Checks storage is available and outputs Strings to a specified output file


ProgressWheel.class:
        Contains code as permitted by the Creative Commons Attribution 3.0 license. The file "ProgressWheel.java" is authored by Todd Davies, as cited on lines 17-22 of this file. Please see lines 17-22 of "ProgressWheel.java" for details relating to the Creative Commons Attribution 3.0 license that permits free use of this class.


Settings.class:
        Contains the simple initialization code to launch the settings page which is structured by res/layout/settings.xml.