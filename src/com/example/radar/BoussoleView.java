package com.example.radar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class BoussoleView extends View {
	//~--- fields -------------------------------------------------------------
	private Paint circlePaint;
	private Paint northPaint;
	private Paint southPaint;

	private Path trianglePath;
	
	//Rotation vers la droite en degree pour pointer le Nord 
	private float northOrientation=0;

	//~--- constructors -------------------------------------------------------
	// Constructeur
	public BoussoleView(Context context) {
		super(context);
		initView();
	}

	// Constructeur utilisé pour instancier la vue depuis sa
	// déclaration dans un fichier XML
	public BoussoleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	// idem au précédant
	public BoussoleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	//~--- get methods --------------------------------------------------------
	// permet de récupérer l'orientation de la boussole
	public float getNorthOrientation() {
		return northOrientation;
	}

	//~--- set methods --------------------------------------------------------

	// permet de changer l'orientation de la boussole
	public void setNorthOrientation(float rotation) {
		//on met à jour l'orientation uniquement si elle a changé
		if (rotation != this.northOrientation)
		{
			this.northOrientation = rotation;
			//on demande à notre vue de se redessiner
			this.invalidate();
		}
	}

	//~--- methods ------------------------------------------------------------
	// Permet de définir la taille de notre vue
	// /!\ par défaut un cadre de 100x100 si non redéfini
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredWidth  = measure(widthMeasureSpec);
		int measuredHeight = measure(heightMeasureSpec);

		// Notre vue sera un carré, on garde donc le minimum
		int d = Math.min(measuredWidth, measuredHeight);

		setMeasuredDimension(d, d);
	}

	// Déterminer la taille de notre vue
	private int measure(int measureSpec) {
		int result   = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.UNSPECIFIED) {
			// Le parent ne nous a pas donné d'indications,
			// on fixe donc une taille
			result = 150;
		} else {
			// On va prendre la taille de la vue parente
			result = specSize;
		}

		return result;
	}
	
	private void initView() {
	    Resources r = this.getResources();

	    // Paint pour l'arrière plan de la boussole
	    circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Lisser les formes
	    circlePaint.setColor(r.getColor(R.color.compassCircle)); // Définir la couleur

	    // Paint pour les 2 aiguilles, Nord et Sud
	    northPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    northPaint.setColor(r.getColor(R.color.northPointer));
	    southPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    southPaint.setColor(r.getColor(R.color.southPointer));

	    // Path pour dessiner les aiguilles
	    trianglePath = new Path();
	}

	// Appelée pour redessiner la vue
	@Override
	protected void onDraw(Canvas canvas) {
		//On détermine le point au centre de notre vue
	    int centerX = getMeasuredWidth() / 2;
	    int centerY = getMeasuredHeight() / 2;

	    // On détermine le diamètre du cercle (arrière plan de la boussole)
	    int radius = Math.min(centerX, centerY);

	  	// On dessine un cercle avec le " pinceau " circlePaint
	    canvas.drawCircle(centerX, centerY, radius, circlePaint);

	    // On sauvegarde la position initiale du canevas
	    canvas.save();

	    // On tourne le canevas pour que le nord pointe vers le haut
	    canvas.rotate(-northOrientation, centerX, centerY);

	    // on créer une forme triangulaire qui part du centre du cercle et
	    // pointe vers le haut
	    trianglePath.reset();//RAZ du path (une seule instance)
	    trianglePath.moveTo(centerX, 10);
	    trianglePath.lineTo(centerX - 10, centerY);
	    trianglePath.lineTo(centerX + 10, centerY);

	    // On désigne l'aiguille Nord
	    canvas.drawPath(trianglePath, northPaint);
	    
	    // On tourne notre vue de 180° pour désigner l'auguille Sud
	    canvas.rotate(180, centerX, centerY);
	    canvas.drawPath(trianglePath, southPaint);

	    // On restaure la position initiale (inutile dans notre exemple, mais prévoyant)
	    canvas.restore();
	}
	
	private void setActionButtons() {
		// TODO Auto-generated method stub
		Button btn_geolocalisation = (Button) findViewById(R.id.btn_geo);
		btn_geolocalisation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				new PhotoVille();
			}
		});
		
		Button btn_alentours = (Button) findViewById(R.id.btn_alentours);
		btn_alentours.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//Intent i = new Intent(this, HelloGoogleMapActivity.class);
				//startActivity(i);
			}
		});
	}
}
