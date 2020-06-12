package com.hmelikyan.deliveryapp.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.hmelikyan.deliveryapp.model.FragmentPagerModel

class FragmentViewPagerAdapter(
    fm: FragmentManager,
    private val fragmentList: ArrayList<FragmentPagerModel>
) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment = fragmentList[position].fragment


    override fun getCount(): Int = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence? = fragmentList[position].title
}