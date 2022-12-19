package com.intractable.simm.view.activities
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.RawContacts
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.intractable.simm.databinding.ActivityTextShareBinding


@Suppress("UNREACHABLE_CODE", "NAME_SHADOWING")
class TextShareActivity : AppCompatActivity() {
    var flag =0
    var name = "Simm"

    private lateinit var textShareBinding: ActivityTextShareBinding
//    object BrroadCastReciever : BroadcastReceiver() {
//
//        @SuppressLint("NewApi")
//        @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
//        override fun onReceive(context: Context, intent: Intent?) {
//
//            context.unregisterReceiver(this)
//
//            val componentName =
//                intent?.getParcelableExtra<ComponentName>(Intent.EXTRA_CHOSEN_COMPONENT)
//                Toast.makeText(context,componentName.toString(),Toast.LENGTH_SHORT).show()
//            Log.e("component", componentName.toString())
//
//        }
//    }

    @SuppressLint("UnspecifiedImmutableFlag", "Range", "Recycle")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textShareBinding = ActivityTextShareBinding.inflate(layoutInflater)
        setContentView(textShareBinding.root)
        checkForPermission(android.Manifest.permission.WRITE_CONTACTS, 101)



//        textShareBinding.btContact.setOnClickListener {
//
//
//            contactExists( "1234")
//        }


        textShareBinding.btShare.setOnClickListener {
            importContact()
        }
//            val intent = Intent(Intent.ACTION_SEND)
//            intent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
//            intent.type = "text/plain"
//
//            val shareAction = Intent.ACTION_SEND
//            val receiver = Intent(shareAction)
//            receiver.putExtra("test", "test")
//            val pendingIntent =
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    PendingIntent.getBroadcast(this, 0, receiver, PendingIntent.FLAG_MUTABLE)
//                } else {
//                    PendingIntent.getActivity(applicationContext, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT)
//                }
//            val chooser = Intent.createChooser(intent, "test", pendingIntent.intentSender)
//
//
//            applicationContext.registerReceiver(BrroadCastReciever, IntentFilter(shareAction))
//            flag = 1
//            startActivity(chooser)
//            super.onPause()
//        }
        }

//    override fun onResume() {
//        super.onResume()
//        if (flag == 1) {
//            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
//
//        }
//    }


//    @SuppressLint("Range")
//    fun contactExists(sPhoneNr: String): String? {
//        if (sPhoneNr === "") return ""
//
//        val rawContactUri = RawContacts.CONTENT_URI.buildUpon()
//            .appendQueryParameter(RawContacts.ACCOUNT_NAME, null)
//            .appendQueryParameter(RawContacts.ACCOUNT_TYPE, null)
//            .build()
//        val contentResolver = contentResolver
//        val phones: Cursor? = contentResolver.query(
//            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//            null,
//            ContactsContract.CommonDataKinds.Phone.NUMBER + " = " + sPhoneNr,
//            null,
//            null
//        )
//        if (phones!!.moveToFirst()) // .getCount() > 0
//        {
//            try {
//                val number: String =
//                    phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
//                Toast.makeText(this, number,Toast.LENGTH_SHORT).show()
//
//                return phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID))
//            } catch (e: Exception) {
//                Log.d("Phone-Number: ", e.message!!)
//            }
//            return ""
//        }
//        return ""
//    }


    private fun checkForPermission(permission: String, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) -> {
                }
                else -> ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    requestCode
                )
            }
        }
    }
    fun importContact() {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION)
        intent.type = ContactsContract.RawContacts.CONTENT_TYPE
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(ContactsContract.Intents.Insert.NAME, "Grace")
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, "1234")
        startActivity(intent)
    }


}







