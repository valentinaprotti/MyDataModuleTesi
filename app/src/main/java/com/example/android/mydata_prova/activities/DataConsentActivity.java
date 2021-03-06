package com.example.android.mydata_prova.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.mydata_prova.R;

import java.util.StringTokenizer;

public class DataConsentActivity extends AppCompatActivity {

	private TextView mConsentTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_consent);

		mConsentTextView = (TextView) findViewById(R.id.tv_dataconsent);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		populateConsentTextView();
	}

	private void populateConsentTextView() {
		mConsentTextView.setText("");
		String allDConsents = this.getIntent().getStringExtra(Intent.EXTRA_TEXT);
		if (allDConsents != null) {
			StringTokenizer st = new StringTokenizer(allDConsents, System.getProperty("line.separator"));
			int count = st.countTokens();
			for (int i=0; i<count; i++) {
				mConsentTextView.append("• ");
				mConsentTextView.append(st.nextToken());
				mConsentTextView.append(System.getProperty("line.separator"));
				mConsentTextView.append(System.getProperty("line.separator"));
			}
		}
	}

	@Override
	public void onBackPressed() {
		NavUtils.navigateUpFromSameTask(this);
		super.onBackPressed();
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
}
