package com.example.dam.listacontactos;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import android.provider.ContactsContract;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

public class Contacto implements Serializable, Comparable<Contacto> {
    private long id;
    private String nombre;
    private List<String> telefonos;

    public Contacto(long id, String nombre, List<String> telefonos) {
        this.id = id;
        this.nombre = nombre;
        this.telefonos = telefonos;
    }

    public Contacto() {
        this(0, "", new ArrayList<String>());
    }

    public String getTelefono(int location) {
        return telefonos.get(location);
    }

    public void setTelefono(int location, String telefono) {
        this.telefonos.set(location, telefono);
    }

    public int size() {
        return telefonos.size();
    }

    public boolean isEmpty() {
        return telefonos.isEmpty();
    }

    public boolean addTelefono(String object) {
        return telefonos.add(object);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<String> telefonos) {
        this.telefonos = telefonos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contacto contacto = (Contacto) o;

        return id == contacto.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }


    @Override
    public int compareTo(Contacto contacto) {
        int r = this.nombre.compareTo(contacto.nombre);
        if (r == 0) {
            r = (int) (this.id - contacto.id);
        }
        return r;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", telefonos=" + telefonos +
                '}';
    }
}