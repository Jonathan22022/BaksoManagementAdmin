package com.example.baksomanagementadmin.ui

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.AddOn
import com.example.baksomanagementadmin.data.repository.AddOnRepository
import java.io.File

class TambahAddOnFragment : Fragment() {

    private lateinit var btnPickImage: FrameLayout
    private lateinit var imgPreview: ImageView
    private lateinit var layoutPlaceholder: LinearLayout
    private lateinit var repository: AddOnRepository

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tambah_add_on, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val etNama = view.findViewById<EditText>(R.id.etNamaAddOn)
        val etHarga = view.findViewById<EditText>(R.id.etHargaAddOn)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)

        btnPickImage = view.findViewById(R.id.btnPickImage)
        imgPreview = view.findViewById(R.id.imgPreview)
        layoutPlaceholder = view.findViewById(R.id.layoutPlaceholder)

        repository = AddOnRepository()

        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnSubmit.setOnClickListener {

            val nama = etNama.text.toString()
            val harga = etHarga.text.toString().toIntOrNull() ?: 0

            if (nama.isEmpty()) {
                toast("Nama wajib diisi")
                return@setOnClickListener
            }

            if (harga <= 0) {
                toast("Harga harus diisi")
                return@setOnClickListener
            }

            if (imageUri == null) {
                toast("Pilih gambar dulu")
                return@setOnClickListener
            }

            uploadImageToCloudinary(imageUri!!,
                onSuccess = { url ->

                    val data = AddOn(
                        name = nama,
                        price = harga,
                        gambarUrl = url
                    )

                    repository.addAddOn(
                        data,
                        onSuccess = { toast("Berhasil tambah") },
                        onError = { toast(it.message ?: "Error") }
                    )
                },
                onError = { toast(it) }
            )
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    // picker
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
            if (uri != null) {
                imgPreview.setImageURI(uri)
                layoutPlaceholder.visibility = View.GONE
            }
        }

    private fun uriToFile(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "upload.jpg")

        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
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
                override fun onStart(requestId: String?) {}

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val url = resultData?.get("secure_url").toString()
                    onSuccess(url)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onError(error?.description ?: "Upload gagal")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }
}