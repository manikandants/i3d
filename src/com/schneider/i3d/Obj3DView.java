package com.schneider.i3d;

import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.widget.FrameLayout;
public class Obj3DView extends RendererActivity {  
  private Object3dContainer faceObject3D;  
  private Camera mCamera;
  private CameraPreview mPreview;
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
	  FrameLayout ll = (FrameLayout) this.findViewById(R.id.background);
	// Create an instance of Camera
      mCamera = getCameraInstance();

      // Create our Preview view and set it as the content of our activity.
      mPreview = new CameraPreview(this, mCamera);
      ll.addView(mPreview);
      ll.addView(_glSurfaceView);
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
    IParser myParser = Parser.createParser(Parser.Type.OBJ, getResources(), getResources().getResourceName(R.raw.cube_obj),true);  
    myParser.parse();  
    faceObject3D = myParser.getParsedObject();  
    faceObject3D.position().x = faceObject3D.position().y = faceObject3D.position().z = 0;  
    faceObject3D.scale().x = faceObject3D.scale().y = faceObject3D.scale().z = 1f;
    faceObject3D.colorMaterialEnabled(true);
    // Depending on the model you will need to change the scale faceObject3D.scale().x = faceObject3D.scale().y = faceObject3D.scale().z = 0.009f;  
    scene.backgroundColor().setAll(0x00000000);
    scene.addChild(faceObject3D);
  }  
  @Override  
  public void updateScene() {  
    //faceObject3D.rotation().x += 1;
    faceObject3D.rotation().y += 1;
    //faceObject3D.rotation().z += 1;
  }  
} 