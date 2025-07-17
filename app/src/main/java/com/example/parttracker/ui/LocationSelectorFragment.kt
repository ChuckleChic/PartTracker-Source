//package com.example.parttracker.ui
//
//import android.os.Bundle
//import android.view.*
//import android.widget.Button
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import com.example.parttracker.R
//
//class LocationSelectorFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return inflater.inflate(R.layout.fragment_location_selector, container, false)
//    }
//
////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        super.onViewCreated(view, savedInstanceState)
////
////        view.findViewById<Button>(R.id.btnPaintshop).setOnClickListener {
////            val action = LocationSelectorFragmentDirections
////                .actionLocationSelectorFragmentToScanFragment(location = "Paintshop")
////            findNavController().navigate(action)
////        }
////
////        view.findViewById<Button>(R.id.btnCTL).setOnClickListener {
////            val action = LocationSelectorFragmentDirections
////                .actionLocationSelectorFragmentToScanFragment(location = "CTL")
////            findNavController().navigate(action)
////        }
////    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val sharedPref = requireContext().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
//        val role = sharedPref.getString("userRole", "") ?: ""
//
//        val btnPaintshop = view.findViewById<Button>(R.id.btnPaintshop)
//        val btnCTL = view.findViewById<Button>(R.id.btnCTL)
//
//        // Hide both by default
//        btnPaintshop.visibility = View.GONE
//        btnCTL.visibility = View.GONE
//
//        // Show based on role
//        when (role) {
//            "PaintShopOperator" -> btnPaintshop.visibility = View.VISIBLE
//            "VehicleAssemblyOperator" -> btnCTL.visibility = View.VISIBLE
//            "Admin" -> {
//                btnPaintshop.visibility = View.VISIBLE
//                btnCTL.visibility = View.VISIBLE
//            }
//        }
//
//        // Navigate if visible
//        btnPaintshop.setOnClickListener {
//            val action = LocationSelectorFragmentDirections
//                .actionLocationSelectorFragmentToScanFragment(location = "Paintshop")
//            findNavController().navigate(action)
//        }
//
//        btnCTL.setOnClickListener {
//            val action = LocationSelectorFragmentDirections
//                .actionLocationSelectorFragmentToScanFragment(location = "CTL")
//            findNavController().navigate(action)
//        }
//    }
//
//}

package com.example.parttracker.ui

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.parttracker.R

class LocationSelectorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_location_selector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ Fixed: Use "Paint Shop" to match ScanFragment logic
        view.findViewById<Button>(R.id.btnPaintshop).setOnClickListener {
            val action = LocationSelectorFragmentDirections
                .actionLocationSelectorFragmentToScanFragment(location = "Paint Shop")
            findNavController().navigate(action)
        }

        view.findViewById<Button>(R.id.btnCTL).setOnClickListener {
            val action = LocationSelectorFragmentDirections
                .actionLocationSelectorFragmentToScanFragment(location = "CTL")
            findNavController().navigate(action)
        }
    }
}
