package rrc.bit.picturethis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewPlace extends AppCompatActivity {

    EditText etTitle, etDescription;
    Button btnSubmit;

    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place);

        FirebaseApp.initializeApp(this);
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        // get "place" from database
        final DatabaseReference databasePlaces = db.getReference("place");

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlace(databasePlaces);
            }
        });

        Intent intent = getIntent();
        account = intent.getParcelableExtra("account");
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void addPlace(DatabaseReference database) {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // create unique primary key
        String id = database.push().getKey();
        // create new place using information from activity
        Place newPlace = new Place(id, title, description, account.getId());
        // add new place to database
        database.child(id).setValue(newPlace);

        Toast.makeText(this, "Place created.", Toast.LENGTH_SHORT).show();
    }
}
