package org.environmentronic.iasssystem.modulos;

import android.os.Environment;

import org.environmentronic.iasssystem.R;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class Genericos {

    public Genericos() {

    }

    public static String validaNombre(String nombre) {

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

    public static boolean borrarCache() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/Android/data/org.environmentronic.iasssystem/files/Pictures");
        //comprueba si es directorio.
        if (dir.isDirectory()) {
            //obtiene un listado de los archivos contenidos en el directorio.
            String[] hijos = dir.list();
            //Elimina los archivos contenidos.
            for (int i = 0; i < hijos.length; i++) {
                new File(dir, hijos[i]).delete();
            }
            return true;
        }
        return false;
    }

    public static String cleanString(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return texto;
    }
}
