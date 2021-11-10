package org.environmentronic.iasssystem.activities.principales;

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
import org.environmentronic.iasssystem.R;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    public static final int SIGN_IN_CODE = 777;
    private final String facebook = "https://www.facebook.com/roosevelt.leyva";
    private final String twitter = "https://twitter.com/EnvironTronic?s=08";
    private final String instagram = "https://www.instagram.com/repleyva/?hl=es-la";
    private final String github = "https://github.com/RusvelLeyva";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);

        ImageView imagen = (ImageView) findViewById(R.id.imagen);
        TextView texto = (TextView) findViewById(R.id.texto);
        LinearLayout linearLayout4 = (LinearLayout) findViewById(R.id.linearLayout4);
        LinearLayout iniG = (LinearLayout) findViewById(R.id.linearLayout);
        LinearLayout redes = (LinearLayout) findViewById(R.id.linearLayout3);
        ImageView btnFacebook = (ImageView) findViewById(R.id.btnFacebook);
        ImageView btnTwitter = (ImageView) findViewById(R.id.btnTwitter);
        ImageView btnInsta = (ImageView) findViewById(R.id.btnInsta);
        ImageView btnGit = (ImageView) findViewById(R.id.btnGit);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // configuracion para el boton de inicio de sesión
        ImageView signInButton = (ImageView) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(v -> {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, SIGN_IN_CODE);
        });

        Animation animation_rigth = AnimationUtils.loadAnimation(this, R.anim.animation_rigth);
        Animation animation_left = AnimationUtils.loadAnimation(this, R.anim.animation_left);
        imagen.startAnimation(animation_rigth);
        texto.startAnimation(animation_left);
        linearLayout4.startAnimation(animation_left);
        signInButton.startAnimation(animation_left);
        iniG.startAnimation(animation_rigth);
        redes.startAnimation(animation_rigth);

        btnFacebook.setOnClickListener(v -> {
            Uri link = Uri.parse(facebook);
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        });

        btnTwitter.setOnClickListener(v -> {
            Uri link = Uri.parse(twitter);
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        });

        btnInsta.setOnClickListener(v -> {
            Uri link = Uri.parse(instagram);
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        });

        btnGit.setOnClickListener(v -> {
            Uri link = Uri.parse(github);
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_CODE) {
            assert data != null;
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            assert result != null;
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            goMainScreen();
        } else {
            Toast.makeText(this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
        }
    }

    public void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        finish();
    }

    public void onInfoClick(View View) {
        startActivity(new Intent(this, InfoAppActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        finish();
    }

    public void downloadManual(View view) {
        Uri link = Uri.parse("https://firebasestorage.googleapis.com/v0/b/iasssystem.appspot.com/o/MANUAL%2FMANUAL%20DE%20USUARIO%20DE%20LA%20APLICACI%C3%93N%20M%C3%93VIL%20IASSSYSTEM.pdf?alt=media&token=ca2ad8b7-4d3a-4dc6-8332-cf68c3e9beb1");
        Intent intent = new Intent(Intent.ACTION_VIEW, link);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}