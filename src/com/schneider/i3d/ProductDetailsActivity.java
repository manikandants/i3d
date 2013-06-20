package com.schneider.i3d;

import java.io.IOException;

import us.monoid.web.JSONResource;
import us.monoid.web.Resty;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ProductDetailsActivity extends Activity {
	final Handler mHandler = new Handler();
	private Product product = new Product();
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
	private String queryString;
	protected Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_details);
		final Bundle bundle = this.getIntent().getExtras();
		queryString = bundle.getString("com.schneider.message");
		Thread thread = new Thread(new Request());
		thread.start();
		ImageButton button = (ImageButton) findViewById(R.id.iib3d);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(ProductDetailsActivity.this,Obj3DView.class);
				intent.putExtra("com.schneider.message", product.objName);
				startActivity(intent); 
			}
		});
	}
	class Request implements Runnable{
		final String url = getResources().getString(R.string.server_address);
		@Override
		public void run() {
			Resty resty = new Resty();
			JSONResource jsonResource;
			try {
				String fullUrl = url + queryString;
				Log.e("TAG", fullUrl);
				jsonResource = resty.json(fullUrl);
				product.productName = jsonResource.get("productName").toString();
				product.productType = jsonResource.get("productType").toString();
				product.country = jsonResource.get("country").toString();
				product.segment = jsonResource.get("segment").toString();
				product.objName = jsonResource.get("objName").toString();
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
