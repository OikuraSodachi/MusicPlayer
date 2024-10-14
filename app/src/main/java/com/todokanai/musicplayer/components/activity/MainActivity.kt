package com.todokanai.musicplayer.components.activity

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
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

    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    private lateinit var activityResult: ActivityResultLauncher<String>
    private val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        requestStorageManageAccess_td(this)
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
            Exitbtn.setOnClickListener { viewModel.exit(this@MainActivity, serviceIntent)}      //----Exitbtn에 대한 동작
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
                launchForeground(this@MainActivity, serviceIntent)
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
}