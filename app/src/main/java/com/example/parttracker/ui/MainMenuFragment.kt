//////package com.example.parttracker.ui
//////
//////import android.os.Build
//////import android.os.Bundle
//////import android.view.LayoutInflater
//////import android.view.View
//////import android.view.ViewGroup
//////import android.widget.Button
//////import androidx.annotation.RequiresApi
//////import androidx.fragment.app.Fragment
//////import androidx.lifecycle.ViewModelProvider
//////import androidx.navigation.fragment.findNavController
//////import com.example.parttracker.R
//////import com.example.parttracker.repository.PartRepository
//////import com.example.parttracker.viewmodel.DashboardViewModel
//////import com.example.parttracker.viewmodel.DashboardViewModelFactory
//////
//////class MainMenuFragment : Fragment() {
//////
//////    val repository = PartRepository.getInstance(requireContext())
//////    val viewModelFactory = DashboardViewModelFactory(requireContext())
//////    val viewModel = ViewModelProvider(this, viewModelFactory)[DashboardViewModel::class.java]
//////
//////
//////
//////
//////
//////
//////
//////    override fun onCreateView(
//////        inflater: LayoutInflater, container: ViewGroup?,
//////        savedInstanceState: Bundle?
//////    ): View? {
//////        return inflater.inflate(R.layout.fragment_main_menu, container, false)
//////    }
//////
//////    @RequiresApi(Build.VERSION_CODES.O)
//////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//////        super.onViewCreated(view, savedInstanceState)
//////
//////        val viewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]
//////        viewModel.fullSyncFromFirebase(requireContext())
//////
//////
//////
//////
//////
//////        view.findViewById<Button>(R.id.btnGoToPlan).setOnClickListener {
//////            findNavController().navigate(R.id.action_mainMenuFragment_to_planFragment)
//////        }
//////
//////
//////        view.findViewById<Button>(R.id.btnStatus).setOnClickListener {
//////            findNavController().navigate(R.id.action_mainMenuFragment_to_statusFragment)
//////        }
//////
//////        view.findViewById<Button>(R.id.btnScanQR).setOnClickListener {
//////            findNavController().navigate(R.id.action_mainMenuFragment_to_locationSelectorFragment)
//////        }
//////
//////        view.findViewById<Button>(R.id.btnGenerateQR).setOnClickListener {
//////            findNavController().navigate(R.id.action_mainMenuFragment_to_generateQRFragment)
//////        }
//////
//////        view.findViewById<Button>(R.id.btnViewScanHistory).setOnClickListener {
//////            findNavController().navigate(R.id.action_mainMenuFragment_to_scanHistoryFragment)
//////        }
//////
//////
//////        view.findViewById<Button>(R.id.btnDashboard).setOnClickListener {
//////            findNavController().navigate(R.id.action_mainMenuFragment_to_dashboardFragment)
//////        }
//////
//////
//////    }
//////}
//////
//////
////
////
////package com.example.parttracker.ui
////
////import android.os.Build
////import android.os.Bundle
////import android.view.LayoutInflater
////import android.view.View
////import android.view.ViewGroup
////import android.widget.Button
////import androidx.annotation.RequiresApi
////import androidx.fragment.app.Fragment
////import androidx.lifecycle.ViewModelProvider
////import androidx.navigation.fragment.findNavController
////import com.example.parttracker.R
////import com.example.parttracker.viewmodel.DashboardViewModel
////import com.example.parttracker.viewmodel.DashboardViewModelFactory
////
////class MainMenuFragment : Fragment() {
////
////    override fun onCreateView(
////        inflater: LayoutInflater, container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View? {
////        return inflater.inflate(R.layout.fragment_main_menu, container, false)
////    }
////
////    @RequiresApi(Build.VERSION_CODES.O)
////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        super.onViewCreated(view, savedInstanceState)
////
////        // ✅ Safe to use requireContext() now
////        val viewModelFactory = DashboardViewModelFactory(requireContext())
////        val viewModel = ViewModelProvider(this, viewModelFactory)[DashboardViewModel::class.java]
////
////        // ⬇️ Sync all Firestore data locally
////        viewModel.fullSyncFromFirebase(requireContext())
////
////        // 🔘 Navigation setup
////        view.findViewById<Button>(R.id.btnGoToPlan).setOnClickListener {
////            findNavController().navigate(R.id.action_mainMenuFragment_to_planFragment)
////        }
////
////        view.findViewById<Button>(R.id.btnStatus).setOnClickListener {
////            findNavController().navigate(R.id.action_mainMenuFragment_to_statusFragment)
////        }
////
////        view.findViewById<Button>(R.id.btnScanQR).setOnClickListener {
////            findNavController().navigate(R.id.action_mainMenuFragment_to_locationSelectorFragment)
////        }
////
////        view.findViewById<Button>(R.id.btnGenerateQR).setOnClickListener {
////            findNavController().navigate(R.id.action_mainMenuFragment_to_generateQRFragment)
////        }
////
////        view.findViewById<Button>(R.id.btnViewScanHistory).setOnClickListener {
////            findNavController().navigate(R.id.action_mainMenuFragment_to_scanHistoryFragment)
////        }
////
////        view.findViewById<Button>(R.id.btnDashboard).setOnClickListener {
////            findNavController().navigate(R.id.action_mainMenuFragment_to_dashboardFragment)
////        }
////    }
////}
////
//
//
//package com.example.parttracker.ui
//
//import android.os.Build
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import androidx.annotation.RequiresApi
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import androidx.navigation.fragment.findNavController
//import com.example.parttracker.R
//import com.example.parttracker.viewmodel.DashboardViewModel
//import com.example.parttracker.viewmodel.DashboardViewModelFactory
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//class MainMenuFragment : Fragment() {
//
//    private lateinit var db: FirebaseFirestore
//    private lateinit var auth: FirebaseAuth
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_main_menu, container, false)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        db = FirebaseFirestore.getInstance()
//        auth = FirebaseAuth.getInstance()
//
//        val viewModelFactory = DashboardViewModelFactory(requireContext())
//        val viewModel = ViewModelProvider(this, viewModelFactory)[DashboardViewModel::class.java]
//        viewModel.fullSyncFromFirebase(requireContext())
//
//        val btnPlan = view.findViewById<Button>(R.id.btnGoToPlan)
//        val btnStatus = view.findViewById<Button>(R.id.btnStatus)
//        val btnScanQR = view.findViewById<Button>(R.id.btnScanQR)
//        val btnGenerateQR = view.findViewById<Button>(R.id.btnGenerateQR)
//        val btnScanHistory = view.findViewById<Button>(R.id.btnViewScanHistory)
//        val btnDashboard = view.findViewById<Button>(R.id.btnDashboard)
//
//        val userId = auth.currentUser?.uid
//
//        if (userId != null) {
//            db.collection("users").document(userId).get().addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val role = document.getString("role")
//
//                    // 🔐 Hide all buttons by default
//                    btnPlan.visibility = View.GONE
//                    btnScanQR.visibility = View.GONE
//                    btnScanHistory.visibility = View.GONE
//                    btnStatus.visibility = View.GONE
//                    btnGenerateQR.visibility = View.GONE
//                    btnDashboard.visibility = View.GONE
//
//                    when (role) {
//                        "PaintshopOperator" -> {
//                            btnScanQR.visibility = View.VISIBLE
//                            btnScanHistory.visibility = View.VISIBLE
//                            btnDashboard.visibility = View.VISIBLE
//                        }
//                        "CTLOperator" -> {
//                            btnStatus.visibility = View.VISIBLE
//                            btnScanHistory.visibility = View.VISIBLE
//                            btnDashboard.visibility = View.VISIBLE
//                        }
//                        "PlanManager" -> {
//                            btnPlan.visibility = View.VISIBLE
//                            btnDashboard.visibility = View.VISIBLE
//                        }
//                        "ProductionTeam" -> {
//                            btnStatus.visibility = View.VISIBLE
//                            btnDashboard.visibility = View.VISIBLE
//                        }
//                        "Admin" -> {
//                            btnPlan.visibility = View.VISIBLE
//                            btnScanQR.visibility = View.VISIBLE
//                            btnStatus.visibility = View.VISIBLE
//                            btnGenerateQR.visibility = View.VISIBLE
//                            btnScanHistory.visibility = View.VISIBLE
//                            btnDashboard.visibility = View.VISIBLE
//                        }
//                    }
//
//                    // 🔘 Button Navigation logic (safe, since only visible ones are clickable)
//                    btnPlan.setOnClickListener {
//                        findNavController().navigate(R.id.action_mainMenuFragment_to_planFragment)
//                    }
//
//                    btnStatus.setOnClickListener {
//                        findNavController().navigate(R.id.action_mainMenuFragment_to_statusFragment)
//                    }
//
//                    btnScanQR.setOnClickListener {
//                        findNavController().navigate(R.id.action_mainMenuFragment_to_locationSelectorFragment)
//                    }
//
//                    btnGenerateQR.setOnClickListener {
//                        findNavController().navigate(R.id.action_mainMenuFragment_to_generateQRFragment)
//                    }
//
//                    btnScanHistory.setOnClickListener {
//                        findNavController().navigate(R.id.action_mainMenuFragment_to_scanHistoryFragment)
//                    }
//
//                    btnDashboard.setOnClickListener {
//                        findNavController().navigate(R.id.action_mainMenuFragment_to_dashboardFragment)
//                    }
//                }
//            }.addOnFailureListener {
//                // Handle fetch failure
//            }
//        }
//    }
//}


