package org.environmentronic.iasssystem.activities.principales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.environmentronic.iasssystem.R;
import org.environmentronic.iasssystem.activities.docentes.CrearClaseActivity;
import org.environmentronic.iasssystem.activities.docentes.ListaClasesActivity;
import org.environmentronic.iasssystem.activities.estudiantes.IngresarAClaseActivity;
import org.environmentronic.iasssystem.activities.estudiantes.SubirFotoActivity;
import org.environmentronic.iasssystem.activities.estudiantes.VerClasesEstudiantesActivity;

/* TODO: 2/07/2021
 *   Hasta este momento toda la funcionalidad de la aplicacion se encuentra activa
 * */

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ImageView photoPerfil;
    private RelativeLayout barra;

    private TextView tvNombre;
    private TextView tvCorreo;

    private Button btnIngresarComoDocente;
    private Button btnIngresarComoEstudiante;

    private Button btnSubirFotos;
    private Button btnVerMisClases;
    private Button btnIngresarAClases;

    private Button btnCrearClase;
    private Button btnListaClases;

    private LinearLayout tarjetaUsuario;
    private LinearLayout lyEstudiante;
    private LinearLayout lyDocente;

    private RelativeLayout cabeceraCrearClase;
    private RelativeLayout cabeceraListaClases;

    private LinearLayout lyCrearClases;
    private LinearLayout lyprogreso;


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

    private CardView datosClase;


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

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;
    private StorageReference storageReference;
    private ProgressDialog mProgress;

    // -------------------------------- variables para los docente --------------------------------

    private TextView prueba;
    private FirebaseDatabase database;

    // ----------------------------- Variables para ingresar a clases -----------------------------


    // --------------------------- variables ver lista de clases Docente --------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_main);

        mProgress = new ProgressDialog(this);

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

        cabeceraCrearClase = (RelativeLayout) findViewById(R.id.cabeceraCrearClase);
        cabeceraListaClases = (RelativeLayout) findViewById(R.id.cabeceraListaClases);

        lyCrearClases = (LinearLayout) findViewById(R.id.lyCrearClases);

        scrollFoto = (ScrollView) findViewById(R.id.scrollFoto);
        scrollIngClases = (ScrollView) findViewById(R.id.scrollIngClases);
        scrollCrearClases = (ScrollView) findViewById(R.id.scrollCrearClases);
        scrollVerClases = (ScrollView) findViewById(R.id.scrollVerClases);
        scrollListaClases = (ScrollView) findViewById(R.id.scrollListaClases);

        datosClase = (CardView) findViewById(R.id.datosClase);
        storageReference = FirebaseStorage.getInstance().getReference();
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

        // ------------------------------- Variables para crear clase -----------------------------------------

        database = FirebaseDatabase.getInstance();
        prueba = (TextView) findViewById(R.id.prueba);

        padre.setOnClickListener(v -> {
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

        Intent intent = new Intent(this, SubirFotoActivity.class);
        intent.putExtra("idusuario", idUsuario);
        intent.putExtra("nomusuario", nombreUsuario);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        finish();

        /*scrollFoto.setVisibility(View.VISIBLE);
        tvNombre.startAnimation(animation_left_ocult_long);
        btnIngresarComoDocente.startAnimation(animation_left_ocult_long);
        btnIngresarComoEstudiante.startAnimation(animation_left_ocult_long);
        tarjetaUsuario.startAnimation(animation_rigth_ocult_long);
        btnVerMisClases.startAnimation(animaciondere_ocult);
        btnIngresarAClases.startAnimation(animaciondere_ocult);
        btnSubirFotos.startAnimation(animation_rigth_ocult_long);
        barra.startAnimation(animation_rigth_ocult_long);*/

        /*new Handler().postDelayed(() -> {
            btnIngresarAClases.setVisibility(View.GONE);
            btnVerMisClases.setVisibility(View.GONE);
        }, normal);*/

        /*new Handler().postDelayed(() -> {
            tvNombre.setVisibility(View.GONE);
            tarjetaUsuario.setVisibility(View.GONE);
            lyEstudiante.setVisibility(View.GONE);
            barra.setVisibility(View.GONE);
            btnIngresarComoDocente.setVisibility(View.GONE);
            btnIngresarComoEstudiante.setVisibility(View.GONE);
        }, largo);*/

        /*mostrarBarraIzq();

        new Handler().postDelayed(() -> {
            cabeceraFoto.setVisibility(View.VISIBLE);
            fotoUsuario.startAnimation(animation_rigth);
            tvNombreFoto.setAnimation(animation_left);
        }, normal);

        new Handler().postDelayed(() -> {
            lyFoto.setVisibility(View.VISIBLE);
            btnTomarFoto.startAnimation(animation_left);
            btnBorrarFoto.startAnimation(animation_left);
            btnSubirFoto.startAnimation(animation_left);
            btnEliminarFotobbdd.startAnimation(animation_left);
        }, normal);*/
    }

    public void verMisClases(View view) {

        Intent intent = new Intent(this, VerClasesEstudiantesActivity.class);
        intent.putExtra("idusuario", idUsuario);
        intent.putExtra("nomusuario", nombreUsuario);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        finish();

        /*scrollVerClases.setVisibility(View.VISIBLE);
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
        }, largo);*/

        // Fin ocultar main --------------------------------------------------
        /*mostrarBarraIzq();

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
                ponerDatos();
            }, normal);

        }, normal);*/
    }

    public void ingresarClase(View view) {

        Intent intent = new Intent(this, IngresarAClaseActivity.class);
        intent.putExtra("idusuario", idUsuario);
        intent.putExtra("nomusuario", nombreUsuario);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        finish();

        /*scrollIngClases.setVisibility(View.VISIBLE);
        tvNombre.startAnimation(animation_left_ocult_long);
        btnIngresarComoDocente.startAnimation(animation_left_ocult);
        btnIngresarComoEstudiante.startAnimation(animation_left_ocult);
        tarjetaUsuario.startAnimation(animation_rigth_ocult_long);
        btnVerMisClases.startAnimation(animaciondere_ocult);
        btnIngresarAClases.startAnimation(animation_rigth_ocult_long);
        btnSubirFotos.startAnimation(animaciondere_ocult);
        barra.startAnimation(animation_rigth_ocult_long);*/

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnSubirFotos.setVisibility(View.INVISIBLE);
                btnVerMisClases.setVisibility(View.INVISIBLE);
                btnIngresarComoDocente.setVisibility(View.INVISIBLE);
                btnIngresarComoEstudiante.setVisibility(View.INVISIBLE);
            }
        }, normal);*/

        /*new Handler().postDelayed(new Runnable() {
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
        }, largo);*/

        //mostrarBarraIzq();

        /*new Handler().postDelayed(new Runnable() {
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
        }, normal);*/
    }

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

        Intent intent = new Intent(this, CrearClaseActivity.class);
        intent.putExtra("idusuario", idUsuario);
        intent.putExtra("nomusuario", nombreUsuario);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        finish();

        // Inicio ocultar main --------------------------------------------------
        /*scrollCrearClases.setVisibility(View.VISIBLE);
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
        }, largo);*/

        // Fin ocultar main --------------------------------------------------
        //mostrarBarraIzq();

        // mostrar componentes de la pantalla --------------------------------
        /*new Handler().postDelayed(() -> {
            cabeceraCrearClase.setVisibility(View.VISIBLE);
            fotoUsuarioDocente.startAnimation(animation_rigth);
            tvNombreCrearClase.setAnimation(animation_left);
        }, normal);

        new Handler().postDelayed(() -> {
            lyCrearClases.setVisibility(View.VISIBLE);
            etNombreClase.startAnimation(animation_left);
            etCodigoClase.startAnimation(animation_rigth);
            btnDocenteCrearClase.startAnimation(animation_left);
        }, acond1);*/
    }

    public void verListaClases(View view) {
        Intent intent = new Intent(this, ListaClasesActivity.class);
        intent.putExtra("idusuario", idUsuario);
        intent.putExtra("nomusuario", nombreUsuario);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        finish();
        // Inicio ocultar main --------------------------------------------------
        /*scrollListaClases.setVisibility(View.VISIBLE);
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
        }, largo);*/

        // Fin ocultar main --------------------------------------------------
        //mostrarBarraIzq();

        // mostrar componentes principales de la pantalla --------------------------------
        /*new Handler().postDelayed(() -> {
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
                ponerDatosClasesDocente();
            }, normal);

        }, normal);*/
    }

    /*private void mostrarBarraIzq() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                barraFoto.setVisibility(View.VISIBLE);
                barraFoto.startAnimation(animation_left);
            }
        }, normal);
    }*/

    public void onMainFotoClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
}