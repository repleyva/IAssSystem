package org.environmentronic.iasssystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.environmentronic.iasssystem.R;
import org.environmentronic.iasssystem.activities.estudiantes.EstudianteEnClase;

import java.util.List;

public class AdaptadorAlumnosEnClases extends RecyclerView.Adapter<AdaptadorAlumnosEnClases.ViewHolder> {

    private List<EstudianteEnClase> estudiantesEnClases;
    private LayoutInflater inflater;
    private Context context;

    public AdaptadorAlumnosEnClases(List<EstudianteEnClase> estudiantesEnClases, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.estudiantesEnClases = estudiantesEnClases;
        this.context = context;
    }


    @NonNull
    @Override
    public AdaptadorAlumnosEnClases.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_estudiantes_en_clase, parent, false);
        return new AdaptadorAlumnosEnClases.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorAlumnosEnClases.ViewHolder holder, int position) {
        final EstudianteEnClase estudiante = estudiantesEnClases.get(position);

        holder.tvnombreEstudiante.setText("Estudiante: " + estudiante.getNombreEstudiante());

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