package org.environmentronic.iasssystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class UsActivity extends AppCompatActivity {

    private String facebookR = "https://www.facebook.com/roosevelt.leyva";
    private ImageView btnFacebookRusvel;
    private String twitterR = "https://twitter.com/repleyva";
    private ImageView btnTwitterRusvel;
    private String instagramR = "https://www.instagram.com/repleyva/?hl=es-la";
    private ImageView btnInstaRusvel;
    private String githubR = "https://github.com/RusvelLeyva";
    private ImageView btnGitRusvel;
    private String linkedinR = "https://www.linkedin.com/in/rusvel-enrique-pasos-leyva-969b9918b/";
    private ImageView btnLinkedinRusvel;

    private String facebookC = "https://www.facebook.com/carlosandres.bermudezarango.9";
    private ImageView btnFacebookCarlos;
    private String instagramC = "https://www.instagram.com/carlosandres.bermudezarango.9/?hl=es-la";
    private ImageView btnInstaCarlos;
    private String linkedinC = "https://www.linkedin.com/in/carlos-andres-bermudez-arango-6320a3129/";
    private ImageView btnLinkedinCarlos;

    private LinearLayout tarjetaCarlos;
    private LinearLayout tarjetaRusvel;
    private LinearLayout infoRusvel;
    private LinearLayout infoCarlos;

    private Animation animation_rigth;
    private Animation animation_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us);

        changeStatusBarColor();

        btnFacebookRusvel = (ImageView) findViewById(R.id.btnFacebookRusvel);
        btnTwitterRusvel = (ImageView) findViewById(R.id.btnTwitterRusvel);
        btnInstaRusvel = (ImageView) findViewById(R.id.btnInstaRusvel);
        btnLinkedinRusvel = (ImageView) findViewById(R.id.btnLinkedinRusvel);
        btnGitRusvel = (ImageView) findViewById(R.id.btnGitRusvel);

        btnFacebookCarlos = (ImageView) findViewById(R.id.btnFacebookCarlos);
        btnInstaCarlos = (ImageView) findViewById(R.id.btnInstaCarlos);
        btnLinkedinCarlos = (ImageView) findViewById(R.id.btnLinkedinCarlos);

        tarjetaCarlos = (LinearLayout) findViewById(R.id.tarjetaCarlos);
        tarjetaRusvel = (LinearLayout) findViewById(R.id.tarjetaRusvel);
        infoRusvel = (LinearLayout) findViewById(R.id.infoRusvel);
        infoCarlos = (LinearLayout) findViewById(R.id.infoCarlos);

        animation_rigth = AnimationUtils.loadAnimation(this, R.anim.animation_rigth);
        animation_left = AnimationUtils.loadAnimation(this, R.anim.animation_left);

        tarjetaRusvel.startAnimation(animation_left);
        tarjetaCarlos.startAnimation(animation_rigth);
        infoRusvel.startAnimation(animation_left);
        infoCarlos.startAnimation(animation_rigth);

        btnFacebookRusvel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse(facebookR);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
            }
        });

        btnTwitterRusvel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse(twitterR);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
            }
        });

        btnInstaRusvel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse(instagramR);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
            }
        });

        btnLinkedinRusvel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse(linkedinR);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
            }
        });

        btnGitRusvel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse(githubR);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
            }
        });

        btnFacebookCarlos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse(facebookC);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
            }
        });

        btnInstaCarlos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse(instagramC);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
            }
        });

        btnLinkedinCarlos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse(linkedinC);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
            }
        });
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view){
        startActivity(new Intent(this,MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}