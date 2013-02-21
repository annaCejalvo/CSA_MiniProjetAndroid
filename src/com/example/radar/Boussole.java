package com.example.radar;

import android.app.Activity;
import android.os.Bundle;

public class Boussole extends Activity {
	//La vue de notre boussole
	private BoussoleView compassView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accueil);
		//On r�cup�re notre vue
		compassView = (BoussoleView)findViewById(R.id.boussoleView);
		//Et on essaie de faire pointer notre aiguille du Nord au point � 45�
		compassView.setNorthOrientation(45);
	}
}
