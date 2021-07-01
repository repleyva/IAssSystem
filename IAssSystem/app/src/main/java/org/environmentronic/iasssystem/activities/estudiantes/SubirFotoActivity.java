package org.environmentronic.iasssystem.activities.estudiantes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import org.environmentronic.iasssystem.modulos.Genericos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SubirFotoActivity extends AppCompatActivity {

    private ConstraintLayout barraFoto;
    private LinearLayout fotoUsuario;
    private TextView tvNombreFoto;

    private Button btnTomarFoto;
    private Button btnBorrarFoto;
    private Button btnSubirFoto;
    private Button btnEliminarFotobbdd;

    private String nombreUsuario;
    private String nombreCorto;
    private String idUsuario;

    private ImageView fotoTomada;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;

    //Animaciones
    private Animation animation_rigth;
    private Animation animation_left;

    private StorageReference storageReference;
    private ProgressDialog mProgress;
    private String currentPhotoPath = null;
    private static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_subir_foto);

        barraFoto = (ConstraintLayout) findViewById(R.id.barraFoto);
        tvNombreFoto = (TextView) findViewById(R.id.tvNombreFoto);
        btnTomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        btnSubirFoto = (Button) findViewById(R.id.btnSubirFoto);
        btnBorrarFoto = (Button) findViewById(R.id.btnBorrarFoto);
        btnEliminarFotobbdd = (Button) findViewById(R.id.btnEliminarFotobbdd);
        fotoUsuario = (LinearLayout) findViewById(R.id.fotoUsuario);

        fotoTomada = (ImageView) findViewById(R.id.photoBBDD);

        idUsuario = getIntent().getStringExtra("idusuario");
        nombreUsuario = getIntent().getStringExtra("nomusuario");

        //nombreCorto = validaNombre(nombreUsuario);
        nombreCorto = Genericos.validaNombre(nombreUsuario);

        animation_left = AnimationUtils.loadAnimation(this, R.anim.animation_left);
        animation_rigth = AnimationUtils.loadAnimation(this, R.anim.animation_rigth);

        barraFoto.startAnimation(animation_left);
        fotoUsuario.startAnimation(animation_rigth);
        tvNombreFoto.setAnimation(animation_left);
        btnTomarFoto.startAnimation(animation_left);
        btnBorrarFoto.startAnimation(animation_left);
        btnSubirFoto.startAnimation(animation_left);
        btnEliminarFotobbdd.startAnimation(animation_left);

        // subir foto
        storageReference = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);
    }

    public void tomarFoto(View view) {
        dispatchTakePictureIntent();
    }

    // captura la foto
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "org.environmentronic.iasssystem.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // crea una imagen
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // muestra la foto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(currentPhotoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(imageBitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(imageBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(imageBitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = imageBitmap;
            }

            fotoTomada.setImageBitmap(rotatedBitmap);
        }
    }

    // la foto rota, entonces
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
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

        if (compruebaConexion(this)) {

            // ------------------------------- borramos la foto -----------------------------
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("Si eliminas la foto de la base de datos tendrás que acceder a tus clases nuevamente para actualizarla en cada una, ¿Realmente deseas eliminarla?");
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
                            Toast.makeText(getApplicationContext(), "La foto fué borrada con éxito", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred
                            finishProgressBar();
                            Toast.makeText(getApplicationContext(), "Si no cuentas con una foto, no tienes nada que borrar.", Toast.LENGTH_SHORT).show();
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

    public void subirFotoFirebase(View view) {

        boolean coneccion = compruebaConexion(this);

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
                        Toast.makeText(getApplicationContext(), "Hubo un error intentando subir la foto", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        finishProgressBar();
                        fotoTomada.setImageResource(R.drawable.ic_register_hero);
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

    public void onMainClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
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