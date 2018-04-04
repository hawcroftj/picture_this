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
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

public class Main extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences prefs;
    private GoogleSignInAccount account;

    // define app level constants
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_FIND_PLACE = 2;
    static final int REQUEST_PICK_IMAGE = 3;
    static final String TAG = "PT_log";

    // Google sign in variables
    private final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    // request user id, email address, and profile (DEFAULT SIGN IN)
    private GoogleSignInOptions googleSignInOptions =
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize app for Firebase
        FirebaseApp.initializeApp(this);

        // initialize preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // GoogleSignInClient using options from googleSignInOptions
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        Button btnMap = findViewById(R.id.btnMap);
        Button btnListPlace = findViewById(R.id.btnListPlace);
        Button btnNewPlace = findViewById(R.id.btnNewPlace);
        Button btnSettings = findViewById(R.id.btnSettings);
        SignInButton btnSignIn = findViewById(R.id.btnSignIn);

        btnMap.setOnClickListener(this);
        btnListPlace.setOnClickListener(this);
        btnNewPlace.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // check for an existing Google sign in, returns null if no sign in found
        account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
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
            case R.id.btnSignIn:
                signIn();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if sign in is successful, receive the signed in account
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if(account == null) {
            Log.d(TAG, "updateUI null");
        } else {
            Log.d(TAG, "updateUI account");
        }
    }

    private void signIn() {
        // request user sign in
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            Log.d(TAG, account.getDisplayName() + " " + account.getEmail() + " " + account.getId());

            updateUI(account);
        } catch (ApiException e) {
            Log.d(TAG, "handleSignInResult failed = " + e.getStatusCode());
            updateUI(null);
        }
    }
}
