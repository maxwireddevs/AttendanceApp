package com.example.studentlistapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.studentlistapp.R;

public class LoginScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        Button login = (Button) findViewById(R.id.check_button);
    }

    public void validate(View v){
        EditText username = (EditText) findViewById(R.id.username_edittext);
        EditText password = (EditText) findViewById((R.id.password_edittext));
        if ((username.getText().toString().equals("Admin"))&&(password.getText().toString().equals("1234"))){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(LoginScreen.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
        }
    }


}

