package com.example.baksomanagementadmin.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.BahanBaku
import com.example.baksomanagementadmin.data.repository.BahanBakuRepository
import java.io.File

class AddBahanBakuFragment : Fragment() {

    private lateinit var btnPickImage: FrameLayout
    private lateinit var imgPreview: ImageView
    private lateinit var layoutPlaceholder: LinearLayout
    private lateinit var BahanBakuRepository: BahanBakuRepository
    private var imageUri: Uri? = null
    private val TAG = "AddBahanBakuDebug"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_bahan_baku, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        val etNama = view.findViewById<EditText>(R.id.etNamaMenu)
        val etHarga = view.findViewById<EditText>(R.id.etHarga)
        val etBerat = view.findViewById<EditText>(R.id.etBerat)

        BahanBakuRepository = BahanBakuRepository()

        btnSubmit.setOnClickListener {

            val nama = etNama.text.toString()
            val harga = etHarga.text.toString().toIntOrNull() ?: 0
            val berat = etBerat.text.toString().toDoubleOrNull() ?: 0.0

            if (nama.isEmpty()) {
                Toast.makeText(requireContext(), "Nama wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (harga <= 0) {
                Toast.makeText(requireContext(), "Harga harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (berat <= 0) {
                Toast.makeText(requireContext(), "Berat harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageUri == null) {
                Toast.makeText(requireContext(), "Pilih gambar dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadImageToCloudinary(imageUri!!,
                onSuccess = { url ->

                    val data = BahanBaku(
                        nama = nama,
                        harga = harga,
                        berat = berat,
                        gambarUrl = url
                    )

                    BahanBakuRepository.addBahanBaku(
                        data,
                        onSuccess = {
                            Toast.makeText(requireContext(), "Berhasil tambah", Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onError = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "upload_image.jpg")

        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }

        return file
    }

    private fun uploadImageToCloudinary(
        uri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val file = uriToFile(uri)

        MediaManager.get().upload(file.path)
            .callback(object : UploadCallback {

                override fun onStart(requestId: String?) {
                    Log.d(TAG, "Upload started")
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    Log.d(TAG, "Uploading: $bytes / $totalBytes")
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val url = resultData?.get("secure_url").toString()
                    Log.d(TAG, "Upload SUCCESS: $url")
                    onSuccess(url)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.e(TAG, "Upload ERROR: ${error?.description}")
                    onError(error?.description ?: "Upload gagal")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }
}