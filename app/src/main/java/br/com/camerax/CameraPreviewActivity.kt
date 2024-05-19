package br.com.camerax

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import br.com.camerax.databinding.ActivityCameraPreviewBinding
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraPreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraPreviewBinding
    // a pessoa que vai controlar a instancia de camera aberta, nao deixa abrir 5 vezes a instancia da camera
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    //selecionar se deseja camera frontal ou traseira
    private lateinit var cameraSelector: CameraSelector

    //imagem capturada
    private var imageCapture:ImageCapture? = null

    //executor de thread separado
    private lateinit var imgCaptureExecutor: ExecutorService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imgCaptureExecutor = Executors.newSingleThreadExecutor()

        //chamar o metodo startCamera
        startCamera()

        // evento click no bota para chamar o take foto
        binding.btnTakePhoto.setOnClickListener {
            takePhoto()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                blinkPreview()
            }
        }
    }

    private fun startCamera() {
        cameraProviderFuture.addListener({
            //objeto que cuida da imagem, builder controi o objeto
            imageCapture = ImageCapture.Builder().build()

            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            try {
                //abrir preview
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            } catch (e: Exception) {
                Log.e("CameraPreview", "Falha ao abrir a camera")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto(){
        imageCapture?.let {
            //nome do arquivo para gravar a foto
            val fileName = "FOTO_JPEG_${System.currentTimeMillis()}"
            val file = File(externalMediaDirs[0], fileName)

            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            it.takePicture(
                outputFileOptions,
                imgCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback{
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i("cameraPreview", "a imagem foi salva no diretorio: ${file.toUri()}")
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(binding.root.context, "Erro ao salvar foto", Toast.LENGTH_LONG).show()
                        Log.e("cameraPreview", "Excesao ao gravar arquivo: ${exception}")
                    }

                }
            )
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun blinkPreview () {
        binding.root.postDelayed({
            binding.root.foreground = ColorDrawable(Color.WHITE)
            binding.root.postDelayed({
                binding.root.foreground = null
            }, 58)

        }, 130)
    }
}