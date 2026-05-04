package com.example.baksomanagementadmin.ui

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.AddOn
import com.example.baksomanagementadmin.data.repository.AddOnRepository
import java.io.File

class EditAddOnFragment : Fragment() {

    private lateinit var repository: AddOnRepository

    private lateinit var etNama: EditText
    private lateinit var etHarga: EditText
    private lateinit var imgPreview: ImageView
    private lateinit var layoutPlaceholder: LinearLayout
    private lateinit var btnPickImage: FrameLayout
    private lateinit var btnSubmit: Button

    private var imageUri: Uri? = null
    private var currentImageUrl: String = ""
    private var addOnId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_edit_add_on, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        repository = AddOnRepository()
        addOnId = arguments?.getString("ADDON_ID") ?: ""

        // bind view
        etNama = view.findViewById(R.id.etNamaAddOn)
        etHarga = view.findViewById(R.id.etHargaAddOn)
        imgPreview = view.findViewById(R.id.imgPreview)
        layoutPlaceholder = view.findViewById(R.id.layoutPlaceholder)
        btnPickImage = view.findViewById(R.id.btnPickImage)
        btnSubmit = view.findViewById(R.id.btnSubmit)

        btnSubmit.text = "Update Add On"

        loadData()

        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnSubmit.setOnClickListener {
            updateData()
        }
    }

    // 🔥 Load data lama
    private fun loadData() {
        repository.getAddOnList { list ->
            val data = list.find { it.id == addOnId }

            if (data != null) {
                etNama.setText(data.name)
                etHarga.setText(data.price.toString())

                currentImageUrl = data.gambarUrl

                Glide.with(requireContext())
                    .load(data.gambarUrl)
                    .into(imgPreview)

                layoutPlaceholder.visibility = View.GONE
            }
        }
    }

    // 🔥 Update logic
    private fun updateData() {

        val nama = etNama.text.toString()
        val harga = etHarga.text.toString().toIntOrNull() ?: 0

        if (nama.isEmpty()) {
            toast("Nama wajib diisi")
            return
        }

        if (harga <= 0) {
            toast("Harga wajib diisi")
            return
        }

        if (imageUri != null) {
            uploadImageToCloudinary(imageUri!!,
                onSuccess = { url ->
                    saveToFirestore(nama, harga, url)
                },
                onError = { toast(it) }
            )
        } else {
            saveToFirestore(nama, harga, currentImageUrl)
        }
    }

    private fun saveToFirestore(nama: String, harga: Int, imageUrl: String) {

        val updated = AddOn(
            id = addOnId,
            name = nama,
            price = harga,
            gambarUrl = imageUrl
        )

        repository.updateAddOn(
            updated,
            onSuccess = { toast("Berhasil update") },
            onError = { toast(it.message ?: "Error") }
        )
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