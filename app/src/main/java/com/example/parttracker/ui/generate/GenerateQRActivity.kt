//package com.example.parttracker.ui.generate
//
//import android.graphics.Bitmap
//import android.graphics.Color
//import android.os.Bundle
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import com.example.parttracker.R
//import com.google.zxing.BarcodeFormat
//import com.google.zxing.EncodeHintType
//import com.google.zxing.MultiFormatWriter
//import com.google.zxing.common.BitMatrix
//import org.json.JSONObject
//import java.text.SimpleDateFormat
//import java.util.*
//
//class GenerateQRActivity : AppCompatActivity() {
//
//    private lateinit var etPartName: EditText
//    private lateinit var etProductId: EditText
//    private lateinit var etTrolleyName: EditText
//    private lateinit var etTrolleyNumber: EditText
//    private lateinit var btnGenerateQR: Button
//    private lateinit var qrCodeImageView: ImageView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_generate_qr)
//
//        etPartName = findViewById(R.id.etPartName)
//        etProductId = findViewById(R.id.etProductId)
//        etTrolleyName = findViewById(R.id.etTrolleyName)
//        etTrolleyNumber = findViewById(R.id.etTrolleyNumber)
//        btnGenerateQR = findViewById(R.id.btnGenerateQR)
//        ivQRCode = findViewById(R.id.ivQRCode)
//
//        btnGenerateQR.setOnClickListener {
//            generateQRCode()
//        }
//    }
//
//    private fun generateQRCode() {
//        val partName = etPartName.text.toString().trim()
//        val productId = etProductId.text.toString().trim()
//        val trolleyName = etTrolleyName.text.toString().trim()
//        val trolleyNumber = etTrolleyNumber.text.toString().trim()
//
//        if (partName.isEmpty() || productId.isEmpty() || trolleyName.isEmpty() || trolleyNumber.isEmpty()) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val dispatchTime = System.currentTimeMillis()
//        val dispatchTimeFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(dispatchTime))
//        val sequenceNumber = (1000..9999).random()
//
//        val dataJson = JSONObject().apply {
//            put("partName", partName)
//            put("productId", productId)
//            put("trolleyName", trolleyName)
//            put("trolleyNumber", trolleyNumber)
//            put("sequenceNumber", sequenceNumber)
//            put("dispatchTime", dispatchTimeFormatted)
//        }
//
//        val bitmap = encodeTextToBitmap(dataJson.toString())
//        qrCodeImageView.setImageBitmap(bitmap)
//    }
//
//    private fun encodeTextToBitmap(text: String): Bitmap {
//        val writer = MultiFormatWriter()
//        val hints = mapOf(EncodeHintType.MARGIN to 1, EncodeHintType.CHARACTER_SET to "UTF-8")
//        val bitMatrix: BitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512, hints)
//
//        val width = bitMatrix.width
//        val height = bitMatrix.height
//        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
//
//        for (x in 0 until width) {
//            for (y in 0 until height) {
//                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
//            }
//        }
//        return bmp
//    }
//}
