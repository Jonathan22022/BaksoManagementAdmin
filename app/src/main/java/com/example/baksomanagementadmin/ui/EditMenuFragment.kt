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
    private lateinit var layoutAddons: LinearLayout
    private lateinit var containerAddons: LinearLayout
    private lateinit var btnTambahAddon: Button
    private lateinit var rgAddons: RadioGroup
    private lateinit var imgPreview: ImageView
    private lateinit var layoutPlaceholder: LinearLayout
    private val TAG = "EditMenuDebug"

    private val addonViews = mutableListOf<View>()

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
        layoutAddons = view.findViewById(R.id.layoutAddons)
        containerAddons = view.findViewById(R.id.containerAddons)
        btnTambahAddon = view.findViewById(R.id.btnTambahAddon)
        layoutPlaceholder = view.findViewById(R.id.layoutPlaceholder)
        rgAddons = view.findViewById(R.id.rgAddons)
        loadMenuData()
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
                Log.d(TAG, "Image URL: ${it.gambarUrl}")
                Log.d(TAG, "Total addons: ${it.addons.size}")
                etNamaMenu.setText(it.namaMenu)
                etHarga.setText(it.harga.toString())

                oldImageUrl = it.gambarUrl
                if (oldImageUrl.isNotEmpty()) {
                    Glide.with(requireContext())
                        .load(oldImageUrl)
                        .into(imgPreview)
                    layoutPlaceholder.visibility = View.GONE
                }

                if (it.addons.isNotEmpty()) {
                    layoutAddons.visibility = View.VISIBLE
                    rgAddons.check(R.id.rbYa)

                    for (addon in it.addons) {
                        Log.d(TAG, "Addon: ${addon.name} - ${addon.price}")
                        addAddonView(addon.name, addon.price)
                    }
                }
            }
        }
    }
    private fun addAddonView(name: String = "", price: Int = 0) {
        Log.d(TAG, "Addon view added: $name - $price")
        Log.d(TAG, "Total addon views: ${addonViews.size}")
        val view = layoutInflater.inflate(R.layout.item_addon, containerAddons, false)

        val etName = view.findViewById<EditText>(R.id.etAddonName)
        val etPrice = view.findViewById<EditText>(R.id.etAddonPrice)
        val btnRemove = view.findViewById<Button>(R.id.btnRemove)

        etName.setText(name)
        etPrice.setText(if (price == 0) "" else price.toString())

        btnRemove.setOnClickListener {
            containerAddons.removeView(view)
            addonViews.remove(view)
        }

        addonViews.add(view)
        containerAddons.addView(view)
    }

    private fun updateMenu() {
        Log.d(TAG, "Update button clicked")

        val namaMenu = etNamaMenu.text.toString()
        val harga = etHarga.text.toString().toIntOrNull() ?: 0

        Log.d(TAG, "Nama: $namaMenu")
        Log.d(TAG, "Harga: $harga")
        Log.d(TAG, "ImageUri baru: $imageUri")
        Log.d(TAG, "Old Image URL: $oldImageUrl")

        if (namaMenu.isEmpty()) {
            Toast.makeText(requireContext(), "Nama menu wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val addonsList = mutableListOf<AddOn>()
        for (addonView in addonViews) {
            val name = addonView.findViewById<EditText>(R.id.etAddonName).text.toString()
            val price = addonView.findViewById<EditText>(R.id.etAddonPrice).text.toString().toIntOrNull() ?: 0

            if (name.isNotEmpty()) {
                addonsList.add(AddOn(name, price))
            }
        }

        Log.d(TAG, "Total addons input: ${addonsList.size}")

        for (addon in addonsList) {
            Log.d(TAG, "Addon -> ${addon.name} : ${addon.price}")
        }

        if (imageUri != null) {
            uploadImageToCloudinary(imageUri!!,
                onSuccess = { newUrl ->
                    updateToFirestore(namaMenu, harga, newUrl, addonsList)
                },
                onError = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            updateToFirestore(namaMenu, harga, oldImageUrl, addonsList)
        }
    }

    private fun updateToFirestore(
        namaMenu: String,
        harga: Int,
        imageUrl: String,
        addons: List<AddOn>
    ) {
        val updatedMenu = Menu(
            id = menuId,
            namaMenu = namaMenu,
            harga = harga,
            gambarUrl = imageUrl,
            bihun = false,
            mie = false,
            keduanya = false,
            addons = addons
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