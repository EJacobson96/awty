package edu.washington.ericjj96.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart.setOnClickListener {
            when {
                messageText.text.toString().isEmpty() -> errorMessage("Must enter a message")
                numberText.text.toString().isEmpty() -> errorMessage("Must enter a phone number")
                minuteText.text.toString().isEmpty() -> errorMessage("Must enter a time (in minutes)")
                minuteText.text.toString().toInt() < 1 -> errorMessage("Time cannot be 0 or a negative integer")
                else ->
                    if (validateButton()) {
                        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        var phoneNumber = numberText.text.toString()
                        if (phoneNumber.length == 10) {
                            phoneNumber = "(" + phoneNumber.substring(0..2) + ") " + phoneNumber.substring(3..5) + "-" + phoneNumber.substring(6)
                        }
                        val intent = Intent("edu.washington.ericjj96.awty").apply {
                            putExtra("message", "${phoneNumber}: ${messageText.text}")
                        }
                        val intentFilter = IntentFilter("edu.washington.ericjj96.awty")
                        registerReceiver(Receiver(), intentFilter)
                        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

                        if (btnStart.text == "Start") {
                            val time = (minuteText.text.toString().toInt() * 60000).toLong()
                            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                    SystemClock.elapsedRealtime() + time,
                                    time, pendingIntent)
                            btnStart.text = "Stop"
                        } else {
                            alarmManager.cancel(pendingIntent)
                            btnStart.text = "Start"
                        }
                    } else {
                        Toast.makeText(this, "Can't have empty fields!", Toast.LENGTH_SHORT).show()
                        Log.i("error", "can't have empty fields")
                    }
            }
        }


    }

    private fun errorMessage(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(Receiver())
    }

    fun validateButton(): Boolean {
        return !messageText.text.isEmpty() &&
                !minuteText.text.isEmpty() && !numberText.text.isEmpty()
                    && minuteText.text.toString().toInt() > 0
    }
}

class Receiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val msg = intent?.getStringExtra("message")
        Log.i("Broadcasting: ", "$msg")
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
