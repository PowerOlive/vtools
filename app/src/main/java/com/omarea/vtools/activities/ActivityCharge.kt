package com.omarea.vtools.activities

import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import com.omarea.data_collection.GlobalStatus
import com.omarea.store.BatteryHistoryStore
import com.omarea.vtools.R
import com.omarea.vtools.dialogs.DialogElectricityUnit
import kotlinx.android.synthetic.main.activity_charge.*
import java.util.*

class ActivityCharge : ActivityBase() {
    private lateinit var storage: BatteryHistoryStore
    private var timer: Timer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charge)

        setBackArrow()

        storage = BatteryHistoryStore(this)
        electricity_adj_unit.setOnClickListener {
            DialogElectricityUnit().showDialog(this)
        }
    }

    override fun onResume() {
        super.onResume()
        title = getString(R.string.menu_charge)
        timer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    updateUI()
                }
            }, 0, 200)
        }
    }

    override fun onPause() {
        timer?.cancel()
        timer = null
        super.onPause()
    }

    private val hander = Handler()
    private fun updateUI() {
        hander.post {
            view_speed.invalidate()
            view_time.invalidate()
            view_temperature.invalidate()
            charge_state.text = when (GlobalStatus.batteryStatus) {
                BatteryManager.BATTERY_STATUS_DISCHARGING -> {
                    getString(R.string.battery_status_discharging)
                }
                BatteryManager.BATTERY_STATUS_CHARGING -> {
                    getString(R.string.battery_status_charging)
                }
                BatteryManager.BATTERY_STATUS_FULL -> {
                    getString(R.string.battery_status_full)
                }
                BatteryManager.BATTERY_STATUS_UNKNOWN -> {
                    getString(R.string.battery_status_unknown)
                }
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> {
                    getString(R.string.battery_status_not_charging)
                }
                else -> getString(R.string.battery_status_unknown)
            }
        }
    }
}