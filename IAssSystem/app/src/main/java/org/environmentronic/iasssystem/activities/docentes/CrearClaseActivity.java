package org.environmentronic.iasssystem.activities.docentes;

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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.environmentronic.iasssystem.R;
import org.environmentronic.iasssystem.activities.principales.MainActivity;

import java.io.ByteArrayOutputStream;

public class CrearClaseActivity extends AppCompatActivity {

    private ConstraintLayout barraFoto;
    private LinearLayout fotoUsuarioDocente;
    private TextView tvNombreCrearClase;
    private TextInputLayout etNombreClase;
    private TextInputLayout etCodigoClase;
    private Button btnDocenteCrearClase;

    private String nombreUsuario;
    private String idUsuario;

    private ProgressDialog mProgress;

    //Animaciones
    private Animation animation_rigth;
    private Animation animation_left;
    private Animation animation_down;

    private EditText editTextNombreClase;
    private EditText editTextcodigoClase;

    private ImageView fotoClase;
    private TextView tvNClase;
    private TextView tvNCodigo;

    private StorageReference storageReference;
    private FirebaseDatabase database;

    private CardView datosClaseCreada;
    private Integer acond1 = 1340;
    private Integer acond2 = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_crear_clase);

        mProgress = new ProgressDialog(this);

        barraFoto = (ConstraintLayout) findViewById(R.id.barraFoto);
        fotoUsuarioDocente = (LinearLayout) findViewById(R.id.fotoUsuarioDocente);
        tvNombreCrearClase = (TextView) findViewById(R.id.tvNombreCrearClase);
        etNombreClase = (TextInputLayout) findViewById(R.id.etNombreClase);
        etCodigoClase = (TextInputLayout) findViewById(R.id.etCodigoClase);
        btnDocenteCrearClase = (Button) findViewById(R.id.btnDocenteCrearClase);

        idUsuario = getIntent().getStringExtra("idusuario");
        nombreUsuario = getIntent().getStringExtra("nomusuario");

        animation_left = AnimationUtils.loadAnimation(this, R.anim.animation_left);
        animation_rigth = AnimationUtils.loadAnimation(this, R.anim.animation_rigth);
        animation_down = AnimationUtils.loadAnimation(this, R.anim.animation_down);

        barraFoto.startAnimation(animation_left);
        fotoUsuarioDocente.startAnimation(animation_rigth);
        tvNombreCrearClase.setAnimation(animation_left);
        etNombreClase.startAnimation(animation_left);
        etCodigoClase.startAnimation(animation_rigth);
        btnDocenteCrearClase.startAnimation(animation_left);

        editTextcodigoClase = (EditText) findViewById(R.id.editTextcodigoClase);
        editTextNombreClase = (EditText) findViewById(R.id.editTextNombreClase);
        fotoClase = (ImageView) findViewById(R.id.fotoClase);
        tvNClase = (TextView) findViewById(R.id.tvNClase);
        tvNCodigo = (TextView) findViewById(R.id.tvNCodigo);
        datosClaseCreada = (CardView) findViewById(R.id.datosClaseCreada);

        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();

    }

    public void accionCrearClase(View view) {

        if (compruebaConexion(this)) {
            String nClase = editTextNombreClase.getText().toString().toUpperCase();
            String nCodigo = editTextcodigoClase.getText().toString().toUpperCase();

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

                UploadTask uploadTask = storageReference.child("DOCENTES/" + idUsuario + "/" + idUsuario + "_" + nombreUsuario + "_" + nClase + "." + nCodigo + "/" + "clase.png").putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        finishProgressBar();
                        Toast.makeText(getApplicationContext(), "Hubo un error intentando crear clase", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        fotoClase.setImageResource(R.drawable.ic_register_hero);
                        tvNClase.setText(nClase);
                        tvNCodigo.setText(nCodigo);

                        DatabaseReference myRef = database.getReference().child("DOCENTES");

                        myRef.child(idUsuario)
                                .child("CLASES")
                                .child(nCodigo)
                                .setValue(idUsuario + "_" + nombreUsuario + "_" + nClase + "." + nCodigo);

                        finishProgressBar();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                datosClaseCreada.setVisibility(View.VISIBLE);
                                datosClaseCreada.setAnimation(animation_down);
                            }
                        }, acond2);

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
}