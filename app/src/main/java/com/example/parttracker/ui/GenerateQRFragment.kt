//////////package com.example.parttracker.ui
//////////
//////////import android.graphics.Bitmap
//////////import android.graphics.Color
//////////import android.os.Bundle
//////////import android.view.LayoutInflater
//////////import android.view.View
//////////import android.view.ViewGroup
//////////import android.widget.*
//////////import androidx.fragment.app.Fragment
//////////import com.example.parttracker.R
//////////import com.google.zxing.BarcodeFormat
//////////import com.google.zxing.EncodeHintType
//////////import com.google.zxing.MultiFormatWriter
//////////import com.google.zxing.common.BitMatrix
//////////import org.json.JSONObject
//////////import java.text.SimpleDateFormat
//////////import java.util.*
//////////
//////////class GenerateQRFragment : Fragment() {
//////////
//////////    private lateinit var etPartName: EditText
//////////    private lateinit var etProductId: EditText
//////////    private lateinit var etTrolleyName: EditText
//////////    private lateinit var etTrolleyNumber: EditText
//////////    private lateinit var etSequenceNumber: EditText
//////////    private lateinit var etLocation: EditText
//////////    private lateinit var ivQRCode: ImageView
//////////    private lateinit var btnGenerateQR: Button
//////////
//////////    override fun onCreateView(
//////////        inflater: LayoutInflater, container: ViewGroup?,
//////////        savedInstanceState: Bundle?
//////////    ): View {
//////////        val view = inflater.inflate(R.layout.fragment_generate_qr, container, false)
//////////
//////////        etPartName = view.findViewById(R.id.etPartName)
//////////        etProductId = view.findViewById(R.id.etProductId)
//////////        etTrolleyName = view.findViewById(R.id.etTrolleyName)
//////////        etTrolleyNumber = view.findViewById(R.id.etTrolleyNumber)
//////////        etSequenceNumber = view.findViewById(R.id.etSequenceNumber)
//////////        //etLocation = view.findViewById(R.id.etLocation)
//////////        ivQRCode = view.findViewById(R.id.ivQRCode)
//////////        btnGenerateQR = view.findViewById(R.id.btnGenerateQR)
//////////
//////////        btnGenerateQR.setOnClickListener { generateQR() }
//////////
//////////        return view
//////////    }
//////////
//////////    private fun generateQR() {
//////////        val partName = etPartName.text.toString().trim()
//////////        val productId = etProductId.text.toString().trim()
//////////        val trolleyName = etTrolleyName.text.toString().trim()
//////////        val trolleyNumber = etTrolleyNumber.text.toString().trim()
//////////        val sequenceNumber = etSequenceNumber.text.toString().toIntOrNull() ?: 0
//////////        //val location = etLocation.text.toString().trim()
//////////
//////////        if (partName.isEmpty() || productId.isEmpty() || trolleyName.isEmpty() || trolleyNumber.isEmpty() || location.isEmpty()) {
//////////            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
//////////            return
//////////        }
//////////
//////////        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
//////////
//////////        val jsonData = JSONObject().apply {
//////////            put("partName", partName)
//////////            put("productId", productId)
//////////            put("trolleyName", trolleyName)
//////////            put("trolleyNumber", trolleyNumber)
//////////            put("sequenceNumber", sequenceNumber)
//////////            //put("location", location)
//////////            put("timestamp", currentTime)
//////////        }.toString()
//////////
//////////        val bitmap = generateQRCodeBitmap(jsonData)
//////////        ivQRCode.setImageBitmap(bitmap)
//////////    }
//////////
//////////    private fun generateQRCodeBitmap(text: String): Bitmap {
//////////        val size = 512
//////////        val hints = mapOf(EncodeHintType.MARGIN to 1)
//////////        val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
//////////
//////////        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).apply {
//////////            for (x in 0 until size) {
//////////                for (y in 0 until size) {
//////////                    setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
//////////                }
//////////            }
//////////        }
//////////    }
//////////}
////////
////////package com.example.parttracker.ui
////////
////////import android.graphics.Bitmap
////////import android.graphics.Color
////////import android.os.Bundle
////////import android.view.LayoutInflater
////////import android.view.View
////////import android.view.ViewGroup
////////import android.widget.*
////////import androidx.fragment.app.Fragment
////////import com.example.parttracker.R
////////import com.google.zxing.BarcodeFormat
////////import com.google.zxing.EncodeHintType
////////import com.google.zxing.MultiFormatWriter
////////import com.google.zxing.common.BitMatrix
////////import org.json.JSONObject
////////import java.text.SimpleDateFormat
////////import java.util.*
////////
////////class GenerateQRFragment : Fragment() {
////////
////////    private lateinit var etPartName: EditText
////////    private lateinit var etProductId: EditText
////////    private lateinit var etTrolleyName: EditText
////////    private lateinit var etTrolleyNumber: EditText
////////    private lateinit var etSequenceNumber: EditText
////////    private lateinit var ivQRCode: ImageView
////////    private lateinit var btnGenerateQR: Button
////////
////////    override fun onCreateView(
////////        inflater: LayoutInflater, container: ViewGroup?,
////////        savedInstanceState: Bundle?
////////    ): View {
////////        val view = inflater.inflate(R.layout.fragment_generate_qr, container, false)
////////
////////        // Bind UI elements
////////        etPartName = view.findViewById(R.id.etPartName)
////////        etProductId = view.findViewById(R.id.etProductId)
////////        etTrolleyName = view.findViewById(R.id.etTrolleyName)
////////        etTrolleyNumber = view.findViewById(R.id.etTrolleyNumber)
////////        etSequenceNumber = view.findViewById(R.id.etSequenceNumber)
////////        ivQRCode = view.findViewById(R.id.ivQRCode)
////////        btnGenerateQR = view.findViewById(R.id.btnGenerateQR)
////////
////////        btnGenerateQR.setOnClickListener { generateQR() }
////////
////////        return view
////////    }
////////
////////    private fun generateQR() {
////////        val partName = etPartName.text.toString().trim()
////////        val productId = etProductId.text.toString().trim()
////////        val trolleyName = etTrolleyName.text.toString().trim()
////////        val trolleyNumber = etTrolleyNumber.text.toString().trim()
////////        val sequenceNumber = etSequenceNumber.text.toString().toIntOrNull() ?: 0
////////
////////        if (partName.isEmpty() || productId.isEmpty() || trolleyName.isEmpty() || trolleyNumber.isEmpty()) {
////////            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
////////            return
////////        }
////////
////////        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
////////
////////        val qrData = JSONObject().apply {
////////            put("partName", partName)
////////            put("productId", productId)
////////            put("trolleyName", trolleyName)
////////            put("trolleyNumber", trolleyNumber)
////////            put("sequenceNumber", sequenceNumber)
////////            put("timestamp", timestamp)
////////        }.toString()
////////
////////        val qrBitmap = generateQRCodeBitmap(qrData)
////////        ivQRCode.setImageBitmap(qrBitmap)
////////    }
////////
////////    private fun generateQRCodeBitmap(text: String): Bitmap {
////////        val size = 512
////////        val hints = mapOf(EncodeHintType.MARGIN to 1)
////////        val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
////////
////////        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).apply {
////////            for (x in 0 until size) {
////////                for (y in 0 until size) {
////////                    setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
////////                }
////////            }
////////        }
////////    }
////////}
//////
//////package com.example.parttracker.ui
//////
//////import android.graphics.Bitmap
//////import android.graphics.Color
//////import android.os.Bundle
//////import android.view.LayoutInflater
//////import android.view.View
//////import android.view.ViewGroup
//////import android.widget.*
//////import androidx.fragment.app.Fragment
//////import com.example.parttracker.R
//////import com.google.zxing.BarcodeFormat
//////import com.google.zxing.EncodeHintType
//////import com.google.zxing.MultiFormatWriter
//////import com.google.zxing.common.BitMatrix
//////import org.json.JSONObject
//////import java.text.SimpleDateFormat
//////import java.util.*
//////
//////class GenerateQRFragment : Fragment() {
//////
//////    private lateinit var etPartName: EditText
//////    private lateinit var etProductId: EditText
//////    private lateinit var etTrolleyName: EditText
//////    private lateinit var etTrolleyNumber: EditText
//////    private lateinit var etSequenceNumber: EditText
//////    private lateinit var etQuantity: EditText
//////    private lateinit var ivQRCode: ImageView
//////    private lateinit var btnGenerateQR: Button
//////
//////    override fun onCreateView(
//////        inflater: LayoutInflater, container: ViewGroup?,
//////        savedInstanceState: Bundle?
//////    ): View {
//////        val view = inflater.inflate(R.layout.fragment_generate_qr, container, false)
//////
//////        etPartName = view.findViewById(R.id.etPartName)
//////        etProductId = view.findViewById(R.id.etProductId)
//////        etTrolleyName = view.findViewById(R.id.etTrolleyName)
//////        etTrolleyNumber = view.findViewById(R.id.etTrolleyNumber)
//////        etSequenceNumber = view.findViewById(R.id.etSequenceNumber)
//////        etQuantity = view.findViewById(R.id.etQuantity)  // ✅ NEW FIELD
//////        ivQRCode = view.findViewById(R.id.ivQRCode)
//////        btnGenerateQR = view.findViewById(R.id.btnGenerateQR)
//////
//////        btnGenerateQR.setOnClickListener { generateQR() }
//////
//////        return view
//////    }
//////
//////    private fun generateQR() {
//////        val partName = etPartName.text.toString().trim()
//////        val productId = etProductId.text.toString().trim()
//////        val trolleyName = etTrolleyName.text.toString().trim()
//////        val trolleyNumber = etTrolleyNumber.text.toString().trim()
//////        val sequenceNumber = etSequenceNumber.text.toString().toIntOrNull() ?: 0
//////        val quantity = etQuantity.text.toString().toIntOrNull() ?: 0  // ✅ PARSE QUANTITY
//////
//////        if (partName.isEmpty() || productId.isEmpty() || trolleyName.isEmpty() || trolleyNumber.isEmpty() || quantity == 0) {
//////            Toast.makeText(requireContext(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
//////            return
//////        }
//////
//////        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
//////
//////        val qrData = JSONObject().apply {
//////            put("partName", partName)
//////            put("productId", productId)
//////            put("trolleyName", trolleyName)
//////            put("trolleyNumber", trolleyNumber)
//////            put("sequenceNumber", sequenceNumber)
//////            put("quantity", quantity)  // ✅ ADD TO JSON
//////            put("timestamp", timestamp)
//////        }.toString()
//////
//////        val qrBitmap = generateQRCodeBitmap(qrData)
//////        ivQRCode.setImageBitmap(qrBitmap)
//////    }
//////
//////    private fun generateQRCodeBitmap(text: String): Bitmap {
//////        val size = 512
//////        val hints = mapOf(EncodeHintType.MARGIN to 1)
//////        val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
//////
//////        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).apply {
//////            for (x in 0 until size) {
//////                for (y in 0 until size) {
//////                    setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
//////                }
//////            }
//////        }
//////    }
//////}
////
////
////// ✅ GenerateQRFragment.kt (Updated)
////package com.example.parttracker.ui
////
////import android.graphics.Bitmap
////import android.graphics.Color
////import android.os.Bundle
////import android.view.LayoutInflater
////import android.view.View
////import android.view.ViewGroup
////import android.widget.*
////import androidx.fragment.app.Fragment
////import com.example.parttracker.R
////import com.google.zxing.BarcodeFormat
////import com.google.zxing.EncodeHintType
////import com.google.zxing.MultiFormatWriter
////import com.google.zxing.common.BitMatrix
////import org.json.JSONObject
////import java.text.SimpleDateFormat
////import java.util.*
////
////class GenerateQRFragment : Fragment() {
////
////    private lateinit var etPartName: AutoCompleteTextView
////    private lateinit var etProductId: EditText
////    private lateinit var etTrolleyName: EditText
////    private lateinit var etTrolleyNumber: EditText
////    private lateinit var etSequenceNumber: EditText
////    private lateinit var etQuantity: EditText
////    private lateinit var ivQRCode: ImageView
////    private lateinit var btnGenerateQR: Button
////
////    override fun onCreateView(
////        inflater: LayoutInflater, container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View {
////        val view = inflater.inflate(R.layout.fragment_generate_qr, container, false)
////
////        etPartName = view.findViewById(R.id.etPartName)
////        etProductId = view.findViewById(R.id.etProductId)
////        etTrolleyName = view.findViewById(R.id.etTrolleyName)
////        etTrolleyNumber = view.findViewById(R.id.etTrolleyNumber)
////        etSequenceNumber = view.findViewById(R.id.etSequenceNumber)
////        etQuantity = view.findViewById(R.id.etQuantity)
////        ivQRCode = view.findViewById(R.id.ivQRCode)
////        btnGenerateQR = view.findViewById(R.id.btnGenerateQR)
////
////        // ✅ Dropdown logic added here
////        val partNames = listOf(
////            "Panel", "Upper", "Lower", "Fender", "Neck P/C", "LH/RH", "Handle Bar", "Glove Box", "Lid Cover", "Set", "Rear Cover"
////        )
////        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, partNames)
////        (etPartName as? AutoCompleteTextView)?.setAdapter(adapter)
////
////        btnGenerateQR.setOnClickListener { generateQR() }
////
////        return view
////    }
////
////
////    private fun generateQR() {
////        val partName = etPartName.text.toString().trim()
////        val productId = etProductId.text.toString().trim()
////        val trolleyName = etTrolleyName.text.toString().trim()
////        val trolleyNumber = etTrolleyNumber.text.toString().trim()
////        val sequenceNumber = etSequenceNumber.text.toString().toIntOrNull() ?: 0
////        val quantity = etQuantity.text.toString().toIntOrNull() ?: 0
////
////        if (partName.isEmpty() || productId.isEmpty() || trolleyName.isEmpty() || trolleyNumber.isEmpty() || quantity == 0) {
////            Toast.makeText(requireContext(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
////            return
////        }
////
////        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
////
////        val qrData = JSONObject().apply {
////            put("partName", partName)
////            put("productId", productId)
////            put("trolleyName", trolleyName)
////            put("trolleyNumber", trolleyNumber)
////            put("sequenceNumber", sequenceNumber)
////            put("quantity", quantity)
////            put("timestamp", timestamp)
////        }.toString()
////
////
////        val qrBitmap = generateQRCodeBitmap(qrData)
////        ivQRCode.setImageBitmap(qrBitmap)
////        ivQRCode.visibility = View.VISIBLE
////
////    }
////
////    private fun generateQRCodeBitmap(text: String): Bitmap {
////        val size = 512
////        val hints = mapOf(EncodeHintType.MARGIN to 1)
////        val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
////
////        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).apply {
////            for (x in 0 until size) {
////                for (y in 0 until size) {
////                    setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
////                }
////            }
////        }
////    }
////}
//
//package com.example.parttracker.ui
//
//import android.content.ContentValues
//import android.content.Context
//import android.graphics.*
//import android.graphics.pdf.PdfDocument
//import android.os.Bundle
//import android.provider.MediaStore
//import android.print.PrintAttributes
//import android.print.PrintManager
//import android.util.Log
//import android.view.*
//import android.widget.*
//import androidx.core.content.FileProvider
//import androidx.fragment.app.Fragment
//import com.example.parttracker.R
//import com.google.zxing.*
//import com.google.zxing.common.BitMatrix
//import org.json.JSONObject
//import java.io.File
//import java.io.FileOutputStream
//import java.text.SimpleDateFormat
//import java.util.*
//import android.Manifest
//import android.content.pm.PackageManager
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//
//
//class GenerateQRFragment : Fragment() {
//
//    private lateinit var etPartName: AutoCompleteTextView
//    private lateinit var etProductId: EditText
//    private lateinit var etTrolleyName: EditText
//    private lateinit var etTrolleyNumber: EditText
//    private lateinit var etSequenceNumber: EditText
//    private lateinit var etQuantity: EditText
//    private lateinit var ivQRCode: ImageView
//    private lateinit var btnGenerateQR: Button
//    private lateinit var btnSaveImage: Button
//    private lateinit var btnSavePDF: Button
//    private lateinit var btnPrintQR: Button
//    private var currentQRBitmap: Bitmap? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val view = inflater.inflate(R.layout.fragment_generate_qr, container, false)
//
//        etPartName = view.findViewById(R.id.etPartName)
//        etProductId = view.findViewById(R.id.etProductId)
//        etTrolleyName = view.findViewById(R.id.etTrolleyName)
//        etTrolleyNumber = view.findViewById(R.id.etTrolleyNumber)
//        etSequenceNumber = view.findViewById(R.id.etSequenceNumber)
//        etQuantity = view.findViewById(R.id.etQuantity)
//        ivQRCode = view.findViewById(R.id.ivQRCode)
//        btnGenerateQR = view.findViewById(R.id.btnGenerateQR)
//        btnSaveImage = view.findViewById(R.id.btnSaveImage)
//        btnSavePDF = view.findViewById(R.id.btnSavePDF)
//        btnPrintQR = view.findViewById(R.id.btnPrintQR)
//
//        val partNames = listOf("Panel", "Upper", "Lower", "Fender", "Neck P/C", "LH/RH", "Handle Bar", "Glove Box", "Lid Cover", "Set", "Rear Cover")
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, partNames)
//        etPartName.setAdapter(adapter)
//
//        btnGenerateQR.setOnClickListener { generateQR() }
//        btnSaveImage.setOnClickListener { saveQRAsImage() }
//        btnSavePDF.setOnClickListener { saveQRAsPDF() }
//        btnPrintQR.setOnClickListener { printQRBitmap() }
//
//        return view
//    }
//
//    private fun generateQR() {
//        val partName = etPartName.text.toString().trim()
//        val productId = etProductId.text.toString().trim()
//        val trolleyName = etTrolleyName.text.toString().trim()
//        val trolleyNumber = etTrolleyNumber.text.toString().trim()
//        val sequenceNumber = etSequenceNumber.text.toString().toIntOrNull() ?: 0
//        val quantity = etQuantity.text.toString().toIntOrNull() ?: 0
//
//        if (partName.isEmpty() || productId.isEmpty() || trolleyName.isEmpty() || trolleyNumber.isEmpty() || quantity == 0) {
//            Toast.makeText(requireContext(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
//        val qrData = JSONObject().apply {
//            put("partName", partName)
//            put("productId", productId)
//            put("trolleyName", trolleyName)
//            put("trolleyNumber", trolleyNumber)
//            put("sequenceNumber", sequenceNumber)
//            put("quantity", quantity)
//            put("timestamp", timestamp)
//        }.toString()
//
//        currentQRBitmap = generateQRCodeBitmap(qrData)
//        ivQRCode.setImageBitmap(currentQRBitmap)
//        ivQRCode.visibility = View.VISIBLE
//    }
//
//    private fun generateQRCodeBitmap(text: String): Bitmap {
//        val size = 512
//        val hints = mapOf(EncodeHintType.MARGIN to 1)
//        val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
//
//        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).apply {
//            for (x in 0 until size) {
//                for (y in 0 until size) {
//                    setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
//                }
//            }
//        }
//    }
//
//    private fun saveQRAsImage() {
//        currentQRBitmap?.let { bitmap ->
//            val filename = "QR_${System.currentTimeMillis()}.png"
//            val resolver = requireContext().contentResolver
//            val contentValues = ContentValues().apply {
//                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
//                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
//                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/QRGenerator")
//            }
//
//            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//            uri?.let {
//                resolver.openOutputStream(it)?.use { out ->
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
//                    Toast.makeText(requireContext(), "Saved to Gallery", Toast.LENGTH_SHORT).show()
//                }
//            }
//        } ?: Toast.makeText(requireContext(), "Generate QR first", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun saveQRAsPDF() {
//        currentQRBitmap?.let { bitmap ->
//            val pdfDocument = PdfDocument()
//            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
//            val page = pdfDocument.startPage(pageInfo)
//            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
//            pdfDocument.finishPage(page)
//
//            val file = File(requireContext().getExternalFilesDir(null), "QR_${System.currentTimeMillis()}.pdf")
//            pdfDocument.writeTo(FileOutputStream(file))
//            pdfDocument.close()
//            Toast.makeText(requireContext(), "Saved PDF at ${file.path}", Toast.LENGTH_SHORT).show()
//        } ?: Toast.makeText(requireContext(), "Generate QR first", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun printQRBitmap() {
//        currentQRBitmap?.let { bitmap ->
//            val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
//            val printAdapter = BitmapPrintAdapter(requireContext(), bitmap)
//            printManager.print("QR_Code_Print", printAdapter, PrintAttributes.Builder().build())
//        } ?: Toast.makeText(requireContext(), "Generate QR first", Toast.LENGTH_SHORT).show()
//    }
//}


