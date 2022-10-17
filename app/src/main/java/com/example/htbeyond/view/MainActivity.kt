package com.example.htbeyond.view

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            guideButton.setOnClickListener {
                when (UiType.from(viewModel.getCurrentStatus())) {
                    UiType.REQUEST_BLUETOOTH_ENABLE -> {
                        requestBluetoothEnable()
                    }
                    UiType.REQUEST_BLUETOOTH_PERMISSION -> {
                        requestPermission()
                    }
                    UiType.ALL_PREPARED -> {
                        startScan()
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
    }

    private fun updateUi(uiState: MainViewModel.UiState) {
        when (uiState.isBluetoothAvailable) {
            true -> {
                when (uiState.isBluetoothGranted) {
                    true -> {
                        when (uiState.isBluetoothEnabled) {
                            true -> {
                                with(binding) {
                                    guideTextView.text = "블루투스 기기를 찾아보세요."
                                    guideButton.text = "블루투스 탐색 시작"
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

    override fun requestPermission() {
        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN)
    }

    override fun startScan() {

    }

    override fun stopScan() {

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
                viewModel.checkStatus()
            } else {
                Toast.makeText(this@MainActivity, "권한 승인 중 취소하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
}