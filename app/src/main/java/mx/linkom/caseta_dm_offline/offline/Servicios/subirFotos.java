package mx.linkom.caseta_dm_offline.offline.Servicios;

import static solar.blaz.date.week.WeekDatePicker.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import mx.linkom.caseta_dm_offline.R;
import mx.linkom.caseta_dm_offline.offline.Database.Database;
import mx.linkom.caseta_dm_offline.offline.Database.UrisContentProvider;

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

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        storage = FirebaseStorage.getInstance();
                        storageReference = storage.getReference();

                        System.out.println("Servicio de fotos");

                        nombres = (ArrayList<String>) intent.getExtras().getSerializable("nombres");
                        rutasFirebase = (ArrayList<String>) intent.getExtras().getSerializable("direccionesFirebase");
                        rutasDispositivo = (ArrayList<String>) intent.getExtras().getSerializable("rutasDispositivo");

                        //Si los array son vacios terminar el servicio
                        if (nombres.isEmpty() || rutasDispositivo.isEmpty() || rutasFirebase.isEmpty()){
                            stopSelf();
                            onDestroy();
                        }

                        promedio = (int) 100/rutasDispositivo.size();

                        final String CHANNELID = "Foreground Service ID";
                        NotificationChannel channel = new NotificationChannel(
                                CHANNELID,
                                CHANNELID,
                                NotificationManager.IMPORTANCE_LOW
                        );

                        notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        builder = new NotificationCompat.Builder(getApplicationContext(), CHANNELID);
                        builder.setContentTitle("Cargando...")
                                .setContentText("Subiendo imagenes capturadas en Offline")
                                .setSmallIcon(R.drawable.ic_subir)
                                .setPriority(NotificationCompat.PRIORITY_LOW);


                        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                        notificationManager.notify(10045, builder.build());


                        subirImagenes();
                    }
                }
        ).start();

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

                        int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, "titulo = " + "'" + nombres.get(i-1) + "'", null);

                        System.out.println("Valor de eliminar en recibir fotos offline: " + eliminar);

                        //Si la imagen se subio con exito, volver a llamar el método
                        if (i == rutasDispositivo.size()){
                            builder.setContentText("Carga completada")
                                    .setProgress(0,0,false);
                            notificationManager.notify(10045, builder.build());

                            stopSelf();
                            onDestroy();
                        }
                        subirImagenes();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "error al cargar imagen");
                        stopSelf();
                        onDestroy();
                    }
                });

            }else {
                stopSelf();
                onDestroy();
            }
        }else {
            stopSelf();
            onDestroy();
        }


    }
}
