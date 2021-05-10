package org.environmentronic.iasssystem.adapters;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchHelperListener listener;
    private Integer estudianteAdapter;
    private Integer docentesAdapter;
    private Integer estudiantesEnClaseAdapter;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs,
                                   RecyclerItemTouchHelperListener listener,
                                   Integer estudianteAdapter,
                                   Integer docentesAdapter,
                                   Integer estudiantesEnClaseAdapter) {

        super(dragDirs, swipeDirs);
        this.listener = listener;
        this.estudianteAdapter = estudianteAdapter;
        this.docentesAdapter = docentesAdapter;
        this.estudiantesEnClaseAdapter = estudiantesEnClaseAdapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            if (estudianteAdapter != 0) {
                View foregroundView = ((AdaptadorClasesEstudiante.ViewHolder) viewHolder).tarjetaEstudianteABorrar;
                getDefaultUIUtil().onSelected(foregroundView);
            }

            if (estudiantesEnClaseAdapter != 0){
                View foregroundView = ((AdaptadorEstudiantesEnClases.ViewHolder) viewHolder).tarjetaEstudianteEnClaseABorrar;
                getDefaultUIUtil().onSelected(foregroundView);
            }

            if (docentesAdapter != 0){
                View foregroundView = ((AdaptadorClasesDocente.ViewHolder) viewHolder).tarjetaDocenteABorrar;
                getDefaultUIUtil().onSelected(foregroundView);
            }
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c,
                                @NonNull RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (estudianteAdapter != 0) {
            View foregroundView = ((AdaptadorClasesEstudiante.ViewHolder) viewHolder).tarjetaEstudianteABorrar;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }

        if (estudiantesEnClaseAdapter != 0) {
            View foregroundView = ((AdaptadorEstudiantesEnClases.ViewHolder) viewHolder).tarjetaEstudianteEnClaseABorrar;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }

        if (docentesAdapter != 0) {
            View foregroundView = ((AdaptadorClasesDocente.ViewHolder) viewHolder).tarjetaDocenteABorrar;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        if (estudianteAdapter != 0) {
            View foregroundView = ((AdaptadorClasesEstudiante.ViewHolder) viewHolder).tarjetaEstudianteABorrar;
            getDefaultUIUtil().clearView(foregroundView);
        }

        if (estudiantesEnClaseAdapter != 0){
            View foregroundView = ((AdaptadorEstudiantesEnClases.ViewHolder) viewHolder).tarjetaEstudianteEnClaseABorrar;
            getDefaultUIUtil().clearView(foregroundView);
        }

        if (docentesAdapter != 0){
            View foregroundView = ((AdaptadorClasesDocente.ViewHolder) viewHolder).tarjetaDocenteABorrar;
            getDefaultUIUtil().clearView(foregroundView);
        }

    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (estudianteAdapter != 0) {
            View foregroundView = ((AdaptadorClasesEstudiante.ViewHolder) viewHolder).tarjetaEstudianteABorrar;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }

        if (estudiantesEnClaseAdapter != 0) {
            View foregroundView = ((AdaptadorEstudiantesEnClases.ViewHolder) viewHolder).tarjetaEstudianteEnClaseABorrar;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }

        if (docentesAdapter != 0) {
            View foregroundView = ((AdaptadorClasesDocente.ViewHolder) viewHolder).tarjetaDocenteABorrar;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwipe(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
