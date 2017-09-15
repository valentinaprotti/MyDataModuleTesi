package com.example.android.mydata_prova.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.example.android.mydata_prova.R;
import com.example.android.mydata_prova.controller.IController;
import com.example.android.mydata_prova.controller.MyController;
import com.example.android.mydata_prova.model.MyData.MyData;
import com.example.android.mydata_prova.model.consents.ServiceConsent;
import com.example.android.mydata_prova.model.services.ServiceProva;
import com.example.android.mydata_prova.model.users.IUser;
import com.example.android.mydata_prova.utilities.VoiceSupport;

import java.util.Locale;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/* Questa classe rappresenta l'interfaccia grafica che va inserita all'interno dell'applicazione
 * di GoBoBus (aggiornata con l'interfaccia p2p) per consentire all'utente (si scelga se modellare
 * l'utente, mantenendone ad esempio un database in cloud che a questo livello risulterebbe ancora
 * abbastanza inutile, oppure farne a meno gestendo solamente le preferenze dell'utente a livello
 * locale) di gestire nel dettaglio i dati condivisi con gli altri utenti della rete di dispositivi
 * che devono scambiarsi i dati.
 *
 * Allo stato attuale i dati qui inseriti sono di prova, non avendo sotto mano i veri dati di cui
 * dover regolare lo scambio, ma il meccanismo è quello corretto: ho la possibilità di scegliere
 * quali dati condividere e quali no, oppure di disattivare lo scambio dei dati, oppure di revocare
 * del tutto il consenso allo scambio dei dati. In questi ultimi due casi è ancora da stabilire
 * cosa ciò comporti nell'utilizzo dell'applicazione (probabilmente nulla? Oppure si può scegliere
 * di impedire la fruizione dei dati in tempo reale forniti dalla rete degli utenti se non si
 * condividono i propri?).
 *
 * Nello specifico questa classe si appoggia ad un controller (MyController) che regola la creazione
 * e l'accesso dell'utente (per ora hard cabled in attesa di sapere se possa essere di interesse,
 * come indicato sopra) e l'eventuale creazione di un account utente presso il servizio, l'unico
 * per ora, che dovrebbe essere GoBoBus (attualmente servizio di prova).
 * Inizializza poi l'interfaccia grafica con le attuali preferenze dell'utente: in questo caso
 * cerca se l'utente desideri l'assistente vocale (in tal caso lo inizializza) e dovrebbe cercare
 * se esiste ed è attivo, disabilitato o revocato il service consent e quali dei dati l'utente abbia
 * interesse a condividere e quali meno, presumibilmente attingendo per il primo caso dai service
 * consent dell'utente, e nel secondo caso nei data consent..? Nel dubbio ho inserito la preferenza
 * tra le sharedpreferences come tutto il resto.
 */

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Switch mLocationSwitch;
    private Button mDisableButton;
    private Button mWithdrawButton;
	private TextToSpeech tts;

	private boolean voiceSupport;
	private SharedPreferences sharedPreferences;
	private IUser user;
	private boolean locationConsent;
	private ServiceProva serviceProva;
	private ServiceConsent userSC;
	private IController controller;
	private String email;
	private String pass;
	private Switch mOtherSwitch;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

		mLocationSwitch = (Switch) findViewById(R.id.locationSwitch);
		mOtherSwitch = (Switch) findViewById(R.id.otherSwitch);
		mDisableButton = (Button) findViewById(R.id.button_disable);
		mDisableButton.setOnClickListener(this);
		mWithdrawButton = (Button) findViewById(R.id.button_withdraw);
		mWithdrawButton.setOnClickListener(this);

		setTitle("Gestione Consent");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


		// servizio a cui si riferisce
		serviceProva = new ServiceProva();

		email = "nomecognome@prova.it";
		pass = "password";
		if (this.getIntent().hasExtra(Intent.EXTRA_EMAIL)) {
			email = this.getIntent().getStringExtra(Intent.EXTRA_EMAIL);
		}
		if (this.getIntent().hasExtra(Intent.EXTRA_TEXT)) {
			pass = this.getIntent().getStringExtra(Intent.EXTRA_TEXT);
		}

		controller = new MyController();
		controller.logInUser(email, pass.toCharArray());
		user = MyData.getInstance().loginUser(email, pass.toCharArray());

		userSC = user.getActiveSCForService(serviceProva);
		if (userSC == null) {
			// l'utente ha un account ma non è attivo: inizializzo di conseguenza la gui
			mDisableButton.setText("Abilita\n" +
					"consenso");
			mLocationSwitch.setEnabled(false);
			mOtherSwitch.setEnabled(false);
		}

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

		// ottengo l'attuale preferenza per la condivisione della posizione
		// TODO: NOTA: per adesso c'è solo la posizione come dato, ed ho evitato di farlo,
		// ma nel caso tutti i dati non avessero il consenso ad essere condivisi è come aver
		// disabilitato il service consent del tutto
		locationConsent = sharedPreferences.getBoolean("LocationConsent", true); // corretto salvarlo nelle preferences?
		// dovrei recuperarlo dal DataConsent per questo servizio..?

		mLocationSwitch.setChecked(locationConsent);
		mLocationSwitch.setTextOn("ON");
		mLocationSwitch.setTextOff("OFF");
		mLocationSwitch.setOnClickListener(this);

		// se arrivo in questa schermata dopo aver creato un nuovo account:
		if (this.getIntent().hasExtra(EXTRA_MESSAGE))
			Toast.makeText(this, this.getIntent().getStringExtra(EXTRA_MESSAGE), Toast.LENGTH_SHORT).show();
    }

	@Override
	public void onBackPressed() {
		Intent i = new Intent(this, MainActivity.class);
		i.putExtra("EXTRA_CLOSED", "Sessione terminata");
		startActivity(i);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
			 * Handler di tutti i pulsanti e gli switch che avvia il metodo corretto
			 */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_disable:
            	disableOption(view);
                break;
            case R.id.button_withdraw:
            	withdrawOption(view);
                break;
			case R.id.locationSwitch:
				changeLocationConsent(view);
				break;
        }
    }

    /* Questo metodo viene invocato al cambio della preferenza della condivisione della posizione
     * (switch). Esso chiede conferma della decisione dell'utente, con supporto vocale, e se
     * confermato, modifica la preferenza dell'utente nella condivisione di quel dato nelle
     * sharedpreferences. Contestualmente deve anche creare un nuovo dataconsent? Direi di no..
     * Se non viene confermato, lo switch torna nella posizione precedente e nulla cambia.
     */
	public void changeLocationConsent(View view) {
		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(this)){
				tts.speak("Il consenso per la posizione sarà " + (mLocationSwitch.isChecked() ? "attivato" : "disattivato") + ". Procedere?", TextToSpeech.QUEUE_FLUSH, null);
			}
		Toast.makeText(this, "Il consenso per la posizione sarà " + (mLocationSwitch.isChecked() ? "attivato" : "disattivato") + ". Procedere?", Toast.LENGTH_SHORT).show();

		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(mLocationSwitch.isChecked() ? "Attiva" : "Disattiva")
				.setMessage("Il consenso per la posizione sarà " + (mLocationSwitch.isChecked() ? "attivato" : "disattivato") + ".\nProcedere?")
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// cambia la preferenza nelle sharedPreferences
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putBoolean("LocationConsent", mLocationSwitch.isChecked());
						editor.commit();

						// cambia lo status del consent per la posizione
						/*Set<String> data = new HashSet<String>();
						if (mLocationSwitch.isChecked()) {
							data.add(Metadata.DATOUNOPROVA_CONST);
							data.add(Metadata.DATODUEPROVA_CONST);
						} else {
							data.add(Metadata.DATODUEPROVA_CONST);
						}
						OutputDataConsent outputDataConsent = new OutputDataConsent(data, userSC);
						user.addDataConsent(outputDataConsent, serviceProva);*/
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mLocationSwitch.setChecked(!mLocationSwitch.isChecked());
					}
				})
				.show();
	}

	/* Questo metodo viene invocato alla pressione del pulsante "Revoca consenso".
	 * Anch'esso chiede conferma della decisione dell'utente, con supporto vocale, e se
	 * confermato, cambia opportunamente lo stato del service consent. TODO: In questo caso dovrebbe
	 * completamente lanciare una nuova schermata, la stessa della creazione dell'account presso
	 * questo servizio che ho messo nel to do in alto, perché il consent è stato revocato e non
	 * è più attivabile da questo status (withdrawn).
	 * Se non viene confermato, nulla cambia.
	 */
	public void withdrawOption(final View view) {
		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(this)){
				tts.speak("Il consenso sarà revocato. Procedere?", TextToSpeech.QUEUE_FLUSH, null);
			}
		Toast.makeText(this, "Il consenso sarà revocato. Procedere?", Toast.LENGTH_SHORT).show();

		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Revoca")
				.setMessage("Verrà eliminato l'account presso questo servizio.\nProcedere?")
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// cambia lo stato del consent a withdrawn
						controller.withdrawConsentForService(serviceProva);
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.remove("LocationConsent");
						editor.commit();

						// avvia l'activity per creare un nuovo account MyData
						Intent i = new Intent (UserProfileActivity.this, NewAccountActivity.class);
						i.putExtra(EXTRA_MESSAGE, "Account eliminato con successo");
						i.putExtra(Intent.EXTRA_EMAIL, email);
						i.putExtra(Intent.EXTRA_TEXT, pass);
						startActivity(i);
					}
				})
				.setNegativeButton("No", null)
				.show();
	}

	/* Questo metodo viene invocato alla pressione del pulsante "Disabilita/Abilita consenso".
     * Anch'esso chiede conferma della decisione dell'utente, con supporto vocale, e se
     * confermato, cambia opportunamente lo stato del service consent. Disattiva o attiva inoltre
     * tutti gli switch e cambia il testo del pulsante in modo opportuno.
     * Se non viene confermato, nulla cambia.
     */
	public void disableOption(View v){
		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(this)){
				tts.speak("Il consenso sarà " + ((mDisableButton.getText().toString().contains("Disabilita")) ? "disattivato" : "attivato") + ". Procedere?", TextToSpeech.QUEUE_FLUSH, null);
			}
		Toast.makeText(this, "Il consenso sarà " + ((mDisableButton.getText().toString().contains("Disabilita")) ? "disattivato" : "attivato") + ". Procedere?", Toast.LENGTH_SHORT).show();

		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(mDisableButton.getText().toString().contains("Disabilita") ? "Disabilita" : "Abilita")
				.setMessage("Procedere?")
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// cambia lo stato del consent enabled>disabled o disabled>enabled
						// cambia il pulsante ad "Attiva"/"Disattiva"
						if (mDisableButton.getText().toString().contains("Disabilita")) {
							controller.toggleStatus(serviceProva, false);
							mDisableButton.setText("Abilita\n" +
									"consenso");
							mLocationSwitch.setEnabled(false);
							mOtherSwitch.setEnabled(false);
						} else {
							controller.toggleStatus(serviceProva, true);
							mDisableButton.setText("Disabilita\n" +
									"consenso");
							mLocationSwitch.setEnabled(true);
							mOtherSwitch.setEnabled(true);
						}
					}
				})
				.setNegativeButton("No", null)
				.show();
	}

	/* Questo metodo viene invocato alla pressione del pulsante "Mostra Data Consents".
	 * Esso apre una schermata che stampa a video tutti i Data Consent relativi a tutti gli eventuali
	 * account che l'utente ha presso questo servizio.
	 * Nella versione finale probabilmente non avrà ragione d'essere, ma per testing è utile.
	 */
	public void viewDataConsents(View v) {
		String allDConsents = controller.getAllDConsents(serviceProva);
		Intent i = new Intent(this,DataConsentActivity.class);
		i.putExtra(Intent.EXTRA_TEXT, allDConsents);
		startActivity(i);
	}
}
