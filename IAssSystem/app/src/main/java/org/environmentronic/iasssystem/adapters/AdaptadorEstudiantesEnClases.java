package org.environmentronic.iasssystem.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.environmentronic.iasssystem.R;
import org.environmentronic.iasssystem.activities.estudiantes.EstudianteEnClase;

import java.util.List;

public class AdaptadorEstudiantesEnClases extends RecyclerView.Adapter<AdaptadorEstudiantesEnClases.ViewHolder> {

    private List<EstudianteEnClase> estudiantesEnClases;
    private LayoutInflater inflater;
    private Context context;

    public AdaptadorEstudiantesEnClases(List<EstudianteEnClase> estudiantesEnClases, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.estudiantesEnClases = estudiantesEnClases;
        this.context = context;
    }


    @NonNull
    @Override
    public AdaptadorEstudiantesEnClases.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_estudiantes_en_clase, parent, false);
        return new AdaptadorEstudiantesEnClases.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorEstudiantesEnClases.ViewHolder holder, int position) {
        final EstudianteEnClase estudiante = estudiantesEnClases.get(position);

        holder.tvnombreEstudiante.setText("Estudiante: " + estudiante.getNombreEstudiante());

        /*estudiantesEnClases.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());*/

    }

    @Override
    public int getItemCount() {
        return estudiantesEnClases.size();
    }

    public void setItems(List<EstudianteEnClase> items) {
        estudiantesEnClases = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView foto_estudiante;
        TextView tvnombreEstudiante;

        ViewHolder(View view) {
            super(view);
            foto_estudiante = view.findViewById(R.id.foto_estudiante);
            tvnombreEstudiante = view.findViewById(R.id.tvnombreEstudiante);
        }
    }
}
