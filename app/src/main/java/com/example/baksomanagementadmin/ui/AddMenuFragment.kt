package com.example.baksomanagementadmin.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.Menu
import com.example.baksomanagementadmin.data.repository.BahanBakuRepository
import com.example.baksomanagementadmin.data.repository.MenuRepository
import java.io.File

class AddMenuFragment : Fragment() {

    private lateinit var btnPickImage: FrameLayout
    private lateinit var imgPreview: ImageView
    private lateinit var layoutPlaceholder: LinearLayout
    private lateinit var menuRepository: MenuRepository
    private lateinit var rvBahan: RecyclerView
    private lateinit var bahanAdapter: BahanMenuAdapter
    private lateinit var bahanRepository: BahanBakuRepository

    private var imageUri: Uri? = null

    private val TAG = "AddMenuDebug"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btnPickImage = view.findViewById(R.id.btnPickImage)
        imgPreview = view.findViewById(R.id.imgPreview)
        layoutPlaceholder = view.findViewById(R.id.layoutPlaceholder)
        menuRepository = MenuRepository()
        rvBahan = view.findViewById(R.id.rvBahan)
        bahanRepository = BahanBakuRepository()
        rvBahan.layoutManager =
            LinearLayoutManager(requireContext())
        loadBahanBaku()
        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {

            val namaMenu = view.findViewById<EditText>(R.id.etNamaMenu).text.toString()
            val description = view.findViewById<EditText>(R.id.etDescription).text.toString()
            val bahanList =
                bahanAdapter.getSelectedBahan()
            val harga = view.findViewById<EditText>(R.id.etHarga).text.toString().toIntOrNull() ?: 0

            if (namaMenu.isEmpty()) {
                Toast.makeText(requireContext(), "Nama menu wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageUri == null) {
                Toast.makeText(requireContext(), "Pilih gambar dulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadImageToCloudinary(imageUri!!,
                onSuccess = { imageUrl ->

                    val menu = Menu(
                        namaMenu = namaMenu,
                        harga = harga,
                        gambarUrl = imageUrl,
                        description = description,
                        bahanList = bahanList
                    )

                    menuRepository.addMenu(menu,
                        onSuccess = {
                            Toast.makeText(requireContext(), "Menu berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                            Toast.makeText(requireContext(), "Firestore gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onError = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            )

            Log.d(TAG, "Submit clicked")
            Log.d(TAG, "Nama Menu: $namaMenu")
            Log.d(TAG, "Harga: $harga")
            Log.d(TAG, "ImageUri: $imageUri")
        }
    }

    private fun loadBahanBaku() {

        bahanRepository.getBahanBakuList { list ->

            bahanAdapter = BahanMenuAdapter(list)

            rvBahan.adapter = bahanAdapter
        }
    }
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
            if (uri != null) {
                imgPreview.setImageURI(uri)
                layoutPlaceholder.visibility = View.GONE
                Log.d(TAG, "Image selected: $uri")
            } else {
                Log.d(TAG, "Image selection cancelled")
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