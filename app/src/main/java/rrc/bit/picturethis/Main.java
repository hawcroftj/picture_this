package rrc.bit.picturethis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

import org.w3c.dom.Text;

public class Main extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences prefs;
    private GoogleSignInAccount account;

    // define app level constants
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_FIND_PLACE = 2;
    static final int REQUEST_PICK_IMAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize app for Firebase
        FirebaseApp.initializeApp(this);

        // initialize preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Button btnMap = findViewById(R.id.btnMap);
        Button btnListPlace = findViewById(R.id.btnListPlace);
        Button btnNewPlace = findViewById(R.id.btnNewPlace);
        Button btnSettings = findViewById(R.id.btnSettings);

        btnMap.setOnClickListener(this);
        btnListPlace.setOnClickListener(this);
        btnNewPlace.setOnClickListener(this);
        btnSettings.setOnClickListener(this);

        // get the user account
        Intent intent = getIntent();
        account = intent.getParcelableExtra("account");
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        try {
            tvWelcome.setText(String.format("Welcome, %s", account.getDisplayName()));
        } catch(NullPointerException e) { }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnMap:
                startActivity(new Intent(this, Map.class));
                break;
            case R.id.btnListPlace:
                Intent iListPlace = new Intent(this, ListPlace.class);
                iListPlace.putExtra("account", account);
                startActivity(iListPlace);
                break;
            case R.id.btnNewPlace:
                Intent iNewPlace = new Intent(this, NewPlace.class);
                iNewPlace.putExtra("account", account);
                startActivity(iNewPlace);
                break;
            case R.id.btnSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }
}
