package edu.temple.studybuddies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void register() {
        fragmentManager.beginTransaction().replace(R.id.loginContainer, new LoginFragment()).addToBackStack(null).commit();
    }
}