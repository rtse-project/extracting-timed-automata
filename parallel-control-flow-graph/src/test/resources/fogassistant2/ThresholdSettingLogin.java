package fogassistant2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ThresholdSettingLogin extends ActionBarActivity {
	
	private EditText password_field;
	private TextView password_info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_threshold_setting_login);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		password_field = (EditText) findViewById(R.id.edit_password);
		password_info = (TextView) findViewById(R.id.password_info);
	}
	
    public void login(View view){
		
		if (password_field.getText().toString().equals("freezing")){
			password_info.setText("Password corretta");
			Intent intent = new Intent(this, ThresholdSetting.class);
	    	startActivity(intent);
		}
		else {
			password_info.setText("Password sbagliata");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.threshold_setting_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
