package com.example.android.mydata_prova.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.mydata_prova.R;
import com.example.android.mydata_prova.controller.IController;
import com.example.android.mydata_prova.controller.MyController;
import com.example.android.mydata_prova.model.services.AbstractService;
import com.example.android.mydata_prova.model.services.ServiceProva;
import com.example.android.mydata_prova.utilities.VoiceSupport;

import java.util.Locale;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class NewAccountActivity extends AppCompatActivity implements View.OnClickListener {

	private ImageButton mNewAccountButton;
	private SharedPreferences sharedPreferences;
	private boolean voiceSupport;
	private TextToSpeech tts;
	private String email;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_account);

		mNewAccountButton = (ImageButton) findViewById(R.id.button_add);
		mNewAccountButton.setOnClickListener(this);

		// controllo se l'utente preferisce l'assistente vocale o meno
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		voiceSupport = sharedPreferences.getBoolean("VoiceSupport", true);

		if(voiceSupport) {
			tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int status) {
					if (status != TextToSpeech.ERROR) {
						tts.setLanguage(Locale.getDefault());
					}
				}
			});
		}

		if (this.getIntent().hasExtra(Intent.EXTRA_EMAIL)) {
			email = this.getIntent().getStringExtra(Intent.EXTRA_EMAIL);
		}
		if (this.getIntent().hasExtra(Intent.EXTRA_TEXT)) {
			password = this.getIntent().getStringExtra(Intent.EXTRA_TEXT);
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.button_add) {
			newAccount(view);
		}
	}

	private void newAccount(View v) {
		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(this)){
				tts.speak("Sta per essere creato un nuovo account MyData per questo servizio. Procedere?", TextToSpeech.QUEUE_FLUSH, null);
			}
		Toast.makeText(this, "Sta per essere creato un nuovo account MyData per questo servizio. Procedere?", Toast.LENGTH_SHORT).show();

		if (!isFinishing()) {
			new AlertDialog.Builder(NewAccountActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Nuovo account MyData")
					.setMessage("Procedere?")
					.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// viene creato un account per l'utente
							// vengo reindirizzato alla schermata di gestione dei consent
							// TODO: fare sì che tornando indietro dalla successiva schermata non si possa ritornare a questa (finish()?)
							AbstractService serviceProva = new ServiceProva();
							IController controller = new MyController();
							controller.logInUser("nomecognome@prova.it", "password".toCharArray());
							controller.addService(serviceProva);

							SharedPreferences.Editor editor = sharedPreferences.edit();
							editor.putBoolean("LocationConsent", true);
							editor.commit();

							Intent i = new Intent(NewAccountActivity.this, UserProfileActivity.class);
							i.putExtra(EXTRA_MESSAGE, "Account creato con successo");
							i.putExtra(Intent.EXTRA_EMAIL, email);
							i.putExtra(Intent.EXTRA_TEXT, password);
							startActivity(i);
						}
					})
					.setNegativeButton("No", null)
					.show();
		}
	}
}
