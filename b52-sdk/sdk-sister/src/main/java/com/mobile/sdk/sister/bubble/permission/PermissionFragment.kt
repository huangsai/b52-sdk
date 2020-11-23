package com.mobile.sdk.sister.bubble.permission

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

internal class PermissionFragment : Fragment() {

    companion object {
        private var onPermissionResult: OnPermissionResult? = null

        fun requestPermission(activity: Activity, onPermissionResult: OnPermissionResult) {
            this.onPermissionResult = onPermissionResult
            activity.fragmentManager
                .beginTransaction()
                .add(PermissionFragment(), activity.localClassName)
                .commitAllowingStateLoss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        PermissionUtils.requestPermission(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PermissionUtils.requestCode) {
            Handler(Looper.getMainLooper()).postDelayed({
                val activity = activity ?: return@postDelayed
                val check = PermissionUtils.checkPermission(activity)
                onPermissionResult?.permissionResult(check)
                fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
            }, 500)
        }
    }

}