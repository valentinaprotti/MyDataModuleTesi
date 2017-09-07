package com.example.android.mydata_prova.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.android.mydata_prova.R;
import com.example.android.mydata_prova.controller.IController;
import com.example.android.mydata_prova.controller.MyController;
import com.example.android.mydata_prova.model.MyData.MyData;
import com.example.android.mydata_prova.model.services.AbstractService;
import com.example.android.mydata_prova.model.services.ServiceProva;
import com.example.android.mydata_prova.model.users.IUser;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mEnterButton;
	private IController controller;
	private IUser user;
	private AbstractService serviceProva;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEnterButton = (Button) findViewById(R.id.button_enter);
        mEnterButton.setOnClickListener(this);

		// servizio a cui si riferisce
		serviceProva = new ServiceProva();

		// TODO: utente che sta utilizzando l'applicazione
		// (per ora inizializzato così in attesa del lavoro aggiornato dell'app in cui inserire,
		// eventualmente, l'utente nel modello)
		controller = new MyController();
		controller.createMyDataUser("Nome", "Cognome", new Date(1995, 9, 22), "nomecognome@prova.it", "password".toCharArray());
		user = MyData.getInstance().loginUser("nomecognome@prova.it", "password".toCharArray());


		// per test
		// controller.addService(serviceProva);
    }

    @Override
    public void onClick(View view) {
        Intent i = null;
        switch(view.getId()){
            case R.id.button_enter:
				if (user.hasAccountAtService(serviceProva)) {
					// L'utente ha un account presso il servizio: va avviata l'activity UserProfileActivity
					i = new Intent(MainActivity.this, UserProfileActivity.class);
				} else {
					// No account presso il servizio: va avviata l'activity NewAccountActivity
					i = new Intent(MainActivity.this, NewAccountActivity.class);
				}
        }
		i.putExtra(Intent.EXTRA_EMAIL, "nomecognome@prova.it");
		i.putExtra(Intent.EXTRA_TEXT, "password");
        startActivity(i);
    }
}