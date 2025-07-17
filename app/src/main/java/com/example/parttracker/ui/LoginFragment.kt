// LoginFragment.kt
package com.example.parttracker.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.parttracker.R
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameField = view.findViewById(R.id.etUsername)
        passwordField = view.findViewById(R.id.etPassword)
        loginButton = view.findViewById(R.id.btnLogin)

        loginButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firestore.collection("users").document(username)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val storedPassword = document.getString("Password")
                        val role = document.getString("Role")

                        if (password == storedPassword && role != null) {
                            // ✅ Save role in SharedPreferences
//                            val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
//                            sharedPref.edit().putString("userRole", role).apply()

                            val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                            sharedPref.edit()
                                .putBoolean("isLoggedIn", true)
                                .putString("userRole", role)
                                .apply()


                            Toast.makeText(requireContext(), "Login successful as $role", Toast.LENGTH_SHORT).show()

                            // Navigate to Main Menu
                            findNavController().navigate(R.id.action_loginFragment_to_mainMenuFragment)
                        } else {
                            Toast.makeText(requireContext(), "Incorrect password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
