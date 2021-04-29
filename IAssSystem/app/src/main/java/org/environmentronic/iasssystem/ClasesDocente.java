package org.environmentronic.iasssystem;

public class ClasesDocente {

    private String materia;
    private String codigo;
    private String numStudiante;

    public ClasesDocente(String materia, String codigo, String numStudiante) {
        this.materia = materia;
        this.codigo = codigo;
        this.numStudiante = numStudiante;
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

    public String getNumStudiante() {
        return numStudiante;
    }

    public void setNumStudiante(String numStudiante) {
        this.numStudiante = numStudiante;
    }
}
