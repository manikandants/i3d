package com.schneider.i3d;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

import com.schneider.scanner.IntentIntegrator;
import com.schneider.scanner.IntentResult;

public class MainActivity extends Activity implements ViewFactory{
	protected static final String MESSAGE = "com.schneider.message";
    private ImageSwitcher mSwitcher;
	private int i=0;
	protected Intent intent;
	private String SearchString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		mSwitcher = (ImageSwitcher) findViewById(R.id.latestUpdate);
        mSwitcher.setFactory(this);
        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_in));
        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_out));
        mSwitcher.setBackgroundColor(Color.WHITE);
        final Handler mHandler = new Handler();
        final Runnable mUpdateResults = new Runnable() {
            public void run() {
     	        mSwitcher.setImageResource(mImageIds[++i%mImageIds.length]);
             }
        };
         int delay = 200;
         int period = mImageIds.length*delay;
         Timer timer = new Timer();
         timer.scheduleAtFixedRate(new TimerTask() {
 
        public void run() {
              mHandler.post(mUpdateResults);
         }
      }, delay, period);
         ImageButton bScan = (ImageButton) findViewById(R.id.ibScan);
         ImageButton bSearch = (ImageButton) findViewById(R.id.ibSearch);
         final EditText eSearch = (EditText) findViewById(R.id.ieQuery);
         bScan.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				IntentIntegrator.initiateScan(MainActivity.this);
 			}
 		});
         bSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SearchString = eSearch.getText().toString();
				intent = new Intent(MainActivity.this,ProductDetailsActivity.class);
	    		intent.putExtra(MESSAGE, SearchString);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	 private Integer[] mImageIds = { R.drawable.anya, R.drawable.asfora,
	            R.drawable.merten, R.drawable.ovalis, R.drawable.sedna,
	            R.drawable.ultimate, R.drawable.unica, R.drawable.vivace,
	            R.drawable.zencelo};

	public View makeView() {
		ImageView i = new ImageView(this);
        i.setBackgroundColor(0xFFFFFFFF);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        return i;
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
	    	case IntentIntegrator.REQUEST_CODE: {
		    	if (resultCode != RESULT_CANCELED) {
			    	IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
			    	if (scanResult != null) {
			    		SearchString = scanResult.getContents();
			    		Intent intent = new Intent(MainActivity.this,ProductDetailsActivity.class);
			    		intent.putExtra("com.schneider.message", SearchString);
						startActivity(intent);
			    	}
		    	}
		    	break;
	    	}
    	}
    }
}
