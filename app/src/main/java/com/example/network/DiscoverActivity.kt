package com.example.network

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.network.adapter.DiscoverFirstAdapter
import com.example.network.adapter.NotificationAdapter
import com.example.network.database.AppDatabase
import com.example.network.database.model.HeadingImageNewsModel
import com.example.network.databinding.ActivityDiscoverBinding
import com.example.network.fragment.TopicViewPagerFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "DiscoverActivity_CommTag"

class DiscoverActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiscoverBinding
    private lateinit var firstRowAdapter: DiscoverFirstAdapter
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var divider: DividerItemDecoration
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_discover)
        divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        
        setupFirstRow() // row 1 - types of news
        setupNotificationRow() // notification row
        setupTopicRow() // topic row
    }
    
    private fun setupFirstRow() {
        firstRowAdapter = DiscoverFirstAdapter {
            val msg = "first row clicked on $it"
            Log.d(TAG, msg)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        binding.newsTypes.apply {
            adapter = firstRowAdapter
            layoutManager = LinearLayoutManager(this@DiscoverActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }
    
    private fun setupNotificationRow() {
        notificationAdapter = NotificationAdapter {
            val msg = "notification clicked on $it"
            Log.d(TAG, msg)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        val newsDatabase = AppDatabase.getDatabase(this)
        val list = mutableListOf<HeadingImageNewsModel>()
        GlobalScope.launch(Dispatchers.IO) {
            val newsList = newsDatabase.newsDao().getAllNews()
            for (news in newsList) {
                val headingImageNewsModel = HeadingImageNewsModel(news.title, news.image)
                list.add(headingImageNewsModel)
            }
            launch(Dispatchers.Main) {
                notificationAdapter.setNotificationList(list)
                binding.notificationRV.apply {
                    adapter = notificationAdapter
                    layoutManager = LinearLayoutManager(this@DiscoverActivity)
                    addItemDecoration(divider)
                }
            }
        }
    }
    
    private fun setupTopicRow() {
        binding.viewPagerTabs.adapter = ScreenSlidePagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPagerTabs) { tab, position ->
            tab.text = when (position) {
                0 -> "water"
                1 -> "Oxygen"
                2 -> "Bio fuels"
                else -> "India"
            }
        }.attach()
    }
    
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return 4
        }
        
        override fun createFragment(position: Int): Fragment {
            return TopicViewPagerFragment.newInstance(position)
        }
    }
    
}