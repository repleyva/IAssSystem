package org.environmentronic.iasssystem.activities.docentes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.environmentronic.iasssystem.R;
import org.environmentronic.iasssystem.activities.principales.MainActivity;

import java.io.ByteArrayOutputStream;

public class FotoAsistenciaActivity extends AppCompatActivity {

    private ProgressDialog mProgress;
    private TextView tvNombreFoto;
    private LinearLayout fotoUsuario;
    private Button btnTomarFoto, btnBorrarFoto, btnSubirFoto, btnEliminarFotobbdd;
    private ImageView photoAsistencia;

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

    // datos de la tarjeta
    private String materia;
    private String codigo;
    private String idUsuario;
    private String nombreUsuario;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor();
        setContentView(R.layout.activity_foto_asistencia);

        tvNombreFoto = (TextView) findViewById(R.id.tvNombreFoto);
        fotoUsuario = (LinearLayout) findViewById(R.id.fotoUsuario);
        btnTomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        btnBorrarFoto = (Button) findViewById(R.id.btnBorrarFoto);
        btnSubirFoto = (Button) findViewById(R.id.btnSubirFoto);
        btnEliminarFotobbdd = (Button) findViewById(R.id.btnEliminarFotobbdd);
        photoAsistencia = (ImageView) findViewById(R.id.photoAsistencia);

        mProgress = new ProgressDialog(this);

        materia = getIntent().getStringExtra("materia");
        codigo = getIntent().getStringExtra("codigo");
        idUsuario = getIntent().getStringExtra("idusuario");
        nombreUsuario = getIntent().getStringExtra("nomusuario");

        tvNombreFoto.setText("Asistencia de\n\nClase: " + materia + "\nCodigo: " + codigo + "\nDocente: " + nombreUsuario);

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

        tvNombreFoto.setAnimation(animation_left);
        fotoUsuario.setAnimation(animation_rigth);
        btnTomarFoto.setAnimation(animation_left);
        btnEliminarFotobbdd.setAnimation(animation_left);
        btnSubirFoto.setAnimation(animation_left);
        btnBorrarFoto.setAnimation(animation_left);

        // subir foto
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void tomarFotoD(View view) {
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
            photoAsistencia.setImageBitmap(imageBitmap);
        }
    }

    public void subirFotoFirebaseD(View view) {

        boolean coneccion = compruebaConexion(this);

        if (imageBitmap != null) {
            if (coneccion) {
                showProgressBar("Subiendo foto de asistencia, espere ...");
                // Get the data from an ImageView as bytes
                photoAsistencia.setDrawingCacheEnabled(true);
                photoAsistencia.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) photoAsistencia.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = storageReference.child("DOCENTES/" + idUsuario + "/" + idUsuario + "_" + nombreUsuario + "_" + materia + "." + codigo + "/" + "FOTO_CLASE/" + "asistencia.png").putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        finishProgressBar();
                        Toast.makeText(getApplicationContext(), "Hubo un error intentando subir la foto", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        finishProgressBar();
                        photoAsistencia.setImageResource(R.drawable.ic_register_hero);
                        imageBitmap = null;
                        Toast.makeText(getApplicationContext(), "La foto se subió con exito", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Debes tener acceso a internet", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debe tomar una foto", Toast.LENGTH_SHORT).show();
        }
    }

    public void setBtnBorrarFotoD(View view) {

        if (imageBitmap != null) {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("¿Realmente deseas eliminar la foto?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    photoAsistencia.setImageResource(R.drawable.ic_register_hero);
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

    public void setBtnEliminarFotobbddD(View view) {

        if (compruebaConexion(this)) {

            // ------------------------------- borramos la foto -----------------------------
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("¿Realmente deseas eliminar la foto de asistencia?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {

                    photoAsistencia.setImageResource(R.drawable.ic_register_hero);
                    imageBitmap = null;

                    showProgressBar("Estamos eliminando la foto, espere ...");

                    // Delete the file
                    storageReference.child("DOCENTES/" + idUsuario + "/" + idUsuario + "_" + nombreUsuario + "_" + materia + "." + codigo + "/" + "FOTO_CLASE/" + "asistencia.png")
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                            finishProgressBar();
                            Toast.makeText(getApplicationContext(), "La foto fué borrada con éxito", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred
                            finishProgressBar();
                            Toast.makeText(getApplicationContext(), "Si no has tomado una foto de asistencia, no tienes nada que borrar.", Toast.LENGTH_SHORT).show();
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

    private void changeStatusBarColor() {
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void onBackClick(View view) {
        Intent intent = new Intent(this, InfoClasesAlumnoActivity.class);
        intent.putExtra("materia", materia);
        intent.putExtra("codigo", codigo);
        intent.putExtra("idusuario", idUsuario);
        intent.putExtra("nomusuario", nombreUsuario);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        finish();
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

    @Override
    public void onBackPressed() {

    }
}