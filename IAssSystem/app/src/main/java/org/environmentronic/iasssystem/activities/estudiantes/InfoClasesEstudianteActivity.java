package org.environmentronic.iasssystem.activities.estudiantes;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.environmentronic.iasssystem.R;
import org.environmentronic.iasssystem.activities.docentes.ListaClasesActivity;
import org.environmentronic.iasssystem.activities.principales.MainActivity;
import org.environmentronic.iasssystem.adapters.AdaptadorEstudiantesEnClases;

import java.util.ArrayList;
import java.util.List;


public class InfoClasesEstudianteActivity extends AppCompatActivity {

    private TextView tvClase, tvDocente, tvCodigo, tvEstudiantesReg;
    private ImageView imagen;

    //Animaciones
    private Animation animation_rigth;
    private Animation animation_down;
    private Animation animation_left;

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

    // base de datos
    private FirebaseDatabase database;

    // datos de la tarjeta
    private String docente;
    private String materia;
    private String codigo;
    private String idDocente;

    private String nombreUsuario;
    private String idUsuario;

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

        docente = getIntent().getStringExtra("docente");
        materia = getIntent().getStringExtra("materia");
        codigo = getIntent().getStringExtra("codigo");
        idDocente = getIntent().getStringExtra("iddocente");
        idUsuario = getIntent().getStringExtra("idusuario");
        nombreUsuario = getIntent().getStringExtra("nomusuario");

        tvClase.setText("Materia: " + materia);
        tvDocente.setText("Docente: " + docente);
        tvCodigo.setText("Código: " + codigo);

        animation_left = AnimationUtils.loadAnimation(this, R.anim.animation_left);
        animation_rigth = AnimationUtils.loadAnimation(this, R.anim.animation_rigth);
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

        // base de datos
        database = FirebaseDatabase.getInstance();

        buscarCompaneros();
    }

    private void buscarCompaneros() {

        new Handler().postDelayed(() -> {
            lyprogresoEst = (LinearLayout) findViewById(R.id.lyprogresoEst);
            lyprogresoEst.setVisibility(View.VISIBLE);
            tvCargandoCompaneros = (TextView) findViewById(R.id.tvCargandoCompaneros);
            cprogressEst = (ProgressBar) findViewById(R.id.cprogressEst);

            new Handler().postDelayed(() -> {
                ponerDatos();
            }, normal);

        }, normal);
    }

    private void ponerDatos() {
        if (compruebaConexion(this)) {
            buscarCompanerosDataBase();
        } else {
            lyprogresoEst.setVisibility(View.GONE);
            Toast.makeText(this, "Debe tener acceso a internet para ver a sus compañeros. Por favor conectese a internet y vuelva a ingresar a este apartador", Toast.LENGTH_LONG).show();
        }
    }

    public void buscarCompanerosDataBase() {

        List<String> estudiantes = new ArrayList<>();
        estudiantes.clear();

        // Create a Cloud Storage reference from the app
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference
                .child("DOCENTES/" + idDocente + "/" + idDocente + "_" + docente + "_" + materia + "." + codigo + "/ESTUDIANTES/")
                .listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {

                        for (StorageReference item : listResult.getPrefixes()) {
                            // All the items under listRef.
                            estudiantes.add(item.getName());
                        }

                        estudiantes.remove("clase.png");
                        estudiantes.remove("FOTO_CLASE");

                        if (!estudiantes.isEmpty()) {

                            lyprogresoEst.setVisibility(View.GONE);
                            String nombreEstudiante = "";
                            String idEstudiante = "";

                            estudiantesEnClases.clear();
                            for (int i = 0; i < estudiantes.size() ; i++) {
                                if ((!estudiantes.get(i).equals("clase.png"))){
                                    nombreEstudiante = estudiantes.get(i).replaceAll(".*_", "");
                                    idEstudiante = estudiantes.get(i).replaceAll("_" + nombreEstudiante, "");
                                    //nombreEstudiante = estudiantes.get(i).replace(".png", "");
                                    // sacamos el id del estudiante y el nombre
                                    estudiantesEnClases.add(new EstudianteEnClase(nombreEstudiante, idEstudiante));
                                }
                            }

                            rvEstudiantesEnClase.setHasFixedSize(true);
                            rvEstudiantesEnClase.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            rvEstudiantesEnClase.setAdapter(estudiantesAdapter);

                            new Handler().postDelayed(() -> {
                                rvEstudiantesEnClase.setVisibility(View.VISIBLE);
                                rvEstudiantesEnClase.setAnimation(animation_down);
                            }, normal);

                        } else {
                            pruebaEstudiantes.setVisibility(View.VISIBLE);
                            pruebaEstudiantes.setText("No existen estudiantes registrados en la clase");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                        lyprogresoEst.setVisibility(View.GONE);
                        pruebaEstudiantes.setVisibility(View.VISIBLE);
                        pruebaEstudiantes.setText("Ha ocurrido un error, compruebe su conexión  internet o intentelo más tarde");
                    }
                });
    }

    public void onMainClick(View view) {
        Intent intent = new Intent(this, VerClasesEstudiantesActivity.class);
        intent.putExtra("idusuario", idUsuario);
        intent.putExtra("nomusuario", nombreUsuario);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        finish();
    }

    private void changeStatusBarColor() {
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
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

    @Override
    public void onBackPressed() {

    }
}