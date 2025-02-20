package com.skysoftsolution.basictoadavance.singnatureview
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.databinding.ActivitySignatureViewBinding
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.app.Dialog
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.SeekBar


class SignatureViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignatureViewBinding
    private var currentColor: Int = Color.BLACK // Default color

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignatureViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Clear Signature
        binding.clearButton.setOnClickListener {
            binding.signatureView.clearSignature()
        }

        // Save Signature
        binding.saveButton.setOnClickListener {
            val bitmap = binding.signatureView.getSignatureBitmap()
            saveBitmapToFile(bitmap)
        }

        // Open Custom Color Picker
        binding.colorPickerButton.setOnClickListener {
            showColorPickerDialog()
        }
    }

    private fun showColorPickerDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_color_picker)
        dialog.setTitle("Pick a Color")

        val preview = dialog.findViewById<View>(R.id.color_preview)
        val seekBarRed = dialog.findViewById<SeekBar>(R.id.seekbar_red)
        val seekBarGreen = dialog.findViewById<SeekBar>(R.id.seekbar_green)
        val seekBarBlue = dialog.findViewById<SeekBar>(R.id.seekbar_blue)
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)
        val btnOk = dialog.findViewById<Button>(R.id.btn_ok)

        // Extract RGB values from the current color
        val red = Color.red(currentColor)
        val green = Color.green(currentColor)
        val blue = Color.blue(currentColor)

        // Set initial seekbar values
        seekBarRed.progress = red
        seekBarGreen.progress = green
        seekBarBlue.progress = blue

        // Update preview color
        fun updatePreviewColor() {
            val color = Color.rgb(seekBarRed.progress, seekBarGreen.progress, seekBarBlue.progress)
            preview.setBackgroundColor(color)
        }
        updatePreviewColor()

        // SeekBar listeners
        seekBarRed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updatePreviewColor()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        seekBarGreen.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updatePreviewColor()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        seekBarBlue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updatePreviewColor()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Cancel button
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        // OK button
        btnOk.setOnClickListener {
            currentColor = Color.rgb(seekBarRed.progress, seekBarGreen.progress, seekBarBlue.progress)
            binding.signatureView.setSignatureColor(currentColor)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveBitmapToFile(bitmap: Bitmap) {
        // Save bitmap logic remains the same
    }
}

