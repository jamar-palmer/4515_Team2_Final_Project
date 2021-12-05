package edu.temple.studybuddies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginAndRegisterActivity extends AppCompatActivity  implements LoginFragment.LoginFragmentInterface, RegisterFragment.RegisterFragmentInterface {
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_register);
        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.loginContainer, new LoginFragment()).addToBackStack(null).commit();
    }

    @Override
    public void itemClicked() {
        fragmentManager.beginTransaction().replace(R.id.loginContainer, new RegisterFragment()).addToBackStack(null).commit();
    }

    @Override
    public void login() {
        EditText usernameEditText = findViewById(R.id.editUsername);
        EditText passwordEditText = findViewById(R.id.editPassword);
        Query foundUsersMatching = StudyBuddies.db.collection("users")
                .whereEqualTo("username", usernameEditText.getText().toString())
                .whereEqualTo("password", passwordEditText.getText().toString());
        foundUsersMatching.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("ACTIVE_USER_ID",
                                    queryDocumentSnapshots.getDocuments().get(0).getId());
                            startActivity(intent);
                        } else {
                            TextView errorText = findViewById(R.id.loginErrorText);
                            errorText.setText(getString(R.string.login_error));
                        }
                    }
                });
    }

    @Override
    public void register() {
        // get references to user input EditTexts and error message TextView
        EditText usernameEditText = findViewById(R.id.registerUsernameEditText);
        EditText passwordEditText = findViewById(R.id.registerPasswordEditText);
        EditText passwordConfirmEditText = findViewById(R.id.registerPasswordConfirmEditText);
        EditText firstNameEditText = findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = findViewById(R.id.lastNameEditText);
        TextView errorTextView = findViewById(R.id.registerErrorTextView);

        // get strings from password fields and confirm the passwords match
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();
        if (!password.equals(passwordConfirm)) {
            errorTextView.setText(getString(R.string.register_password_error));
            return;
        }

        // create a query matching the username field
        Query foundUsersMatching = StudyBuddies.db.collection("users")
                .whereEqualTo("username", usernameEditText.getText().toString());
        foundUsersMatching.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // make sure the username does not already exist in the database
                    if(queryDocumentSnapshots.size() > 0) {
                        errorTextView.setText(getString(R.string.register_username_error));
                        return;
                    }
                    // instantiate a Map to pass into the document creation function
                    // using fields filled by the user in the EditTexts
                    Map<String, Object> nUser = new HashMap<>();
                    nUser.put("firstName", firstNameEditText.getText().toString());
                    nUser.put("lastName", lastNameEditText.getText().toString());
                    nUser.put("username", usernameEditText.getText().toString());
                    nUser.put("password", passwordEditText.getText().toString());
                    // create the document in the "users" collection
                    StudyBuddies.addDocument(nUser, StudyBuddies.CollectionType.USERS,
                            ref -> {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("ACTIVE_USER_ID", ref.getId());
                                startActivity(intent);
                            });
                });
    }
}