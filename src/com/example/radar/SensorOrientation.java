package com.example.radar;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class SensorOrientation  extends Activity implements SensorEventListener {
	/**
	 * The Tag for the Log
	 */
	private static final String LOG_TAG = "SensorsOrientation";

	/**
	 * Current value of the accelerometer
	 */
	float x, y, z;

	LinearLayout.LayoutParams lParamsName;

	LinearLayout xyAccelerationLayout;

	OrientationView xyAccelerationView;

	ProgressBar pgbX, pgbY, pgbZ;

	/** * The sensor manager */
	SensorManager sensorManager;

	Sensor orientation;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// build the GUI
		setContentView(R.layout.main);
		// instantiate the progress bars
		pgbX = (ProgressBar) findViewById(R.id.progressBarX);
		pgbY = (ProgressBar) findViewById(R.id.progressBarY);
		pgbZ = (ProgressBar) findViewById(R.id.progressBarZ);
		// the azimut max value
		pgbX.setMax((int) 360);
		// the pitch max value
		pgbY.setMax((int) 180);
		// the roll max value
		pgbZ.setMax((int) 90);
		// Then manage the sensors and listen for changes
		// Instantiate the SensorManager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// Instantiate the accelerometer
		orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		// Then build the GUI:
		// Build the acceleration view
		// first retrieve the layout:
		xyAccelerationLayout = (LinearLayout) findViewById(R.id.layoutOfXYAcceleration);
		// then build the view
		xyAccelerationView = new OrientationView(this);
		// define the layout parameters and add the view to the layout
		LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		// add the view in the layout
		xyAccelerationLayout.addView(xyAccelerationView, layoutParam);
	}

	@Override
	protected void onPause() {
		sensorManager.unregisterListener(this, orientation);
		xyAccelerationView.isPausing.set(true);
		super.onPause();
	}

	@Override
	protected void onResume() {
		sensorManager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_UI);
		xyAccelerationView.isPausing.set(false);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		xyAccelerationView.isRunning.set(false);
		super.onDestroy();
	}

	private void updateProgressBar() {
		if (x > 0) {
			pgbX.setProgress((int) x);
		} else {
			pgbX.setSecondaryProgress(-1 * (int) x);
		}
		if (y > 0) {
			pgbY.setProgress((int) y);
		} else {
			pgbY.setSecondaryProgress(-1 * (int) y);
		}
		if (z > 0) {
			pgbZ.setProgress((int) z);
		} else {
			pgbZ.setSecondaryProgress(-1 * (int) z);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Nothing to do
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// update only when your are in the right case:
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			// the azimuts
			x = event.values[0];
			// the pitch
			y = event.values[1];
			// the roll
			z = event.values[2];
			// update the progressBar
			updateProgressBar();
			Log.d(LOG_TAG, "Sensor's values (" + ((int) x) + "," + ((int) y) + "," + ((int) z) + ")");
		}
	}
}