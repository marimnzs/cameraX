package br.com.camerax

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import br.com.camerax.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //inflar a activity, atributo que vai rechear o layout com o que vc criou
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOpenCamera.setOnClickListener {

            //solicitar permissao
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }
    }

    private val cameraProviderResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                abrirTelaDePreview()
            } else {

            }
        }
    private fun abrirTelaDePreview(){
        val intentCameraPreview = Intent(this, CameraPreviewActivity::class.java)
        startActivity(intentCameraPreview)
    }
}