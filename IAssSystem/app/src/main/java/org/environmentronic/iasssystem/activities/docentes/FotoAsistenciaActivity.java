package org.environmentronic.iasssystem.activities.docentes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import org.environmentronic.iasssystem.modulos.Genericos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

public class FotoAsistenciaActivity extends AppCompatActivity implements EliminarFotoAsistenciaDialogo.iFinalizoCuadroDialogo {

    private ProgressDialog mProgress;
    private TextView tvNombreFoto;
    private LinearLayout fotoUsuario;
    private Button btnTomarFoto, btnBorrarFoto, btnSubirFoto, btnEliminarFotobbdd;
    private Button btnSelectFoto;
    private ImageView photoAsistencia;

    //Animaciones
    private Animation animation_rigth;
    private Animation animation_left;

    // constantes timer
    private Integer largo = 2000;
    private Integer normal = 1000;

    // datos de la tarjeta
    private String materia;
    private String codigo;
    private String idUsuario;
    private String nombreUsuario;

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private Bitmap imageBitmap;
    private StorageReference storageReference;
    private static final int REQUEST_TAKE_PHOTO = 100;
    private String currentPhotoPath = null;
    private Bitmap rotatedBitmap;
    private Context context;

    private static int SELECT_IMAGE_CODE = 1;
    private Uri imagen;
    private boolean camara = false;
    private boolean galeria = false;
    private boolean puedeSubirFoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor();
        setContentView(R.layout.activity_foto_asistencia);

