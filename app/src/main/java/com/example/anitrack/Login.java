package com.example.anitrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    EditText ufield,pfiels;
    Button buttonLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       ufield = findViewById(R.id.ufield);
       pfiels = findViewById(R.id.pfield);
       buttonLogin = findViewById(R.id.buttonLogin);

      buttonLogin.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              String username = ufield.getText().toString();
              String password = pfiels.getText().toString();

              if(username.equals("asd") && password.equals("123")){

                  //redirecting to main page
                  Intent intent = new Intent(Login.this,HomeActivity.class);
                  startActivity(intent);
                  finish();

              }else{
                  Toast.makeText(Login.this,"Invalid Credentials", Toast.LENGTH_SHORT).show();
              }
          }
      });





    }

}