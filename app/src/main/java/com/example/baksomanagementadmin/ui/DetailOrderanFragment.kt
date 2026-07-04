package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.repository.OrderRepository
import com.example.baksomanagementadmin.data.repository.UserRepository
import com.example.baksomanagementadmin.utils.DistanceUtils
import com.google.firebase.firestore.ListenerRegistration
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import com.google.firebase.auth.FirebaseAuth
import android.widget.LinearLayout

class DetailOrderanFragment : Fragment(R.layout.fragment_detail_orderan) {

    companion object {
        private const val TAG = "DetailOrderanDebug"
    }
    private var currentPickupType: String = "dine_in"
    private val repository = OrderRepository()
    private val userRepository = UserRepository()
    private lateinit var statusList: List<String>

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        Log.d(TAG, "===== onViewCreated() DIPANGGIL =====")

        val orderId =
            arguments?.getString(
                "ORDER_ID"
            ) ?: run {
                Log.e(TAG, "ORDER_ID tidak ditemukan di arguments, fragment dihentikan")
                return
            }

        Log.d(TAG, "orderId diterima = $orderId")

        val tvTitle =
            view.findViewById<TextView>(
                R.id.tvTitle
            )

        val tvNamaUser =
            view.findViewById<TextView>(
                R.id.tvNamaUser
            )

        val cardDelivery =
            view.findViewById<View>(R.id.cardDelivery)

        val tvAlamatTujuan =
            view.findViewById<TextView>(R.id.tvAlamatTujuan)

        val tvJarakWaktu =
            view.findViewById<TextView>(R.id.tvJarakWaktu)

        val btnMulaiPerjalanan =
            view.findViewById<Button>(R.id.btnMulaiPerjalanan)

        val btnSampaiTujuan =
            view.findViewById<Button>(R.id.btnSampaiTujuan)

        val rvItems =
            view.findViewById<RecyclerView>(
                R.id.rvItems
            )

        val spStatus =
            view.findViewById<Spinner>(
                R.id.spStatus
            )

        val btnUpdate =
            view.findViewById<Button>(
                R.id.btnUpdateStatus
            )

        val btnSiapDiambil =
            view.findViewById<Button>(
                R.id.btnSiapDiambil
            )

        val btnCancel =
            view.findViewById<Button>(
                R.id.btnCancel
            )

        rvItems.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        Log.d(TAG, "Semua view berhasil di-bind, memanggil getOrderDetail()")

        repository.getOrderDetail(
            orderId
        ){ order, items ->

            Log.d(TAG, "===== CALLBACK getOrderDetail() =====")

            if (order == null) {
                Log.e(TAG, "Order NULL untuk orderId = $orderId, callback dihentikan")
                return@getOrderDetail
            }

            currentPickupType = order.pickupType

            Log.d(TAG, "currentPickupType di-set = $currentPickupType")

            Log.d(
                TAG,
                """
                DATA ORDER DITERIMA
                ------------------------
                id             = ${order.id}
                userID         = ${order.userID}
                status         = ${order.status}
                pickupType     = ${order.pickupType}
                deliveryAddress= ${order.deliveryAddress}
                latitude       = ${order.latitude}
                longitude      = ${order.longitude}
                total          = ${order.total}
                itemCount      = ${items.size}
                ------------------------
                """.trimIndent()
            )

            statusList =
                if (order.pickupType == "delivery") {
                    listOf(
                        "pending",
                        "diproses"
                    )
                } else {
                    listOf(
                        "pending",
                        "diproses",
                        "siap_diambil"
                    )
                }

            Log.d(TAG, "statusList untuk pickupType=${order.pickupType} -> $statusList")

            val spinnerAdapter = object : ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                statusList
            ) {

                override fun getView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {

                    val view = super.getView(position, convertView, parent)
                    (view as TextView).setTextColor(
                        resources.getColor(R.color.setting_text, null)
                    )
                    return view
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {

                    val view =
                        super.getDropDownView(position, convertView, parent)

                    (view as TextView).setTextColor(
                        resources.getColor(R.color.setting_text, null)
                    )

                    view.setBackgroundColor(
                        resources.getColor(R.color.setting_card, null)
                    )

                    return view
                }
            }

            spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )

            spStatus.adapter = spinnerAdapter

            tvTitle.text =
                "Order #${order.id.take(8)}"

            rvItems.adapter =
                DetailOrderItemAdapter(
                    items
                )

            Log.d(TAG, "RecyclerView adapter di-set dengan ${items.size} item")

            if (order.status == "diproses" && order.pickupType != "delivery") {
                btnSiapDiambil.visibility = View.VISIBLE
                Log.d(TAG, "btnSiapDiambil -> VISIBLE (status=diproses, pickupType=${order.pickupType})")
            } else {
                btnSiapDiambil.visibility = View.GONE
                Log.d(TAG, "btnSiapDiambil -> GONE")
            }

