package com.example.housepick.data.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.housepick.Application
import com.example.housepick.MainActivity
import com.example.housepick.R
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class NotificationsService : Service() {
    private var handler: Handler? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private lateinit var mSocket: Socket

    override fun onCreate() {
        super.onCreate()
        try {
            val options: IO.Options = IO.Options.builder().setUpgrade(false).build()
            mSocket = IO.socket("http://" + Application.IPSocket + ":8000", options)
        } catch (e: URISyntaxException) {
            print(e)
        }


        handler = Handler()

    }

    private fun runOnUiThread(runnable: Runnable) {
        handler!!.post(runnable)
    }

    //example to update
    private val onNewHouse = Emitter.Listener { args ->
        runOnUiThread {
            if (Application.allowNotifications) {
                val json: JSONObject = args[0] as JSONObject
                val housing: JSONObject = json.getJSONObject("newHousing")
                val titleHouse: String = housing.getString("title")

                val title = "La maison: $titleHouse est en ligne"
                val text = "Une nouvelle maison est en ligne, venez la consulter"
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                createNotification(title, text, intent, Application.getIdNotifParis())
            }

        }

    }


    private fun createNotification(title: String, text: String, intent: Intent, id: Int) {
        if (Application.isActivityVisible()) {
            Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
        } else {
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(this, "Notifications")
                .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(title)
                .setContentText(text).setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent).setAutoCancel(true)
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(id, builder.build())
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mSocket.connect()
        mSocket.on("/housings", onNewHouse)
        return START_STICKY
    }


    override fun onDestroy() {
        println("destroy")
        super.onDestroy()/*mSocket.disconnect()
        mSocket.off()*/
    }


}