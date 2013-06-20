package com.schneider.i3d;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Color4;
import min3d.vos.Light;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
public class Obj3DView extends RendererActivity {  
	final Handler mHandler = new Handler();
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			ll.addView(_glSurfaceView);
		}
	};
	private Object3dContainer Object1;  
	private Object3dContainer Object2;
	private Object3dContainer Object3;
	private Camera mCamera;
	private CameraPreview mPreview;
	private GestureDetector gesturedetector = null;
	View layout;
	private float xRotation = 0;
	private float yRotation = 0;
	private Color4 colors[] = {
			new Color4(0xFF, 0x00, 0x00, 0xFF),
			new Color4(0x00, 0xFF, 0x00, 0xFF),
			new Color4(0x00, 0x00, 0xFF, 0xFF),
			new Color4(0xFF, 0xFF, 0x00, 0xFF),
			new Color4(0xFF, 0x00, 0xFF, 0xFF),
			new Color4(0x00, 0xFF, 0xFF, 0xFF),
			new Color4(0x00, 0x00, 0x00, 0xFF),
			new Color4(0xFF, 0xFF, 0xFF, 0xFF)
	};
	private int colorCount = 0;
	private int bgColorCount = 0;
	private String queryString;
	FrameLayout ll;
	private ImageView imageView;
	@Override 
	protected void glSurfaceViewConfig()
	{
		// !important
		_glSurfaceView.setEGLConfigChooser(8,8,8,8, 16, 0);
		_glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
	}
	@Override
	protected void onCreateSetContentView() {
		setContentView(R.layout.background);
		final Bundle bundle = this.getIntent().getExtras();
		queryString = bundle.getString("com.schneider.message");
		Thread thread = new Thread(new Request());
		thread.start();
		// Create an instance of Camera
		mCamera = getCameraInstance();

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		ll = (FrameLayout) this.findViewById(R.id.background);
		ll.addView(mPreview);
		layout = (LinearLayout)findViewById(R.id.gestureView);
		gesturedetector = new GestureDetector(getApplicationContext(),new MyGestureListener());
		layout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gesturedetector.onTouchEvent(event);
				return true;
			}
		});
	}
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		}
		catch (Exception e){
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}
	/** Called when the activity is first created. */  
	@Override  
	public void initScene()  
	{  
		scene.lights().add(new Light());  
		scene.lights().add(new Light());  
		Light myLight = new Light();
		myLight.position.setZ(150);  
		scene.lights().add(myLight);  
		IParser myParser = Parser.createParserFromFile(getApplicationContext(),"s1_"+queryString,true);  
		myParser.parseFromFile();  
		Object1 = myParser.getParsedObject();  
		Object1.position().x = Object1.position().y = Object1.position().z = 0;  
		Object1.scale().x = Object1.scale().y = Object1.scale().z = 0.03f;
		Object1.colorMaterialEnabled(true);
		Object1.vertexColorsEnabled(false);
		Object1.defaultColor(new Color4(0x00,0x00,0x7f,0xff));
		// Depending on the model you will need to change the scale faceObject3D.scale().x = faceObject3D.scale().y = faceObject3D.scale().z = 0.009f;  
		scene.backgroundColor().setAll(0x00000000);
		scene.addChild(Object1);
		
		myParser = Parser.createParserFromFile(getApplicationContext(),"s2_"+queryString,true);  
		myParser.parseFromFile();  
		Object2 = myParser.getParsedObject();  
		Object2.position().x = Object2.position().y = Object2.position().z = 0;  
		Object2.scale().x = Object2.scale().y = Object2.scale().z = 0.03f;
		Object2.colorMaterialEnabled(true);
		Object2.vertexColorsEnabled(false);
		Object2.defaultColor(new Color4(0x00,0x00,0x7f,0xff));
		// Depending on the model you will need to change the scale faceObject3D.scale().x = faceObject3D.scale().y = faceObject3D.scale().z = 0.009f;  
		scene.backgroundColor().setAll(0x00000000);
		scene.addChild(Object2);
		
		myParser = Parser.createParserFromFile(getApplicationContext(),"s3_"+queryString,true);  
		myParser.parseFromFile();  
		Object3 = myParser.getParsedObject();  
		Object3.position().x = Object3.position().y = Object3.position().z = 0;  
		Object3.scale().x = Object3.scale().y = Object3.scale().z = 0.03f;
		Object3.colorMaterialEnabled(true);
		Object3.vertexColorsEnabled(false);
		Object3.defaultColor(new Color4(0x00,0x00,0x7f,0xff));
		// Depending on the model you will need to change the scale faceObject3D.scale().x = faceObject3D.scale().y = faceObject3D.scale().z = 0.009f;  
		scene.backgroundColor().setAll(0x00000000);
		scene.addChild(Object3);
	}  
	@Override  
	public void updateScene() {  
		Object1.rotation().x = xRotation;
		Object1.rotation().z = yRotation;
		Object1.defaultColor(colors[colorCount]);
		Object2.rotation().x = xRotation;
		Object2.rotation().z = yRotation;
		Object2.defaultColor(colors[(colorCount+1)%colors.length]);
		Object3.rotation().x = xRotation;
		Object3.rotation().z = yRotation;
		Object3.defaultColor(colors[(colorCount+2)%colors.length]);
		if(bgColorCount>0)
		{
			scene.backgroundColor().setAll(colors[bgColorCount-1]);
		}
		else
		{
			scene.backgroundColor().setAll(0x00000000);
		}
		//faceObject3D.rotation().z += 1;
	}  
	public boolean dispatchTouchEvent(MotionEvent ev){
		super.dispatchTouchEvent(ev);
		return gesturedetector.onTouchEvent(ev);
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
		private static final int SWIPE_MIN_DISTANCE = 10;
		private static final int SWIPE_MAX_OFF_PATH = 100;
		private static final int SWIPE_THRESHOLD_VELOCITY = 20;
		private static final int ROTATION_SPEED_DIVISION = 5;

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			colorCount++;
			colorCount%=colors.length;
			return super.onSingleTapConfirmed(e);
		}
		@Override
		public void onLongPress(MotionEvent e) {
			bgColorCount++;
			bgColorCount%=(colors.length+1);
			super.onLongPress(e);
		}
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,	float velocityY) {
			float dX = e2.getX()-e1.getX();
			float dY = e1.getY()-e2.getY();
			if (Math.abs(dY)<SWIPE_MAX_OFF_PATH &&
					Math.abs(velocityX)>=SWIPE_THRESHOLD_VELOCITY &&
					Math.abs(dX)>=SWIPE_MIN_DISTANCE ) {
				if (dX>0) {
					//Toast.makeText(getApplicationContext(), "Right Swipe", Toast.LENGTH_SHORT).show();
					yRotation -= Math.abs(dX)/ROTATION_SPEED_DIVISION;
				} else {
					//Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
					yRotation += Math.abs(dX)/ROTATION_SPEED_DIVISION;
				}
				return true;
			} else if (Math.abs(dX)<SWIPE_MAX_OFF_PATH &&		
					Math.abs(velocityY)>=SWIPE_THRESHOLD_VELOCITY &&		
					Math.abs(dY)>=SWIPE_MIN_DISTANCE ) {
				if (dY>0) {
					//Toast.makeText(getApplicationContext(), "Up Swipe", Toast.LENGTH_SHORT).show();
					xRotation -= Math.abs(dY)/ROTATION_SPEED_DIVISION;
				} else {
					//Toast.makeText(getApplicationContext(), "Down Swipe", Toast.LENGTH_SHORT).show();
					xRotation += Math.abs(dY)/ROTATION_SPEED_DIVISION;
				}
				return true;
			}
			return false;
		}
	}
	class Request implements Runnable{
		@Override
		public void run() {
			try {
				final String url = getResources().getString(R.string.server_address);
				URL objUrl = new URL(url+"obj/s1_"+queryString);
				URLConnection connection = objUrl.openConnection();
	            connection.connect();
	            File objFile = new File(getApplicationContext().getFilesDir().getPath().toString()+"/s1_"+queryString);
	            if (!objFile.exists()){
	               objFile.createNewFile();
	            }
	         // download the file
	            InputStream input = new BufferedInputStream(objUrl.openStream());
	            OutputStream output = new FileOutputStream(objFile.getAbsolutePath());

	            byte data[] = new byte[8192];
	            int count;
	            while ((count = input.read(data)) != -1) {
	                output.write(data, 0, count);
	            }
	            output.flush();
	            output.close();
	            input.close();
	            
	            objUrl = new URL(url+"obj/s2_"+queryString);
				connection = objUrl.openConnection();
	            connection.connect();
	            objFile = new File(getApplicationContext().getFilesDir().getPath().toString()+"/s2_"+queryString);
	            if (!objFile.exists()){
	               objFile.createNewFile();
	            }
	         // download the file
	            input = new BufferedInputStream(objUrl.openStream());
	            output = new FileOutputStream(objFile.getAbsolutePath());

	            while ((count = input.read(data)) != -1) {
	                output.write(data, 0, count);
	            }
	            output.flush();
	            output.close();
	            input.close();
	            
	            objUrl = new URL(url+"obj/s3_"+queryString);
				connection = objUrl.openConnection();
	            connection.connect();
	            objFile = new File(getApplicationContext().getFilesDir().getPath().toString()+"/s3_"+queryString);
	            if (!objFile.exists()){
	               objFile.createNewFile();
	            }
	         // download the file
	            input = new BufferedInputStream(objUrl.openStream());
	            output = new FileOutputStream(objFile.getAbsolutePath());

	            while ((count = input.read(data)) != -1) {
	                output.write(data, 0, count);
	            }
	            output.flush();
	            output.close();
	            input.close();
	            
				mHandler.post(mUpdateResults);
			} catch (IOException e) {
				Log.e("data", "IOException");
				e.printStackTrace();
			} catch (Exception e) {
				Log.e("data", "Exception");
				e.printStackTrace();
			}
		}
	}
} 