package org.environmentronic.iasssystem.activities.docentes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.environmentronic.iasssystem.R;
import org.environmentronic.iasssystem.activities.estudiantes.EstudianteEnClase;
import org.environmentronic.iasssystem.activities.principales.MainActivity;
import org.environmentronic.iasssystem.adapters.AdaptadorEstudiantesEnClases;

import java.util.ArrayList;
import java.util.List;

public class InfoClasesAlumnoActivity extends AppCompatActivity {

    private TextView tvClase, tvCodigo, tvAlumnosReg;
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
    private RecyclerView rvAlumnosEnClase;

    // adaptadores
    private AdaptadorEstudiantesEnClases estudiantesAdapter;

    // variables para validar compañeros
    private TextView pruebaAlumnos;

    // progress
    private ProgressBar cprogressAlum;

    // mensajes
    private TextView tvCargandoAlumnos;

    // linears
    private LinearLayout lyprogresoAlum;

    // base de datos
    private FirebaseDatabase database;

    // datos de la tarjeta
    private String materia;
    private String codigo;
    private String idUsuario;
    private String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor();
        setContentView(R.layout.activity_info_clases_alumno);

        tvClase = (TextView) findViewById(R.id.tvClaseD);
        tvCodigo = (TextView) findViewById(R.id.tvCodigoD);
        tvAlumnosReg = (TextView) findViewById(R.id.tvAlumnosReg);
        imagen = (ImageView) findViewById(R.id.imagenD);

        pruebaAlumnos = (TextView) findViewById(R.id.pruebaAlumnos);

        materia = getIntent().getStringExtra("materia");
        codigo = getIntent().getStringExtra("codigo");
        idUsuario = getIntent().getStringExtra("idusuario");
        nombreUsuario = getIntent().getStringExtra("nomusuario");

        tvClase.setText("Materia: " + materia);
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
        tvCodigo.setAnimation(animation_left);
        imagen.setAnimation(animation_rigth);
        tvAlumnosReg.setAnimation(animation_left);

        // inicializamos las listas
        estudiantesEnClases = new ArrayList<>();

        // recicladores
        rvAlumnosEnClase = (RecyclerView) findViewById(R.id.rvAlumnosEnClase);
        estudiantesAdapter = new AdaptadorEstudiantesEnClases(estudiantesEnClases, this);

        // base de datos
        database = FirebaseDatabase.getInstance();

        buscarAlumnos();
    }

    private void buscarAlumnos() {
        new Handler().postDelayed(() -> {
            lyprogresoAlum = (LinearLayout) findViewById(R.id.lyprogresoAlum);
            lyprogresoAlum.setVisibility(View.VISIBLE);
            tvCargandoAlumnos = (TextView) findViewById(R.id.tvCargandoAlumnos);
            cprogressAlum = (ProgressBar) findViewById(R.id.cprogressAlum);

            new Handler().postDelayed(() -> {
                ponerDatos();
            }, normal);

        }, normal);
    }

    private void ponerDatos() {
        if (compruebaConexion(this)) {
            buscarAlumnosDataBase();
        } else {
            lyprogresoAlum.setVisibility(View.GONE);
            Toast.makeText(this, "Debe tener acceso a internet para ver a sus alumnos. Por favor conectese a internet y vuelva a ingresar a este apartador", Toast.LENGTH_LONG).show();
        }
    }

    private void buscarAlumnosDataBase() {

        List<String> estudiantes = new ArrayList<>();
        estudiantes.clear();

        // Create a Cloud Storage reference from the app
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference
                .child("DOCENTES/" + idUsuario + "/" + idUsuario + "_" + nombreUsuario + "_" + materia + "." + codigo + "/")
                .listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {

                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            estudiantes.add(item.getName());
                        }

                        estudiantes.remove("clase.png");

                        if (!estudiantes.isEmpty()) {

                            lyprogresoAlum.setVisibility(View.GONE);
                            String nombreEstudiante = "";

                            estudiantesEnClases.clear();
                            for (int i = 0; i < estudiantes.size() ; i++) {
                                if ((!estudiantes.get(i).equals("clase.png"))){
                                    nombreEstudiante = estudiantes.get(i).replace(".png", "");
                                    estudiantesEnClases.add(new EstudianteEnClase(nombreEstudiante));
                                }
                            }

                            rvAlumnosEnClase.setHasFixedSize(true);
                            rvAlumnosEnClase.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            rvAlumnosEnClase.setAdapter(estudiantesAdapter);

                            new Handler().postDelayed(() -> {
                                rvAlumnosEnClase.setVisibility(View.VISIBLE);
                                rvAlumnosEnClase.setAnimation(animation_down);
                            }, normal);

                        } else {
                            lyprogresoAlum.setVisibility(View.GONE);
                            pruebaAlumnos.setVisibility(View.VISIBLE);
                            pruebaAlumnos.setText("No existen estudiantes registrados en la clase");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                        lyprogresoAlum.setVisibility(View.GONE);
                        pruebaAlumnos.setVisibility(View.VISIBLE);
                        pruebaAlumnos.setText("Ha ocurrido un error, compruebe su conexión  internet o intentelo más tarde");
                    }
                });
    }

    public static boolean compruebaConexion(Context context) {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

    private void changeStatusBarColor() {
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void onMainClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        finish();
    }

}