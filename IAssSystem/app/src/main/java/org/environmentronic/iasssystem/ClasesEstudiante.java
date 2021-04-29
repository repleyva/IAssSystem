package org.environmentronic.iasssystem;

public class ClasesEstudiante {

    private String docente;
    private String codigo;
    private String materia;

    public ClasesEstudiante(String docente, String codigo, String materia) {
        this.docente = docente;
        this.codigo = codigo;
        this.materia = materia;
    }

    public String getDocente() {
        return docente;
    }

    public void setDocente(String docente) {
        this.docente = docente;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }
}