package com.example.parttracker.ui

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.parttracker.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class GenerateQRFragment : Fragment() {

    private lateinit var etPartName: AutoCompleteTextView
    private lateinit var etProductId: EditText
    private lateinit var etTrolleyName: EditText
    private lateinit var etTrolleyNumber: EditText
    private lateinit var etSequenceNumber: EditText
    private lateinit var etQuantity: EditText
    private lateinit var ivQRCode: ImageView
    private lateinit var btnGenerateQR: Button
    private lateinit var btnSaveImage: Button
    private lateinit var btnSavePDF: Button
    private lateinit var btnPrintQR: Button
    private var currentQRBitmap: Bitmap? = null

    companion object {
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_generate_qr, container, false)

        val sharedPrefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val role = sharedPrefs.getString("userRole", "") ?: ""

        if (role != "Admin") {
            Toast.makeText(requireContext(), "Access Denied: Admins only", Toast.LENGTH_LONG).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return view // or return a dummy view to stop interaction
        }


        // UI references
        etPartName = view.findViewById(R.id.etPartName)
        etProductId = view.findViewById(R.id.etProductId)
        etTrolleyName = view.findViewById(R.id.etTrolleyName)
        etTrolleyNumber = view.findViewById(R.id.etTrolleyNumber)
        etSequenceNumber = view.findViewById(R.id.etSequenceNumber)
        etQuantity = view.findViewById(R.id.etQuantity)
        ivQRCode = view.findViewById(R.id.ivQRCode)
        btnGenerateQR = view.findViewById(R.id.btnGenerateQR)
        btnSaveImage = view.findViewById(R.id.btnSaveImage)
        btnSavePDF = view.findViewById(R.id.btnSavePDF)
        btnPrintQR = view.findViewById(R.id.btnPrintQR)

        val partNames = listOf("Side Panel", "Upper Shield", "Lower Shield", "Front Fender", "Neck Piece", "Shield LH/RH", "Handle Bar", "Glove Box", "Lid Cover", "Rear Shield")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, partNames)
        etPartName.setAdapter(adapter)

        btnGenerateQR.setOnClickListener { generateQR() }
        btnSaveImage.setOnClickListener { checkAndSaveImage() }
        btnSavePDF.setOnClickListener { saveQRAsPDF() }
        btnPrintQR.setOnClickListener { printQRBitmap() }

        return view
    }

    private fun generateQR() {
        val partName = etPartName.text.toString().trim() // can be empty
        val productId = etProductId.text.toString().trim() // can be empty
        val trolleyName = etTrolleyName.text.toString().trim()
        val trolleyNumber = etTrolleyNumber.text.toString().trim()
        val sequenceNumber = etSequenceNumber.text.toString().toIntOrNull() ?: 0
        val quantity = etQuantity.text.toString().toIntOrNull() ?: 0

        if (trolleyName.isEmpty() || trolleyNumber.isEmpty() || quantity == 0) {
            Toast.makeText(requireContext(), "Please enter trolley name, number, and quantity", Toast.LENGTH_SHORT).show()
            return
        }

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val qrData = JSONObject().apply {
            put("partName", partName) // even if empty
            put("productId", productId) // even if empty
            put("trolleyName", trolleyName)
            put("trolleyNumber", trolleyNumber)
            put("sequenceNumber", sequenceNumber)
            put("quantity", quantity)
            put("timestamp", timestamp)
        }.toString()

        currentQRBitmap = generateQRCodeBitmap(qrData)
        ivQRCode.setImageBitmap(currentQRBitmap)
        ivQRCode.visibility = View.VISIBLE
    }

    private fun generateQRCodeBitmap(text: String): Bitmap {
        val size = 512
        val hints = mapOf(EncodeHintType.MARGIN to 1)
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)

        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).apply {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }

    private fun checkAndSaveImage() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_REQUEST_CODE
                )
                return
            }
        }
        saveQRAsImage()
    }

    private fun saveQRAsImage() {
        val bitmap = currentQRBitmap
        if (bitmap == null) {
            Toast.makeText(requireContext(), "Generate QR first", Toast.LENGTH_SHORT).show()
            return
        }

        val filename = "QR_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/QRGenerator")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = requireContext().contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri == null) {
            Toast.makeText(requireContext(), "Unable to save image: URI is null", Toast.LENGTH_LONG).show()
            return
        }

        try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            Toast.makeText(requireContext(), "Saved to Gallery", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }


    private fun saveQRAsPDF() {
        currentQRBitmap?.let { bitmap ->
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
            pdfDocument.finishPage(page)

            val file = File(requireContext().getExternalFilesDir(null), "QR_${System.currentTimeMillis()}.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            Toast.makeText(requireContext(), "Saved PDF at ${file.path}", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(requireContext(), "Generate QR first", Toast.LENGTH_SHORT).show()
    }

    private fun printQRBitmap() {
        currentQRBitmap?.let { bitmap ->
            val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
            val printAdapter = BitmapPrintAdapter(requireContext(), bitmap)
            printManager.print("QR_Code_Print", printAdapter, PrintAttributes.Builder().build())
        } ?: Toast.makeText(requireContext(), "Generate QR first", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveQRAsImage()
            } else {
                Toast.makeText(requireContext(), "Storage permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
