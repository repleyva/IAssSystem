package org.environmentronic.iasssystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.environmentronic.iasssystem.adapters.AdaptadorClasesDocente;
import org.environmentronic.iasssystem.adapters.AdaptadorClasesEstudiante;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ImageView photoPerfil;
    private RelativeLayout barra;

    private TextView tvNombre;
    private TextView tvNombreFoto;
    private TextView tvNombreIngClases;
    private TextView tvNombreCrearClase;
    private TextView tvNombreVerClases;
    private TextView tvNombreListaClases;
    private TextView tvCorreo;

    private Button btnIngresarComoDocente;
    private Button btnIngresarComoEstudiante;

    private Button btnSubirFotos;
    private Button btnVerMisClases;
    private Button btnIngresarAClases;

    private Button btnCrearClase;
    private Button btnListaClases;

    private Button btnTomarFoto;
    private Button btnBorrarFoto;
    private Button btnSubirFoto;
    private Button btnEliminarFotobbdd;

    private Button btnIngresarAclase;
    private Button btnDocenteCrearClase;

    private LinearLayout tarjetaUsuario;
    private LinearLayout lyEstudiante;
    private LinearLayout lyDocente;

    private RelativeLayout cabeceraFoto;
    private RelativeLayout cabeceraIngFoto;
    private RelativeLayout cabeceraCrearClase;
    private RelativeLayout cabeceraVerClases;
    private RelativeLayout cabeceraListaClases;

    private LinearLayout lyFoto;
    private LinearLayout lyIngClases;
    private LinearLayout lyCrearClases;
    private LinearLayout lyprogreso;
    private LinearLayout lyprogreso_docente;

    private LinearLayout fotoUsuario;
    private LinearLayout fotoUsuarioBBDD;
    private LinearLayout fotoUsuarioDocente;
    private LinearLayout fotoUsuarioVerClases;
    private LinearLayout fotoUsuarioListaClases;

    private GoogleApiClient googleApiClient;

    private Boolean banderabtnEstudiante = false;
    private Boolean banderabtnDocente = false;

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

    private LinearLayout padre;
    private ConstraintLayout barraFoto;

    private ScrollView scrollFoto;
    private ScrollView scrollIngClases;
    private ScrollView scrollCrearClases;
    private ScrollView scrollVerClases;
    private ScrollView scrollListaClases;

    private TextInputLayout etCodigo;
    private TextInputLayout etNombreClase;
    private TextInputLayout etCodigoClase;

    private CardView datosClase;
    private CardView datosClaseCreada;

    // crear tarjetas de clases
    private List<ClasesEstudiante> clasesEstudiantes;
    private List<ClasesDocente> clasesDocentes;

    // recycler
    private RecyclerView rvClasesEstudiante;
    private RecyclerView rvClasesDocente;
    private AdaptadorClasesEstudiante listaClases;
    private AdaptadorClasesDocente listaClasesDocente;

    // progress
    private ProgressBar cprogress;
    private ProgressBar cprogress_docente;

    // mensajes
    private TextView tvCargandoClases;
    private TextView tvCargandoClases_docente;

    // constantes timer
    private Integer largo = 2000;
    private Integer normal = 1000;
    private Integer delay = 667;
    private Integer acond1 = 1340;
    private Integer acond2 = 600;

    /*
    3000 = 2000
    1000 = 667
    1500 = 1000
    2000 = 1340
    900 = 600
    */

    // ---------------------------------- Variables para el backend -------------------------------
    private String nombreUsuario;
    private String correoUsuario;
    private String idUsuario;
    private String fotoPerfilUsuario;
    private String nombreCorto;

    private ImageView fotoTomada;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;
    private StorageReference storageReference;
    private ProgressDialog mProgress;

    // -------------------------------- variables para los docente --------------------------------
    private EditText editTextNombreClase;
    private EditText editTextcodigoClase;

    private ImageView fotoClase;
    private TextView tvNClase;
    private TextView tvNCodigo;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_main);

        photoPerfil = (ImageView) findViewById(R.id.photoPerfil);
        barra = (RelativeLayout) findViewById(R.id.barra);
        tvNombre = (TextView) findViewById(R.id.tvNombre);
        tvCorreo = (TextView) findViewById(R.id.tvCorreo);
        btnIngresarComoDocente = (Button) findViewById(R.id.btnIngresarComoDocente);
        btnIngresarComoEstudiante = (Button) findViewById(R.id.btnIngresarComoEstudiante);
        tarjetaUsuario = (LinearLayout) findViewById(R.id.tarjetaUsuario);
        lyEstudiante = (LinearLayout) findViewById(R.id.lyEstudiante);
        lyDocente = (LinearLayout) findViewById(R.id.lyDocente);

        btnVerMisClases = (Button) findViewById(R.id.btnVerMisClases);
        btnSubirFotos = (Button) findViewById(R.id.btnSubirFotos);
        btnIngresarAClases = (Button) findViewById(R.id.btnIngresarAClases);

        btnCrearClase = (Button) findViewById(R.id.btnCrearClase);
        btnListaClases = (Button) findViewById(R.id.btnListaClases);

        padre = (LinearLayout) findViewById(R.id.padre);
        barraFoto = (ConstraintLayout) findViewById(R.id.barraFoto);

        tvNombreFoto = (TextView) findViewById(R.id.tvNombreFoto);
        tvNombreIngClases = (TextView) findViewById(R.id.tvNombreIngClases);
        tvNombreCrearClase = (TextView) findViewById(R.id.tvNombreCrearClase);
        tvNombreVerClases = (TextView) findViewById(R.id.tvNombreVerClases);
        tvNombreListaClases = (TextView) findViewById(R.id.tvNombreListaClases);

        btnTomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        btnSubirFoto = (Button) findViewById(R.id.btnSubirFoto);
        btnBorrarFoto = (Button) findViewById(R.id.btnBorrarFoto);
        btnEliminarFotobbdd = (Button) findViewById(R.id.btnEliminarFotobbdd);

        btnIngresarAclase = (Button) findViewById(R.id.btnIngresarAclase);
        btnDocenteCrearClase = (Button) findViewById(R.id.btnDocenteCrearClase);

        cabeceraFoto = (RelativeLayout) findViewById(R.id.cabeceraFoto);
        cabeceraIngFoto = (RelativeLayout) findViewById(R.id.cabeceraIngFoto);
        cabeceraCrearClase = (RelativeLayout) findViewById(R.id.cabeceraCrearClase);
        cabeceraVerClases = (RelativeLayout) findViewById(R.id.cabeceraVerClases);
        cabeceraListaClases = (RelativeLayout) findViewById(R.id.cabeceraListaClases);

        lyFoto = (LinearLayout) findViewById(R.id.lyFoto);
        lyIngClases = (LinearLayout) findViewById(R.id.lyIngClases);
        lyCrearClases = (LinearLayout) findViewById(R.id.lyCrearClases);

        fotoUsuario = (LinearLayout) findViewById(R.id.fotoUsuario);
        fotoUsuarioBBDD = (LinearLayout) findViewById(R.id.fotoUsuarioBBDD);
        fotoUsuarioDocente = (LinearLayout) findViewById(R.id.fotoUsuarioDocente);
        fotoUsuarioVerClases = (LinearLayout) findViewById(R.id.fotoUsuarioVerClases);
        fotoUsuarioListaClases = (LinearLayout) findViewById(R.id.fotoUsuarioListaClases);

        scrollFoto = (ScrollView) findViewById(R.id.scrollFoto);
        scrollIngClases = (ScrollView) findViewById(R.id.scrollIngClases);
        scrollCrearClases = (ScrollView) findViewById(R.id.scrollCrearClases);
        scrollVerClases = (ScrollView) findViewById(R.id.scrollVerClases);
        scrollListaClases = (ScrollView) findViewById(R.id.scrollListaClases);

        etCodigo = (TextInputLayout) findViewById(R.id.etCodigo);
        etNombreClase = (TextInputLayout) findViewById(R.id.etNombreClase);
        etCodigoClase = (TextInputLayout) findViewById(R.id.etCodigoClase);

        datosClase = (CardView) findViewById(R.id.datosClase);
        datosClaseCreada = (CardView) findViewById(R.id.datosClaseCreada);

        // inicializamos las listas
        clasesEstudiantes = new ArrayList<>();
        clasesDocentes = new ArrayList<>();

        // recicladores
        rvClasesEstudiante = (RecyclerView) findViewById(R.id.rvClasesEstudiante);
        rvClasesDocente = (RecyclerView) findViewById(R.id.rvClasesDocente);
        listaClases = new AdaptadorClasesEstudiante(clasesEstudiantes, this);
        listaClasesDocente = new AdaptadorClasesDocente(clasesDocentes, this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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

        btnIngresarComoDocente.startAnimation(animation_left);
        btnIngresarComoEstudiante.startAnimation(animation_left);
        tvNombre.setAnimation(animation_left);
        tarjetaUsuario.setAnimation(animation_rigth);

        // ------------------------------ Casting variables backend -------------------------------------------
        fotoTomada = (ImageView) findViewById(R.id.photoBBDD);

        // subir foto
        storageReference = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);

        // ------------------------------- Variables para crear clase -----------------------------------------
        editTextcodigoClase = (EditText) findViewById(R.id.editTextcodigoClase);
        editTextNombreClase = (EditText) findViewById(R.id.editTextNombreClase);
        fotoClase = (ImageView) findViewById(R.id.fotoClase);
        tvNClase = (TextView) findViewById(R.id.tvNClase);
        tvNCodigo = (TextView) findViewById(R.id.tvNCodigo);
        db = FirebaseFirestore.getInstance();

        padre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (banderabtnDocente) {
                    btnListaClases.startAnimation(animaciondere_ocult);
                    btnCrearClase.startAnimation(animaciondere_ocult);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lyDocente.setVisibility(View.GONE);
                        }
                    }, delay);
                    banderabtnDocente = false;
                }

                if (banderabtnEstudiante) {
                    btnVerMisClases.startAnimation(animaciondere_ocult);
                    btnIngresarAClases.startAnimation(animaciondere_ocult);
                    btnSubirFotos.startAnimation(animaciondere_ocult);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lyEstudiante.setVisibility(View.GONE);
                        }
                    }, delay);
                    banderabtnEstudiante = false;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> optionalPendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (optionalPendingResult.isDone()) {
            GoogleSignInResult result = optionalPendingResult.get();
            handleSignInResult(result);
        } else {
            optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    handleSignInResult(result);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (!result.isSuccess()) {
            goToScreenLogin();
        } else {
            if (compruebaConexion(this)) {
                GoogleSignInAccount cuenta = result.getSignInAccount();
                nombreUsuario = cuenta.getDisplayName();
                correoUsuario = cuenta.getEmail();
                idUsuario = cuenta.getId();
                fotoPerfilUsuario = cuenta.getPhotoUrl().toString();
                tvNombre.setText("Bienvenido\na IAssSystem\n" + nombreUsuario);
                tvCorreo.setText(correoUsuario);
                Glide.with(this).load(cuenta.getPhotoUrl()).into(photoPerfil);
            } else {
                Toast.makeText(getApplicationContext(), "Debe tener acceso a internet", Toast.LENGTH_SHORT).show();
                goToScreenLogin();
            }
        }

    }

    private void goToScreenLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
    }

    public void cerrarSesion(View view) {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goToScreenLogin();
                } else {
                    Toast.makeText(MainActivity.this, "No se logró cerrar la sesión", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void revokar(View view) {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goToScreenLogin();
                } else {
                    Toast.makeText(MainActivity.this, "No se logró revocar la sesión", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onAboutUs(View View) {
        startActivity(new Intent(this, UsActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        finish();
    }

    // ****************************** métodos de estudiantes ******************************

    public void ingEstudiante(View view) {
        if (banderabtnDocente) {
            lyDocente.setVisibility(View.GONE);
            lyEstudiante.setVisibility(View.VISIBLE);
            btnVerMisClases.startAnimation(animation_rigth);
            btnIngresarAClases.startAnimation(animation_rigth);
            btnSubirFotos.startAnimation(animation_rigth);
            banderabtnEstudiante = true;
            banderabtnDocente = false;
        } else {
            lyEstudiante.setVisibility(View.VISIBLE);
            btnVerMisClases.startAnimation(animation_rigth);
            btnIngresarAClases.startAnimation(animation_rigth);
            btnSubirFotos.startAnimation(animation_rigth);
            banderabtnEstudiante = true;
        }
    }

    public void subirFoto(View view) {
        scrollFoto.setVisibility(View.VISIBLE);
        tvNombre.startAnimation(animation_left_ocult_long);
        btnIngresarComoDocente.startAnimation(animation_left_ocult_long);
        btnIngresarComoEstudiante.startAnimation(animation_left_ocult_long);
        tarjetaUsuario.startAnimation(animation_rigth_ocult_long);
        btnVerMisClases.startAnimation(animaciondere_ocult);
        btnIngresarAClases.startAnimation(animaciondere_ocult);
        btnSubirFotos.startAnimation(animation_rigth_ocult_long);
        barra.startAnimation(animation_rigth_ocult_long);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnIngresarAClases.setVisibility(View.GONE);
                btnVerMisClases.setVisibility(View.GONE);
            }
        }, normal);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvNombre.setVisibility(View.GONE);
                tarjetaUsuario.setVisibility(View.GONE);
                lyEstudiante.setVisibility(View.GONE);
                barra.setVisibility(View.GONE);
                btnIngresarComoDocente.setVisibility(View.GONE);
                btnIngresarComoEstudiante.setVisibility(View.GONE);
            }
        }, largo);

        mostrarBarraIzq();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cabeceraFoto.setVisibility(View.VISIBLE);
                fotoUsuario.startAnimation(animation_rigth);
                tvNombreFoto.setAnimation(animation_left);
            }
        }, normal);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lyFoto.setVisibility(View.VISIBLE);
                btnTomarFoto.startAnimation(animation_left);
                btnBorrarFoto.startAnimation(animation_left);
                btnSubirFoto.startAnimation(animation_left);
                btnEliminarFotobbdd.startAnimation(animation_left);
            }
        }, normal);
    }

    public void tomarFoto(View view) {
        abrirCamara();
    }

    public void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            fotoTomada.setImageBitmap(imageBitmap);
        }
    }

    public void subirFotoFirebase(View view) {

        boolean coneccion = compruebaConexion(this);
        nombreCorto = validaNombre(nombreUsuario);

        if (imageBitmap != null) {
            if (coneccion) {
                showProgressBar("Subiendo foto, espere ...");
                // Get the data from an ImageView as bytes
                fotoTomada.setDrawingCacheEnabled(true);
                fotoTomada.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) fotoTomada.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = storageReference.child("ESTUDIANTES/" + idUsuario + "/" + nombreCorto + ".png").putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        finishProgressBar();
                        Toast.makeText(MainActivity.this, "Hubo un error intentando subir la foto", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        finishProgressBar();
                        fotoTomada.setImageResource(R.drawable.ic_register_hero);
                        imageBitmap = null;
                        Toast.makeText(MainActivity.this, "La foto se subió con exito", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Debes tener acceso a internet", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debe tomar una foto", Toast.LENGTH_SHORT).show();
        }
    }

    public void setBtnBorrarFoto(View view) {

        if (imageBitmap != null) {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("¿Realmente deseas eliminar la foto?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    fotoTomada.setImageResource(R.drawable.ic_register_hero);
                    imageBitmap = null;
                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {

                }
            });
            dialogo1.show();
        } else {
            Toast.makeText(this, "Debe tomar una foto", Toast.LENGTH_SHORT).show();
        }
    }

    public void setBtnEliminarFotobbdd(View view) {

        nombreCorto = validaNombre(nombreUsuario);
        if (compruebaConexion(this)) {

            // ------------------------------- borramos la foto -----------------------------
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("¿Realmente deseas eliminar la foto de la base de datos?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {

                    fotoTomada.setImageResource(R.drawable.ic_register_hero);
                    imageBitmap = null;

                    showProgressBar("Estamos eliminando la foto, espere ...");

                    // Delete the file
                    storageReference.child("ESTUDIANTES/" + idUsuario + "/" + nombreCorto + ".png").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                            finishProgressBar();
                            Toast.makeText(MainActivity.this, "La foto fué borrada con éxito", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred
                            finishProgressBar();
                            Toast.makeText(MainActivity.this, "Si no cuentas con una foto, no tienes nada que borrar.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {

                }
            });
            dialogo1.show();
        } else {
            Toast.makeText(this, "No tiene acceso a internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void verMisClases(View view) {

        scrollVerClases.setVisibility(View.VISIBLE);
        tvNombre.startAnimation(animation_left_ocult_long);
        btnIngresarComoDocente.startAnimation(animation_left_ocult_long);
        btnIngresarComoEstudiante.startAnimation(animation_left_ocult_long);
        tarjetaUsuario.startAnimation(animation_rigth_ocult_long);
        btnVerMisClases.startAnimation(animation_rigth_ocult_long);
        btnIngresarAClases.startAnimation(animaciondere_ocult);
        btnSubirFotos.startAnimation(animaciondere_ocult);
        barra.startAnimation(animation_rigth_ocult_long);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnSubirFotos.setVisibility(View.INVISIBLE);
                btnVerMisClases.setVisibility(View.INVISIBLE);
                btnIngresarAClases.setVisibility(View.INVISIBLE);
            }
        }, normal);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnSubirFotos.setVisibility(View.GONE);
                btnVerMisClases.setVisibility(View.GONE);
                btnIngresarAClases.setVisibility(View.GONE);
                tvNombre.setVisibility(View.GONE);
                tarjetaUsuario.setVisibility(View.GONE);
                lyEstudiante.setVisibility(View.GONE);
                barra.setVisibility(View.GONE);
                btnIngresarComoDocente.setVisibility(View.GONE);
                btnIngresarComoEstudiante.setVisibility(View.GONE);
                btnIngresarAClases.setVisibility(View.GONE);
            }
        }, largo);

        // Fin ocultar main --------------------------------------------------
        mostrarBarraIzq();

        // mostrar componentes principales de la pantalla --------------------------------
        new Handler().postDelayed(() -> {
            cabeceraVerClases.setVisibility(View.VISIBLE);
            fotoUsuarioVerClases.startAnimation(animation_rigth);
            tvNombreVerClases.setAnimation(animation_left);
        }, normal);

        new Handler().postDelayed(() -> {
            lyprogreso = (LinearLayout) findViewById(R.id.lyprogreso);
            lyprogreso.setVisibility(View.VISIBLE);
            tvCargandoClases = (TextView) findViewById(R.id.tvCargandoClases);
            cprogress = (ProgressBar) findViewById(R.id.cprogress);

            new Handler().postDelayed(() -> {
                lyprogreso.setVisibility(View.GONE);
            }, normal);

            new Handler().postDelayed(() -> {
                rvClasesEstudiante.setVisibility(View.VISIBLE);
                rvClasesEstudiante.setAnimation(animation_down);
                ponerDatos();

            }, normal);

        }, normal);
    }

    private void ponerDatos() {
        // mostramos las clases
        clasesEstudiantes.clear();
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));
        clasesEstudiantes.add(new ClasesEstudiante(
                "Rusvel Enrique Pasos Leyva", "EL447", "Tratamiento de Señales"));

        rvClasesEstudiante.setHasFixedSize(true);
        rvClasesEstudiante.setLayoutManager(new LinearLayoutManager(this));
        rvClasesEstudiante.setAdapter(listaClases);
    }

    public void ingresarClase(View view) {
        scrollIngClases.setVisibility(View.VISIBLE);
        tvNombre.startAnimation(animation_left_ocult_long);
        btnIngresarComoDocente.startAnimation(animation_left_ocult);
        btnIngresarComoEstudiante.startAnimation(animation_left_ocult);
        tarjetaUsuario.startAnimation(animation_rigth_ocult_long);
        btnVerMisClases.startAnimation(animaciondere_ocult);
        btnIngresarAClases.startAnimation(animation_rigth_ocult_long);
        btnSubirFotos.startAnimation(animaciondere_ocult);
        barra.startAnimation(animation_rigth_ocult_long);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnSubirFotos.setVisibility(View.INVISIBLE);
                btnVerMisClases.setVisibility(View.INVISIBLE);
                btnIngresarComoDocente.setVisibility(View.INVISIBLE);
                btnIngresarComoEstudiante.setVisibility(View.INVISIBLE);
            }
        }, normal);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnSubirFotos.setVisibility(View.GONE);
                btnVerMisClases.setVisibility(View.GONE);
                tvNombre.setVisibility(View.GONE);
                tarjetaUsuario.setVisibility(View.GONE);
                lyEstudiante.setVisibility(View.GONE);
                barra.setVisibility(View.GONE);
                btnIngresarAClases.setVisibility(View.GONE);
            }
        }, largo);

        mostrarBarraIzq();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cabeceraIngFoto.setVisibility(View.VISIBLE);
                fotoUsuarioBBDD.startAnimation(animation_rigth);
                tvNombreIngClases.setAnimation(animation_left);
            }
        }, normal);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lyIngClases.setVisibility(View.VISIBLE);
                etCodigo.startAnimation(animation_left);
                btnIngresarAclase.startAnimation(animation_left);
            }
        }, normal);
    }

    public void accionIngresarClase(View view) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                datosClase.setVisibility(View.VISIBLE);
            }
        }, 900);

        datosClase.setAnimation(animation_down);

    }

    // ****************************** métodos de docentes ******************************

    public void ingDocente(View view) {
        if (banderabtnEstudiante) {
            lyEstudiante.setVisibility(View.GONE);
            lyDocente.setVisibility(View.VISIBLE);
            btnListaClases.startAnimation(animation_rigth);
            btnCrearClase.startAnimation(animation_rigth);
            banderabtnDocente = true;
            banderabtnEstudiante = false;
        } else {
            lyDocente.setVisibility(View.VISIBLE);
            btnListaClases.startAnimation(animation_rigth);
            btnCrearClase.startAnimation(animation_rigth);
            banderabtnDocente = true;
        }
    }

    public void crearClase(View view) {
        // Inicio ocultar main --------------------------------------------------
        scrollCrearClases.setVisibility(View.VISIBLE);
        tvNombre.startAnimation(animation_left_ocult_long);
        btnIngresarComoDocente.startAnimation(animation_left_ocult);
        btnIngresarComoEstudiante.startAnimation(animation_left_ocult);
        tarjetaUsuario.startAnimation(animation_rigth_ocult_long);
        btnListaClases.startAnimation(animaciondere_ocult);
        btnCrearClase.startAnimation(animation_rigth_ocult_long);
        barra.startAnimation(animation_rigth_ocult_long);

        new Handler().postDelayed(() -> {
            btnListaClases.setVisibility(View.INVISIBLE);
            btnCrearClase.setVisibility(View.INVISIBLE);
            btnIngresarComoDocente.setVisibility(View.INVISIBLE);
            btnIngresarComoEstudiante.setVisibility(View.INVISIBLE);
        }, normal);

        new Handler().postDelayed(() -> {
            btnListaClases.setVisibility(View.GONE);
            btnCrearClase.setVisibility(View.GONE);
            tvNombre.setVisibility(View.GONE);
            tarjetaUsuario.setVisibility(View.GONE);
            lyEstudiante.setVisibility(View.GONE);
            barra.setVisibility(View.GONE);
        }, largo);

        // Fin ocultar main --------------------------------------------------
        mostrarBarraIzq();

        // mostrar componentes de la pantalla --------------------------------
        new Handler().postDelayed(() -> {
            cabeceraCrearClase.setVisibility(View.VISIBLE);
            fotoUsuarioDocente.startAnimation(animation_rigth);
            tvNombreCrearClase.setAnimation(animation_left);
        }, normal);

        new Handler().postDelayed(() -> {
            lyCrearClases.setVisibility(View.VISIBLE);
            etNombreClase.startAnimation(animation_left);
            etCodigoClase.startAnimation(animation_rigth);
            btnDocenteCrearClase.startAnimation(animation_left);
        }, acond1);
    }

    public void accionCrearClase(View view) {
        
        if (compruebaConexion(this)) {
            String nClase = editTextNombreClase.getText().toString().toUpperCase();
            String nCodigo = editTextcodigoClase.getText().toString().toUpperCase();
            nombreCorto = validaNombre(nombreUsuario);

            if ((nClase.isEmpty()) || (nCodigo.isEmpty())) {
                Toast.makeText(this, "Debe ingresar los parámetros requeridos", Toast.LENGTH_SHORT).show();
            } else {
                // --------------------------- se crea la clase -------------------------
                showProgressBar("Creando clase, espere ...");
                // Get the data from an ImageView as bytes
                fotoClase.setImageResource(R.drawable.ic_register_hero);
                fotoClase.buildDrawingCache();
                Bitmap bmap = fotoClase.getDrawingCache();
                fotoClase.setImageBitmap(bmap);
                fotoClase.buildDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = storageReference.child("DOCENTES/" + idUsuario + "/" + idUsuario + "_" + nombreCorto + "_" + nClase + "." + nCodigo + "/" + "clase.png").putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        finishProgressBar();
                        Toast.makeText(MainActivity.this, "Hubo un error intentando crear clase", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.

                        tvNClase.setText(nClase);
                        tvNCodigo.setText(nCodigo);

                        finishProgressBar();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                datosClaseCreada.setVisibility(View.VISIBLE);
                            }
                        }, acond2);

                        datosClaseCreada.setAnimation(animation_down);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                editTextNombreClase.setText("");
                                editTextcodigoClase.setText("");
                            }
                        }, acond2);
                    }
                });
                // ----------------------------------------------------------------------
            }
        } else {
            Toast.makeText(this, "Debe tener acceso a Internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void verListaClases(View view) {
        // Inicio ocultar main --------------------------------------------------
        scrollListaClases.setVisibility(View.VISIBLE);
        tvNombre.startAnimation(animation_left_ocult_long);
        btnIngresarComoDocente.startAnimation(animation_left_ocult);
        btnIngresarComoEstudiante.startAnimation(animation_left_ocult);
        tarjetaUsuario.startAnimation(animation_rigth_ocult_long);
        btnListaClases.startAnimation(animation_rigth_ocult_long);
        btnCrearClase.startAnimation(animaciondere_ocult);
        barra.startAnimation(animation_rigth_ocult_long);

        new Handler().postDelayed(() -> {
            btnListaClases.setVisibility(View.INVISIBLE);
            btnCrearClase.setVisibility(View.INVISIBLE);
            btnIngresarComoDocente.setVisibility(View.INVISIBLE);
            btnIngresarComoEstudiante.setVisibility(View.INVISIBLE);
        }, normal);

        new Handler().postDelayed(() -> {
            btnListaClases.setVisibility(View.GONE);
            btnCrearClase.setVisibility(View.GONE);
            tvNombre.setVisibility(View.GONE);
            tarjetaUsuario.setVisibility(View.GONE);
            lyEstudiante.setVisibility(View.GONE);
            barra.setVisibility(View.GONE);
        }, largo);

        // Fin ocultar main --------------------------------------------------
        mostrarBarraIzq();

        // mostrar componentes principales de la pantalla --------------------------------
        new Handler().postDelayed(() -> {
            cabeceraListaClases.setVisibility(View.VISIBLE);
            fotoUsuarioListaClases.startAnimation(animation_rigth);
            tvNombreListaClases.setAnimation(animation_left);
        }, normal);

        new Handler().postDelayed(() -> {
            lyprogreso_docente = (LinearLayout) findViewById(R.id.lyprogreso_docente);
            lyprogreso_docente.setVisibility(View.VISIBLE);
            tvCargandoClases_docente = (TextView) findViewById(R.id.tvCargandoClases_docente);
            cprogress_docente = (ProgressBar) findViewById(R.id.cprogress_docente);

            new Handler().postDelayed(() -> {
                lyprogreso_docente.setVisibility(View.GONE);
            }, normal);

            new Handler().postDelayed(() -> {
                rvClasesDocente.setVisibility(View.VISIBLE);
                rvClasesDocente.setAnimation(animation_down);
                ponerDatosClasesDocente();

            }, normal);

        }, normal);
    }

    private void ponerDatosClasesDocente() {
        // mostramos las clases
        clasesDocentes.clear();
        clasesDocentes.add(new ClasesDocente(
                "Tratamiento de Imágenes", "EL447", "3"));
        clasesDocentes.add(new ClasesDocente(
                "Tratamiento de Imágenes", "EL447", "3"));
        clasesDocentes.add(new ClasesDocente(
                "Tratamiento de Imágenes", "EL447", "3"));
        clasesDocentes.add(new ClasesDocente(
                "Tratamiento de Imágenes", "EL447", "3"));
        clasesDocentes.add(new ClasesDocente(
                "Tratamiento de Imágenes", "EL447", "3"));
        clasesDocentes.add(new ClasesDocente(
                "Tratamiento de Imágenes", "EL447", "3"));
        clasesDocentes.add(new ClasesDocente(
                "Tratamiento de Imágenes", "EL447", "3"));
        clasesDocentes.add(new ClasesDocente(
                "Tratamiento de Imágenes", "EL447", "3"));
        clasesDocentes.add(new ClasesDocente(
                "Tratamiento de Imágenes", "EL447", "3"));


        rvClasesDocente.setHasFixedSize(true);
        rvClasesDocente.setLayoutManager(new LinearLayoutManager(this));
        rvClasesDocente.setAdapter(listaClasesDocente);
    }

    // ****************************** Otras configuraciones ******************************
    private void mostrarBarraIzq() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                barraFoto.setVisibility(View.VISIBLE);
                barraFoto.startAnimation(animation_left);
            }
        }, normal);
    }

    public void onMainFotoClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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


}