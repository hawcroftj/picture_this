package rrc.bit.picturethis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Login extends AppCompatActivity {
    private GoogleSignInAccount account;
    // Google sign in variables
    private final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    // request user id, email address, and profile (DEFAULT SIGN IN)
    private GoogleSignInOptions googleSignInOptions =
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // GoogleSignInClient using options from googleSignInOptions
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        SignInButton btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // check for an existing Google sign in, returns null if no sign in found
        account = GoogleSignIn.getLastSignedInAccount(this);
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

    private void signIn() {
        // request user sign in
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            if(completedTask.isSuccessful() && account != null) {
                Intent intent = new Intent(this, Main.class);
                intent.putExtra("account", account);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error signing in.", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) { }
    }
}
