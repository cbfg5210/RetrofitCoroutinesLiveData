package com.example.retrofitdemo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.retrofitdemo.databinding.ActivityMainBinding
import com.example.retrofitdemo.tools.RequestStatus

class MainActivity : AppCompatActivity() {
    // 利用系统扩展的代理，快速生成viewModel
    private val viewModel by viewModels<MainViewModel>()

    private lateinit var viewBinding: ActivityMainBinding

    private val mAdapter = MainRvAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.recyclerView.adapter = mAdapter

        // 定义LiveData
        viewModel.newsLiveData.observe(this, {
            when (it.requestStatus) {
                RequestStatus.START -> {
                }
                RequestStatus.SUCCESS -> {
                    it.data?.let { newsBean ->
                        // 直接刷新界面
                        mAdapter.setList(newsBean.result)
                    }
                }
                RequestStatus.ERROR -> {
                    Toast.makeText(this, "网络出错了", Toast.LENGTH_SHORT).show()
                }
                RequestStatus.COMPLETE -> {
                    Toast.makeText(this, "网络请求完成了", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        // 在你需要数据的地方，调用方法获取数据
        viewModel.getNews()
    }
}


