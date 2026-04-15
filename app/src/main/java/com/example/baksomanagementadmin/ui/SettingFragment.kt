package com.example.baksomanagementadmin.ui

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.remote.FirebaseClient
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

class SettingFragment : Fragment() {

    private val TAG = "SettingFragment"
    private var imageUri: Uri? = null
    private var imgProfile: ImageView? = null
    private var cameraUri: Uri? = null
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseClient.firestore

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageUri = it
                imgProfile?.setImageURI(it)
                Log.d(TAG, "Image dipilih dari galeri: $uri")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /**setupNotificationSwitch(view)**/
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<View>(R.id.btnAccount).setOnClickListener {
            showEditProfileDialog()
        }

        view.findViewById<View>(R.id.btnNotification).setOnClickListener {
            /**setupNotificationSwitch()**/
        }

        view.findViewById<View>(R.id.btnLanguage).setOnClickListener {
            Toast.makeText(requireContext(), "Language Setting", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btnBackground).setOnClickListener {
            Toast.makeText(requireContext(), "Background Setting", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btnChangeAccount).setOnClickListener {
            showChangeAccountDialog()
        }

        view.findViewById<View>(R.id.btnDeleteAccount).setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    /**private fun setupNotificationSwitch(view: View) {

        val switchNotification =
            view.findViewById<Switch>(R.id.switchNotification)

        val sharedPref = requireActivity()
            .getSharedPreferences(
                "app_settings",
                AppCompatActivity.MODE_PRIVATE
            )

        switchNotification.isChecked =
            sharedPref.getBoolean(
                "global_notification",
                true
            )

        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Matikan Notifikasi?")
                    .setMessage(
                        "Pengingat tidak akan muncul."
                    )
                    .setPositiveButton("Ya") { _, _ ->
                        sharedPref.edit()
                            .putBoolean(
                                "global_notification",
                                false
                            )
                            .apply()
                        switchNotification.isChecked = false
                    }
                    .setNegativeButton("Batal") { _, _ ->
                        switchNotification.isChecked = true
                    }
                    .show()
            } else {
                sharedPref.edit()
                    .putBoolean(
                        "global_notification",
                        true
                    )
                    .apply()
            }
        }
    }**/

    private fun deleteAccount() {

        val user = auth.currentUser ?: return
        val uid = user.uid
        Log.d(TAG, "Menghapus akun UID: $uid")
        firestore.collection("users")
            .document(uid)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Data Firestore berhasil dihapus")
                user.delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Akun Auth berhasil dihapus")
                        Toast.makeText(
                            requireContext(),
                            "Akun berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()

                        goToFirstPage()
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "Gagal menghapus Auth user", it)
                        Toast.makeText(
                            requireContext(),
                            "Gagal menghapus akun",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
    }

    private fun goToFirstPage() {
        findNavController().navigate(R.id.firstPageFragment)
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Akun")
            .setMessage("Jika akun dihapus maka semua data akan hilang. Apakah Anda yakin?")
            .setPositiveButton("Ya") { _, _ ->
                deleteAccount()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showChangeAccountDialog() {
        Toast.makeText(requireContext(), "Change Account", Toast.LENGTH_SHORT).show()
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imageUri?.let {
                    imgProfile?.setImageURI(it)
                    Log.d(TAG, "Foto berhasil diambil dari kamera: $it")
                }
            }
        }

    private fun openCamera() {
        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.Images.Media.TITLE, "profile_picture")
            put(android.provider.MediaStore.Images.Media.DESCRIPTION, "From Camera")
        }

        cameraUri = requireContext().contentResolver.insert(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        imageUri = cameraUri
        takePictureLauncher.launch(cameraUri)
    }

    private fun showEditProfileDialog() {

        Log.d(TAG, "Membuka dialog edit profile")
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        imgProfile = dialogView.findViewById(R.id.imgProfileEdit)
        val etNama = dialogView.findViewById<EditText>(R.id.etNama)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)
        val etPhone = dialogView.findViewById<EditText>(R.id.etPhone)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        val uid = auth.currentUser?.uid ?: return

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.show()

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                Log.d(TAG, "Data user berhasil diambil")
                etNama.setText(doc.getString("nama"))
                etEmail.setText(doc.getString("email"))
                etPhone.setText(doc.getString("noTelp"))
                val imageUrl = doc.getString("profilePicture")

                Log.d(TAG, "Nama: $etNama")
                Log.d(TAG, "Email: $etEmail")
                Log.d(TAG, "Phone: $etPhone")
                Log.d(TAG, "Image URL: $imageUrl")

                if (!imageUrl.isNullOrEmpty()) {

                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_account_)
                        .circleCrop()
                        .into(imgProfile!!)
                }
            }

        imgProfile!!.setOnClickListener {

            val options = arrayOf("Kamera", "Galeri")

            AlertDialog.Builder(requireContext())
                .setTitle("Pilih Foto")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> {
                            Log.d(TAG, "Membuka Kamera")
                            openCamera()
                        }
                        1 -> {
                            Log.d(TAG, "Membuka Galeri")
                            pickImageLauncher.launch("image/*")
                        }
                    }
                }
                .show()
        }

        btnSave.setOnClickListener {

            val nama = etNama.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()

            Log.d(TAG, "Klik Save Profile")
            Log.d(TAG, "Nama: $nama")
            Log.d(TAG, "Email: $email")
            Log.d(TAG, "Phone: $phone")

            if (imageUri != null) {
                Log.d(TAG, "Upload gambar baru ke Cloudinary")
                uploadProfileImage(imageUri!!) { imageUrl ->
                    updateUser(uid, nama, email, phone, imageUrl)
                    dialog.dismiss()
                }

            } else {
                Log.d(TAG, "Tidak ada gambar baru")
                updateUser(uid, nama, email, phone, null)
                dialog.dismiss()
            }
        }
    }

    private fun uploadProfileImage(uri: Uri, onComplete: (String) -> Unit) {
        Log.d(TAG, "Mulai upload ke Cloudinary: $uri")
        MediaManager.get()
            .upload(uri)
            .option("folder", "profile_images")
            .callback(object : UploadCallback {

                override fun onStart(requestId: String?) {
                    Log.d(TAG, "Upload dimulai")
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    Log.d(TAG, "Upload progress: $bytes / $totalBytes")
                }

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val imageUrl = resultData?.get("secure_url").toString()
                    Log.d(TAG, "Upload berhasil. URL: $imageUrl")
                    onComplete(imageUrl)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.e(TAG, "Upload gagal: ${error?.description}")
                    Toast.makeText(
                        requireContext(),
                        "Upload gagal",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    Log.e(TAG, "Upload dijadwalkan ulang")
                }
            })
            .dispatch()
    }

    private fun updateUser(
        uid: String,
        nama: String,
        email: String,
        phone: String,
        imageUrl: String?
    ) {
        Log.d(TAG, "Update user Firestore")
        val updateMap = mutableMapOf<String, Any>(
            "nama" to nama,
            "email" to email,
            "noTelp" to phone,
            "updatedAt" to System.currentTimeMillis()
        )

        imageUrl?.let {
            updateMap["profilePicture"] = it
        }

        firestore.collection("users")
            .document(uid)
            .update(updateMap)
            .addOnSuccessListener {
                Log.d(TAG, "Firestore update berhasil")
                auth.currentUser?.updateEmail(email)

                Toast.makeText(
                    requireContext(),
                    "Profile berhasil diupdate",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Log.e(TAG, "Firestore update gagal", it)
            }
    }
}