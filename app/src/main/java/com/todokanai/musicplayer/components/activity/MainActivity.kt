package com.todokanai.musicplayer.components.activity

import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.tabs.TabLayoutMediator
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.components.fragments.MusicListFragment
import com.todokanai.musicplayer.components.fragments.PlayingFragment
import com.todokanai.musicplayer.components.fragments.SettingsFragment
import com.todokanai.musicplayer.components.service.MusicService.Companion.serviceIntent
import com.todokanai.musicplayer.components.view.adapter.FragmentAdapter
import com.todokanai.musicplayer.databinding.ActivityMainBinding
import com.todokanai.musicplayer.tools.independent.requestStorageManageAccess_td
import com.todokanai.musicplayer.variables.Variables.Companion.isServiceOn
import com.todokanai.musicplayer.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object{
        fun mainIntent(context: Context) = PendingIntent.getActivity(context,0, Intent( mainOpenIntent(context)), PendingIntent.FLAG_IMMUTABLE)
        fun mainOpenIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    private lateinit var activityResult: ActivityResultLauncher<String>
    private val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        adjustInsets(binding.root)
        if(!Environment.isExternalStorageManager()){
            permissionDialog()
        }

       // requestStorageManageAccess_td(this)
        activityResult =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (!isGranted)
                    finish()
            }

        val fragmentList = listOf(MusicListFragment(), PlayingFragment(), SettingsFragment())
        val adapter = FragmentAdapter(this)
        adapter.fragmentList = fragmentList

        binding.run{
            trackPager.isUserInputEnabled = false
            trackPager.adapter = adapter
            Exitbtn.setOnClickListener { viewModel.exit(this@MainActivity, serviceIntent(this@MainActivity))}      //----Exitbtn에 대한 동작
            /*
            floatingBtnLayout.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    MainFloatingActionButton(
                        modifier = Modifier,
                        onClick = {println("test")}
                    )
                }
            }
             */
        }

        val list = listOf(
            AppCompatResources.getDrawable(this, R.drawable.baseline_list_24),
            AppCompatResources.getDrawable(this,R.drawable.baseline_play_arrow_24),
            AppCompatResources.getDrawable(this,R.drawable.baseline_settings_24)
        )
        TabLayoutMediator(binding.tabLayout, binding.trackPager) { tab, position ->
            tab.icon = list[position]
        }.attach()

        viewModel.run {
            getPermission(this@MainActivity)
            if(!isServiceOn) {
                launchForeground(this@MainActivity, serviceIntent(this@MainActivity))
            }
        }
        /** 뒤로가기 버튼 override
         *
         *  (홈 버튼을 사용하여) Activity를 명시적으로 살려둘 경우 외에는 onDestroy 호출
         * **/
        onBackPressedDispatcher.addCallback {
            finish()
        }
    }

    fun permissionDialog(){
        AlertDialog.Builder(this)
            .setMessage(
                "Storage permission is required"
            )
            .setPositiveButton(
                "go to settings",
                object: DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                     //   requestStorageManageAccess_td(this@MainActivity)
                    }
                }
            )
            .setOnDismissListener {
                requestStorageManageAccess_td(this)
            }
            .show()
    }

    /** enableEdgeToEdge 로 인한 view 가려짐 문제 해결 **/
    fun adjustInsets(view: View){
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                 or WindowInsetsCompat.Type.displayCutout()
            )
            v.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = bars.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }
    }
}