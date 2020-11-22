package com.mobile.sdk.sister.ui.views

import android.app.Dialog
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.anim.AppFloatDefaultAnimator
import com.lzf.easyfloat.anim.DefaultAnimator
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnFloatCallbacks
import com.mobile.guava.android.context.dp2px
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.AppManager
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.SisterX
import com.mobile.sdk.sister.databinding.SisterLayoutFloatingBinding
import kotlin.math.roundToInt

object MyKeyboardHelper : OnFloatCallbacks, View.OnClickListener {

    private var neededRecoverFloatWindow = false
    private val location = intArrayOf(200, 200)

    init {
        SisterX.hasBufferMsgItems.observeForever {
            EasyFloat.getAppFloatView(SisterX.TAG)?.let { view ->
                setView(EasyFloat.lastFlag, view)
            }
        }
        AndroidX.isAppInForeground.observeForever {
            if (true == it) {
                if (neededRecoverFloatWindow) {
                    neededRecoverFloatWindow = false
                    showFloatWindow()
                }
            } else {
                if (EasyFloat.appFloatIsShow(SisterX.TAG)) {
                    neededRecoverFloatWindow = true
                    hideFloatWindow()
                }
            }
        }
    }

    fun isKeyboardVisible(dialog: Dialog): Boolean {
        val r = Rect()
        val activityRoot = dialog.window!!.decorView
        val visibleThreshold = dialog.context.dp2px(100f).roundToInt()
        activityRoot.getWindowVisibleDisplayFrame(r)
        val heightDiff = activityRoot.rootView.height - r.height()
        return heightDiff > visibleThreshold
    }

    override fun createdResult(isCreated: Boolean, msg: String?, view: View?) {
    }

    override fun dismiss() {
    }

    override fun drag(view: View, event: MotionEvent) {
    }

    override fun dragEnd(view: View) {
        view.getLocationOnScreen(location)
        setView(EasyFloat.lastFlag, view)
    }

    override fun hide(view: View) {
    }

    override fun show(view: View) {
        neededRecoverFloatWindow = false
    }

    override fun touchEvent(view: View, event: MotionEvent) {
    }

    override fun onClick(v: View) {
        AppManager.currentActivity()?.let {
            if (it is FragmentActivity) {
                SisterX.show(it, false)
            }
        }
    }

    fun showFloatWindow() {
        try {
            if (EasyFloat.appFloatIsShow(SisterX.TAG)) {
                return
            }
            EasyFloat.with(AndroidX.myApp)
                // 设置浮窗xml布局文件，并可设置详细信息
                .setLayout(R.layout.sister_layout_floating) { view ->
                    setView(EasyFloat.lastFlag, view)
                    view.setOnClickListener(this)
                }
                // 设置浮窗显示类型，默认只在当前Activity显示，可选一直显示、仅前台显示、仅后台显示
                .setShowPattern(ShowPattern.ALL_TIME)
                // 设置吸附方式，共15种模式，详情参考SidePattern
                .setSidePattern(SidePattern.RESULT_SIDE)
                // 设置浮窗的标签，用于区分多个浮窗
                .setTag(SisterX.TAG)
                // 设置浮窗是否可拖拽，默认可拖拽
                .setDragEnable(true)
                // 系统浮窗是否包含EditText，仅针对系统浮窗，默认不包含
                .hasEditText(false)
                // 设置浮窗固定坐标，ps：设置固定坐标，Gravity属性和offset属性将无效
                .setLocation(location[0], location[1])
                // 设置浮窗的对齐方式和坐标偏移量
                // .setGravity(Gravity.END or Gravity.CENTER_VERTICAL, 0, 0)
                // 设置宽高是否充满父布局，直接在xml设置match_parent属性无效
                .setMatchParent(widthMatch = false, heightMatch = false)
                // 设置Activity浮窗的出入动画，可自定义，实现相应接口即可（策略模式），无需动画直接设置为null
                .setAnimator(DefaultAnimator())
                // 设置系统浮窗的出入动画，使用同上
                .setAppFloatAnimator(AppFloatDefaultAnimator())
                // 设置系统浮窗的不需要显示的页面
                // .setFilter(MainActivity::class.java, SecondActivity::class.java)
                // 浮窗的一些状态回调，如：创建结果、显示、隐藏、销毁、touchEvent、拖拽过程、拖拽结束。
                // ps：通过Kotlin DSL实现的回调，可以按需复写方法，用到哪个写哪个
                .registerCallbacks(this)
                .show()
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }

    fun hideFloatWindow() {
        EasyFloat.dismissAppFloat(SisterX.TAG)
    }

    private fun setView(flag: Int, view: View) {
        val rIsInvisible = SisterX.bufferMsgItems.isEmpty()
        val binding = SisterLayoutFloatingBinding.bind(
            view.findViewById(R.id.sister_layout_floating)
        )
        binding.viewDotLeftTop.isInvisible = true
        binding.viewDotLeftBottom.isInvisible = true
        binding.viewDotRightTop.isInvisible = true
        binding.viewDotRightBottom.isInvisible = true
        when (flag) {
            1 -> {
                binding.viewDotRightTop.isInvisible = rIsInvisible
            }
            2 -> {
                binding.viewDotLeftBottom.isInvisible = rIsInvisible
            }
            3 -> {
                binding.viewDotLeftTop.isInvisible = rIsInvisible
            }
            4 -> {
                binding.viewDotLeftTop.isInvisible = rIsInvisible
            }
        }
    }
}