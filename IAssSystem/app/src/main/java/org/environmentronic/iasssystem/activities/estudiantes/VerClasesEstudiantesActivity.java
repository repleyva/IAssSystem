package org.environmentronic.iasssystem.activities.estudiantes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.firebase.storage.StorageReference;

import org.environmentronic.iasssystem.R;
import org.environmentronic.iasssystem.activities.principales.MainActivity;
import org.environmentronic.iasssystem.adapters.AdaptadorClasesEstudiante;
import org.environmentronic.iasssystem.adapters.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

public class VerClasesEstudiantesActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    private ConstraintLayout barraFoto;
    private LinearLayout fotoUsuarioVerClases;
    private TextView tvNombreVerClases;

    //Animaciones
    private Animation animation_rigth;
    private Animation animation_left;
    private Animation animation_down;

    private LinearLayout lyprogreso;
    private TextView tvCargandoClases;
    private ProgressBar cprogress;
    private Integer normal = 1000;

    private ArrayList<ClasesEstudiante> clasesEstudiantes;
    private RecyclerView rvClasesEstudiante;
    private AdaptadorClasesEstudiante listaClases;

    private FirebaseDatabase database;
    private TextView pruebaClases;

    private String nombreUsuario;
    private String nombreCorto;
    private String idUsuario;

    private ProgressDialog mProgress;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_ver_clases_estudiantes);

        idUsuario = getIntent().getStringExtra("idusuario");
        nombreUsuario = getIntent().getStringExtra("nomusuario");

        barraFoto = (ConstraintLayout) findViewById(R.id.barraFoto);
        fotoUsuarioVerClases = (LinearLayout) findViewById(R.id.fotoUsuarioVerClases);
        tvNombreVerClases = (TextView) findViewById(R.id.tvNombreVerClases);

        clasesEstudiantes = new ArrayList<>();
        rvClasesEstudiante = (RecyclerView) findViewById(R.id.rvClasesEstudiante);
        listaClases = new AdaptadorClasesEstudiante(clasesEstudiantes, this);

        animation_left = AnimationUtils.loadAnimation(this, R.anim.animation_left);
        animation_rigth = AnimationUtils.loadAnimation(this, R.anim.animation_rigth);
        animation_down = AnimationUtils.loadAnimation(this, R.anim.animation_down);

        barraFoto.startAnimation(animation_left);
        fotoUsuarioVerClases.startAnimation(animation_rigth);
        tvNombreVerClases.setAnimation(animation_left);

        database = FirebaseDatabase.getInstance();
        pruebaClases = (TextView) findViewById(R.id.pruebaClases);

        nombreCorto = validaNombre(nombreUsuario);
        mProgress = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        new Handler().postDelayed(() -> {
            lyprogreso = (LinearLayout) findViewById(R.id.lyprogreso);
            lyprogreso.setVisibility(View.VISIBLE);
            tvCargandoClases = (TextView) findViewById(R.id.tvCargandoClases);
            cprogress = (ProgressBar) findViewById(R.id.cprogress);


            new Handler().postDelayed(() -> {
                ponerDatos();
            }, normal);

        }, normal);

        // evento click para la lista de clases de los estudiantes
        listaClases.setOnClickListener(v -> {
            String docente = clasesEstudiantes.get(rvClasesEstudiante.getChildAdapterPosition(v)).getDocente();
            String materia = clasesEstudiantes.get(rvClasesEstudiante.getChildAdapterPosition(v)).getMateria();
            String codigo = clasesEstudiantes.get(rvClasesEstudiante.getChildAdapterPosition(v)).getCodigo();
            String iddocente = clasesEstudiantes.get(rvClasesEstudiante.getChildAdapterPosition(v)).getIdDocente();

            Intent intent = new Intent(getApplicationContext(), InfoClasesEstudianteActivity.class);
            intent.putExtra("docente", docente);
            intent.putExtra("materia", materia);
            intent.putExtra("codigo", codigo);
            intent.putExtra("iddocente", iddocente);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        });

        // eliminar clases estudiante
        ItemTouchHelper.SimpleCallback simpleCallbackEstudiantes = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, VerClasesEstudiantesActivity.this, 1, 0, 0);
        new ItemTouchHelper(simpleCallbackEstudiantes).attachToRecyclerView(rvClasesEstudiante);

    }

    private void ponerDatos() {
        if (compruebaConexion(this)) {
            buscarClasesDataBase();
        } else {
            lyprogreso.setVisibility(View.GONE);
            Toast.makeText(this, "Debe tener acceso a internet para ver sus clases. Por favor conectese a internet y vuelva a ingresar a este apartador", Toast.LENGTH_LONG).show();
        }
    }

    private void buscarClasesDataBase() {

        clasesEstudiantes.clear();

        List<String> clases = new ArrayList<>();
        List<String> docentes = new ArrayList<>();
        List<String> codigos = new ArrayList<>();
        List<String> nClases = new ArrayList<>();
        List<String> idDocentes = new ArrayList<>();

        clases.clear();
        docentes.clear();
        codigos.clear();
        nClases.clear();
        idDocentes.clear();

        DatabaseReference myRef = database.getReference().child("ESTUDIANTES").child(idUsuario).child("CLASES");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot clase :
                        dataSnapshot.getChildren()) {
                    clases.add(clase.getValue().toString());
                }

                lyprogreso.setVisibility(View.GONE);

                if (!clases.isEmpty()) {

                    String cadenaSinCodigo = "";
                    String cadenaSinNombreSinId = "";
                    String cadenaSinClaseSinCodigo = "";
                    String nombreDocente = "";
                    String idDocente = "";
                    String nombreMateria = "";
                    String codigo = "";


                    for (int i = 0; i < clases.size(); i++) {
                        cadenaSinNombreSinId = clases.get(i).replaceAll(".*_", "");
                        cadenaSinClaseSinCodigo = clases.get(i).replaceAll("_" + cadenaSinNombreSinId, "");
                        nombreDocente = cadenaSinClaseSinCodigo.replaceAll(".*_", "");
                        idDocente = cadenaSinClaseSinCodigo.replaceAll("_" + nombreDocente, "");
                        cadenaSinCodigo = clases.get(i).substring(0, clases.get(i).indexOf("."));
                        nombreMateria = cadenaSinCodigo.replaceAll(cadenaSinClaseSinCodigo + "_", "");
                        codigo = cadenaSinNombreSinId.replaceAll(nombreMateria + ".", "");

                        codigos.add(codigo);
                        docentes.add(nombreDocente);
                        nClases.add(nombreMateria);
                        idDocentes.add(idDocente);
                    }

                    clasesEstudiantes.clear();
                    for (int i = 0; i < clases.size(); i++) {

                        // asi debo meter las clases
                        clasesEstudiantes.add(new ClasesEstudiante(
                                docentes.get(i), codigos.get(i), nClases.get(i), idDocentes.get(i)));
                    }

                    rvClasesEstudiante.setHasFixedSize(true);
                    rvClasesEstudiante.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rvClasesEstudiante.setAdapter(listaClases);

                    new Handler().postDelayed(() -> {
                        rvClasesEstudiante.setVisibility(View.VISIBLE);
                        rvClasesEstudiante.setAnimation(animation_down);
                    }, normal);

                } else {
                    pruebaClases.setVisibility(View.VISIBLE);
                    pruebaClases.setText("No estás registrado en ninguna clase");
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {

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

    private static String validaNombre(String nombre) {

        String primerNombre;
        String primerApellido;

        List posiciones = new ArrayList();

        for (int i = 0; i < nombre.length(); i++) {
            char indice = nombre.charAt(i);
            if (indice == ' ') {
                posiciones.add(i);
            }
        }

        if (posiciones.size() == 3) {
            primerNombre = nombre.substring(0, (int) posiciones.get(0)).trim();
            primerApellido = nombre.substring((int) posiciones.get(1), (int) posiciones.get(2)).trim();
            return nombre = primerNombre + " " + primerApellido;
        } else if (posiciones.size() == 2) {
            primerNombre = nombre.substring(0, (int) posiciones.get(0)).trim();
            primerApellido = nombre.substring((int) posiciones.get(0), (int) posiciones.get(1)).trim();
            return nombre = primerNombre + " " + primerApellido;
        } else if (posiciones.size() == 1) {
            return nombre;
        }

        return nombre;
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
        if (viewHolder instanceof AdaptadorClasesEstudiante.ViewHolder) {
            String docente = clasesEstudiantes.get(rvClasesEstudiante.getChildAdapterPosition(viewHolder.itemView)).getDocente();
            String materia = clasesEstudiantes.get(rvClasesEstudiante.getChildAdapterPosition(viewHolder.itemView)).getMateria();
            String codigo = clasesEstudiantes.get(rvClasesEstudiante.getChildAdapterPosition(viewHolder.itemView)).getCodigo();
            String iddocente = clasesEstudiantes.get(rvClasesEstudiante.getChildAdapterPosition(viewHolder.itemView)).getIdDocente();
            int posicion = viewHolder.getAdapterPosition();

            DatabaseReference myRef = database.getReference().child("ESTUDIANTES");

            if (compruebaConexion(this)) {

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿Realmente deseas eliminar esta clase?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", (dialogo11, id) -> {

                    showProgressBar("Eliminando clase, espere ...");
                    listaClases.removeItem(posicion);
                    // eliminamos la foto de la carpeta de la clase
                    storageReference.child("DOCENTES/" + iddocente + "/" + iddocente + "_" + docente + "_" + materia + "." + codigo + "/" + nombreCorto + ".png")
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    // eliminamos la marca de realtime database
                                    myRef.child(idUsuario).child("CLASES").child(codigo)
                                            .removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    clasesEstudiantes.clear();
                                                    finishProgressBar();
                                                    buscarClasesDataBase();
                                                    Toast.makeText(getApplicationContext(), "¡Clase eliminada con exito!", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            finishProgressBar();
                                            Toast.makeText(getApplicationContext(), "Ha ocurrido un error, intente mas tarde", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred
                            finishProgressBar();
                            Toast.makeText(getApplicationContext(), "Ocurrió un error al eliminar la clase", Toast.LENGTH_SHORT).show();
                        }
                    });
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

    public void onMainClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        finish();
    }
}