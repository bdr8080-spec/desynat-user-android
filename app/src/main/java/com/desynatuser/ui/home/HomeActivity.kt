package com.desynatuser.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.desynatuser.R
import com.desynatuser.adapter.BottomNavAdapter
import com.desynatuser.base.BaseActivity
import com.desynatuser.data.view_model.LocationViewModel
import com.desynatuser.databinding.ActivityHomeBinding
import com.desynatuser.ui.response.BottomNavItem
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.myfatoorah.sdk.views.MFSDK
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    private var binding: ActivityHomeBinding? =null
    private lateinit var controller: NavController
    var bottonNavItemList = ArrayList<BottomNavItem>()
    private var bottomAdapter : BottomNavAdapter? =null
    private val locationViewModel: LocationViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var longitude = 0.0
    var latitude = 0.0
    var locationGet: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        CommonMethod.detectGestureMode(binding?.root!!,binding?.viewBottom!!)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val imeInset = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            val reduceBy = resources.getDimensionPixelSize(R.dimen.keyboard_padding_reduce)

            v.setPadding(
                v.paddingLeft,
                v.paddingTop,
                v.paddingRight,
                imeInset
              //  if (binding?.viewBottom?.isVisible == true)  imeInset - reduceBy else imeInset
            )
            insets
        }

        MFSDK.setUpActionBar(isShowToolBar = false)
        initView()
        initControl()
        observer()
    }

    override fun initView() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (Constants.location==""){
            getCurrentLocation()
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController
        val navGraph = controller.navInflater.inflate(R.navigation.home_navigation_graph)
        controller.graph = navGraph

        val openScreen = intent.getStringExtra("open_screen")
        if (openScreen == "profile") {
            controller.navigate(R.id.profile)
        }

        bottonNavItemList.clear()
        bottonNavItemList.add(BottomNavItem("Home",R.drawable.ic_home,R.drawable.ic_selected_home))
        bottonNavItemList.add(BottomNavItem("Catalog",R.drawable.ic_catalog,R.drawable.ic_selected_catalog))
        bottonNavItemList.add(BottomNavItem("Streaming",R.drawable.ic_streaming,R.drawable.ic_streaming))
        bottonNavItemList.add(BottomNavItem("My Bag",R.drawable.ic_bag,R.drawable.ic_slected_bag))
        bottonNavItemList.add(BottomNavItem("Profile",R.drawable.ic_profile,R.drawable.ic_selected_profile))

        setBottomAdapter()

        val bottomNavDestinations = setOf(
            R.id.home,
            R.id.catalog,
            R.id.myBag,
            R.id.profile,
        )

        controller.addOnDestinationChangedListener { _, destination, _ ->

            if (destination.id in bottomNavDestinations) {
                binding?.rvBottomNav?.isVisible = true
                binding?.viewBackground?.isVisible = true
            } else {
                binding?.rvBottomNav?.isVisible = false
                binding?.viewBackground?.isVisible = false
            }

            when (destination.id) {
                R.id.home -> {
                    bottomAdapter?.setSelectedItem(0)
                }
                R.id.catalog -> {
                    bottomAdapter?.setSelectedItem(1)
                }
                R.id.myBag -> {
                    if (!Constants.isGuest) {
                        bottomAdapter?.setSelectedItem(3)
                    }
                    else{
                        CommonMethod.showLoginAlert(this)
                    }
                }
                R.id.profile -> {
                    bottomAdapter?.setSelectedItem(4)
                }
            }
        }

        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    override fun initControl() {
    }

    override fun observer() {
    }

    fun setBottomAdapter(){
        bottomAdapter = BottomNavAdapter(this,bottonNavItemList, object :BottomNavAdapter.BottomNavItemClick{
            override fun onBottomNavItemClick(position: Int) {
                when(position) {
                    0 -> controller.navigate(R.id.home)
                    1 -> controller.navigate(R.id.catalog)
                    //2 -> controller.navigate(R.id.catalog)
                    3 -> if (!Constants.isGuest){ controller.navigate(R.id.myBag)} else{
                        CommonMethod.showLoginAlert(this@HomeActivity)
                    }
                    4 -> controller.navigate(R.id.profile)
                }

            }


        })
        binding?.rvBottomNav?.adapter = bottomAdapter

    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationViewModel.setLoading(true)

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    // Success
                    // handleLocation(location)
                    locationGet = true
                    latitude = location.latitude
                    longitude = location.longitude
                    updateLocationText(latitude,longitude)
                } else {
                    // Try last known location
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                        if (lastLocation != null) {
                            //handleLocation(lastLocation)
                            latitude = location?.latitude ?: 0.0
                            longitude = location?.longitude ?: 0.0

                            Log.e(
                                "LocationSuccess",
                                "Location is null from both current and last known.latitude:$latitude, longitude:$longitude"
                            )
                            updateLocationText(latitude,longitude)
                        } else {
                            locationViewModel.setLoading(false)
                            Log.e(
                                "LocationError",
                                "Location is null from both current and last known."
                            )
                            Toast.makeText(
                                this,
                                "Unable to fetch location. Try moving outside or checking settings.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100
            )
        }
    }

    fun updateLocationText(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val fullAddress = address.getAddressLine(0) ?: "Unknown Location"
                val locality = address.locality ?: "Unknown Locality"   // City or Town
                val subLocality = address.subLocality ?: "Unknown Sublocality" // Area inside city
                val stateh = address.adminArea ?: "N/A"
                val postalCode = address.postalCode ?: "Unknown Pincode" // Zip Code
                val houseNumber = address.subThoroughfare ?: address.premises ?: address.featureName
                locationViewModel.setLoading(false)
                Constants.location = fullAddress
                Constants.latitude = this.latitude
                Constants.longitude = this.longitude
                locationViewModel.setLocation(this.latitude, this.longitude)

            } else {
                Constants.location  = "No address found"
            }
        } catch (e: IOException) {
            Log.e("MapsDebug", "Geocoder failed: ${e.message}")
            Constants.location = "Error fetching address"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (Constants.currentScreen=="payment"||Constants.currentScreen=="orderPayment") {
            if (requestCode == 991) {
                EventBus.getDefault().post(PaymentResultEvent(requestCode, resultCode, data))
            }

        }
        Log.d("test",requestCode.toString())
    }

    fun updateAdapterCount(){
       bottomAdapter?.setCartCount(Constants.cartItem)
    }

    private fun handleDeepLink(intent: Intent?) {

        val uri = intent?.data ?: return

        if (uri.path?.contains("/user/product/detail-page") == true) {

            val productId = uri.lastPathSegment
            val ref = uri.getQueryParameter("ref")

            val bundle = Bundle().apply {
                putString("productId", productId)
                putString("REF_ID", ref)
            }
            controller.navigate(R.id.productDetail, bundle)

        }
    }

}

data class PaymentResultEvent(
    val requestCode: Int,
    val resultCode: Int,
    val data: Intent?
)

