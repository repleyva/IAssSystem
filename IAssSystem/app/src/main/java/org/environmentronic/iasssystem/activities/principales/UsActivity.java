package org.environmentronic.iasssystem.activities.principales;

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
import org.environmentronic.iasssystem.R;

public class UsActivity extends AppCompatActivity {

    private final String facebookR = "https://www.facebook.com/roosevelt.leyva";
    private final String twitterR = "https://twitter.com/repleyva";
    private final String instagramR = "https://www.instagram.com/repleyva/?hl=es-la";
    private final String githubR = "https://github.com/RusvelLeyva";
    private final String linkedinR = "https://www.linkedin.com/in/rusvel-enrique-pasos-leyva-969b9918b/";
    private final String facebookC = "https://www.facebook.com/carlosandres.bermudezarango.9";
    private final String instagramC = "https://www.instagram.com/carlosandres.bermudezarango.9/?hl=es-la";
    private final String linkedinC = "https://www.linkedin.com/in/carlos-andres-bermudez-arango-6320a3129/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us);

        changeStatusBarColor();

        ImageView btnFacebookRusvel = (ImageView) findViewById(R.id.btnFacebookRusvel);
        ImageView btnTwitterRusvel = (ImageView) findViewById(R.id.btnTwitterRusvel);
        ImageView btnInstaRusvel = (ImageView) findViewById(R.id.btnInstaRusvel);
        ImageView btnLinkedinRusvel = (ImageView) findViewById(R.id.btnLinkedinRusvel);
        ImageView btnGitRusvel = (ImageView) findViewById(R.id.btnGitRusvel);

        ImageView btnFacebookCarlos = (ImageView) findViewById(R.id.btnFacebookCarlos);
        ImageView btnInstaCarlos = (ImageView) findViewById(R.id.btnInstaCarlos);
        ImageView btnLinkedinCarlos = (ImageView) findViewById(R.id.btnLinkedinCarlos);

        LinearLayout tarjetaCarlos = (LinearLayout) findViewById(R.id.tarjetaCarlos);
        LinearLayout tarjetaRusvel = (LinearLayout) findViewById(R.id.tarjetaRusvel);
        LinearLayout infoRusvel = (LinearLayout) findViewById(R.id.infoRusvel);
        LinearLayout infoCarlos = (LinearLayout) findViewById(R.id.infoCarlos);

        Animation animation_rigth = AnimationUtils.loadAnimation(this, R.anim.animation_rigth);
        Animation animation_left = AnimationUtils.loadAnimation(this, R.anim.animation_left);

        tarjetaRusvel.startAnimation(animation_left);
        tarjetaCarlos.startAnimation(animation_rigth);
        infoRusvel.startAnimation(animation_left);
        infoCarlos.startAnimation(animation_rigth);

        btnFacebookRusvel.setOnClickListener(v -> {
            Uri link = Uri.parse(facebookR);
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        });

        btnTwitterRusvel.setOnClickListener(v -> {
            Uri link = Uri.parse(twitterR);
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        });

        btnInstaRusvel.setOnClickListener(v -> {
            Uri link = Uri.parse(instagramR);
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        });

        btnLinkedinRusvel.setOnClickListener(v -> {
            Uri link = Uri.parse(linkedinR);
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        });

        btnGitRusvel.setOnClickListener(v -> {
            Uri link = Uri.parse(githubR);
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        });

        btnFacebookCarlos.setOnClickListener(v -> {
            Uri link = Uri.parse(facebookC);
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        });

        btnInstaCarlos.setOnClickListener(v -> {
            Uri link = Uri.parse(instagramC);
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        });

        btnLinkedinCarlos.setOnClickListener(v -> {
            Uri link = Uri.parse(linkedinC);
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
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
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}