package com.example.parttracker.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.parttracker.R
import com.example.parttracker.viewmodel.DashboardViewModel
import com.example.parttracker.viewmodel.DashboardViewModelFactory
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.parttracker.firebase.FirebaseSyncManager




class MainMenuFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val role = sharedPref.getString("userRole", "") ?: ""

        val viewModelFactory = DashboardViewModelFactory(requireContext())
        val viewModel = ViewModelProvider(this, viewModelFactory)[DashboardViewModel::class.java]
        viewModel.fullSyncFromFirebase(requireContext())
        FirebaseSyncManager.startListening(requireContext())


        val btnPlan = view.findViewById<Button>(R.id.btnGoToPlan)
        val btnStatus = view.findViewById<Button>(R.id.btnStatus)
        val btnScanQR = view.findViewById<Button>(R.id.btnScanQR)
        val btnGenerateQR = view.findViewById<Button>(R.id.btnGenerateQR)
        val btnScanHistory = view.findViewById<Button>(R.id.btnViewScanHistory)
        val btnDashboard = view.findViewById<Button>(R.id.btnDashboard)

        // Hide all by default
        btnPlan.visibility = View.GONE
        btnScanQR.visibility = View.GONE
        btnScanHistory.visibility = View.GONE
        btnStatus.visibility = View.GONE
        btnGenerateQR.visibility = View.GONE
        btnDashboard.visibility = View.GONE

        // Show based on role
        when (role) {
            "PaintShopOperator" -> {
                btnScanQR.visibility = View.VISIBLE
                btnStatus.visibility = View.VISIBLE
                btnScanHistory.visibility = View.VISIBLE
                btnDashboard.visibility = View.VISIBLE
            }
            "VehicleAssemblyOperator" -> {
                btnScanQR.visibility = View.VISIBLE
                btnStatus.visibility = View.VISIBLE
                btnScanHistory.visibility = View.VISIBLE
                btnDashboard.visibility = View.VISIBLE
            }
            "Plan" -> {  // 🔁 previously you had "PlanManager"
                btnPlan.visibility = View.VISIBLE
                btnDashboard.visibility = View.VISIBLE
            }
            "Production" -> {  // 🔁 previously you had "ProductionTeam"
                btnStatus.visibility = View.VISIBLE
                btnDashboard.visibility = View.VISIBLE
            }
            "Admin" -> {
                btnPlan.visibility = View.VISIBLE
                btnScanQR.visibility = View.VISIBLE
                btnStatus.visibility = View.VISIBLE
                btnGenerateQR.visibility = View.VISIBLE
                btnScanHistory.visibility = View.VISIBLE
                btnDashboard.visibility = View.VISIBLE
            }
        }


        // Navigation
        btnPlan.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_planFragment)
        }

        btnStatus.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_statusFragment)
        }

        btnScanQR.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_locationSelectorFragment)
        }

        btnGenerateQR.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_generateQRFragment)
        }

        btnScanHistory.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_scanHistoryFragment)
        }

        btnDashboard.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_dashboardFragment)
        }


        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply() // Clear login session

            findNavController().navigate(R.id.action_mainMenuFragment_to_loginFragment)
        }



        val footerText = view.findViewById<TextView>(R.id.footerText)
        footerText.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("About This App")
                .setMessage(
                    "This application was developed as part of the Summer Internship Project at Bajaj Auto Limited.\n\n" +
                            " Developed by: Priya Jha\n" +
                            " Email: priya18jha08@gmail.com\n" +
                            " Mentor: Mr. Amar N. Pawar"
                )
                .setPositiveButton("OK", null)
                .show()
        }





    }
}

