package com.example.dam.listacontactos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;


public class CopiaDeSeguidad extends AppCompatActivity {

    List<Contacto> contactos;
    TextView tvTexto, tvSincro;
    SharedPreferences sincro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copia_de_seguridad);
        tvTexto = (TextView)findViewById(R.id.tvTextoCopia);
        tvSincro = (TextView)findViewById(R.id.tvSincro);
        contactos = ListaContactos.getContactos();
        sincro = getPreferences(Context.MODE_PRIVATE);
        String sync = sincro.getString("sincro", "no");
        // Si la sincronización está activada ejecuta la copia
        if (sync.equals("yes")){
            try {
                copiar(null);

                tvSincro.setText(Utils.getAhora());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Mantener las etiquetas tras cambiar la orientación
    @Override
    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putString("output", tvTexto.getText().toString());
        b.putString("sincro", tvSincro.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle b) {
        super.onRestoreInstanceState(b);
        String o = b.getString("output");
        String s = b.getString("sincro");
        if (s != null || o != null) {
            tvTexto.setText(o);
            tvSincro.setText(s);
        }
    }

    public void copiar(View view) throws IOException {
        FileOutputStream fosxml = new FileOutputStream(new File(getExternalFilesDir(null), "backup.xml"));
        XmlSerializer docxml = Xml.newSerializer();
        docxml.setOutput(fosxml, "UTF-8");
        docxml.startDocument(null, true);
        docxml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        List<Contacto> l = contactos;
        docxml.startTag(null, "contactos");
        int position = 0;
        for (Contacto s:l){
            s = contactos.get(position);
            s.setTelefonos(s.getTelefonos());
            docxml.startTag(null, "contacto");
            docxml.startTag(null, "nombre");
            docxml.attribute(null, "id", String.valueOf(s.getId()));
            Log.v("Id: ", String.valueOf(s.getId()));
            docxml.text(s.getNombre());
            Log.v("Nombre: ", s.getNombre());
            docxml.endTag(null, "nombre");

            //Separa cada número mediante las comas
            String telefonos = Utils.formatear(s.getTelefonos().toString(), "[]");
            String[] telefArray = telefonos.split(",");

            for (int i = 0; i < telefArray.length; i++) {
                docxml.startTag(null, "telefono");

                docxml.text(telefArray[i]);

                //Escribe todos los numeros en una etiqueta//
                docxml.endTag(null, "telefono");
            }
            docxml.endTag(null, "contacto");
            position+=1;
        }
        docxml.endDocument();
        docxml.flush();
        fosxml.close();
        Log.v("COPIA: ", "Copia realizada");
        //----------------------------------------------Sincro----------------------------------------
        String sync = sincro.getString("sincro", "no");
        if (sync.equals("yes")){
            tvSincro.setText(Utils.getAhora());
        }
    }

    public void mostrar(View view) throws IOException, XmlPullParserException {
        tvTexto.setText("");
        XmlPullParser lectorxml = Xml.newPullParser();
        lectorxml.setInput(new FileInputStream(new File(getExternalFilesDir(null), "backup.xml")), "utf-8");
        int evento = lectorxml.getEventType();
        while (evento != XmlPullParser.END_DOCUMENT){
            if (evento == XmlPullParser.START_TAG){
                String etiqueta = lectorxml.getName();
                if(etiqueta.compareTo("nombre")==0){
                    String atrib = lectorxml.getAttributeValue(null, "id");
                    String texto = lectorxml.nextText();
                    tvTexto.append("Id: " + atrib + " Nombre: " + texto + "\n");
                }else if (etiqueta.compareTo("telefono")==0){
                    String texto = lectorxml.nextText();
                    tvTexto.append("    Telefono: " + texto + "\n");
                }
            }
            evento = lectorxml.next();
        }
    }


    public void sincronizar(View view) throws IOException, InterruptedException {
        SharedPreferences.Editor editor;
        switch (sincro.getString("sincro", "no")){
            case "yes":
                // Si la sincronización estaba activada la desactiva y limpia la etiqueta
                Log.v("SYNC", sincro.getString("sincro", "no"));
                editor = sincro.edit();
                editor.putString("sincro", "no");
                editor.apply();
                tvSincro.setText(R.string.sincroNO);
                break;
            case "no":
                // Si la sincronización estaba desactivada la activa, hace una copia y escribe la fecha y hora
                Log.v("SYNC", sincro.getString("sincro", "no"));
                editor = sincro.edit();
                editor.putString("sincro", "yes");
                editor.apply();
                copiar(null);
                tvSincro.setText(Utils.getAhora());
                break;
            default:
                // Default //
                Log.v("SYNC", sincro.getString("sincro", "no"));
                editor = sincro.edit();
                editor.putString("sincro", "no");
                editor.apply();
                tvSincro.setText(R.string.sincroNO);
                break;
        }
    }
}
