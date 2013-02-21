package com.example.radar;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PhotoVille extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_ville);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_photo_ville, menu);
		return true;
	}
	
	public void methodeAcceuil(){
		
	}

}
