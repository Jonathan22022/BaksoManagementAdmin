package com.example.baksomanagementadmin.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.BahanBaku
import com.example.baksomanagementadmin.data.repository.BahanBakuRepository
import java.io.File
import android.widget.ArrayAdapter
import android.widget.Spinner

class EditBahanBakuFragment : Fragment() {

    private lateinit var repository: BahanBakuRepository

    private lateinit var etNama: EditText
    private lateinit var etHarga: EditText
    private lateinit var etBerat: EditText
    private lateinit var imgPreview: ImageView
    private lateinit var layoutPlaceholder: LinearLayout
    private lateinit var btnPickImage: FrameLayout
    private lateinit var btnSubmit: Button
    private lateinit var spSatuan: Spinner
    private val TAG = "EditBahanBakuDebug"
    private val satuanList = listOf("kg", "liter")
    private var imageUri: Uri? = null
    private var currentImageUrl: String = ""
    private var bahanId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_bahan_baku, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        repository = BahanBakuRepository()
        bahanId = arguments?.getString("BAHAN_ID") ?: ""
        etNama = view.findViewById(R.id.etNamaMenu)
        etHarga = view.findViewById(R.id.etHarga)
        etBerat = view.findViewById(R.id.etBerat)
        imgPreview = view.findViewById(R.id.imgPreview)
        layoutPlaceholder = view.findViewById(R.id.layoutPlaceholder)
        btnPickImage = view.findViewById(R.id.btnPickImage)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        spSatuan = view.findViewById(R.id.spSatuan)
        val satuanList = listOf("kg", "liter")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            satuanList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSatuan.adapter = adapter
        btnSubmit.text = "Update Bahan Baku"

        loadData()

        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnSubmit.setOnClickListener {
            updateData()
        }
    }

    private fun loadData() {
        repository.getBahanBakuList { list ->
            val data = list.find { it.id == bahanId }

            if (data != null) {
                etNama.setText(data.nama)
                etHarga.setText(data.harga.toString())
                etBerat.setText(data.berat.toString())

                currentImageUrl = data.gambarUrl

                Glide.with(requireContext())
                    .load(data.gambarUrl)
                    .into(imgPreview)
                val posisi = satuanList.indexOf(data.satuan)

                if (posisi >= 0) {
                    spSatuan.setSelection(posisi)
                }
                layoutPlaceholder.visibility = View.GONE
            }
        }
    }

    // 🔥 Update data
    private fun updateData() {

        val nama = etNama.text.toString()
        val harga = etHarga.text.toString().toIntOrNull() ?: 0
        val berat = etBerat.text.toString().toDoubleOrNull() ?: 0.0
        val satuan = spSatuan.selectedItem.toString()
        if (nama.isEmpty()) {
            toast("Nama wajib diisi")
            return
        }

        if (imageUri != null) {
            // upload gambar baru
            uploadImageToCloudinary(imageUri!!,
                onSuccess = { url ->
                    saveToFirestore(nama, harga, berat,satuan, url)
                },
                onError = { toast(it) }
            )
        } else {
            // pakai gambar lama
            saveToFirestore(nama, harga, berat, satuan,currentImageUrl)
        }
    }

    private fun saveToFirestore(
        nama: String,
        harga: Int,
        berat: Double,
        satuan: String,
        imageUrl: String
    ) {
        val updated = BahanBaku(
            id = bahanId,
            nama = nama,
            harga = harga,
            berat = berat,
            satuan = satuan,
            gambarUrl = imageUrl
        )

        repository.updateBahanBaku(
            updated,
            onSuccess = { toast("Berhasil update") },
            onError = { toast(it.message ?: "Error") }
        )
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private val pickImageLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
            if (uri != null) {
                imgPreview.setImageURI(uri)
                layoutPlaceholder.visibility = View.GONE
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