//package com.example.parttracker.ui.status
//
//import android.os.Bundle
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.activity.ComponentActivity
//import androidx.lifecycle.ViewModelProvider
//import com.example.parttracker.R
//import com.example.parttracker.viewmodel.PartViewModel
////import com.example.parttracker.viewmodel.PartViewModel
//import com.example.parttracker.model.LocationCount
//
//
//class StatusActivity : ComponentActivity() {
//
//    private lateinit var viewModel: PartViewModel
//    private lateinit var container: LinearLayout
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_status)
//
//        container = findViewById(R.id.status_container)
//
//        viewModel = ViewModelProvider(this)[PartViewModel::class.java]
//
//        viewModel.partCountsByLocation.observe(this) { counts ->
//            container.removeAllViews()
//            for (entry in counts) {
//                val card = TextView(this).apply {
//                    text = "${entry.location}: ${entry.count} parts"
//                    textSize = 18f
//                    setPadding(16, 16, 16, 16)
//                }
//                container.addView(card)
//            }
//        }
//    }
//}
