package com.example.fullscrollbottomsheet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.fullscrollbottomsheet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupFullScrollBottomSheet()
    }

    private fun setupFullScrollBottomSheet() {
        supportFragmentManager
                .beginTransaction()
                .replace(mBinding.mFullScrollBottomSheet.id, BottomSheetFragment.newInstance(), BottomSheetFragment.TAG)
                .commitAllowingStateLoss()
    }
}