            if (order.pickupType == "delivery") {

                Log.d(TAG, "pickupType = delivery, menampilkan cardDelivery")

                cardDelivery.visibility = View.VISIBLE

                tvAlamatTujuan.text =
                    "Alamat : ${order.deliveryAddress.ifBlank { "-" }}"

                Log.d(TAG, "tvAlamatTujuan di-set = ${order.deliveryAddress.ifBlank { "-" }}")

                btnMulaiPerjalanan.visibility =
                    if (order.status == "diproses") View.VISIBLE else View.GONE

                btnSampaiTujuan.visibility =
                    if (order.status == "dalam_perjalanan") View.VISIBLE else View.GONE

                Log.d(
                    TAG,
                    "btnMulaiPerjalanan visibility=${btnMulaiPerjalanan.visibility}, " +
                            "btnSampaiTujuan visibility=${btnSampaiTujuan.visibility} (status order=${order.status})"
                )

                val adminUid =
                    FirebaseAuth.getInstance().currentUser?.uid

                Log.d(TAG, "adminUid = $adminUid")

                if (adminUid != null &&
                    order.latitude != 0.0 &&
                    order.longitude != 0.0
                ) {

                    Log.d(
                        TAG,
                        "Koordinat order valid (lat=${order.latitude}, lng=${order.longitude}), " +
                                "memanggil userRepository.getUserById($adminUid)"
                    )

                    userRepository.getUserById(adminUid) { adminUser ->

                        Log.d(TAG, "===== CALLBACK getUserById(adminUid) =====")

                        if (adminUser != null) {
                            Log.d(
                                TAG,
                                "Data admin diterima -> latitude=${adminUser.latitude}, longitude=${adminUser.longitude}"
                            )
                        } else {
                            Log.e(TAG, "adminUser NULL, kemungkinan dokumen users/$adminUid tidak ditemukan")
                        }

                        if (adminUser != null &&
                            (adminUser.latitude != 0.0 || adminUser.longitude != 0.0)
                        ) {

                            val jarakKm = hitungJarakKm(
                                adminUser.latitude,
                                adminUser.longitude,
                                order.latitude,
                                order.longitude
                            )

                            val menit = estimasiWaktuMenit(jarakKm)

                            Log.d(
                                TAG,
                                "HASIL PERHITUNGAN -> jarakKm=$jarakKm, estimasiMenit=$menit"
                            )

                            tvJarakWaktu.text =
                                "Estimasi Jarak : %.1f km (± %d menit)".format(
                                    jarakKm,
                                    menit
                                )

                        } else {

                            Log.e(TAG, "Koordinat admin kosong/0.0, tidak bisa hitung jarak")

                            tvJarakWaktu.text =
                                "Estimasi Jarak : Lokasi toko belum diatur di Setting"
                        }
                    }

                } else {

                    Log.e(
                        TAG,
                        "Kondisi tidak terpenuhi untuk hitung jarak -> " +
                                "adminUid=$adminUid, order.latitude=${order.latitude}, order.longitude=${order.longitude}"
                    )

                    tvJarakWaktu.text =
                        "Estimasi Jarak : Koordinat pelanggan tidak tersedia"
                }

            } else {

                Log.d(TAG, "pickupType = ${order.pickupType}, cardDelivery -> GONE")

                cardDelivery.visibility = View.GONE
            }

            btnMulaiPerjalanan.setOnClickListener {

                Log.d(TAG, "btnMulaiPerjalanan diklik, orderId=$orderId")

                repository.updateOrderStatus(
                    orderId,
                    "dalam_perjalanan"
                ) {
                    Log.d(TAG, "updateOrderStatus SUKSES -> status=dalam_perjalanan")

                    Toast.makeText(
                        requireContext(),
                        "Status: dalam perjalanan",
                        Toast.LENGTH_SHORT
                    ).show()

                    btnMulaiPerjalanan.visibility = View.GONE
                    btnSampaiTujuan.visibility = View.VISIBLE
                }
            }

            btnSampaiTujuan.setOnClickListener {

                Log.d(TAG, "btnSampaiTujuan diklik, orderId=$orderId")

                repository.updateOrderStatus(
                    orderId,
                    "sampai_tujuan"
                ) {
                    Log.d(TAG, "updateOrderStatus SUKSES -> status=sampai_tujuan")

                    Toast.makeText(
                        requireContext(),
                        "Pesanan sampai tujuan",
                        Toast.LENGTH_SHORT
                    ).show()

                    btnSampaiTujuan.visibility = View.GONE
                }
            }

            Log.d(TAG, "Memanggil userRepository.getUserById(${order.userID}) untuk info pelanggan")

            userRepository.getUserById(
                order.userID
            ){ user ->

                Log.d(TAG, "===== CALLBACK getUserById(order.userID) =====")

                if (user != null) {
                    Log.d(
                        TAG,
                        "Data pelanggan diterima -> nama=${user.nama}, email=${user.email}, noTelp=${user.noTelp}"
                    )
                } else {
                    Log.e(TAG, "User pelanggan NULL untuk userID=${order.userID}")
                }

                tvNamaUser.text =
                    buildString {

                        append("Nama : ")
                        append(user?.nama)
                        append("\n")
                        append("Email : ")
                        append(user?.email)
                        append("\n")
                        append("No HP : ")
                        append(user?.noTelp)
                    }
            }

