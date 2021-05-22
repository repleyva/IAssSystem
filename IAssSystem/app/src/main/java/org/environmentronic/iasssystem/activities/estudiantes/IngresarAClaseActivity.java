package org.environmentronic.iasssystem.activities.estudiantes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.environmentronic.iasssystem.R;
import org.environmentronic.iasssystem.activities.principales.MainActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class IngresarAClaseActivity extends AppCompatActivity {

    private ConstraintLayout barraFoto;
    private LinearLayout fotoUsuarioBBDD;
    private TextView tvNombreIngClases;
    private TextInputLayout etCodigo;
    private Button btnIngresarAclase;

    private TextView prueba;
    private EditText editTextcodigo;
    private TextView clase;
    private TextView docente;
    private CardView datosClase;
    private ImageView photoBBDDIngClases;

    private Animation animation_rigth;
    private Animation animation_left;
    private Animation animation_down;

    private ProgressDialog mProgress;
    private Boolean banderaIngClases = false;

    private Integer acond1 = 1340;
    private Integer acond2 = 600;

    private FirebaseDatabase database;
    private StorageReference storageReference;

    private String nombreUsuario;
    private String nombreCorto;
    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_ingresar_aclase);

        idUsuario = getIntent().getStringExtra("idusuario");
        nombreUsuario = getIntent().getStringExtra("nomusuario");
        nombreCorto = validaNombre(nombreUsuario);

        mProgress = new ProgressDialog(this);

        barraFoto = (ConstraintLayout) findViewById(R.id.barraFoto);
        fotoUsuarioBBDD = (LinearLayout) findViewById(R.id.fotoUsuarioBBDD);
        tvNombreIngClases = (TextView) findViewById(R.id.tvNombreIngClases);
        etCodigo = (TextInputLayout) findViewById(R.id.etCodigo);
        btnIngresarAclase = (Button) findViewById(R.id.btnIngresarAclase);
        prueba = (TextView) findViewById(R.id.prueba);
        editTextcodigo = (EditText) findViewById(R.id.editTextcodigo);
        clase = (TextView) findViewById(R.id.clase);
        docente = (TextView) findViewById(R.id.docente);
        datosClase = (CardView) findViewById(R.id.datosClase);
        photoBBDDIngClases = (ImageView) findViewById(R.id.photoBBDDIngClases);

        animation_left = AnimationUtils.loadAnimation(this, R.anim.animation_left);
        animation_rigth = AnimationUtils.loadAnimation(this, R.anim.animation_rigth);
        animation_down = AnimationUtils.loadAnimation(this, R.anim.animation_down);

        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        barraFoto.startAnimation(animation_left);
        fotoUsuarioBBDD.startAnimation(animation_rigth);
        tvNombreIngClases.setAnimation(animation_left);
        etCodigo.startAnimation(animation_left);
        btnIngresarAclase.startAnimation(animation_left);
    }

    public void accionIngresarClase(View view) {

        if (compruebaConexion(this)) {
            if (!editTextcodigo.getText().toString().isEmpty()) {
                showProgressBar("Buscando clase, espere ...");
                DatabaseReference myRef = database.getReference().child("DOCENTES");
                DatabaseReference myRefp = database.getReference();
                List idDocentes = new ArrayList();
                List clasesDocentes = new ArrayList();
                List claves = new ArrayList();
                Set codigos = new HashSet();
                String codigoIng = editTextcodigo.getText().toString().toUpperCase();
                idDocentes.clear();
                clasesDocentes.clear();
                claves.clear();
                codigos.clear();

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        // identifico los id de los docentes
                        for (DataSnapshot idDocente :
                                dataSnapshot.getChildren()) {
                            idDocentes.add(idDocente.getKey());
                        }

                        // busco las clases de cada docente
                        for (int i = 0; i < idDocentes.size(); i++) {
                            // Read from the database
                            int finalI = i;
                            myRef.child((String) idDocentes.get(i))
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // identifico los campos de los docentes
                                            for (DataSnapshot clase :
                                                    dataSnapshot.getChildren()) {
                                                clasesDocentes.add(clase.getKey());
                                            }

                                            for (int j = 0; j < clasesDocentes.size(); j++) {
                                                myRef.child((String) idDocentes.get(finalI)).child((String) clasesDocentes.get(j)).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        // This method is called once with the initial value and again
                                                        // whenever data at this location is updated.
                                                        // identifico los dodigos de los docentes
                                                        for (DataSnapshot clase :
                                                                dataSnapshot.getChildren()) {
                                                            codigos.add(clase.getValue());
                                                        }

                                                        String codigoCompleto = null;
                                                        String cadenaSinCodigo = "";
                                                        String nombreClase = "";
                                                        String cadenaSinCodigoSinClase = "";
                                                        String nombreDocente = "";
                                                        String idDocente = "";

                                                        Iterator<String> it = codigos.iterator();
                                                        if (!codigoIng.isEmpty()) {
                                                            prueba.setText("");

                                                            while (it.hasNext()) {
                                                                codigoCompleto = it.next();
                                                                if (codigoCompleto.contains(codigoIng)) {
                                                                    banderaIngClases = true;
                                                                    cadenaSinCodigo = codigoCompleto.replaceAll("." + codigoIng, "");
                                                                    nombreClase = cadenaSinCodigo.replaceAll(".*_", "");
                                                                    cadenaSinCodigoSinClase = cadenaSinCodigo.replaceAll("_" + nombreClase, "");
                                                                    nombreDocente = cadenaSinCodigoSinClase.replaceAll(".*_", "");
                                                                    idDocente = cadenaSinCodigoSinClase.replaceAll("_" + nombreDocente, "");
                                                                }
                                                            }

                                                            if (nombreClase.isEmpty() || nombreDocente.isEmpty()) {
                                                                docente.setText("Docente no encontrado");
                                                                clase.setText("Clase no encontrada");
                                                            } else {
                                                                docente.setText(nombreDocente);
                                                                clase.setText(nombreClase);
                                                            }

                                                            if (banderaIngClases) {
                                                                buscarFotoExistente(nombreDocente, nombreClase, codigoIng, idDocente);
                                                            } else {
                                                                finishProgressBar();
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        datosClase.setVisibility(View.VISIBLE);
                                                                        datosClase.setAnimation(animation_down);
                                                                    }
                                                                }, acond1);

                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        editTextcodigo.setText("");
                                                                    }
                                                                }, acond2);
                                                            }
                                                        } else {
                                                            finishProgressBar();
                                                            prueba.setText("Debe ingresar un Código");
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError error) {
                                                        // Failed to read value
                                                        finishProgressBar();
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            finishProgressBar();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        prueba.setText("Ha ocurrido un error, revise su conexión a internet o intentelo de nuevo mas tarde");
                    }
                });
            } else {
                Toast.makeText(this, "Debe ingresar un código", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Para ingresar a una clase debe tener acceso a internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void buscarFotoExistente(String nombreDocente, String nombreClase, String codigoIng, String idDocente) {

        storageReference.child("ESTUDIANTES/" + idUsuario + "/" + nombreCorto + ".png")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        banderaIngClases = false;
                        Glide.with(IngresarAClaseActivity.this).load(uri).into(photoBBDDIngClases);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                subirFotoClase(nombreDocente, nombreClase, codigoIng, idDocente);
                            }
                        }, 2000);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                editTextcodigo.setText("");
                finishProgressBar();
                prueba.setText("Lo sentimos, para ingresar a una clase debes tener una foto para " +
                        "realizar el reconocimiento facial. Dirígete a la sección de subir foto de " +
                        "reconocimiento accediendo al boton de ingresar como estudiante.");
            }
        });


    }

    private void subirFotoClase(String nombreDocente, String nombreClase, String codigoIng, String idDocente) {

        if (photoBBDDIngClases != null) {
            // Get the data from an ImageView as bytes
            photoBBDDIngClases.setDrawingCacheEnabled(true);
            photoBBDDIngClases.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) photoBBDDIngClases.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageReference.child("DOCENTES/" + idDocente + "/" + idDocente + "_" + nombreDocente + "_" + nombreClase + "." + codigoIng + "/" + idUsuario + "_" + nombreUsuario + "/" + nombreCorto + ".png").putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    finishProgressBar();
                    prueba.setText("Ha ocurrido un error, revise su conexión a internet o intentelo de nuevo mas tarde");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.

                    DatabaseReference myRef = database.getReference().child("ESTUDIANTES");

                    myRef.child(idUsuario)
                            .child("CLASES")
                            .child(codigoIng)
                            .setValue(idDocente + "_" + nombreDocente + "_" + nombreClase + "." + codigoIng);

                    finishProgressBar();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            datosClase.setVisibility(View.VISIBLE);
                            datosClase.setAnimation(animation_down);
                        }
                    }, acond1);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editTextcodigo.setText("");
                        }
                    }, acond2);
                }
            });
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

    public void onMainClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        finish();
    }

    @Override
    public void onBackPressed() {

    }

}