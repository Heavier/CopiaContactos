package com.example.dam.listacontactos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

public class ListaContactos extends AppCompatActivity {

    static List<Contacto> contactos;
    Adaptador adaptador;
    ImageView mas;
    Contacto cont;
    CopiaDeSeguidad backup;
    SharedPreferences sincro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__contactos);
        try {
            iniciar();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mas = (ImageView)findViewById(R.id.ivMasOpciones);
        backup = new CopiaDeSeguidad();
        sincro = getPreferences(Context.MODE_PRIVATE);
        String sync = sincro.getString("sincro", "no");
        if (sync.equals("yes")){
            try {
                backup.copiar(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lista__contactos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nuevoContacto:

                AlertDialog.Builder alert= new AlertDialog.Builder(this);
                alert.setTitle(R.string.nuevo);
                LayoutInflater inflater= LayoutInflater.from(this);
                final View vista = inflater.inflate(R.layout.dialogo_nuevo, null);
                alert.setView(vista);
                alert.setPositiveButton(R.string.nuevo,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                long id = contactos.size() - 1;
                                EditText etN, etTel;
                                etN = (EditText) vista.findViewById(R.id.etNuevoNombre);
                                etTel = (EditText) vista.findViewById(R.id.etNuevoTelef);

                                List<String> telf = new ArrayList<>();
                                Contacto c = new Contacto(id, etN.getText().toString(), telf);
                                c.addTelefono(etTel.getText().toString());
                                contactos.add(c);
                                Collections.sort(contactos);
                                adaptador.notifyDataSetChanged();
                            }
                        });
                alert.setNegativeButton(R.string.cancelar, null);
                alert.show();
                return true;
            case R.id.asc:
                Collections.sort(contactos);
                adaptador.notifyDataSetChanged();
                return true;
            case R.id.desc:
                Collections.sort(contactos, Collections.reverseOrder());
                adaptador.notifyDataSetChanged();
                return true;
            case R.id.copia:
                Intent i = new Intent(this, CopiaDeSeguidad.class);
                i.putExtra("contactos", (Serializable) contactos);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void iniciar() throws IOException {
        contactos = Utils.getListaContactos(this);

        //Soluciona el problema del adaptador que sólo escribe los primeros teléfonos que aparecen en pantalla
        for (Contacto element : contactos) {
            element.setTelefonos(Utils.getListaTelefonos(getApplicationContext(), element.getId()));
        }
        //----------------^

        adaptador = new Adaptador(this, contactos);
        final ListView lv = (ListView)findViewById(R.id.lvContactos);
        lv.setAdapter(adaptador);


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(ListaContactos.this);
                dialogo1.setTitle(R.string.confirmar_eliminar);
                dialogo1.setMessage(contactos.get(position).getNombre());
                dialogo1.setCancelable(false);

                dialogo1.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        String dato = contactos.get(position).getNombre();
                        Toast.makeText(ListaContactos.this, dato, Toast.LENGTH_SHORT).show();

                        contactos.remove(position);
                        adaptador.notifyDataSetChanged();
                    }
                });
                dialogo1.setNegativeButton(R.string.cancelar, null);
                dialogo1.show();
                return true;
            }
        });
    }

    public void dialogo(View v){
        final ListView lv = (ListView)findViewById(R.id.lvContactos);
        final int position =  lv.getPositionForView(v);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.detalles);
        LayoutInflater inflater = LayoutInflater.from(this);
        int res = R.layout.dialogo_principal;
        final View vista = inflater.inflate(res, null);
        alert.setView(vista);

        TextView tv1 = (TextView) vista.findViewById(R.id.tvDetallesNombre);
        cont = contactos.get(position);
        tv1.setText(cont.getNombre());

        TextView tv2 = (TextView) vista.findViewById(R.id.tvDialogo);
        String str = cont.getTelefonos().toString();
        tv2.setText(str);

        alert.show();
    }

    public void editar(View v) {
        final ListView lv = (ListView)findViewById(R.id.lvContactos);
        final int position =  lv.getPositionForView(v);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.editar);
        LayoutInflater inflater = LayoutInflater.from(this);
        int res = R.layout.dialogo_editar;
        final View vista = inflater.inflate(res, null);
        alert.setView(vista);

        EditText et1 = (EditText) vista.findViewById(R.id.etNombre);

        cont = contactos.get(position);
        et1.setText(cont.getNombre());

        EditText ed2 = (EditText) vista.findViewById(R.id.etTelf);
        ed2.setText(Utils.formatear(cont.getTelefonos().toString(), "[]"));

        alert.setCancelable(false);
        alert.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                EditText et1 = (EditText) vista.findViewById(R.id.etNombre);
                String nombre = et1.getText().toString();

                EditText ed2 = (EditText) vista.findViewById(R.id.etTelf);
                String telf = ed2.getText().toString();
                cont.setNombre(nombre);

                List<String> listaTelf = new ArrayList<>();
                listaTelf.add(telf);
                cont.setTelefonos(listaTelf);

                adaptador.notifyDataSetChanged();
            }
        });
        alert.setNegativeButton(R.string.cancelar, null);
        alert.show();
    }

    public static List<Contacto> getContactos() {
        return contactos;
    }
}
