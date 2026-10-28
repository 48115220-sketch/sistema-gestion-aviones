package com.terminal.personal_service.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "personal_vuelo")
public class Personal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String apellido;

    @Column(nullable = false, unique = true)
    private String dni;

    private Float sueldo;
    private String cargo;

    @Enumerated(EnumType.STRING)
    private TipoPersonal tipo;

    public Personal() {}

    public Personal(Integer id, String nombre, String apellido, String dni, Float sueldo, String cargo, TipoPersonal tipo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.sueldo = sueldo;
        this.cargo = cargo;
        this.tipo = tipo;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; } 

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public Float getSueldo() { return sueldo; }
    public void setSueldo(Float sueldo) { this.sueldo = sueldo; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public TipoPersonal getTipo() { return tipo; }
    public void setTipo(TipoPersonal tipo) { this.tipo = tipo; }
}