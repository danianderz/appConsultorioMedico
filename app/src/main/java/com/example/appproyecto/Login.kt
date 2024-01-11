package com.example.appproyecto

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.appproyecto.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPassword.text.toString()
            if (TextUtils.isEmpty(email)) {
                binding.txtEmail.error = "Ingrese su email"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                binding.txtPassword.error = "Ingrese su contraseña"
                return@setOnClickListener
            }
            logear(email, password)
        }
    }

    private fun logear(email: String, password: String) {
        val url = "http://10.79.7.33/consultorio/loginCliente.php"
        val params = HashMap<String, String>()
        params["email_cli"] = email
        params["passw_cli"] = password

        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.length() > 0) {
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this, "Error en la petición", Toast.LENGTH_SHORT).show()
                // Maneja el error en la petición
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }
        queue.add(stringRequest)
    }
}