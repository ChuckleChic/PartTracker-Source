//package com.example.parttracker.ui.scan
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.parttracker.model.ScannedPart
//import com.example.parttracker.repository.PartRepository
//import com.google.zxing.integration.android.IntentIntegrator
//import org.json.JSONObject
//import java.text.SimpleDateFormat
//import java.util.*
//import androidx.lifecycle.lifecycleScope
//import kotlinx.coroutines.launch
//
//
//class ScanActivity : AppCompatActivity() {
//
//    private lateinit var repository: PartRepository
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
////        val dao = PartDatabase.getRoomDatabase(this).ScannedPartDao()
////        repository = PartRepository(dao)
//
//
//        val integrator = IntentIntegrator(this)
//        integrator.setPrompt("Scan QR")
//        integrator.setOrientationLocked(true)
//        integrator.initiateScan()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//        if (result != null && result.contents != null) {
//            try {
//                val json = JSONObject(result.contents)
//
//                val scannedPart = ScannedPart(
//                    partName = json.getString("partName"),
//                    productId = json.getString("productId"),
//                    trolleyName = json.getString("trolleyName"),
//                    trolleyNumber = json.getString("trolleyNumber"),
//                    sequenceNumber = json.getInt("sequenceNumber"),
//                    location = json.getString("location"),
//                    id = TODO(),
//                    timestamp = TODO(),
//                )
//
//                lifecycleScope.launch {
//                    repository.insert(scannedPart)
//                }
//
//
//                Toast.makeText(this, "Scanned successfully!", Toast.LENGTH_LONG).show()
//            } catch (e: Exception) {
//                Log.e("ScanActivity", "QR parse error: ${e.message}")
//                Toast.makeText(this, "Invalid QR", Toast.LENGTH_SHORT).show()
//            }
//            finish()
//        } else {
//            Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show()
//            super.onActivityResult(requestCode, resultCode, data)
//        }
//    }
//
//    private fun getCurrentTimestamp(): String {
//        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        return sdf.format(Date())
//    }
//}
