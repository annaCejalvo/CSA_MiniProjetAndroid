package com.example.radar;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class PhotoVille extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Button accueil = (Button) findViewById(R.id.button1);
		
		setContentView(R.layout.activity_photo_ville);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_photo_ville, menu);
		return true;
	}
	
	public void onClickAccueil(View v){
		Intent i = new Intent(this, AccueilActivity.class);
		startActivity(i);
	}

}
