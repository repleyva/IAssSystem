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

import org.environmentronic.iasssystem.modulos.ClasesEstudiante;
import org.environmentronic.iasssystem.R;

import java.util.List;

public class AdaptadorClasesEstudiante extends RecyclerView.Adapter<AdaptadorClasesEstudiante.ViewHolder> implements View.OnClickListener {

    private List<ClasesEstudiante> clasesEstudiantes;
    private LayoutInflater inflater;
    //private Context context;

    // listener para onClick
    private View.OnClickListener listener;

    public AdaptadorClasesEstudiante(List<ClasesEstudiante> clasesEstudiantes, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.clasesEstudiantes = clasesEstudiantes;
        //this.context = context;
    }

    @NonNull
    @Override
    public AdaptadorClasesEstudiante.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_clases_estudiante, parent, false);
        //return new AdaptadorClasesEstudiante.ViewHolder(view);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorClasesEstudiante.ViewHolder holder, int position) {
        final ClasesEstudiante clase = clasesEstudiantes.get(position);

        holder.tvNombreDocente.setText("Docente: " + clase.getDocente());
        holder.tvnombreMateria.setText("Materia: " + clase.getMateria());
        holder.tvNombreCodigo.setText("Código: " + clase.getCodigo());

        /*holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, InfoClasesEstudianteActivity.class);
            intent.putExtra("docente", clase.getDocente());
            intent.putExtra("materia", clase.getMateria());
            intent.putExtra("codigo", clase.getCodigo());
            intent.putExtra("iddocente", clase.getIdDocente());

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            //overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        });*/
    }

    @Override
    public int getItemCount() {
        return clasesEstudiantes.size();
    }

    public void setItems(List<ClasesEstudiante> items) {
        clasesEstudiantes = items;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) listener.onClick(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView foto_docente;
        TextView tvnombreMateria;
        TextView tvNombreDocente;
        TextView tvNombreCodigo;
        public LinearLayout tarjetaEstudianteABorrar;

        public ViewHolder(View view) {
            super(view);
            foto_docente = view.findViewById(R.id.foto_docente);
            tvnombreMateria = view.findViewById(R.id.tvnombreMateria);
            tvNombreDocente = view.findViewById(R.id.tvNombreDocente);
            tvNombreCodigo = view.findViewById(R.id.tvNombreCodigo);
            tarjetaEstudianteABorrar = view.findViewById(R.id.tarjetaEstudianteABorrar);
        }
    }

    public void removeItem(int position){
        clasesEstudiantes.remove(position);
        notifyItemRemoved(position);
    }
}
