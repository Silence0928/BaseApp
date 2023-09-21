package com.stas.whms.module.main

import android.annotation.SuppressLint
import android.view.MenuItem
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.core.os.BuildCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.toast.ToastUtils
import com.lib_common.app.BaseApplication
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.stas.whms.R
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityMainBinding
import com.stas.whms.module.main.fragments.BaleFragment
import com.stas.whms.module.main.fragments.HomeFragment
import com.stas.whms.module.main.fragments.MineFragment
import com.stas.whms.module.main.fragments.ShiftParkFragment

/**
 * 主页
 */
@Route(path = RoutePathConfig.ROUTE_MAIN)
class MainActivity : BaseMvvmActivity<ActivityMainBinding, BaseViewModel>() {

    private var mMenuItem: MenuItem? = null
    private val fragments = arrayListOf<Fragment>()

    /**
     * 判断点击退出程序标识
     */
    private var mBackKeyPressedTimes: Long = 0

    override fun initView() {
        title = "首页"
        mActionBar.leftView.visibility = View.INVISIBLE
        fragments.add(HomeFragment())
        fragments.add(BaleFragment())
        fragments.add(ShiftParkFragment())
        fragments.add(MineFragment())
        mDataBinding.bottomNavigation.itemIconTintList = null
        val vpAdapter = VpAdapter(
            supportFragmentManager,
            lifecycle, fragments
        )
        mDataBinding.viewPager.adapter = vpAdapter
        mDataBinding.viewPager.offscreenPageLimit = fragments.size
        //默认选中首页
        mDataBinding.viewPager.setCurrentItem(0, false)
    }

    override fun onViewEvent() {
        mDataBinding.viewPager.registerOnPageChangeCallback(object:
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (mMenuItem != null) {
                    mMenuItem!!.setChecked(false)
                } else {
                    mDataBinding.bottomNavigation.menu.getItem(0).setChecked(false)
                }
                mMenuItem = mDataBinding.bottomNavigation.menu.getItem(position)
                mMenuItem!!.setChecked(true)
            }
        })

        mDataBinding.bottomNavigation.setOnItemSelectedListener { item ->
            val itemId = item.itemId
            if (itemId == R.id.i_home) { // 首页
                title = resources.getString(R.string.str_home)
                selectTab(0)
            } else if (itemId == R.id.i_home_bale) { // 捆包
                title = resources.getString(R.string.str_home_bale)
                selectTab(1)
            } else if (itemId == R.id.i_home_shifting_parking) { // 移库
                title = resources.getString(R.string.str_home_shifting_parking)
                selectTab(2)
            } else if (itemId == R.id.i_mine) { // 我的
                title = resources.getString(R.string.str_home_mine)
                selectTab(3)
            }
            false
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun getVariableId(): Int {
        return 0
    }

    /**
     * 选择的tab
     *
     * @param position
     */
    private fun selectTab(position: Int) {
        mDataBinding.viewPager.setCurrentItem(position, false)
    }

    /**
     * view pager adapter
     */
    class VpAdapter(fm: FragmentManager?, lifecycle: Lifecycle, private val data: List<Fragment>) :
        FragmentStateAdapter(fm!!, lifecycle) {

        override fun getItemCount(): Int {
            return data.size
        }

        override fun createFragment(position: Int): Fragment {
            return data[position]
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onBackPressed() {
//        if (BuildCompat.isAtLeastT()) {
//            onBackInvokedDispatcher.registerOnBackInvokedCallback(
//                OnBackInvokedDispatcher.PRIORITY_DEFAULT
//            ) {
//                // Back is pressed... Finishing the activity
//                if (System.currentTimeMillis() - mBackKeyPressedTimes > 2000) {
//                    mBackKeyPressedTimes = System.currentTimeMillis()
//                    ToastUtils.showShort(
//                        resources.getString(com.lib_src.R.string.home_exit_app)
//                    )
//                    return@registerOnBackInvokedCallback
//                }
//                finish()
//            }
//        } else {
//            onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    if (System.currentTimeMillis() - mBackKeyPressedTimes > 2000) {
//                        mBackKeyPressedTimes = System.currentTimeMillis()
//                        ToastUtils.showShort(
//                            resources.getString(com.lib_src.R.string.home_exit_app)
//                        )
//                        return
//                    }
//                    finish()
//                }
//            })
//        }
        if (System.currentTimeMillis() - mBackKeyPressedTimes > 2000) {
            mBackKeyPressedTimes = System.currentTimeMillis()
            ToastUtils.showShort(
                resources.getString(com.lib_src.R.string.home_exit_app)
            )
            return
        }
        BaseApplication.getApplication().exitApp()
    }

}