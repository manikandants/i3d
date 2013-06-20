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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
public class Obj3DView extends RendererActivity {  
	final Handler mHandler = new Handler();
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			ll.addView(_glSurfaceView);
		}
	};
	private Object3dContainer faceObject3D;  
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
		IParser myParser = Parser.createParserFromFile(getApplicationContext(),queryString,true);  
		myParser.parseFromFile();  
		faceObject3D = myParser.getParsedObject();  
		faceObject3D.position().x = faceObject3D.position().y = faceObject3D.position().z = 0;  
		faceObject3D.scale().x = faceObject3D.scale().y = faceObject3D.scale().z = 0.05f;
		faceObject3D.colorMaterialEnabled(true);
		faceObject3D.vertexColorsEnabled(false);
		faceObject3D.defaultColor(new Color4(0x00,0x00,0x7f,0xff));
		// Depending on the model you will need to change the scale faceObject3D.scale().x = faceObject3D.scale().y = faceObject3D.scale().z = 0.009f;  
		scene.backgroundColor().setAll(0x00000000);
		scene.addChild(faceObject3D);
	}  
	@Override  
	public void updateScene() {  
		faceObject3D.rotation().x = xRotation;
		faceObject3D.rotation().y = yRotation;
		faceObject3D.defaultColor(colors[colorCount]);
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
		private static final int SWIPE_MAX_OFF_PATH = 200;
		private static final int SWIPE_THRESHOLD_VELOCITY = 20;
		private static final int ROTATION_SPEED_DIVISION = 3;

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
					yRotation += Math.abs(dX)/ROTATION_SPEED_DIVISION;
				} else {
					//Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
					yRotation -= Math.abs(dX)/ROTATION_SPEED_DIVISION;
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
				URL objUrl = new URL(url+"obj/"+queryString);
				URLConnection connection = objUrl.openConnection();
	            connection.connect();
	            int fileLength = connection.getContentLength();
	            File objFile = new File(getApplicationContext().getFilesDir().getPath().toString()+"/"+queryString);
	            if (!objFile.exists()){
	               objFile.createNewFile();
	            }
	         // download the file
	            InputStream input = new BufferedInputStream(objUrl.openStream());
	            OutputStream output = new FileOutputStream(objFile.getAbsolutePath());

	            byte data[] = new byte[fileLength];
	            int count;
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