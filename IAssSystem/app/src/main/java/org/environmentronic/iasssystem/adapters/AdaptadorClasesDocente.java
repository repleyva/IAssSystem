package org.environmentronic.iasssystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.environmentronic.iasssystem.ClasesDocente;
import org.environmentronic.iasssystem.ClasesEstudiante;
import org.environmentronic.iasssystem.R;

import java.util.List;

public class AdaptadorClasesDocente extends RecyclerView.Adapter<AdaptadorClasesDocente.ViewHolder> {

    private List<ClasesDocente> clasesDocentes;
    private LayoutInflater inflater;
    private Context context;

    public AdaptadorClasesDocente(List<ClasesDocente> clasesDocentes, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.clasesDocentes = clasesDocentes;
        this.context = context;
    }


    @NonNull
    @Override
    public AdaptadorClasesDocente.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_clases_docente, parent, false);
        return new AdaptadorClasesDocente.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorClasesDocente.ViewHolder holder, int position) {
        final ClasesDocente clase = clasesDocentes.get(position);

        holder.tvnombreMateria.setText("Materia: " + clase.getMateria());
        holder.tvNombreCodigo.setText("Código: " + clase.getCodigo());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "clase: " + clase.getMateria(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return clasesDocentes.size();
    }

    public void setItems(List<ClasesDocente> items) {
        clasesDocentes = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView foto_docente;
        TextView tvnombreMateria;
        TextView tvNumeroEstudiantes;
        TextView tvNombreCodigo;

        ViewHolder(View view) {
            super(view);
            foto_docente = view.findViewById(R.id.foto_docenteD);
            tvnombreMateria = view.findViewById(R.id.tvnombreMateriaD);
            tvNombreCodigo = view.findViewById(R.id.tvNombreCodigoD);
        }

    }
}
