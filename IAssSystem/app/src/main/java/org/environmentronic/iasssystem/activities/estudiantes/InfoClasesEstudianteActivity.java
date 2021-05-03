package org.environmentronic.iasssystem.activities.estudiantes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.environmentronic.iasssystem.R;
import org.environmentronic.iasssystem.activities.docentes.ClasesDocente;
import org.environmentronic.iasssystem.activities.principales.MainActivity;
import org.environmentronic.iasssystem.adapters.AdaptadorClasesDocente;
import org.environmentronic.iasssystem.adapters.AdaptadorClasesEstudiante;
import org.environmentronic.iasssystem.adapters.AdaptadorEstudiantesEnClases;

import java.util.ArrayList;
import java.util.List;

public class InfoClasesEstudianteActivity extends AppCompatActivity {

    private TextView tvClase, tvDocente , tvCodigo, tvEstudiantesReg;
    private ImageView imagen;

    //Animaciones
    private Animation animation_rigth;
    private Animation animation_up;
    private Animation animation_down;
    private Animation animation_left;
    private Animation animation_left_long;
    private Animation animation_rigth_long;
    private Animation animation_left_ocult;
    private Animation animaciondere_ocult;
    private Animation animation_rigth_ocult_long;
    private Animation animation_left_ocult_long;

    // constantes timer
    private Integer largo = 2000;
    private Integer normal = 1000;

    // crear tarjetas de clases
    private List<EstudianteEnClase> estudiantesEnClases;

    // recycler
    private RecyclerView rvEstudiantesEnClase;

    // adaptadores
    private AdaptadorEstudiantesEnClases estudiantesAdapter;

    // variables para validar compañeros
    private TextView pruebaEstudiantes;

    // progress
    private ProgressBar cprogressEst;

    // mensajes
    private TextView tvCargandoCompaneros;

    // linears
    private LinearLayout lyprogresoEst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor();
        setContentView(R.layout.activity_info_clases_estudiante);

        tvClase = (TextView) findViewById(R.id.tvClase);
        tvDocente = (TextView) findViewById(R.id.tvDocente);
        tvCodigo = (TextView) findViewById(R.id.tvCodigo);
        tvEstudiantesReg = (TextView) findViewById(R.id.tvEstudiantesReg);
        imagen = (ImageView) findViewById(R.id.imagen);

        pruebaEstudiantes = (TextView) findViewById(R.id.pruebaEstudiantes);

        String docente = getIntent().getStringExtra("docente");
        String materia = getIntent().getStringExtra("materia");
        String codigo = getIntent().getStringExtra("codigo");

        tvClase.setText("Materia: " + materia);
        tvDocente.setText("Docente: " +  docente);
        tvCodigo.setText("Código: " + codigo);

        animation_left = AnimationUtils.loadAnimation(this, R.anim.animation_left);
        animation_left_long = AnimationUtils.loadAnimation(this, R.anim.animation_left_long);
        animation_left_ocult = AnimationUtils.loadAnimation(this, R.anim.animation_left_ocult);
        animation_left_ocult_long = AnimationUtils.loadAnimation(this, R.anim.animation_left_ocult_long);
        animaciondere_ocult = AnimationUtils.loadAnimation(this, R.anim.animation_rigth_ocult);
        animation_rigth_ocult_long = AnimationUtils.loadAnimation(this, R.anim.animation_rigth_ocult_long);
        animation_rigth = AnimationUtils.loadAnimation(this, R.anim.animation_rigth);
        animation_rigth_long = AnimationUtils.loadAnimation(this, R.anim.animation_rigth_long);
        animation_up = AnimationUtils.loadAnimation(this, R.anim.animation_up);
        animation_down = AnimationUtils.loadAnimation(this, R.anim.animation_down);

        tvClase.setAnimation(animation_left);
        tvDocente.setAnimation(animation_left);
        tvCodigo.setAnimation(animation_left);
        imagen.setAnimation(animation_rigth);
        tvEstudiantesReg.setAnimation(animation_left);

        // inicializamos las listas
        estudiantesEnClases = new ArrayList<>();

        // recicladores
        rvEstudiantesEnClase = (RecyclerView) findViewById(R.id.rvEstudiantesEnClase);
        estudiantesAdapter = new AdaptadorEstudiantesEnClases(estudiantesEnClases, this);

        buscarCompaneros();
    }

    private void buscarCompaneros() {

        new Handler().postDelayed(() -> {
            lyprogresoEst = (LinearLayout) findViewById(R.id.lyprogresoEst);
            lyprogresoEst.setVisibility(View.VISIBLE);
            tvCargandoCompaneros = (TextView) findViewById(R.id.tvCargandoCompaneros);
            cprogressEst = (ProgressBar) findViewById(R.id.cprogressEst);

            new Handler().postDelayed(() -> {
                lyprogresoEst.setVisibility(View.GONE);
                ///ponerDatos();
            }, normal);

        }, normal);
    }

    public void ponerDatos(){
        estudiantesEnClases.clear();
        // asi debo meter las clases
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));
        estudiantesEnClases.add(new EstudianteEnClase("Rusvel Pasos"));

        rvEstudiantesEnClase.setHasFixedSize(true);
        rvEstudiantesEnClase.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvEstudiantesEnClase.setAdapter(estudiantesAdapter);
    }

    public void onMainClick(View view){
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.stay);
        finish();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    @Override
    public void onBackPressed() {

    }
}