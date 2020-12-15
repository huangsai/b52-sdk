package com.mobile.sdk.sister.bubble

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.PointF
import android.os.Build
import android.view.*
import androidx.core.view.doOnNextLayout
import androidx.core.view.isInvisible
import androidx.core.view.updatePadding
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.sdk.sister.databinding.SisterLayoutBubbleBinding
import kotlin.math.abs

object Bubble : View.OnTouchListener {

    private const val MAX_CLICK_DURATION = 200L

    private val windowManager: WindowManager by lazy {
        AndroidX.myApp.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val firstDown = PointF()
    private val location = PointF()
    private val finalLocation = Point()
    private var windowX: Int = 0
    private var windowY: Int = 0
    private var windowWidth = 0
    private var windowHeight = 0
    private var dockDirection = 0
    private var preDockDirection = 0
    private var startClickTime = 0L
    private var hasPendingMovement = false

    var isShowing = false
        private set

    private var mBinding: SisterLayoutBubbleBinding? = null
    private val binding: SisterLayoutBubbleBinding get() = mBinding!!

    private val layoutParams = WindowManager.LayoutParams().apply {
        type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_FULLSCREEN
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        format = PixelFormat.RGBA_8888
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.START or Gravity.TOP
        x = windowX
        y = windowY
    }

    private val movementRunnable: Runnable = Runnable {
        layoutParams.x = finalLocation.x
        layoutParams.y = finalLocation.y
        windowManager.updateViewLayout(binding.root, layoutParams)
        hasPendingMovement = false
    }

    private val screenSize: Point by lazy { resolveScreenSize() }


    var callback: Callback? = null

    private fun createView() {
        require(mBinding == null)
        mBinding = SisterLayoutBubbleBinding.inflate(
            LayoutInflater.from(AndroidX.myApp),
            null,
            false
        )
        binding.root.setOnTouchListener(this)
        callback?.onViewCreated(binding.root)
        binding.root.doOnNextLayout {
            windowWidth = it.width
            windowHeight = it.height
        }
        windowManager.addView(binding.root, layoutParams)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        callback?.onTouch(v, event)
        event.setLocation(event.rawX, event.rawY)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startClickTime = System.currentTimeMillis()
                binding.root.removeCallbacks(movementRunnable)
                firstDown.set(event.x, event.y)
                location.set(layoutParams.x.toFloat(), layoutParams.y.toFloat())
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - firstDown.x
                val dy = event.y - firstDown.y
                layoutParams.x = (location.x + dx).toInt()
                layoutParams.y = (location.y + dy).toInt()
                windowManager.updateViewLayout(v, layoutParams)
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (
                    System.currentTimeMillis() - startClickTime < MAX_CLICK_DURATION &&
                    abs(event.x - firstDown.x) < 4 &&
                    abs(event.y - firstDown.y) < 4
                ) {
                    if (hasPendingMovement) {
                        movementRunnable.run()
                    }
                    return false
                }
                val rawX = event.rawX.toInt()
                val rawY = event.rawY.toInt()
                val rawX2 = screenSize.x - rawX
                val rawY2 = screenSize.y - rawY
                preDockDirection = dockDirection
                dockDirection = if (abs(rawY - rawY2) <= 16) {
                    if (rawX > rawX2) 3 else 1
                } else {
                    when (minOf(rawX, rawY, rawX2, rawY2)) {
                        rawX -> 1
                        rawX2 -> 3
                        rawY -> 2
                        else -> 4
                    }
                }
                applyViewPadding()
                when (dockDirection) {
                    1 -> finalLocation.set((0 - windowWidth / 2), layoutParams.y)
                    3 -> finalLocation.set((screenSize.x - windowWidth / 2), layoutParams.y)
                    2 -> finalLocation.set(layoutParams.x, (0 - windowHeight / 2))
                    4 -> finalLocation.set(layoutParams.x, (screenSize.y - windowHeight / 2))
                }
                // 坐标系是左上角，假如是右边或者底部贴边修正
                if (dockDirection == 3) {
                    finalLocation.x = finalLocation.x - binding.root.paddingLeft
                }
                if (dockDirection == 4) {
                    finalLocation.y = finalLocation.y - binding.root.paddingTop
                }
                hasPendingMovement = true
                binding.root.postDelayed(movementRunnable, 2000)
                // Timber.tag("ABS").e("====%s,%s", finalLocation.x, finalLocation.y)
                // Timber.tag("ABS").e("====%s,%s", windowWidth, windowHeight)
                // Timber.tag("ABS").e("====%s,%s", layoutParams.x, layoutParams.y)
                // Timber.tag("ABS").e("====%s,%s", screenSize.x, screenSize.y)
                // Timber.tag("ABS").e("====%s", dockDirection)
                // Timber.tag("ABS").e("***************")
                return true
            }
            else -> return false
        }
    }

    private fun resolveScreenSize(): Point {
        return Point().apply {
            AndroidX.myApp.display!!.getRealSize(this)
        }
    }

    private fun applyViewPadding() {
        if (preDockDirection == dockDirection) {
            return
        }
        when (dockDirection) {
            1 -> binding.root.updatePadding(0, 0, windowWidth / 2, 0)
            2 -> binding.root.updatePadding(0, 0, 0, 48)
            3 -> binding.root.updatePadding(108, 0, 0, 0)
            4 -> binding.root.updatePadding(0, windowHeight / 2, 0, 0)
            else -> binding.root.updatePadding(0, 0, 0, 0)
        }
    }

    fun show() {
        if (isShowing) {
            return
        }
        isShowing = true
        if (mBinding == null) {
            createView()
        } else {
            binding.root.isInvisible = false
        }
        callback?.onShow(binding.root)
    }

    fun hide() {
        if (!isShowing) {
            return
        }
        isShowing = false
        binding.root.isInvisible = true
        callback?.onHide(binding.root)
    }

    fun dismiss() {
        dockDirection = 0
        isShowing = false
        binding.root.setOnTouchListener(null)
        binding.root.removeCallbacks(movementRunnable)
        callback?.onDismiss()
        mBinding = null
        callback = null
    }

    fun setBadge(neededShowBadge: Boolean) {
        if (null == mBinding) {
            return
        }
        binding.viewDotLeftTop.isInvisible = true
        binding.viewDotLeftBottom.isInvisible = true
        binding.viewDotRightTop.isInvisible = true
        binding.viewDotRightBottom.isInvisible = true
        when (dockDirection) {
            1 -> {
                binding.viewDotRightTop.isInvisible = !neededShowBadge
            }
            2 -> {
                binding.viewDotLeftBottom.isInvisible = !neededShowBadge
            }
            3 -> {
                binding.viewDotLeftTop.isInvisible = !neededShowBadge
            }
            4 -> {
                binding.viewDotLeftTop.isInvisible = !neededShowBadge
            }
        }
    }

    interface Callback {

        fun onTouch(view: View, event: MotionEvent)

        fun onViewCreated(view: View)

        fun onHide(view: View)

        fun onShow(view: View)

        fun onDismiss()
    }
}