            val index =
                statusList.indexOf(
                    order.status
                )

            Log.d(TAG, "Index status '${order.status}' di statusList = $index")

            if(index >= 0)
                spStatus.setSelection(index)
        }

        btnUpdate.setOnClickListener {

            val selectedStatus =
                spStatus.selectedItem.toString()

            Log.d(
                "STOK_DEBUG",
                "BTN UPDATE DIKLIK -> $selectedStatus"
            )

            Log.d(TAG, "btnUpdate diklik, selectedStatus=$selectedStatus, orderId=$orderId")

            if (selectedStatus == "diproses") {
                Log.d(
                    "STOK_DEBUG",
                    "BTN UPDATE DIKLIK -> $selectedStatus"
                )

                Log.d(TAG, "Memanggil repository.gunakanBahanPesanan($orderId)")

                repository.gunakanBahanPesanan(
                    orderId
                ) {
                    Log.d(
                        "STOK_DEBUG",
                        "SELESAI GUNAKAN BAHAN"
                    )

                    Log.d(TAG, "gunakanBahanPesanan SELESAI, lanjut updateOrderStatus(diproses)")

                    repository.updateOrderStatus(
                        orderId,
                        "diproses"
                    ) {
                        Log.d(
                            "STOK_DEBUG",
                            "STATUS BERHASIL DIPROSES"
                        )

                        Log.d(TAG, "updateOrderStatus SUKSES -> status=diproses")

                        if (currentPickupType != "delivery") {
                            btnSiapDiambil.visibility = View.VISIBLE
                            Log.d(TAG, "btnSiapDiambil -> VISIBLE (pickupType=$currentPickupType)")
                        } else {
                            btnSiapDiambil.visibility = View.GONE
                            Log.d(TAG, "btnSiapDiambil tetap GONE karena pickupType=delivery")
                        }

                        // Untuk delivery, munculkan btnMulaiPerjalanan setelah status diproses
                        if (currentPickupType == "delivery") {
                            btnMulaiPerjalanan.visibility = View.VISIBLE
                            Log.d(TAG, "btnMulaiPerjalanan -> VISIBLE karena status diproses & delivery")
                        }

                        Toast.makeText(
                            requireContext(),
                            "Status diproses & stok dikurangi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } else {

                Log.d(TAG, "Memanggil updateOrderStatus langsung -> $selectedStatus")

                repository.updateOrderStatus(
                    orderId,
                    selectedStatus
                ) {

                    Log.d(TAG, "updateOrderStatus SUKSES -> status=$selectedStatus")

                    Toast.makeText(
                        requireContext(),
                        "Status diperbarui",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        btnSiapDiambil.setOnClickListener {

            Log.d(TAG, "btnSiapDiambil diklik, orderId=$orderId")

            repository.updateOrderStatus(
                orderId,
                "siap_diambil"
            ){

                Log.d(TAG, "updateOrderStatus SUKSES -> status=siap_diambil")

                Toast.makeText(
                    requireContext(),
                    "Pesanan siap diambil",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnCancel.setOnClickListener {

            Log.d(TAG, "btnCancel diklik, orderId=$orderId")

            AlertDialog.Builder(requireContext())
                .setTitle("Batalkan Pesanan")
                .setMessage(
                    "Yakin ingin membatalkan pesanan ini?"
                )
                .setPositiveButton("Ya") { _, _ ->

                    Log.d(TAG, "Konfirmasi cancel = Ya, memanggil repository.cancelOrder($orderId)")

                    repository.cancelOrder(
                        orderId
                    ) {

                        Log.d(TAG, "cancelOrder SUKSES")

                        Toast.makeText(
                            requireContext(),
                            "Pesanan dibatalkan",
                            Toast.LENGTH_SHORT
                        ).show()

                        requireActivity()
                            .onBackPressedDispatcher
                            .onBackPressed()
                    }
                }
                .setNegativeButton(
                    "Tidak"
                ) { _, _ ->
                    Log.d(TAG, "Konfirmasi cancel = Tidak, dibatalkan")
                }
                .show()
        }
    }

    private fun hitungJarakKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {

        val R = 6371.0 // radius bumi km

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a =
            sin(dLat / 2).pow(2) +
                    cos(Math.toRadians(lat1)) *
                    cos(Math.toRadians(lat2)) *
                    sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val hasil = R * c

        Log.d(
            TAG,
            "hitungJarakKm(lat1=$lat1, lon1=$lon1, lat2=$lat2, lon2=$lon2) = $hasil km"
        )

        return hasil
    }

    private fun estimasiWaktuMenit(jarakKm: Double): Int {

        val kecepatanRataRataKmPerJam = 30.0 // asumsi motor kota
        val jam = jarakKm / kecepatanRataRataKmPerJam

        val hasil = (jam * 60).toInt().coerceAtLeast(1)

        Log.d(
            TAG,
            "estimasiWaktuMenit(jarakKm=$jarakKm) = $hasil menit"
        )

        return hasil
    }
}