        if (ContextCompat.checkSelfPermission(FotoAsistenciaActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FotoAsistenciaActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        context = this;
        tvNombreFoto = (TextView) findViewById(R.id.tvNombreFoto);
        fotoUsuario = (LinearLayout) findViewById(R.id.fotoUsuario);
        btnTomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        btnBorrarFoto = (Button) findViewById(R.id.btnBorrarFoto);
        btnSubirFoto = (Button) findViewById(R.id.btnSubirFoto);
        btnSelectFoto = (Button) findViewById(R.id.btnSelectFoto);
        btnEliminarFotobbdd = (Button) findViewById(R.id.btnEliminarFotobbdd);
        photoAsistencia = (ImageView) findViewById(R.id.photoAsistencia);

        mProgress = new ProgressDialog(this);

        materia = getIntent().getStringExtra("materia");
        codigo = getIntent().getStringExtra("codigo");
        idUsuario = getIntent().getStringExtra("idusuario");
        nombreUsuario = getIntent().getStringExtra("nomusuario");

        tvNombreFoto.setText("Asistencia de\n\nClase: " + materia + "\nCodigo: " + codigo + "\nDocente: " + nombreUsuario);

        animation_left = AnimationUtils.loadAnimation(this, R.anim.animation_left);
        animation_rigth = AnimationUtils.loadAnimation(this, R.anim.animation_rigth);

        tvNombreFoto.setAnimation(animation_left);
        fotoUsuario.setAnimation(animation_rigth);
        btnTomarFoto.setAnimation(animation_left);
        btnEliminarFotobbdd.setAnimation(animation_left);
        btnSubirFoto.setAnimation(animation_left);
        btnSelectFoto.startAnimation(animation_left);
        btnBorrarFoto.setAnimation(animation_left);

        // subir foto
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void tomarFotoD(View view) {
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
        String imageFileName = nombreUsuario;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            camara = true;
            imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            rotatedBitmap = rotarImagen(imageBitmap);
            photoAsistencia.setImageBitmap(rotatedBitmap);
            puedeSubirFoto = true;
        } else if (requestCode == SELECT_IMAGE_CODE) {
            galeria = true;
            Uri uri = data.getData();
            imagen = uri;
            photoAsistencia.setImageURI(imagen);
            puedeSubirFoto = true;
        }
    }

    public Bitmap rotarImagen(Bitmap imageBitmap) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch (orientation) {

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
        return rotatedBitmap;
    }

    // la foto rota, entonces
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void subirFotoFirebaseD(View view) throws IOException {

        boolean coneccion = compruebaConexion(FotoAsistenciaActivity.this);

        if (coneccion) {
            if (puedeSubirFoto) {
                showProgressBar("Subiendo foto de asistencia, espere ...");

                String nombreFotoFormat = "";

                if (camara) {
                    // Get the data from an ImageView as bytes
                    Uri file = Uri.fromFile(new File(currentPhotoPath));
                    InputStream imageStream = null;
                    try {
                        imageStream = getApplication().getContentResolver().openInputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap original = BitmapFactory.decodeStream(imageStream);
                    //Bitmap bmp = getResizedBitmap(original, 500);
                    Bitmap rotateBitmap = rotateImageIfRequired(getApplicationContext(), original, file);
                    Bitmap bmp = getResizedBitmap(rotateBitmap, 1000);

                    String path = MediaStore.Images.Media.insertImage(getApplication().getContentResolver(), bmp, String.valueOf(System.currentTimeMillis()), null);
                    imagen = Uri.parse(path);
                    String nombreFoto = "asistencia" + "_" + LocalDate.now() + ".png";
                    nombreFotoFormat = nombreFoto.replace("-", "_");
                    camara = false;
                    galeria = false;
                }

                if (galeria) {
                    // Get the data from an ImageView as bytes
                    Uri file = imagen;
                    InputStream imageStream = null;
                    try {
                        imageStream = getApplication().getContentResolver().openInputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap original = BitmapFactory.decodeStream(imageStream);
                    //Bitmap bmp = getResizedBitmap(original, 500);
                    Bitmap rotateBitmap = rotateImageIfRequired(getApplicationContext(), original, file);
                    Bitmap bmp = getResizedBitmap(rotateBitmap, 1000);

                    String path = MediaStore.Images.Media.insertImage(getApplication().getContentResolver(), bmp, String.valueOf(System.currentTimeMillis()), null);
                    imagen = Uri.parse(path);
                    String nombreFoto = "asistencia" + "_" + LocalDate.now() + ".png";
                    nombreFotoFormat = nombreFoto.replace("-", "_");
                    camara = false;
                    galeria = false;
                }

                UploadTask uploadTask = storageReference.child("DOCENTES/" + idUsuario + "/" + idUsuario + "_" + nombreUsuario + "_" + materia + "." + codigo + "/" + "FOTO_CLASE/" + nombreFotoFormat).putFile(imagen);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        puedeSubirFoto = false;
                        finishProgressBar();
                        Toast.makeText(getApplicationContext(), "Hubo un error intentando subir la foto", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        puedeSubirFoto = false;
                        finishProgressBar();
                        photoAsistencia.setImageResource(R.drawable.ic_register_hero);
                        imageBitmap = null;
                        Toast.makeText(getApplicationContext(), "La foto se subió con exito", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Debe tomar una foto", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debes tener acceso a internet", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);

    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    public void selectFoto(View view) { // seleccionar una foto de la galeria
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "title"), SELECT_IMAGE_CODE);
    }

    public void setBtnBorrarFotoD(View view) {

        if (puedeSubirFoto) {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("¿Realmente deseas eliminar la foto?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    boolean borrado = Genericos.borrarCache();
                    if (borrado) {
                        photoAsistencia.setImageResource(R.drawable.ic_register_hero);
                        imageBitmap = null;
                    }
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
            new EliminarFotoAsistenciaDialogo(context, FotoAsistenciaActivity.this);
        } else {
            Toast.makeText(this, "No tiene acceso a internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void ResultadoCuadroDialogo(String fecha) {

        if ((fecha.length() != 10) || (fecha.contains(" ")) || (!fecha.contains("-"))) {
            Toast.makeText(FotoAsistenciaActivity.this, "Ingrese la fecha en formato válido (AÑO-MES-DIA)", Toast.LENGTH_LONG).show();
        } else {
            String fechaFormateada = fecha.replace("-", "_");

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
                    storageReference.child("DOCENTES/" + idUsuario + "/" + idUsuario + "_" + nombreUsuario + "_" + materia + "." + codigo + "/" + "FOTO_CLASE/" + "asistencia_" + fechaFormateada + ".png")
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            finishProgressBar();
                            Toast.makeText(getApplicationContext(), "La foto fué borrada con éxito", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
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