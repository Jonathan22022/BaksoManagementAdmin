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
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.AddOn
import com.example.baksomanagementadmin.data.model.Menu
import com.example.baksomanagementadmin.data.repository.MenuRepository
import java.io.File

class AddMenuFragment : Fragment() {

    private lateinit var layoutAddons: LinearLayout
    private lateinit var containerAddons: LinearLayout
    private lateinit var btnTambahAddon: Button
    private lateinit var rgAddons: RadioGroup
    private lateinit var btnPickImage: FrameLayout
    private lateinit var imgPreview: ImageView
    private lateinit var menuRepository: MenuRepository
    private lateinit var layoutPlaceholder: LinearLayout
    private val addonViews = mutableListOf<View>()
    private var imageUri: Uri? = null

    private val TAG = "AddMenuDebug"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        layoutAddons = view.findViewById(R.id.layoutAddons)
        containerAddons = view.findViewById(R.id.containerAddons)
        btnTambahAddon = view.findViewById(R.id.btnTambahAddon)
        rgAddons = view.findViewById(R.id.rgAddons)
        btnPickImage = view.findViewById(R.id.btnPickImage)
        imgPreview = view.findViewById(R.id.imgPreview)
        layoutPlaceholder = view.findViewById(R.id.layoutPlaceholder)
        menuRepository = MenuRepository()

        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        rgAddons.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbYa) {
                layoutAddons.visibility = View.VISIBLE
            } else {
                layoutAddons.visibility = View.GONE
                containerAddons.removeAllViews()
                addonViews.clear()
            }
        }

        btnTambahAddon.setOnClickListener {
            addAddonView()
        }

        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {

            val namaMenu = view.findViewById<EditText>(R.id.etNamaMenu).text.toString()
            val harga = view.findViewById<EditText>(R.id.etHarga).text.toString().toIntOrNull() ?: 0

            if (imageUri == null) {
                Toast.makeText(requireContext(), "Pilih gambar dulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadImageToCloudinary(imageUri!!,
                onSuccess = { imageUrl ->

                    val addonsList = mutableListOf<AddOn>()

                    for (addonView in addonViews) {
                        val name = addonView.findViewById<EditText>(R.id.etAddonName).text.toString()
                        val price = addonView.findViewById<EditText>(R.id.etAddonPrice).text.toString().toIntOrNull() ?: 0

                        if (name.isNotEmpty()) {
                            addonsList.add(AddOn(name, price))
                        }
                    }

                    val menu = Menu(
                        namaMenu = namaMenu,
                        harga = harga,
                        gambarUrl = imageUrl,
                        bihun = false,
                        mie = false,
                        keduanya = false,
                        addons = addonsList
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

    private fun addAddonView() {
        val view = layoutInflater.inflate(R.layout.item_addon, containerAddons, false)

        val btnRemove = view.findViewById<Button>(R.id.btnRemove)

        btnRemove.setOnClickListener {
            containerAddons.removeView(view)
            addonViews.remove(view)
        }

        addonViews.add(view)
        containerAddons.addView(view)
    }
}