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
import com.example.baksomanagementadmin.data.model.Menu
import com.example.baksomanagementadmin.data.repository.MenuRepository
import android.util.Log
import com.bumptech.glide.Glide
import java.io.File

class EditMenuFragment : Fragment() {

    private lateinit var menuRepository: MenuRepository

    private var menuId: String = ""
    private var imageUri: Uri? = null
    private var oldImageUrl: String = ""

    private lateinit var etNamaMenu: EditText
    private lateinit var etHarga: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnPickImage: FrameLayout
    private lateinit var imgPreview: ImageView
    private lateinit var layoutPlaceholder: LinearLayout

    private val TAG = "EditMenuDebug"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuId = arguments?.getString("MENU_ID") ?: ""
        Log.d(TAG, "Menu ID: $menuId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        menuRepository = MenuRepository()

        etNamaMenu = view.findViewById(R.id.etNamaMenu)
        etHarga = view.findViewById(R.id.etHarga)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        btnPickImage = view.findViewById(R.id.btnPickImage)
        imgPreview = view.findViewById(R.id.imgPreview)
        layoutPlaceholder = view.findViewById(R.id.layoutPlaceholder)

        loadMenuData()

        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnSubmit.setOnClickListener {
            updateMenu()
        }
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
            if (uri != null) {
                imgPreview.setImageURI(uri)
                Log.d(TAG, "Image selected: $uri")
            } else {
                Log.d(TAG, "Image selection cancelled")
            }
            Toast.makeText(requireContext(), "Gambar dipilih", Toast.LENGTH_SHORT).show()
        }

    private fun loadMenuData() {
        menuRepository.getMenuById(menuId) { menu ->
            menu?.let {
                Log.d(TAG, "Menu loaded: ${it.namaMenu}, Harga: ${it.harga}")

                etNamaMenu.setText(it.namaMenu)
                etHarga.setText(it.harga.toString())

                oldImageUrl = it.gambarUrl

                if (oldImageUrl.isNotEmpty()) {
                    Glide.with(requireContext())
                        .load(oldImageUrl)
                        .into(imgPreview)
                    layoutPlaceholder.visibility = View.GONE
                }
            }
        }
    }

    private fun updateMenu() {
        Log.d(TAG, "Update button clicked")

        val namaMenu = etNamaMenu.text.toString()
        val harga = etHarga.text.toString().toIntOrNull() ?: 0

        if (namaMenu.isEmpty()) {
            Toast.makeText(requireContext(), "Nama menu wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri != null) {
            uploadImageToCloudinary(imageUri!!,
                onSuccess = { newUrl ->
                    updateToFirestore(namaMenu, harga, newUrl)
                },
                onError = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            updateToFirestore(namaMenu, harga, oldImageUrl)
        }
    }

    private fun updateToFirestore(
        namaMenu: String,
        harga: Int,
        imageUrl: String
    ) {
        val updatedMenu = Menu(
            id = menuId,
            namaMenu = namaMenu,
            harga = harga,
            gambarUrl = imageUrl
        )

        menuRepository.updateMenu(updatedMenu,
            onSuccess = {
                Toast.makeText(requireContext(), "Menu berhasil diupdate", Toast.LENGTH_SHORT).show()
            },
            onError = {
                Toast.makeText(requireContext(), "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        )
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