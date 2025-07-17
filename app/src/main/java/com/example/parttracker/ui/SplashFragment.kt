//package com.example.parttracker.ui
//
//import android.content.Context
//import android.os.Bundle
//import android.view.*
//import android.widget.ImageView
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import com.example.parttracker.R
//import android.view.animation.AlphaAnimation
//
//
//class SplashFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View = inflater.inflate(R.layout.fragment_splash, container, false)
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val splashLogo = view.findViewById<ImageView>(R.id.splashLogo)
//        splashLogo.animate()
//            .alpha(1f)
//            .setDuration(800)
//            .start()
//
//        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
//        val role = sharedPref.getString("userRole", null)
//
//        view.postDelayed({
//            if (role.isNullOrEmpty()) {
//                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
//            } else {
//                findNavController().navigate(R.id.action_splashFragment_to_mainMenuFragment)
//            }
//        }, 1500) // slightly more than animation duration
//    }
//
//}


package com.example.parttracker.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.parttracker.R

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logo = view.findViewById<ImageView>(R.id.splashLogo)
        val title = view.findViewById<TextView>(R.id.splashText)
        val tagline = view.findViewById<TextView>(R.id.splashTagline)

        // Define fade-in animation
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 800
            fillAfter = true
        }

        // Start animation for all three views
        logo.startAnimation(fadeIn)
        title.startAnimation(fadeIn)
        tagline.startAnimation(fadeIn)

        // Check user role and navigate after splash
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val role = sharedPref.getString("userRole", null)

        view.postDelayed({
            if (role.isNullOrEmpty()) {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_mainMenuFragment)
            }
        }, 1500) // Slightly more than animation duration
    }
}
