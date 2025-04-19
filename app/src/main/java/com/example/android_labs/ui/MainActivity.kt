package com.example.android_labs.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.android_labs.R
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView
    private val REQUEST_IMAGE_PICK = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawingView)
        val brushSizeText = findViewById<TextView>(R.id.tvBrushSizeValue)
        val brushSizeSeekBar = findViewById<SeekBar>(R.id.seekBarBrushSize).apply {
            setOnSeekBarChangeListener(SeekBarListener {
                drawingView.setBrushSize(it.toFloat())
                brushSizeText.text = it.toString()
            })
        }

        brushSizeText.text = brushSizeSeekBar.progress.toString()

        val colorButtons = mapOf(
            R.id.btnBlack to Color.BLACK,
            R.id.btnBlue to Color.BLUE,
            R.id.btnRed to Color.RED,
            R.id.btnYellow to Color.YELLOW,
            R.id.btnGreen to Color.GREEN,
            R.id.btnViolet to Color.parseColor("#660099")
        )

        colorButtons.forEach { (id, color) ->
            findViewById<Button>(id).setOnClickListener { drawingView.setColor(color) }
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener { drawingView.clearAll() }
        findViewById<Button>(R.id.btnLoadImage).setOnClickListener { openGallery() }
        findViewById<Button>(R.id.btnSave).setOnClickListener { saveDrawing() }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
            startActivityForResult(it, REQUEST_IMAGE_PICK)
        }
    }

    private fun saveDrawing() {
        try {
            File(getExternalFilesDir(null), "drawing_${System.currentTimeMillis()}.png").also { file ->
                FileOutputStream(file).use { stream ->
                    drawingView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream)
                    Toast.makeText(this, "Saved to: ${file.absolutePath}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)?.let {
                        drawingView.setBackgroundBitmap(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Image load failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}