package com.desynatuser.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.desynatuser.R
import com.desynatuser.base.BaseActivity
import com.desynatuser.databinding.ActivitySplashBinding
import com.desynatuser.ui.affiliateUser.AffiliateHomeActivity
import com.desynatuser.ui.auth.AuthActivity
import com.desynatuser.ui.home.HomeActivity
import com.desynatuser.utils.SharedPreference

class SplashActivity : BaseActivity() {
    private var binding: ActivitySplashBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightNavigationBars = false


        initView()
        initControl()
        observer()
    }

    override fun initView() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (SharedPreference.get(this).userType==2){
            if (SharedPreference.get(this).affiliateAccessToken.isNotEmpty()&&SharedPreference.get(this).getAffiliateUser()?.adminVerify=="2") {
                startActivity(Intent(this, AffiliateHomeActivity::class.java))
                finish()
            }
            else{
                SharedPreference.get(this).userType=1
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            }
            else{
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        },1000)
    }

    override fun initControl() {
    }

    override fun observer() {
    }
}