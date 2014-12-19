package com.example.tryingmap;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {
	private GoogleMap googleMap;
	EditText et;
	public LatLng point;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		googleMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		et = (EditText) findViewById(R.id.add);

	}
	
	public void onClick_test(View v){
		Intent i = new Intent(MainActivity.this , test.class);
		Bundle basket = new Bundle();
		basket.putDouble("lng", point.longitude);
		basket.putDouble("lat",point.latitude);
		i.putExtras(basket);
		startActivity(i);
	}
	
	public void onClick_Find(View v) {
		String staddress = et.getText().toString();
		fetchLatLongFromService loc = new fetchLatLongFromService(staddress.replace(" ", "%20"));
		loc.execute();
	}

	public class fetchLatLongFromService extends
			AsyncTask<Void, Void, StringBuilder> {
		String place;

		public fetchLatLongFromService(String place) {
			super();
			this.place = place;

		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			this.cancel(true);
		}
		

		@Override
		protected StringBuilder doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				HttpURLConnection conn = null;
				StringBuilder jsonResults = new StringBuilder();
				String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?address="
						+ this.place + "&sensor=false";

				URL url = new URL(googleMapUrl);
				conn = (HttpURLConnection) url.openConnection();
				InputStreamReader in = new InputStreamReader(
						conn.getInputStream());
				int read;
				char[] buff = new char[1024];
				while ((read = in.read(buff)) != -1) {
					jsonResults.append(buff, 0, read);
				}
				String a = "";
				return jsonResults;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(StringBuilder result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				JSONObject jsonObj = new JSONObject(result.toString());
				JSONArray resultJsonArray = jsonObj.getJSONArray("results");

				// Extract the Place descriptions from the results
				// resultList = new ArrayList<String>(resultJsonArray.length());

				JSONObject before_geometry_jsonObj = resultJsonArray
						.getJSONObject(0);

				JSONObject geometry_jsonObj = before_geometry_jsonObj
						.getJSONObject("geometry");

				JSONObject location_jsonObj = geometry_jsonObj
						.getJSONObject("location");

				String lat_helper = location_jsonObj.getString("lat");
				double lat = Double.valueOf(lat_helper);

				String lng_helper = location_jsonObj.getString("lng");
				double lng = Double.valueOf(lng_helper);

				point = new LatLng(lat, lng);
				CameraUpdate update = CameraUpdateFactory.newLatLngZoom(point,
						16);
				googleMap.addMarker(new MarkerOptions().position(point).title(
						"Your Location"));
				googleMap.animateCamera(update);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}

	}
}