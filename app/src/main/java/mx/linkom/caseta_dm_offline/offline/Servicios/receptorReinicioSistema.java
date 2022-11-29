package mx.linkom.caseta_dm_offline.offline.Servicios;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class receptorReinicioSistema extends BroadcastReceiver {
    //Recibir el mensaje que el sistema operativo android envia cada vez que se reinicia el servicio
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        //Verificar si la acción que recibimos de la intención es igual a la acción de inicio completada
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            //Si se reinicio el dispositivo iniciar nuevamente el servicio
            Intent serviceIntent = new Intent(context, testInternet.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
