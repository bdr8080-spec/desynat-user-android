package com.desynatuser.ui.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseActivity
import com.desynatuser.databinding.ActivityAuthBinding
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.get

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    private lateinit var controller: NavController
    private var binding: ActivityAuthBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightNavigationBars = false

        initView()
        initControl()
        observer()
    }

    override fun initView() {
        CommonMethod.setLocale(this,SharedPreference.get(this).language)
        CommonMethod.detectGestureMode(binding?.root!!,binding?.viewBottom!!)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController

        val navGraph = controller.navInflater.inflate(R.navigation.auth_navigation_graph)
        val fromLanguage = intent.getStringExtra("open_screen")

        val destination = if (fromLanguage != null) {

            if (fromLanguage == "profile") {
                R.id.login
            } else {
                if (SharedPreference.get(this).isTutorial) {
                    R.id.login
                } else {
                    R.id.onBoarding
                }
            }

        } else {
            val savedLanguage = SharedPreference.get(this).language
            if (SharedPreference.get(this).userType == 2) {
                if (SharedPreference.get(this).getUser()?.affiliatedUserdetails?.adminVerify != null) {
                    R.id.login
                } else if (SharedPreference.get(this).isAffiliateTutorial == false) {
                    R.id.onBoarding
                } else {
                    R.id.login
                }
            }
            else if (SharedPreference.get(this).accessToken.isNotEmpty()) {
                R.id.banner
            }
            else if (Constants.isLogout) {
                R.id.login
            } else if (savedLanguage.isEmpty()) {
                R.id.language
            }
            else if (SharedPreference.get(this).isTutorial){
                R.id.onBoarding
            }
            else{
                R.id.login
            }
        }


        navGraph.setStartDestination(
            destination
        )

        controller.graph = navGraph
    }

    override fun initControl() {
    }

    override fun observer() {
    }
}