package org.environmentronic.iasssystem.activities.docentes;

public class ClasesDocente {

    private String materia;
    private String codigo;
    private String idusuario;
    private String nombreUsuario;

    public ClasesDocente(String materia, String codigo, String idusuario, String nombreUsuario) {
        this.materia = materia;
        this.codigo = codigo;
        this.idusuario = idusuario;
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(String idusuario) {
        this.idusuario = idusuario;
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
