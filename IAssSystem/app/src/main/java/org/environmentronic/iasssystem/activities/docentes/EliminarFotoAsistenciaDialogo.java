package org.environmentronic.iasssystem.activities.docentes;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.environmentronic.iasssystem.R;

public class EliminarFotoAsistenciaDialogo {

    public interface iFinalizoCuadroDialogo {
        void ResultadoCuadroDialogo(String fecha);
    }

    private iFinalizoCuadroDialogo interfaz;

    public EliminarFotoAsistenciaDialogo(Context context, iFinalizoCuadroDialogo actividad) {
        interfaz = actividad;
        final Dialog dialogo = new Dialog(context);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setCancelable(true);
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogo.setContentView(R.layout.dialogo_eliminacion_foto_asistencia);

        final EditText fecha = (EditText) dialogo.findViewById(R.id.fechaFotoClaseDeleteET);
        Button confirmar = (Button) dialogo.findViewById(R.id.btnConfirmar);
        Button cancelar = (Button) dialogo.findViewById(R.id.btnCancelar);

        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaz.ResultadoCuadroDialogo(fecha.getText().toString());
                dialogo.dismiss();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogo.dismiss();
            }
        });

        dialogo.show();
    }


}
