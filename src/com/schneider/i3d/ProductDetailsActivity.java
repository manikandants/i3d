package com.schneider.i3d;

import java.io.IOException;

import us.monoid.web.JSONResource;
import us.monoid.web.Resty;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class ProductDetailsActivity extends Activity {
	final Handler mHandler = new Handler();
	private Product product = new Product();;
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			TextView productName = (TextView) findViewById(R.id.itvProductName);
			TextView productType = (TextView) findViewById(R.id.itvProductType);
			TextView country = (TextView) findViewById(R.id.itvCountry);
			TextView segment = (TextView) findViewById(R.id.itvSegment);
 			productName.setText(product.productName);
 			productType.setText(product.productType);
 			country.setText(product.country);
 			segment.setText(product.segment);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_details);
        Thread thread = new Thread(new Request());
        thread.start();
	}
	class Request implements Runnable{
		@Override
        public void run() {
        	Resty resty = new Resty();
        	JSONResource jsonResource;
     		try {
     			jsonResource = resty.json("http://192.168.0.204:3000/productlist/1");
     			product.productName = jsonResource.get("productName").toString();
     			product.productType = jsonResource.get("productType").toString();
     			product.country = jsonResource.get("country").toString();
     			product.segment = jsonResource.get("segment").toString();
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
