package com.mobile.sdk.sister.ui.chat

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.widget.EditText
import android.widget.TextView
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.sdk.sister.R
import java.util.regex.Matcher
import java.util.regex.Pattern

object EmotionHandle {

    private const val PAGE_COUNT = 24
    const val ROW_COUNT = 9
    private val sPatternEmotion: Pattern =
        Pattern.compile("\\[([\u4e00-\u9fa5\\w])+\\]|[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]")

    data class EmotionInfo(val textContent: String, val drawableId: Int)

    private var emotionInfo: List<EmotionInfo> = listOf(
        EmotionInfo("[emoji_01]", R.drawable.emoji_01),
        EmotionInfo("[emoji_02]", R.drawable.emoji_02),
        EmotionInfo("[emoji_03]", R.drawable.emoji_03),
        EmotionInfo("[emoji_04]", R.drawable.emoji_04),
        EmotionInfo("[emoji_05]", R.drawable.emoji_05),
        EmotionInfo("[emoji_06]", R.drawable.emoji_06),
        EmotionInfo("[emoji_07]", R.drawable.emoji_07),
        EmotionInfo("[emoji_08]", R.drawable.emoji_08),
        EmotionInfo("[emoji_09]", R.drawable.emoji_09),
        EmotionInfo("[emoji_10]", R.drawable.emoji_10),
        EmotionInfo("[emoji_11]", R.drawable.emoji_11),
        EmotionInfo("[emoji_12]", R.drawable.emoji_12),
        EmotionInfo("[emoji_13]", R.drawable.emoji_13),
        EmotionInfo("[emoji_14]", R.drawable.emoji_14),
        EmotionInfo("[emoji_15]", R.drawable.emoji_15),
        EmotionInfo("[emoji_16]", R.drawable.emoji_16),
        EmotionInfo("[emoji_17]", R.drawable.emoji_17),
        EmotionInfo("[emoji_18]", R.drawable.emoji_18),
        EmotionInfo("[emoji_19]", R.drawable.emoji_19),
        EmotionInfo("[emoji_20]", R.drawable.emoji_20),
        EmotionInfo("[emoji_21]", R.drawable.emoji_21),
        EmotionInfo("[emoji_22]", R.drawable.emoji_22),
        EmotionInfo("[emoji_23]", R.drawable.emoji_23),
        EmotionInfo("[emoji_24]", R.drawable.emoji_24),
        EmotionInfo("[emoji_25]", R.drawable.emoji_25),
        EmotionInfo("[emoji_26]", R.drawable.emoji_26),
        EmotionInfo("[emoji_27]", R.drawable.emoji_27),
        EmotionInfo("[emoji_28]", R.drawable.emoji_28),
        EmotionInfo("[emoji_29]", R.drawable.emoji_29),
        EmotionInfo("[emoji_30]", R.drawable.emoji_30),
        EmotionInfo("[emoji_31]", R.drawable.emoji_31),
        EmotionInfo("[emoji_32]", R.drawable.emoji_32),
        EmotionInfo("[emoji_33]", R.drawable.emoji_33),
        EmotionInfo("[emoji_34]", R.drawable.emoji_34),
        EmotionInfo("[emoji_35]", R.drawable.emoji_35),
        EmotionInfo("[emoji_36]", R.drawable.emoji_36),
        EmotionInfo("[emoji_37]", R.drawable.emoji_37),
        EmotionInfo("[emoji_38]", R.drawable.emoji_38),
        EmotionInfo("[emoji_39]", R.drawable.emoji_39),
        EmotionInfo("[emoji_40]", R.drawable.emoji_40),
        EmotionInfo("[emoji_41]", R.drawable.emoji_41),
        EmotionInfo("[emoji_42]", R.drawable.emoji_42),
        EmotionInfo("[emoji_43]", R.drawable.emoji_43),
        EmotionInfo("[emoji_44]", R.drawable.emoji_44),
        EmotionInfo("[emoji_45]", R.drawable.emoji_45),
        EmotionInfo("[emoji_46]", R.drawable.emoji_46),
        EmotionInfo("[emoji_47]", R.drawable.emoji_47),
        EmotionInfo("[emoji_48]", R.drawable.emoji_48),
        EmotionInfo("[emoji_49]", R.drawable.emoji_49),
        EmotionInfo("[emoji_50]", R.drawable.emoji_50),
        EmotionInfo("[emoji_51]", R.drawable.emoji_51),
        EmotionInfo("[emoji_52]", R.drawable.emoji_52),
        EmotionInfo("[emoji_53]", R.drawable.emoji_53),
        EmotionInfo("[emoji_54]", R.drawable.emoji_54),
        EmotionInfo("[emoji_55]", R.drawable.emoji_55),
        EmotionInfo("[emoji_56]", R.drawable.emoji_56),
        EmotionInfo("[emoji_57]", R.drawable.emoji_57),
        EmotionInfo("[emoji_58]", R.drawable.emoji_58),
        EmotionInfo("[emoji_59]", R.drawable.emoji_59),
        EmotionInfo("[emoji_60]", R.drawable.emoji_60),
        EmotionInfo("[emoji_61]", R.drawable.emoji_61),
        EmotionInfo("[emoji_62]", R.drawable.emoji_62),
        EmotionInfo("[emoji_63]", R.drawable.emoji_63),
        EmotionInfo("[emoji_64]", R.drawable.emoji_64),
        EmotionInfo("[emoji_65]", R.drawable.emoji_65),
        EmotionInfo("[emoji_66]", R.drawable.emoji_66),
        EmotionInfo("[emoji_67]", R.drawable.emoji_67),
        EmotionInfo("[emoji_68]", R.drawable.emoji_68),
        EmotionInfo("[emoji_69]", R.drawable.emoji_69),
        EmotionInfo("[emoji_70]", R.drawable.emoji_70),
        EmotionInfo("[emoji_71]", R.drawable.emoji_71),
        EmotionInfo("[emoji_72]", R.drawable.emoji_72),
        EmotionInfo("[emoji_73]", R.drawable.emoji_73),
        EmotionInfo("[emoji_74]", R.drawable.emoji_74),
        EmotionInfo("[emoji_75]", R.drawable.emoji_75),
        EmotionInfo("[emoji_76]", R.drawable.emoji_76),
        EmotionInfo("[emoji_77]", R.drawable.emoji_77),
    )

