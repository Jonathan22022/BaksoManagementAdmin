package com.example.baksomanagementadmin.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var bahanBakuRepository: BahanBakuRepository

    private var imageUri: Uri? = null

    companion object {
        private const val TAG = "AddBahanBakuDebug"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "onCreateView() dipanggil")

        return inflater.inflate(
            R.layout.fragment_add_bahan_baku,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        Log.d(TAG, "onViewCreated() dipanggil")

        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        btnPickImage = view.findViewById(R.id.btnPickImage)
        imgPreview = view.findViewById(R.id.imgPreview)
        layoutPlaceholder = view.findViewById(R.id.layoutPlaceholder)
        val etNama = view.findViewById<EditText>(R.id.etNamaMenu)
        val etHarga = view.findViewById<EditText>(R.id.etHarga)
        val etBerat = view.findViewById<EditText>(R.id.etBerat)
        val spSatuan = view.findViewById<Spinner>(R.id.spSatuan)

        val satuanList = listOf(
            "kg",
            "gram",
            "liter"
        )

        Log.d(TAG, "Satuan tersedia: $satuanList")

        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            satuanList
        ) {

            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {

                val view = super.getView(
                    position,
                    convertView,
                    parent
                )

                (view as TextView).setTextColor(
                    resources.getColor(
                        R.color.setting_text,
                        null
                    )
                )

                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {

                val view = super.getDropDownView(
                    position,
                    convertView,
                    parent
                )

                (view as TextView).setTextColor(
                    resources.getColor(
                        R.color.setting_text,
                        null
                    )
                )

                view.setBackgroundColor(
                    resources.getColor(
                        R.color.setting_card,
                        null
                    )
                )

                return view
            }
        }

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spSatuan.adapter = adapter

        bahanBakuRepository = BahanBakuRepository()

        Log.d(TAG, "Repository berhasil dibuat")

        btnPickImage.setOnClickListener {

            Log.d(TAG, "Pick Image ditekan")

            imagePickerLauncher.launch("image/*")
        }

        btnSubmit.setOnClickListener {

            Log.d(TAG, "Tombol Submit ditekan")

            val nama = etNama.text.toString().trim()
            val harga = etHarga.text.toString().toIntOrNull() ?: 0
            val berat = etBerat.text.toString().toDoubleOrNull() ?: 0.0
            val satuan = spSatuan.selectedItem.toString()

            Log.d(
                TAG,
                """
                Data Input:
                nama=$nama
                harga=$harga
                berat=$berat
                satuan=$satuan
                imageUri=$imageUri
                """.trimIndent()
            )

            if (nama.isEmpty()) {

                Log.w(TAG, "Nama kosong")

                Toast.makeText(
                    requireContext(),
                    "Nama wajib diisi",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (harga <= 0) {

                Log.w(TAG, "Harga tidak valid: $harga")

                Toast.makeText(
                    requireContext(),
                    "Harga harus diisi",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (berat <= 0) {

                Log.w(TAG, "Berat tidak valid: $berat")

                Toast.makeText(
                    requireContext(),
                    "Berat harus diisi",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (imageUri == null) {

                Log.w(TAG, "Image URI masih null")

                Toast.makeText(
                    requireContext(),
                    "Pilih gambar dulu",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            Log.d(TAG, "Mulai upload gambar ke Cloudinary")

            uploadImageToCloudinary(
                imageUri!!,
                onSuccess = { url ->

                    Log.d(TAG, "Cloudinary URL: $url")

                    val data = BahanBaku(
                        nama = nama,
                        hargaAwal = harga,
                        beratAwal = berat,

                        hargaTerbaru = harga,
                        berat = berat,

                        satuan = satuan,
                        gambarUrl = url
                    )

                    Log.d(TAG, "Object BahanBaku dibuat")
                    Log.d(TAG, data.toString())

                    bahanBakuRepository.addBahanBaku(
                        data,
                        onSuccess = {

                            Log.d(TAG, "Data berhasil disimpan ke Firestore")

                            Toast.makeText(
                                requireContext(),
                                "Berhasil tambah",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onError = {

                            Log.e(
                                TAG,
                                "Gagal simpan Firestore: ${it.message}"
                            )

                            Toast.makeText(
                                requireContext(),
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                },
                onError = {

                    Log.e(TAG, "Upload Cloudinary gagal: $it")

                    Toast.makeText(
                        requireContext(),
                        it,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    private fun uriToFile(uri: Uri): File {

        Log.d(TAG, "Mengubah URI ke File")
        Log.d(TAG, "URI: $uri")

        val inputStream =
            requireContext().contentResolver.openInputStream(uri)

        val file = File(
            requireContext().cacheDir,
            "upload_image.jpg"
        )

        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }

        Log.d(TAG, "File berhasil dibuat: ${file.absolutePath}")
        Log.d(TAG, "Ukuran file: ${file.length()} bytes")

        return file
    }

    private fun uploadImageToCloudinary(
        uri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {

        Log.d(TAG, "uploadImageToCloudinary() dipanggil")

        val file = uriToFile(uri)

        MediaManager.get()
            .upload(file.path)
            .callback(object : UploadCallback {

                override fun onStart(requestId: String?) {

                    Log.d(
                        TAG,
                        "Upload dimulai. RequestId=$requestId"
                    )
                }

                override fun onProgress(
                    requestId: String?,
                    bytes: Long,
                    totalBytes: Long
                ) {

                    Log.d(
                        TAG,
                        "Progress Upload: $bytes / $totalBytes"
                    )
                }

                override fun onSuccess(
                    requestId: String?,
                    resultData: MutableMap<Any?, Any?>?
                ) {

                    Log.d(TAG, "Upload Cloudinary sukses")
                    Log.d(TAG, "Result Data = $resultData")

                    val url =
                        resultData?.get("secure_url").toString()

                    Log.d(TAG, "Secure URL = $url")

                    onSuccess(url)
                }

                override fun onError(
                    requestId: String?,
                    error: ErrorInfo?
                ) {

                    Log.e(
                        TAG,
                        """
                        Upload gagal
                        RequestId=$requestId
                        Description=${error?.description}
                        Code=${error?.code}
                        """.trimIndent()
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
                        """
                        Upload di-reschedule
                        RequestId=$requestId
                        Error=${error?.description}
                        """.trimIndent()
                    )
                }
            })
            .dispatch()
    }

    private val imagePickerLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            if (uri != null) {

                Log.d(TAG, "Gambar dipilih: $uri")

                imageUri = uri

                imgPreview.setImageURI(uri)
                imgPreview.visibility = View.VISIBLE

                layoutPlaceholder.visibility = View.GONE

            } else {

                Log.w(TAG, "User batal memilih gambar")
            }
        }
}