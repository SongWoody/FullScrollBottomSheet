package com.example.fullscrollbottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.fullscrollbottomsheet.databinding.FragmentBottomSheetBinding

class BottomSheetFragment: Fragment() {

    companion object {
        const val TAG: String = "BottomSheetFragment"

        fun newInstance(): BottomSheetFragment {
            val args = Bundle()
            val playScreenFragment = BottomSheetFragment()
            playScreenFragment.arguments = args
            return playScreenFragment
        }
    }

    lateinit var mBinding: FragmentBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet, container, false)
        return mBinding.root
    }
}