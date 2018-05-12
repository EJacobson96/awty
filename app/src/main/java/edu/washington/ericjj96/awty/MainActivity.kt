package edu.washington.ericjj96.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart.setOnClickListener {
            if (validateButton()) {
                btnStart.text = "Stop"
                val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, Receiver::class.java)
                intent.putExtra("message", "${numberText.text}: ${messageText.text}")
                if (btnStart.text == "Start") {
                    btnStart.text = "Stop"
                    val timeInterval  = (minuteText.text.toString().toInt() * 60000).toLong()
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeInterval, timeInterval, pendingIntent)

                } else {
                    alarmManager.cancel(pendingIntent)
                    btnStart.text = "Start"
                }
            }
        }


    }

    fun validateButton(): Boolean {
        return !messageText.text.isEmpty() &&
                !minuteText.text.isEmpty() && !numberText.text.isEmpty()
    }
}

class Receiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val msg = intent?.getStringExtra("message")
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
