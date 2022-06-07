package org.environmentronic.iasssystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.environmentronic.iasssystem.R;
import org.environmentronic.iasssystem.activities.estudiantes.EstudianteEnClase;

import java.util.List;

public class AdaptadorEstudiantesEnClases extends RecyclerView.Adapter<AdaptadorEstudiantesEnClases.ViewHolder> implements View.OnClickListener{

    private List<EstudianteEnClase> estudiantesEnClases;
    private LayoutInflater inflater;
    private View.OnClickListener listener;

    public AdaptadorEstudiantesEnClases(List<EstudianteEnClase> estudiantesEnClases, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.estudiantesEnClases = estudiantesEnClases;
    }

    @NonNull
    @Override
    public AdaptadorEstudiantesEnClases.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_estudiantes_en_clase, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorEstudiantesEnClases.ViewHolder holder, int position) {
        final EstudianteEnClase estudiante = estudiantesEnClases.get(position);

        holder.tvnombreEstudiante.setText("Estudiante: " + estudiante.getNombreEstudiante());
        holder.idEstudiante.setText("Id: " + estudiante.getIdEstudiante());

    }

    @Override
    public int getItemCount() {
        return estudiantesEnClases.size();
    }

    public void setItems(List<EstudianteEnClase> items) {
        estudiantesEnClases = items;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) listener.onClick(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView foto_estudiante;
        TextView tvnombreEstudiante;
        TextView idEstudiante;
        public LinearLayout tarjetaEstudianteEnClaseABorrar;

        public ViewHolder(View view) {
            super(view);
            foto_estudiante = view.findViewById(R.id.foto_estudiante);
            tvnombreEstudiante = view.findViewById(R.id.tvnombreEstudiante);
            idEstudiante = view.findViewById(R.id.idEstudiante);
            tarjetaEstudianteEnClaseABorrar = view.findViewById(R.id.tarjetaEstudianteEnClaseABorrar);
        }
    }

    public void removeItem(int position){
        estudiantesEnClases.remove(position);
        notifyItemRemoved(position);
    }
}
