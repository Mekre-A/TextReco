package com.quantum.textreco

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector

class MainActivity : AppCompatActivity() {



    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var imageBitmap:Bitmap
    var newImage:ImageView? = null


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    private fun detectTextFromImage(){
        var firebaseVisionImage:FirebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap)
        var firebaseVisionImageDetector:FirebaseVisionTextDetector = FirebaseVision.getInstance().visionTextDetector
        firebaseVisionImageDetector.detectInImage(firebaseVisionImage).addOnSuccessListener {

            displayTextFromImage(it)

        }.addOnFailureListener {

        }
    }

    private fun displayTextFromImage(firebaseVisionText: FirebaseVisionText) {
        var block:List<FirebaseVisionText.Block> = firebaseVisionText.blocks
        if(block.isNotEmpty() ){
            var newStrings:String = ""
            block.forEach {
                newStrings += it.text
                newStrings += "\n"
            }
            val intent = Intent(this, MainActivity2::class.java).apply {
                putExtra(EXTRA_MESSAGE, newStrings)
            }
            startActivity(intent)

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val captureImageBtn:Button = findViewById(R.id.captureImage)
        val detectTextBtn:Button = findViewById(R.id.detectText)
        newImage = findViewById(R.id.newImage)

        captureImageBtn.setOnClickListener {
            dispatchTakePictureIntent()
        }
        detectTextBtn.setOnClickListener {
            detectTextFromImage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageBitmap = data?.extras?.get("data") as Bitmap
            newImage?.setImageBitmap(imageBitmap)
        }
    }
}