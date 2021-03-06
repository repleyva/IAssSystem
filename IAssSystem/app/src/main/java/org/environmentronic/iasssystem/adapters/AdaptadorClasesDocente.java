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

import org.environmentronic.iasssystem.modulos.ClasesDocente;
import org.environmentronic.iasssystem.R;

import java.util.List;

public class AdaptadorClasesDocente extends RecyclerView.Adapter<AdaptadorClasesDocente.ViewHolder> implements View.OnClickListener {

    private List<ClasesDocente> clasesDocentes;
    private LayoutInflater inflater;
    //private Context context;

    // listener para onClick
    private View.OnClickListener listener;

    public AdaptadorClasesDocente(List<ClasesDocente> clasesDocentes, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.clasesDocentes = clasesDocentes;
        //this.context = context;
    }


    @NonNull
    @Override
    public AdaptadorClasesDocente.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_clases_docente, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorClasesDocente.ViewHolder holder, int position) {
        final ClasesDocente clase = clasesDocentes.get(position);

        holder.tvnombreMateria.setText("Materia: " + clase.getMateria());
        holder.tvNombreCodigo.setText("Código: " + clase.getCodigo());

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InfoClasesAlumnoActivity.class);
                intent.putExtra("materia", clase.getMateria());
                intent.putExtra("codigo", clase.getCodigo());
                intent.putExtra("idusuario", clase.getIdusuario());
                intent.putExtra("nomusuario", clase.getNombreUsuario());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                //overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return clasesDocentes.size();
    }

    public void setItems(List<ClasesDocente> items) {
        clasesDocentes = items;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) listener.onClick(v);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView foto_docente;
        TextView tvnombreMateria;
        TextView tvNumeroEstudiantes;
        TextView tvNombreCodigo;
        public LinearLayout tarjetaDocenteABorrar;

        public ViewHolder(View view) {
            super(view);
            foto_docente = view.findViewById(R.id.foto_docenteD);
            tvnombreMateria = view.findViewById(R.id.tvnombreMateriaD);
            tvNombreCodigo = view.findViewById(R.id.tvNombreCodigoD);
            tarjetaDocenteABorrar = view.findViewById(R.id.tarjetaDocenteABorrar);
        }
    }

    public void removeItem(int position){
        clasesDocentes.remove(position);
        notifyItemRemoved(position);
    }
}
