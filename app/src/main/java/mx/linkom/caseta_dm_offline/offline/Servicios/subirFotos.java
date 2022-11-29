package mx.linkom.caseta_dm_offline.offline.Servicios;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import mx.linkom.caseta_dm_offline.R;
import mx.linkom.caseta_dm_offline.offline.Database.Database;

public class subirFotos extends Service {
    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder builder;

    FirebaseStorage storage;
    StorageReference storageReference;
    ArrayList<String> nombres;
    ArrayList<String> rutasFirebase;
    ArrayList<String> rutasDispositivo;

    int i;

    int PROGRESS_MAX = 100;
    int PROGRESS_CURRENT = 0;

    int promedio;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        System.out.println("Servicio de fotos");

        nombres = (ArrayList<String>) intent.getExtras().getSerializable("nombres");
        rutasFirebase = (ArrayList<String>) intent.getExtras().getSerializable("direccionesFirebase");
        rutasDispositivo = (ArrayList<String>) intent.getExtras().getSerializable("rutasDispositivo");

        promedio = (int) 100/rutasDispositivo.size();

        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );

        notificationManager = NotificationManagerCompat.from(this);
        builder = new NotificationCompat.Builder(this, CHANNELID);
        builder.setContentTitle("Cargando...")
                .setContentText("Subiendo imagenes capturadas en Offline")
                .setSmallIcon(R.drawable.ic_subir)
                .setPriority(NotificationCompat.PRIORITY_LOW);


        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        notificationManager.notify(10045, builder.build());


        subirImagenes();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        System.out.println("Se destruyo el servicio");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void subirImagenes(){

        if (nombres.size() != 0 && rutasDispositivo.size() != 0){
            if (i < rutasDispositivo.size()){
                i++;
                PROGRESS_CURRENT += promedio;
                System.out.println("Progreso: " + PROGRESS_CURRENT);

                StorageReference ImageRef = storageReference.child(rutasFirebase.get(i-1));
                Uri uri  = Uri.fromFile(new File(rutasDispositivo.get(i-1)));
                ImageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Database database = new Database(getApplicationContext());
                        final SQLiteDatabase db = database.getWritableDatabase();

                        System.out.println("******************************************************************************************************************************************");
                        System.out.println("******************************************************************************************************************************************");
                        System.out.println("Imagen " + nombres.get(i-1) +" subida a firebase");
                        builder.setContentTitle("Imagenes completadas "+ i + " de " + rutasDispositivo.size());
                        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                        notificationManager.notify(10045, builder.build());
                        System.out.println("******************************************************************************************************************************************");
                        System.out.println("******************************************************************************************************************************************");

                        File path = new File(rutasDispositivo.get(i-1));
                        path.delete();

                        try {
                            db.execSQL("DELETE FROM fotosOffline WHERE titulo = " + "'" + nombres.get(i-1) + "'");
                        }catch (Exception ex){
                            System.out.println(ex.toString());
                            onDestroy();
                        }finally {
                            db.close();
                        }






                        //Si la imagen se subio con exito, volver a llamar el método
                        if (i == rutasDispositivo.size()){
                            builder.setContentText("Carga completada")
                                    .setProgress(0,0,false);
                            notificationManager.notify(10045, builder.build());

                            onDestroy();
                        }
                        subirImagenes();
                    }
                });

            }
        }


    }
}
