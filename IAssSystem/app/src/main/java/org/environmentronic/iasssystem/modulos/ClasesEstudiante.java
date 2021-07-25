package org.environmentronic.iasssystem.modulos;

public class ClasesEstudiante {

    private String docente;
    private String codigo;
    private String materia;
    private String idDocente;

    public ClasesEstudiante(String docente, String codigo, String materia, String idDocente) {
        this.docente = docente;
        this.codigo = codigo;
        this.materia = materia;
        this.idDocente = idDocente;
    }

    public String getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(String idDocente) {
        this.idDocente = idDocente;
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
