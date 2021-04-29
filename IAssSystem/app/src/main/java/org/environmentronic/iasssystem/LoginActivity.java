package org.environmentronic.iasssystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    public static final int SIGN_IN_CODE = 777;
    private ImageView signInButton;
    private ImageView imagen;
    private ImageView barra;
    private TextView texto;

    private Animation animation_down;
    private Animation animation_up;
    private Animation animation_rigth;
    private Animation animation_left;

    private LinearLayout linearLayout4;
    private String facebook = "https://www.facebook.com/roosevelt.leyva";
    private ImageView btnFacebook;
    private String twitter = "https://twitter.com/EnvironTronic?s=08";
    private ImageView btnTwitter;
    private String instagram = "https://www.instagram.com/repleyva/?hl=es-la";
    private ImageView btnInsta;
    private String github = "https://github.com/RusvelLeyva";
    private ImageView btnGit;

    private LinearLayout iniG;
    private LinearLayout redes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);

        imagen = (ImageView) findViewById(R.id.imagen);
        barra = (ImageView) findViewById(R.id.barra);
        texto = (TextView) findViewById(R.id.texto);
        linearLayout4 = (LinearLayout) findViewById(R.id.linearLayout4);

        iniG = (LinearLayout) findViewById(R.id.linearLayout);
        redes = (LinearLayout) findViewById(R.id.linearLayout3);

        btnFacebook = (ImageView) findViewById(R.id.btnFacebook);
        btnTwitter = (ImageView) findViewById(R.id.btnTwitter);
        btnInsta = (ImageView) findViewById(R.id.btnInsta);
        btnGit = (ImageView) findViewById(R.id.btnGit);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // configuracion para el boton de inicio de sesión
        signInButton = (ImageView) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(v -> {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, SIGN_IN_CODE);
        });

        animation_down = AnimationUtils.loadAnimation(this, R.anim.animation_down);
        animation_up = AnimationUtils.loadAnimation(this, R.anim.animation_up);
        animation_rigth = AnimationUtils.loadAnimation(this, R.anim.animation_rigth);
        animation_left = AnimationUtils.loadAnimation(this, R.anim.animation_left);
        imagen.startAnimation(animation_rigth);
        texto.startAnimation(animation_left);
        linearLayout4.startAnimation(animation_left);
        signInButton.startAnimation(animation_left);
        iniG.startAnimation(animation_rigth);
        redes.startAnimation(animation_rigth);

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse(facebook);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
            }
        });

        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse(twitter);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
            }
        });

        btnInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse(instagram);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
            }
        });

        btnGit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse(github);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        if (result.isSuccess()){
            goMainScreen();
        } else {
            Toast.makeText(this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
        }
    }

    public void goMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
        finish();
    }

    public void onLoginClick(View View){
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}