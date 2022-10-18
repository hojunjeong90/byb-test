package com.example.htbeyond.view

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.htbeyond.Constants
import com.example.htbeyond.adapter.BtAdapter
import com.example.htbeyond.business.MainActivityInterface
import com.example.htbeyond.databinding.ActivityMainBinding
import com.example.htbeyond.enum.UiType
import com.example.htbeyond.util.BaseActivity
import com.example.htbeyond.util.activityViewBinding
import com.example.htbeyond.util.activityViewModelBinding
import com.example.htbeyond.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : BaseActivity(), MainActivityInterface {

    override val binding by activityViewBinding(ActivityMainBinding::inflate)

    override val viewModel by activityViewModelBinding(MainViewModel::class.java)

    private val btAdapter = BtAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            recyclerView.apply {
                adapter = btAdapter
                layoutManager = LinearLayoutManager(context)
            }

            guideButton.setOnClickListener {
                when (UiType.from(viewModel.getCurrentStatus())) {
                    UiType.REQUEST_BLUETOOTH_ENABLE -> {
                        requestBluetoothEnable()
                    }
                    UiType.REQUEST_BLUETOOTH_PERMISSION -> {
                        requestPermission()
                    }
                    UiType.ALL_PREPARED -> {
                        btAdapter.clearData()
                        startScan()
                    }
                    UiType.SCANNING -> {
                        stopScan()
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateFlow.collectLatest { uiState ->
                    updateUi(uiState)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bluetoothRepository.foundData.collectLatest { btDevice ->
                btDevice?.let {
                    btAdapter.setData(btDevice)
                }
            }
        }
    }

    private fun updateUi(uiState: MainViewModel.UiState) {
        when (uiState.isBluetoothAvailable) {
            true -> {
                when (uiState.isBluetoothGranted) {
                    true -> {
                        when (uiState.isBluetoothEnabled) {
                            true -> {
                                when(uiState.isBluetoothScanning){
                                    true->{
                                        with(binding) {
                                            guideTextView.text = "블루투스 주변 기기 검색 중입니다."
                                            guideButton.text = "블루투스 탐색 종료"
                                        }
                                    }
                                    false->{
                                        with(binding) {
                                            guideTextView.text = "블루투스 기기를 찾아보세요."
                                            guideButton.text = "블루투스 탐색 시작"
                                        }
                                    }
                                }

                            }
                            false -> {
                                with(binding) {
                                    guideTextView.text = "계속하기 위해 블루투스를 켜주세요."
                                    guideButton.text = "블루투스 켜기"
                                }
                            }
                        }
                    }
                    false -> {
                        with(binding) {
                            guideTextView.text = "계속하기 위해 블루투스 권한을 허용해주세요."
                            guideButton.text = "권한 허용"
                        }
                    }
                }
            }
            false -> {
                with(binding) {
                    guideTextView.text = "이 기기에서는 블루투스를 사용할 수 없습니다."
                    guideButton.text = "앱 사용 불가"
                }
            }
        }
    }

    override fun requestBluetoothEnable() {
        bluetoothEnableLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }

    override fun requestPermission() : Boolean {
        var isPermissionNotGrantedExist = false
        run{
            Constants.PERMISSIONS.forEach { permission ->
                if((ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)){
                    requestPermissionLauncher.launch(permission)
                    isPermissionNotGrantedExist = true
                    return@run
                }
            }
        }
        return isPermissionNotGrantedExist
    }

    override fun startScan() {
        viewModel.startBluetoothScanning()
    }

    override fun stopScan() {
        viewModel.stopBluetoothScanning()
    }

    private val bluetoothEnableLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.checkStatus()
            } else {
                Toast.makeText(this@MainActivity, "블루투스가 켜져있지 않습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                if(!requestPermission()){
                    viewModel.checkStatus()
                }
            } else {
                Toast.makeText(this@MainActivity, "권한 승인 중 취소하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
}