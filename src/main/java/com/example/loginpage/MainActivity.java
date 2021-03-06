package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    View view,view2;
    ImageView imageview2;
    TextView mail,pass,textView3,textView4,frgtpass;

    Button login,signup,btn;

    FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mFirebaseAuth=FirebaseAuth.getInstance();
        pass=(TextView)findViewById(R.id.pass);
        btn=(Button) findViewById(R.id.btn);
        mail=(TextView)findViewById(R.id.mail);
        login=(Button)findViewById(R.id.login);
        signup=(Button)findViewById(R.id.signup);
        frgtpass=findViewById(R.id.frgtpass);
        view2=(View)findViewById(R.id.view2);
        view=(View)findViewById(R.id.view);
        imageview2=(ImageView)findViewById(R.id.imageView2);
        textView3=(TextView)findViewById(R.id.textView3);
        textView4=(TextView)findViewById(R.id.textView4);

        //Fingerprint part
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_SUCCESS:

                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:

                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:


                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:

                break;
        };

        Executor executor = ContextCompat.getMainExecutor(this);
        final BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,map.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setDescription("Use Fingerprint to login")
                .setNegativeButtonText("Cancel")
                .build();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);

            }
        });

        frgtpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,forget.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mail.getText().toString();
                String passw = pass.getText().toString();



                if(isConnected(MainActivity.this)==false){
                    LayoutInflater inflater =  getLayoutInflater();
                    View layout = inflater.inflate(R.layout.alert_dialog,(ViewGroup)findViewById(R.id.alert));

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER,0,150);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);

                    toast.show();
                }
                if(isConnected(MainActivity.this)==true){
                    if (email.isEmpty()){
                        mail.setError("Enter Mail !!");
                        mail.requestFocus();
                        return;
                    }
                    if (passw.isEmpty()){
                        pass.setError("Enter Password !!");
                        pass.requestFocus();
                        return;
                    }
                    LayoutInflater inflater =  getLayoutInflater();
                    View layout = inflater.inflate(R.layout.loading,(ViewGroup)findViewById(R.id.alert));

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER,0,150);

                    toast.setView(layout);

                    toast.show();
                    mFirebaseAuth.signInWithEmailAndPassword(email,passw)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    startActivity(new Intent(MainActivity.this,map.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,"Incorrect Credential !!! ",Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,signup.class);
                startActivity(intent);
                finish();
            }
        });


        }






    private boolean isConnected(MainActivity mainActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wificonn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileconn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wificonn!=null && wificonn.isConnected() || (mobileconn!=null && mobileconn.isConnected()))){
            return true;
        }
        else {
            return false;
        }

    }





}