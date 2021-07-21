package org.environmentronic.iasssystem.activities.docentes;

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
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.environmentronic.iasssystem.R;
import org.environmentronic.iasssystem.activities.principales.MainActivity;
import org.environmentronic.iasssystem.adapters.AdaptadorClasesDocente;
import org.environmentronic.iasssystem.adapters.RecyclerItemTouchHelper;
import org.environmentronic.iasssystem.modulos.Genericos;

import java.util.ArrayList;
import java.util.List;

public class ListaClasesActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private ProgressDialog mProgress;
    private ConstraintLayout barraFoto;
    private LinearLayout fotoUsuarioListaClases;
    private TextView tvNombreListaClases;
    private LinearLayout lyprogreso_docente;

    // progress
    private ProgressBar cprogress_docente;
    private LinearLayout lyprogreso;

    // mensajes
    private TextView tvCargandoClases_docente;

    //Animaciones
    private Animation animation_rigth;
    private Animation animation_left;
    private Animation animation_down;
    private Integer normal = 1000;

    // crear tarjetas de clases
    private ArrayList<ClasesDocente> clasesDocentes;

    // recycler
    private RecyclerView rvClasesDocente;
    private AdaptadorClasesDocente listaClasesDocente;

    private StorageReference storageReference;
    private FirebaseDatabase database;

    private String nombreUsuario;
    private String idUsuario;

    private TextView pruebaClasesDocente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_lista_clases);

        idUsuario = getIntent().getStringExtra("idusuario");
        nombreUsuario = getIntent().getStringExtra("nomusuario");

        mProgress = new ProgressDialog(this);
        barraFoto = (ConstraintLayout) findViewById(R.id.barraFoto);
        fotoUsuarioListaClases = (LinearLayout) findViewById(R.id.fotoUsuarioListaClases);
        tvNombreListaClases = (TextView) findViewById(R.id.tvNombreListaClases);
        pruebaClasesDocente = (TextView) findViewById(R.id.pruebaClasesDocente);

        // inicializamos las listas
        clasesDocentes = new ArrayList<>();
        // recicladores
        rvClasesDocente = (RecyclerView) findViewById(R.id.rvClasesDocente);
        listaClasesDocente = new AdaptadorClasesDocente(clasesDocentes, this);

        animation_left = AnimationUtils.loadAnimation(this, R.anim.animation_left);
        animation_rigth = AnimationUtils.loadAnimation(this, R.anim.animation_rigth);
        animation_down = AnimationUtils.loadAnimation(this, R.anim.animation_down);

        barraFoto.startAnimation(animation_left);
        fotoUsuarioListaClases.startAnimation(animation_rigth);
        tvNombreListaClases.setAnimation(animation_left);

        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();

        new Handler().postDelayed(() -> {
            lyprogreso_docente = (LinearLayout) findViewById(R.id.lyprogreso_docente);
            lyprogreso_docente.setVisibility(View.VISIBLE);
            tvCargandoClases_docente = (TextView) findViewById(R.id.tvCargandoClases_docente);
            cprogress_docente = (ProgressBar) findViewById(R.id.cprogress_docente);

            new Handler().postDelayed(() -> {
                ponerDatosClasesDocente();
            }, normal);

        }, normal);

        // evento click para la lista de clases de los docentes
        listaClasesDocente.setOnClickListener(v -> {

            String materia = clasesDocentes.get(rvClasesDocente.getChildAdapterPosition(v)).getMateria();
            String codigo = clasesDocentes.get(rvClasesDocente.getChildAdapterPosition(v)).getCodigo();
            String idusuario = clasesDocentes.get(rvClasesDocente.getChildAdapterPosition(v)).getIdusuario();
            String nomusuario = clasesDocentes.get(rvClasesDocente.getChildAdapterPosition(v)).getNombreUsuario();

            Intent intent = new Intent(getApplicationContext(), InfoClasesAlumnoActivity.class);
            intent.putExtra("materia", materia);
            intent.putExtra("codigo", codigo);
            intent.putExtra("idusuario", idusuario);
            intent.putExtra("nomusuario", nomusuario);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        });

        // eliminar clases docente
        ItemTouchHelper.SimpleCallback simpleCallbackDocentes = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, ListaClasesActivity.this, 0, 1, 0);
        new ItemTouchHelper(simpleCallbackDocentes).attachToRecyclerView(rvClasesDocente);
    }

    private void ponerDatosClasesDocente() {
        if (compruebaConexion(this)) {
            buscarClasesDataBaseDocente();
        } else {
            lyprogreso.setVisibility(View.GONE);
            Toast.makeText(this, "Debe tener acceso a internet para ver sus clases. Por favor conectese a internet y vuelva a ingresar a este apartador", Toast.LENGTH_LONG).show();
        }
    }

    private void buscarClasesDataBaseDocente() {

        clasesDocentes.clear();

        List<String> clases = new ArrayList<>();
        List<String> codigos = new ArrayList<>();
        List<String> nClases = new ArrayList<>();

        clases.clear();
        codigos.clear();
        nClases.clear();

        DatabaseReference myRef = database.getReference().child("DOCENTES").child(idUsuario).child("CLASES");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot clase :
                        dataSnapshot.getChildren()) {
                    clases.add(clase.getValue().toString());
                }

                lyprogreso_docente.setVisibility(View.GONE);

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
                        nClases.add(nombreMateria);
                    }

                    for (int i = 0; i < clases.size(); i++) {

                        clasesDocentes.add(new ClasesDocente(
                                nClases.get(i), codigos.get(i), idUsuario, nombreUsuario));
                    }

                    rvClasesDocente.setHasFixedSize(true);
                    rvClasesDocente.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rvClasesDocente.setAdapter(listaClasesDocente);

                    new Handler().postDelayed(() -> {
                        rvClasesDocente.setVisibility(View.VISIBLE);
                        rvClasesDocente.setAnimation(animation_down);
                    }, normal);

                } else {
                    pruebaClasesDocente.setVisibility(View.VISIBLE);
                    pruebaClasesDocente.setText("No has creado ninguna clase");
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

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

    public void onMainClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        finish();
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AdaptadorClasesDocente.ViewHolder) {
            String nombreUsuario = clasesDocentes.get(rvClasesDocente.getChildAdapterPosition(viewHolder.itemView)).getNombreUsuario();
            String materia = clasesDocentes.get(rvClasesDocente.getChildAdapterPosition(viewHolder.itemView)).getMateria();
            String codigo = clasesDocentes.get(rvClasesDocente.getChildAdapterPosition(viewHolder.itemView)).getCodigo();
            String idusuario = clasesDocentes.get(rvClasesDocente.getChildAdapterPosition(viewHolder.itemView)).getIdusuario();
            DatabaseReference miMarca = database.getReference().child("DOCENTES").child(idusuario).child("CLASES").child(codigo);
            ArrayList fotos = new ArrayList();
            fotos.clear();
            ArrayList carpeta = new ArrayList();
            carpeta.clear();

            // y borrar en cada estudiante la clase
            // borramos la carpeta

            if (compruebaConexion(this)) {

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿Realmente deseas eliminar esta clase?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", (dialogo11, id) -> {

                    showProgressBar("Eliminando clase, espere ...");
                    listaClasesDocente.removeItem(viewHolder.getAdapterPosition());
                    // eliminamos la foto de la carpeta de la clase
                    storageReference
                            .child("DOCENTES/" + idusuario + "/" + idusuario + "_" + nombreUsuario + "_" + materia + "." + codigo + "/")
                            .listAll()
                            .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                @Override
                                public void onSuccess(ListResult listResult) {

                                    // borramos las fotos
                                    for (StorageReference item :
                                            listResult.getItems()) {
                                        item.delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                        //fotos.add(item.getName());
                                    }

                                    ArrayList<String> nombreCarpetaEstudiante = new ArrayList();
                                    ArrayList<String> idEstudiantes = new ArrayList();
                                    ArrayList<String> nombreEstudiantes = new ArrayList();
                                    ArrayList<String> nombresCortosEstudiantes = new ArrayList();
                                    nombreCarpetaEstudiante.clear();
                                    idEstudiantes.clear();
                                    nombreEstudiantes.clear();
                                    nombresCortosEstudiantes.clear();

                                    storageReference
                                            .child("DOCENTES/" + idusuario + "/" + idusuario + "_" + nombreUsuario + "_" + materia + "." + codigo + "/ESTUDIANTES/")
                                            .listAll()
                                            .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                                @Override
                                                public void onSuccess(ListResult listResult) {

                                                    // leemos la carpeta FOTO_CLASE y ESTUDIANTES
                                                    for (StorageReference carpetaEstudiante :
                                                            listResult.getPrefixes()) {
                                                        nombreCarpetaEstudiante.add(carpetaEstudiante.getName());
                                                    }

                                                    for (int i = 0; i < nombreCarpetaEstudiante.size(); i++) {
                                                        nombreEstudiantes.add(nombreCarpetaEstudiante.get(i).replaceAll(".*_", ""));
                                                        nombresCortosEstudiantes.add(Genericos.validaNombre(nombreEstudiantes.get(i)));
                                                        idEstudiantes.add(nombreCarpetaEstudiante.get(i).replaceAll("_" + nombreEstudiantes.get(i), ""));
                                                    }

                                                    for (int i = 0; i < nombreCarpetaEstudiante.size(); i++) {
                                                        storageReference
                                                                .child("DOCENTES/" + idusuario + "/" + idusuario + "_" + nombreUsuario + "_" + materia + "." + codigo + "/ESTUDIANTES/" + idEstudiantes.get(i) + "_" + nombreEstudiantes.get(i) + "/" + nombresCortosEstudiantes.get(i) + ".png")
                                                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception exception) {

                                                            }
                                                        });
                                                    }

                                                    borrarCarpetaAsistencia(idusuario, materia, codigo, miMarca);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    finishProgressBar();
                                                    Toast.makeText(getApplicationContext(), "Error al eliminar la clase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    finishProgressBar();
                                    Toast.makeText(getApplicationContext(), "Error al eliminar la clase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void borrarCarpetaAsistencia(String idusuario, String materia, String codigo, DatabaseReference miMarca) {
        // borramos la carpeta
        storageReference
                .child("DOCENTES/" + idusuario + "/" + idusuario + "_" + nombreUsuario + "_" + materia + "." + codigo + "/FOTO_CLASE/asistencia.png")
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                // borrar la marca del docente de la clase
                borrarMarcaDocente(miMarca, codigo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                // borrar la marca del docente de la clase
                borrarMarcaDocente(miMarca, codigo);
            }
        });
    }

    private void borrarMarcaDocente(DatabaseReference miMarca, String codigo) {
        miMarca.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        borrarMarcaEstudiante(codigo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finishProgressBar();
                borrarMarcaEstudiante(codigo);
                Toast.makeText(getApplicationContext(), "Ha ocurrido un error, intente mas tarde", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void borrarMarcaEstudiante(String codigo) {
        ArrayList<String> idEstudiantes = new ArrayList();
        idEstudiantes.clear();
        DatabaseReference marcaEstudiante = database.getReference().child("ESTUDIANTES");

        // Read from the database
        marcaEstudiante.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot id :
                            dataSnapshot.getChildren()) {
                        idEstudiantes.add(id.getKey());
                    }

                    for (int i = 0; i < idEstudiantes.size(); i++) {
                        int finalI = i;
                        marcaEstudiante
                                .child(idEstudiantes.get(i))
                                .child("CLASES")
                                .child(codigo)
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if ((finalI == (idEstudiantes.size() - 1))) {
                                    finishProgressBar();
                                    clasesDocentes.clear();
                                    buscarClasesDataBaseDocente();
                                    //Toast.makeText(ListaClasesActivity.this, "¡Clase eliminada con exito!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                finishProgressBar();
                            }
                        });
                    }
                } else {
                    finishProgressBar();
                    clasesDocentes.clear();
                    buscarClasesDataBaseDocente();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                finishProgressBar();
            }
        });
    }
}