package org.environmentronic.iasssystem.activities.docentes;

public class ClasesDocente {

    private String materia;
    private String codigo;

    public ClasesDocente(String materia, String codigo) {
        this.materia = materia;
        this.codigo = codigo;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
