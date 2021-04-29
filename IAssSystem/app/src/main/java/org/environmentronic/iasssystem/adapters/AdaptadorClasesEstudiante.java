package org.environmentronic.iasssystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.environmentronic.iasssystem.ClasesEstudiante;
import org.environmentronic.iasssystem.R;
import org.w3c.dom.Text;

import java.util.List;

public class AdaptadorClasesEstudiante extends RecyclerView.Adapter<AdaptadorClasesEstudiante.ViewHolder> {

    private List<ClasesEstudiante> clasesEstudiantes;
    private LayoutInflater inflater;
    private Context context;

    public AdaptadorClasesEstudiante(List<ClasesEstudiante> clasesEstudiantes, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.clasesEstudiantes = clasesEstudiantes;
        this.context = context;
    }


    @NonNull
    @Override
    public AdaptadorClasesEstudiante.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_clases_estudiante, parent, false);
        return new AdaptadorClasesEstudiante.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorClasesEstudiante.ViewHolder holder, int position) {
        holder.bindData(clasesEstudiantes.get(position));
    }

    @Override
    public int getItemCount() {
        return clasesEstudiantes.size();
    }

    public void setItems(List<ClasesEstudiante> items) {
        clasesEstudiantes = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView foto_docente;
        TextView tvnombreMateria;
        TextView tvNombreDocente;
        TextView tvNombreCodigo;

        ViewHolder(View view) {
            super(view);
            foto_docente = view.findViewById(R.id.foto_docente);
            tvnombreMateria = view.findViewById(R.id.tvnombreMateria);
            tvNombreDocente = view.findViewById(R.id.tvNombreDocente);
            tvNombreCodigo = view.findViewById(R.id.tvNombreCodigo);
        }

        void bindData(ClasesEstudiante clase){
            tvnombreMateria.setText("Clase: " + clase.getMateria());
            tvNombreCodigo.setText("Código: " + clase.getCodigo());
            tvNombreDocente.setText("Docente: " + clase.getDocente());
        }

    }
}
