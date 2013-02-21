package com.example.radar;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class OrientationView extends View {
		/******************************************************************************************/
		/** Private constant **************************************************************************/
		/******************************************************************************************/
		/**
		 * The tag for the log
		 */
		private static final String tag = "SensorsAccelerometer";
		/******************************************************************************************/
		/** Attributes associated to the main activity and the screen *****************************/
		/******************************************************************************************/
		/**
		 * Main activity
		 */
		private SensorOrientation activity;
		/******************************************************************************************/
		/** Attributes associated to canvas **************************************************************************/
		/******************************************************************************************/
		/**
		 * The paint to draw the view
		 */
		private Paint paint = new Paint();
		/**
		 * The Canvas to draw within
		 */
		private Canvas canvas;
		private Path northPath = new Path();
		private Path southPath = new Path();
		private boolean mAnimate;
		private Paint.FontMetrics mFontMetrics;
		private int textColor, backgroundCircleColor, circleColor;
		private float fontHeight;
		/******************************************************************************************/
		/** Attributes used to manage the points coordinates **************************************/
		/******************************************************************************************/

		/**
		 * The source point of the transformation matrix
		 */
		float[] src = new float[8];
		/**
		 * The destination point of the transformation matrix
		 */
		float[] dst = new float[8];

		/******************************************************************************************/
		/** Handler and Thread attribute **********************************************************/
		/******************************************************************************************/
		/**
		 * The boolean to initialize the data upon
		 */
		boolean init = false;
		/** * An atomic boolean to manage the external thread's destruction */
		AtomicBoolean isRunning = new AtomicBoolean(false);
		/** * An atomic boolean to manage the external thread's destruction */
		AtomicBoolean isPausing = new AtomicBoolean(false);
		/**
		 * The handler used to slow down the re-drawing of the view, else the device's battery is
		 * consumed
		 */
		private final Handler slowDownDrawingHandler;
		/**
		 * The thread that call the redraw
		 */
		private Thread background;

		/******************************************************************************************/
		/** Constructors **************************************************************************/
		/******************************************************************************************/

		/**
		 * @param context
		 */
		public OrientationView(Context context) {
			super(context);
			// instanciate the calling activity
			activity = (SensorOrientation) context;
			// Define the path that draw the North arrow (a path is a sucession of lines)
			northPath.moveTo(0, -60);
			northPath.lineTo(-10, 0);
			northPath.lineTo(10,0);
			northPath.close();
			// Define the path that draw the South arrow (a path is a sucession of lines)		
			southPath.moveTo(-10,0);
			southPath.lineTo(0,60);
			southPath.lineTo(10,0);
			southPath.close();
			// Define how to draw text
			paint.setTextSize(20);
			paint.setTextAlign(Paint.Align.CENTER);
			paint.setAntiAlias(true);
			mFontMetrics = paint.getFontMetrics();
			fontHeight = mFontMetrics.ascent + mFontMetrics.descent;
			// define the color used
			Resources res = getResources();
			textColor = res.getColor(R.color.text_color);
			backgroundCircleColor = res.getColor(R.color.background_circle_color);
			circleColor = res.getColor(R.color.circle_color);
			// handler definition to redraw the screen (not too quickly)
			slowDownDrawingHandler = new Handler() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see android.os.Handler#handleMessage(android.os.Message)
				 */
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					redraw();
				}
			};
			// Launching the Thread to update draw
			background = new Thread(new Runnable() {
				/**
				 * The message exchanged between this thread and the handler
				 */
				Message myMessage;

				// Overriden Run method
				public void run() {
					try {
						while (isRunning.get()) {
							if (isPausing.get()) {
								Thread.sleep(2000);
							} else {
								// Redraw to have 30 images by second
								Thread.sleep(1000 / 30);
								// Send the message to the handler (the handler.obtainMessage is more
								// efficient that creating a message from scratch)
								// create a message, the best way is to use that method:
								myMessage = slowDownDrawingHandler.obtainMessage();
								// then send the message

								slowDownDrawingHandler.sendMessage(myMessage);
							}
						}
					} catch (Throwable t) {
						// just end the background thread
					}
				}
			});
			// Initialize the threadSafe booleans
			isRunning.set(true);
			isPausing.set(false);
			// and start it
			background.start();
		}

		/******************************************************************************************/
		/** Drawing method **************************************************************************/
		/******************************************************************************************/

		/**
		 * The method to redraw the view
		 */
		private void redraw() {
			// Log.d(tag, "redraw");
			// and make sure to redraw asap
			invalidate();
		}

		@Override
		public void onDraw(Canvas canvas) {
			int w = getWidth();
			int h = getHeight();
			int cx = w / 2;
			int cy = h / 2;
			int lenght = h / 4;
			// draw the grid
			drawGrid(canvas, w, h);
			// draw the north arrow
			drawNorthArrow(canvas, lenght, cx, cy);
			// draw the pitch
			drawPitchCircle(canvas, lenght, cx, cy);
			// draw the roll
			drawRollCircle(canvas, lenght, cx, cy);

		}

		/**
		 * @param canvas
		 * @param w
		 * @param h
		 */
		private void drawGrid(Canvas canvas, int w, int h) {
			paint.setColor(Color.RED);
			canvas.drawLine(w / 2, 0, w / 2, h, paint);
			canvas.drawLine(w / 4, 0, w / 4, h, paint);
			canvas.drawLine(3 * w / 4, 0, 3 * w / 4, h, paint);
			canvas.drawLine(0, h / 2, w, h / 2, paint);
			canvas.drawLine(0, h / 4, w, h / 4, paint);
			canvas.drawLine(0, 3 * h / 4, w, 3 * h / 4, paint);
		}

		/**
		 * @param canvas
		 * @param h
		 * @param cx
		 * @param cy
		 */
		private void drawRollCircle(Canvas canvas, int lenght, int cx, int cy) {
			// first save the canvas configuration (orientation, translation)
			canvas.save();
			// find the roll to display
			float roll = activity.z;
			// Translate the canvas to draw the circle int the first upper quarter of the screen
			// center on the w/2 axis
			canvas.translate(cx, cy - 3 * lenght / 2);
			// rotate the arc for it to map the roll value
			canvas.rotate(roll, 0, 0);
			// Define the rectangle in which the circle is drawn
			RectF pitchOval = new RectF(-lenght / 2, -lenght / 2, lenght / 2, lenght / 2);
			// draw the background circle
			paint.setColor(backgroundCircleColor);
			canvas.drawArc(pitchOval, 0, 360, false, paint);
			// draw the roll circle
			paint.setColor(circleColor);
			canvas.drawArc(pitchOval, 0, 180, false, paint);
			// and now displays the value of the roll
			paint.setColor(textColor);
			canvas.drawText(((int) roll) + "", 0, -5, paint);
			//And below draw the name Roll
			String rollName=getResources().getString(R.string.roll_name);
			float hText = - fontHeight+5;
			canvas.drawText(rollName, 0, hText, paint);
			// and now go back to the initial canvas state
			canvas.restore();
		}

		/**
		 * @param canvas
		 * @param w
		 * @param cx
		 * @param cy
		 */
		private void drawPitchCircle(Canvas canvas, int lenght, int cx, int cy) {
			// first save the canvas configuration (orientation, translation)
			canvas.save();
			// find the pitch to display
			float pitch = activity.y;
			// Translate the canvas to draw the circle int the last down quarter of the screen
			// center on the w/2 axis
			canvas.translate(cx, cy + 3 * lenght / 2);
			// Define the rectangle in which the circle is drawn
			RectF pitchOval = new RectF(-lenght/2, -lenght/2, lenght/2, lenght/2);
			// draw the background circle
			paint.setColor(backgroundCircleColor);
			canvas.drawCircle(0, 0, lenght / 2, paint);
			// draw the roll circle
			paint.setColor(circleColor);
			//What i want is:90+pitch;360+2*pitch
			//when pitch=0, startAngle= 90, sweepAngle=360
			//when pitch=-45, startAngle= 90+pitch, sweepAngle=360+2*pitch
			//when pitch=-90, startAngle= 0, sweepAngle=180
			//when pitch=-135, startAngle= 90+pitch, sweepAngle=360+2*pitch
			//when pitch=90, startAngle= 0, sweepAngle=-180
			if(pitch<=0) {
				canvas.drawArc(pitchOval, -90-pitch, 360+2*pitch, false, paint);
			}else {
				canvas.drawArc(pitchOval, 90+pitch, 360-2*pitch, false, paint);
			}
			//before
			//canvas.drawArc(pitchOval, 0 - pitch / 2, 180 + pitch, false, paint);
			// and now displays the value of the roll
			paint.setColor(textColor);
			canvas.drawText(((int) pitch) + "", 0, -5, paint);
			//And below draw the name Pitch
			String pitchName=getResources().getString(R.string.pitch_name);
			float hText = - fontHeight+5;
			canvas.drawText(pitchName, 0, hText, paint);
			// and now go back to the initial canvas state
			canvas.restore();
		}

		/**
		 * @param canvas
		 * @param cx
		 * @param cy
		 */
		private void drawNorthArrow(Canvas canvas, int lenght, int cx, int cy) {
			canvas.save();
			// get the azimuth to display
			float azimut = -activity.x;
			// center the arrow in the middle of the screen
			canvas.translate(cx, cy);
			// rotate the canvas such that the arrow will indicate the north
			// even if it draws vertical
			canvas.rotate(azimut);
			// draw the background circle
			paint.setColor(circleColor);
			canvas.drawCircle(0, 0, lenght / 2-fontHeight+10, paint);
			paint.setColor(backgroundCircleColor);
			canvas.drawCircle(0, 0, lenght / 2, paint);
			
			// Now display the scale of the compass
			paint.setColor(Color.WHITE);
			// display the graduation
			//the text position
			
			float hText = - lenght/2 - fontHeight+3;
			// each 15° draw a graduation |
			int step = 15;
			for (int degree = 0; degree < 360; degree = degree + step) {
				// if it's not a cardinal point draw the graduation
				if ((degree % 45) != 0) {
					canvas.drawText("|", 0, hText, paint);				
				}
				canvas.rotate(-step);
			}
			
			// then draw cardinal points
			canvas.drawText("N", 0, hText, paint);
			canvas.rotate(-45);
			canvas.drawText("NW", 0, hText, paint);
			canvas.rotate(-45);
			canvas.drawText("W", 0, hText, paint);
			canvas.rotate(-45);
			canvas.drawText("SW", 0, hText, paint);
			canvas.rotate(-45);
			canvas.drawText("S", 0, hText, paint);
			canvas.rotate(-45);
			canvas.drawText("SE", 0, hText, paint);
			canvas.rotate(-45);
			canvas.drawText("E", 0, hText, paint);
			canvas.rotate(-45);
			canvas.drawText("NE", 0, hText, paint);
			canvas.rotate(-45);

			// Draw the arrow
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.RED);
			canvas.drawPath(northPath, paint);
			paint.setColor(circleColor);
			canvas.drawPath(southPath, paint);
			
			// and now displays the value of the azimut
			paint.setColor(textColor);
			paint.setStyle(Paint.Style.FILL);
			//Draw the text horizontal
			canvas.restore();
			// center the arrow in the middle of the screen
			float hTextValue = - lenght/2+fontHeight-5;
			canvas.translate(cx, cy);
			canvas.drawText(((int) activity.x) + "", 0, -hTextValue, paint);
			canvas.restore();
		}
	}
