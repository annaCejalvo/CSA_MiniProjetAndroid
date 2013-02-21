package com.example.radar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class AccueilActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Button b = (Button) findViewById(R.id.btn_alentours);
		
		setContentView(R.layout.accueil);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.accueil, menu);
		return true;
	}
	
	public void onClickGeo(View v){
		Intent i = new Intent(this, PhotoVille.class);
		startActivity(i);
	}

}