    fun splitEmotionInfo(): ArrayList<List<EmotionInfo>> {
        val size = emotionInfo.size
        //计算可以分成多少页
        val page = (size + PAGE_COUNT - 1) / PAGE_COUNT
        val newList = ArrayList<List<EmotionInfo>>(page)
        for (i in 0 until page) {
            // 开始位置
            val fromIndex: Int = i * PAGE_COUNT
            // 结束位置
            val toIndex: Int =
                if ((i + 1) * PAGE_COUNT < size) (i + 1) * PAGE_COUNT else size
            newList.add(emotionInfo.subList(fromIndex, toIndex))
        }
        return newList
    }

    fun showEmotionText(view: TextView, textContent: String) {
        val spannable =
            buildEmotionSpannable(textContent, view.textSize.toInt())
        if (view is EditText) {
            val start = view.selectionStart
            val editable = view.editableText
            editable?.insert(start, spannable)
        } else {
            view.text = spannable
        }
    }

    fun deleteEmotionText(view: EditText) {
        val selectionStart: Int = view.selectionStart // 获取光标的位置
        val editableText = view.editableText
        val body = view.text
        if (selectionStart > 0) {
            if (body.isNotEmpty()) {
                val tempStr = body.substring(0, selectionStart)
                val startIndex = tempStr.lastIndexOf("[") // 获取最后一个表情开始的位置
                val endIndex = tempStr.lastIndexOf("]") // 获取最后一个表情结束的位置
                if (endIndex != -1 && selectionStart == endIndex + 1) { //最后一个字符是表情
                    editableText.delete(startIndex, selectionStart)
                    return
                }
                editableText.delete(selectionStart - 1, selectionStart)
            }
        }
    }

    private fun buildEmotionSpannable(text: String, textSize: Int): Spannable? {
        val matcherEmotion: Matcher = sPatternEmotion.matcher(text)
        val spannableString = SpannableString(text)
        while (matcherEmotion.find()) {
            val key: String = matcherEmotion.group()
            val imgRes: Int = EmotionHandle.getImageByName(key)
            if (imgRes != -1) {
                val start: Int = matcherEmotion.start()
                val span = createImageSpanByRes(imgRes, AndroidX.myApp, textSize)
                spannableString.setSpan(
                    span,
                    start,
                    start + key.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return spannableString
    }

    private fun createImageSpanByRes(imgRes: Int, context: Context, textSize: Int): ImageSpan {
        val res: Resources = context.resources
        val bitmap = BitmapFactory.decodeResource(res, imgRes)
        val span: ImageSpan?
        val size = textSize * 15 / 10
        val scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true)
        span = ImageSpan(context, scaleBitmap)
        return span
    }

    private fun getImageByName(name: String): Int {
        emotionInfo.forEach {
            if (it.textContent == name) {
                return it.drawableId
            }
        }
        return -1
    }
}