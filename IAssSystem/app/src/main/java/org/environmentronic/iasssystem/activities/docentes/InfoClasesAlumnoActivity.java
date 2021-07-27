package org.environmentronic.iasssystem.activities.docentes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.environmentronic.iasssystem.R;
import org.environmentronic.iasssystem.activities.estudiantes.EstudianteEnClase;
import org.environmentronic.iasssystem.activities.principales.MainActivity;
import org.environmentronic.iasssystem.adapters.AdaptadorEstudiantesEnClases;
import org.environmentronic.iasssystem.adapters.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

import static org.environmentronic.iasssystem.modulos.Genericos.validaNombre;

public class InfoClasesAlumnoActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private TextView tvClase, tvCodigo, tvAlumnosReg;
    private ImageView imagen;
    private Button btnTomarAsistencia;

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

    private ProgressDialog mProgress;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor();
        setContentView(R.layout.activity_info_clases_alumno);

        tvClase = (TextView) findViewById(R.id.tvClaseD);
        tvCodigo = (TextView) findViewById(R.id.tvCodigoD);
        tvAlumnosReg = (TextView) findViewById(R.id.tvAlumnosReg);
        imagen = (ImageView) findViewById(R.id.imagenD);
        btnTomarAsistencia = (Button) findViewById(R.id.btnTomarAsistencia);

        pruebaAlumnos = (TextView) findViewById(R.id.pruebaAlumnos);

        materia = getIntent().getStringExtra("materia");
        codigo = getIntent().getStringExtra("codigo");
        idUsuario = getIntent().getStringExtra("idusuario");
        nombreUsuario = getIntent().getStringExtra("nomusuario");

        tvClase.setText("Materia: " + materia);
        tvCodigo.setText("Código: " + codigo);

        animation_left = AnimationUtils.loadAnimation(this, R.anim.animation_left);
        animation_rigth = AnimationUtils.loadAnimation(this, R.anim.animation_rigth);
        animation_down = AnimationUtils.loadAnimation(this, R.anim.animation_down);

        tvClase.setAnimation(animation_left);
        tvCodigo.setAnimation(animation_left);
        imagen.setAnimation(animation_rigth);
        tvAlumnosReg.setAnimation(animation_left);
        btnTomarAsistencia.setAnimation(animation_left);

        // inicializamos las listas
        estudiantesEnClases = new ArrayList<>();

        // recicladores
        rvAlumnosEnClase = (RecyclerView) findViewById(R.id.rvAlumnosEnClase);
        estudiantesAdapter = new AdaptadorEstudiantesEnClases(estudiantesEnClases, this);

        // base de datos
        database = FirebaseDatabase.getInstance();

        buscarAlumnos();
        mProgress = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        // eliminar clases estudiante
        ItemTouchHelper.SimpleCallback simpleCallbackEstudiantes = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, InfoClasesAlumnoActivity.this, 0, 0, 1);
        new ItemTouchHelper(simpleCallbackEstudiantes).attachToRecyclerView(rvAlumnosEnClase);
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
                .child("DOCENTES/" + idUsuario + "/" + idUsuario + "_" + nombreUsuario + "_" + materia + "." + codigo + "/ESTUDIANTES/")
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

                            lyprogresoAlum.setVisibility(View.GONE);
                            String nombreEstudiante = "";
                            String idEstudiante = "";

                            estudiantesEnClases.clear();
                            for (int i = 0; i < estudiantes.size(); i++) {
                                if ((!estudiantes.get(i).equals("clase.png"))) {
                                    nombreEstudiante = estudiantes.get(i).replaceAll(".*_", "");
                                    idEstudiante = estudiantes.get(i).replaceAll("_" + nombreEstudiante, "");
                                    //nombreEstudiante = estudiantes.get(i).replace(".png", "");
                                    // sacamos el id del estudiante y el nombre
                                    estudiantesEnClases.add(new EstudianteEnClase(nombreEstudiante, idEstudiante));
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
        Intent intent = new Intent(this, ListaClasesActivity.class);
        intent.putExtra("idusuario", idUsuario);
        intent.putExtra("nomusuario", nombreUsuario);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        finish();
    }

    public void tomarAsistencia(View view) {
        Intent intent = new Intent(this, FotoAsistenciaActivity.class);
        intent.putExtra("materia", materia);
        intent.putExtra("codigo", codigo);
        intent.putExtra("idusuario", idUsuario);
        intent.putExtra("nomusuario", nombreUsuario);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        finish();
    }

    @Override
    public void onBackPressed() {

    }

    public void showProgressBar(String mensaje) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgress.setMessage(mensaje);
        mProgress.show();
        mProgress.setCanceledOnTouchOutside(false);
    }

    public void finishProgressBar() {
        mProgress.dismiss();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgress.setCanceledOnTouchOutside(true);
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AdaptadorEstudiantesEnClases.ViewHolder) {

            String nombreEstudiante = estudiantesEnClases.get(rvAlumnosEnClase.getChildAdapterPosition(viewHolder.itemView)).getNombreEstudiante();
            String idEstudiante = estudiantesEnClases.get(rvAlumnosEnClase.getChildAdapterPosition(viewHolder.itemView)).getIdEstudiante();
            String nombreCortoEstudiante = validaNombre(nombreUsuario);

            if (compruebaConexion(this)) {

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿Realmente deseas eliminar a " + nombreCortoEstudiante + "?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", (dialogo11, id) -> {

                    showProgressBar("Eliminando alumno, espere ...");
                    estudiantesAdapter.removeItem(position);
                    // eliminamos la foto de la carpeta de la clase
                    borrarCarpetaEstudiante(idEstudiante, nombreEstudiante, nombreCortoEstudiante);

                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Toast.makeText(getApplicationContext(), "Operación cancelada", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogo1.show();
            } else {
                Toast.makeText(this, "No tiene acceso a internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void borrarCarpetaEstudiante(String idEstudiante, String nombreEstudiante, String nombreCortoEstudiante) {
        storageReference
                .child("DOCENTES/" + idUsuario + "/" + idUsuario + "_" + nombreUsuario + "_" + materia + "." + codigo + "/ESTUDIANTES/" + idEstudiante + "_" + nombreEstudiante + "/" + nombreCortoEstudiante + ".png")
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                // borrar la marca del docente de la clase
                borrarMarcaEstudiante(idEstudiante);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                finishProgressBar();
                Toast.makeText(getApplicationContext(), "Ha ocurrido un error, intente mas tarde", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void borrarMarcaEstudiante(String idEstudiante) {
        DatabaseReference marcaEstudiante = database.getReference().child("ESTUDIANTES").child(idEstudiante).child("CLASES").child(codigo);

        marcaEstudiante.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finishProgressBar();
                        estudiantesEnClases.clear();
                        buscarAlumnosDataBase();
                        Toast.makeText(InfoClasesAlumnoActivity.this, "¡Alumno eliminado con exito!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finishProgressBar();
                Toast.makeText(getApplicationContext(), "Ha ocurrido un error, intente mas tarde", Toast.LENGTH_SHORT).show();
            }
        });
    }
}