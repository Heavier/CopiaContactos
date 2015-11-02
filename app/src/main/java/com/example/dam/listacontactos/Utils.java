package com.example.dam.listacontactos;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Utils {
    public static List<Contacto> getListaContactos(Context contexto) {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1", "1"};
        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";
        Cursor cursor = contexto.getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int indiceNombre = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        List<Contacto> lista = new ArrayList<>();
        Contacto contacto;
        while (cursor.moveToNext()) {
            contacto = new Contacto();
            contacto.setId(cursor.getLong(indiceId));
            contacto.setNombre(cursor.getString(indiceNombre));
            lista.add(contacto);
        }
        /*Nueva línea para evitar los errores durante la ejecucion*/
        cursor.close();

        return lista;
    }

    public static List<String> getListaTelefonos(Context contexto, long id) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String argumentos[] = new String[]{id + ""};
        String orden = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Cursor cursor = contexto.getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        List<String> lista = new ArrayList<>();
        String numero;
        while (cursor.moveToNext()) {
            numero = cursor.getString(indiceNumero);
            lista.add(numero);
        }
        /*Nueva línea para evitar los errores durante la ejecucion*/
        cursor.close();
        return lista;
    }

    //-------------------------Eliminar los corchetes----------------
    public static String formatear(String s_cadena, String s_caracteres) {
        String nueva_cadena = "";
        Character caracter = null;
        boolean valido = true;

        for (int i = 0; i < s_cadena.length(); i++) {
            valido = true;
            for (int j = 0; j < s_caracteres.length(); j++) {
                caracter = s_caracteres.charAt(j);

                if (s_cadena.charAt(i) == caracter) {
                    valido = false;
                    break;
                }
            }
            if (valido)
                nueva_cadena += s_cadena.charAt(i);
        }

        return nueva_cadena;
    }


    public static String getFirstWord(String text) {
        if (text.indexOf(',') > -1) { // Check if there is more than one word.
            return text.substring(0, text.indexOf(',')); // Extract first word.
        } else {
            return text; // Text is the first word itself.
        }
    }

    public static String getAhora(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
}
