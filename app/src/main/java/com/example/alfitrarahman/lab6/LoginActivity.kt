package com.example.alfitrarahman.lab6

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.face.FaceDetector
import kotlinx.android.synthetic.main.activity_home.*
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import org.jetbrains.anko.longToast


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class LoginActivity : AppCompatActivity() {
    private val RC_PICTURE_TAKEN = 1111
    private val RC_PERMISSIONS = 2222
    private lateinit var bitmap: Bitmap
    private val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if((ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), RC_PERMISSIONS)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        startCamera.setOnClickListener {
            startActivityForResult(takePicture, RC_PICTURE_TAKEN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === this.RC_PICTURE_TAKEN && resultCode === RESULT_OK) {
            bitmap = data!!.extras.get("data") as Bitmap
            val detector = FaceDetector.Builder(applicationContext).setTrackingEnabled(false)
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .setProminentFaceOnly(true).build()
            val safeDetector: Detector<Face> = SafeFaceDetector(detector)
            try {
                val frame = Frame.Builder().setBitmap(bitmap).build()
                val faces = safeDetector.detect(frame)
                val face = faces.valueAt(0)
                val smileResult = face.isSmilingProbability
                if (smileResult>0.4) {
                    val intent = Intent (this, MainActivity::class.java)
                    startActivity(intent)
                }else{
                    longToast("You are not smiling enough!")
                }
            }catch (e:RuntimeException){
                longToast("No Face Detected!")
                val reboot = Intent(this, LoginActivity::class.java)
                startActivity(reboot)
            }
        }
        if (requestCode === this.RC_PERMISSIONS && resultCode === RESULT_OK) {
            val reboot = Intent(this, LoginActivity::class.java)
            startActivity(reboot)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}
