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

    companion object {
        private const val TAG = "AddMenuDebug"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "onCreateView called")

        return inflater.inflate(
            R.layout.fragment_add_menu,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated called")

        btnPickImage = view.findViewById(R.id.btnPickImage)
        imgPreview = view.findViewById(R.id.imgPreview)
        layoutPlaceholder = view.findViewById(R.id.layoutPlaceholder)

        menuRepository = MenuRepository()
        bahanRepository = BahanBakuRepository()

        rvBahan = view.findViewById(R.id.rvBahan)
        rvBahan.layoutManager = LinearLayoutManager(requireContext())

        Log.d(TAG, "RecyclerView initialized")

        loadBahanBaku()

        btnPickImage.setOnClickListener {
            Log.d(TAG, "Pick Image Button Clicked")
            pickImageLauncher.launch("image/*")
        }

        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {

            Log.d(TAG, "================================")
            Log.d(TAG, "Submit Button Clicked")
            Log.d(TAG, "================================")

            val namaMenu =
                view.findViewById<EditText>(R.id.etNamaMenu)
                    .text.toString()

            val description =
                view.findViewById<EditText>(R.id.etDescription)
                    .text.toString()

            val harga =
                view.findViewById<EditText>(R.id.etHarga)
                    .text.toString()
                    .toIntOrNull() ?: 0

            val bahanList =
                bahanAdapter.getSelectedBahan()

            Log.d(TAG, "Nama Menu : $namaMenu")
            Log.d(TAG, "Description : $description")
            Log.d(TAG, "Harga : $harga")
            Log.d(TAG, "Image Uri : $imageUri")
            Log.d(TAG, "Jumlah Bahan Dipilih : ${bahanList.size}")

            if (namaMenu.isEmpty()) {

                Log.e(TAG, "VALIDATION FAILED -> Nama menu kosong")

                Toast.makeText(
                    requireContext(),
                    "Nama menu wajib diisi",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (imageUri == null) {

                Log.e(TAG, "VALIDATION FAILED -> Gambar belum dipilih")

                Toast.makeText(
                    requireContext(),
                    "Pilih gambar dulu!",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            Log.d(TAG, "Validation passed")
            Log.d(TAG, "Starting Cloudinary Upload")

            uploadImageToCloudinary(
                imageUri!!,
                onSuccess = { imageUrl ->

                    Log.d(TAG, "Cloudinary URL Received")
                    Log.d(TAG, "Image URL = $imageUrl")

                    val menu = Menu(
                        namaMenu = namaMenu,
                        harga = harga,
                        gambarUrl = imageUrl,
                        description = description,
                        bahanList = bahanList
                    )

                    Log.d(TAG, "Menu Object Created")
                    Log.d(TAG, menu.toString())

                    Log.d(TAG, "Saving Menu To Firestore")

                    menuRepository.addMenu(
                        menu,
                        onSuccess = {

                            Log.d(
                                TAG,
                                "Firestore SUCCESS -> Menu berhasil disimpan"
                            )

                            Toast.makeText(
                                requireContext(),
                                "Menu berhasil ditambahkan",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onError = { exception ->

                            Log.e(
                                TAG,
                                "Firestore ERROR",
                                exception
                            )

                            Toast.makeText(
                                requireContext(),
                                "Firestore gagal: ${exception.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                },
                onError = { error ->

                    Log.e(
                        TAG,
                        "Cloudinary Upload Failed : $error"
                    )

                    Toast.makeText(
                        requireContext(),
                        error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    private fun loadBahanBaku() {

        Log.d(TAG, "Loading Bahan Baku")

        bahanRepository.getBahanBakuList { list ->

            Log.d(
                TAG,
                "Bahan Baku Loaded Successfully"
            )

            Log.d(
                TAG,
                "Total Bahan Baku = ${list.size}"
            )

            list.forEachIndexed { index, bahan ->

                Log.d(
                    TAG,
                    "Bahan[$index] => ${bahan.nama}"
                )
            }

            bahanAdapter = BahanMenuAdapter(list)

            rvBahan.adapter = bahanAdapter

            Log.d(TAG, "RecyclerView Adapter Set")
        }
    }

    private val pickImageLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            imageUri = uri

            if (uri != null) {

                Log.d(TAG, "Image Selected")
                Log.d(TAG, "Uri = $uri")

                imgPreview.setImageURI(uri)
                layoutPlaceholder.visibility = View.GONE

            } else {

                Log.e(TAG, "Image Selection Cancelled")
            }
        }

    private fun uriToFile(uri: Uri): File {

        Log.d(TAG, "Converting Uri To File")
        Log.d(TAG, "Uri = $uri")

        val inputStream =
            requireContext().contentResolver
                .openInputStream(uri)

        val file =
            File(
                requireContext().cacheDir,
                "upload_image.jpg"
            )

        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }

        Log.d(TAG, "File Created")
        Log.d(TAG, "Path = ${file.absolutePath}")
        Log.d(TAG, "Size = ${file.length()} bytes")

        return file
    }

    private fun uploadImageToCloudinary(
        uri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {

        Log.d(TAG, "uploadImageToCloudinary() called")

        val file = uriToFile(uri)

        MediaManager.get()
            .upload(file.path)
            .callback(object : UploadCallback {

                override fun onStart(requestId: String?) {

                    Log.d(
                        TAG,
                        "Cloudinary Upload Started"
                    )

                    Log.d(
                        TAG,
                        "RequestId = $requestId"
                    )
                }

                override fun onProgress(
                    requestId: String?,
                    bytes: Long,
                    totalBytes: Long
                ) {

                    val percent =
                        ((bytes.toDouble() / totalBytes.toDouble()) * 100).toInt()

                    Log.d(
                        TAG,
                        "Uploading... $percent%"
                    )
                }

                override fun onSuccess(
                    requestId: String?,
                    resultData: MutableMap<Any?, Any?>?
                ) {

                    Log.d(
                        TAG,
                        "Cloudinary SUCCESS"
                    )

                    Log.d(
                        TAG,
                        "Result Data = $resultData"
                    )

                    val url =
                        resultData?.get("secure_url").toString()

                    Log.d(
                        TAG,
                        "Secure URL = $url"
                    )

                    onSuccess(url)
                }

                override fun onError(
                    requestId: String?,
                    error: ErrorInfo?
                ) {

                    Log.e(
                        TAG,
                        "Cloudinary ERROR"
                    )

                    Log.e(
                        TAG,
                        "Description = ${error?.description}"
                    )

                    Log.e(
                        TAG,
                        "Code = ${error?.code}"
                    )

                    onError(
                        error?.description ?: "Upload gagal"
                    )
                }

                override fun onReschedule(
                    requestId: String?,
                    error: ErrorInfo?
                ) {

                    Log.w(
                        TAG,
                        "Upload Rescheduled"
                    )

                    Log.w(
                        TAG,
                        "Reason = ${error?.description}"
                    )
                }
            })
            .dispatch()
    }
}