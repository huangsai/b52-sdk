package com.mobile.sdk.sister.ui

import com.mobile.sdk.sister.R

object EmojiHandle {

    private const val PAGE_COUNT = 24
    const val ROW_COUNT = 9

    data class EmojiInfo(val textContent: String, val drawableId: Int)

    var emojiInfos: List<EmojiInfo> = listOf(
        EmojiInfo("[emoji_01]", R.drawable.emoji_01),
        EmojiInfo("[emoji_02]", R.drawable.emoji_02),
        EmojiInfo("[emoji_03]", R.drawable.emoji_03),
        EmojiInfo("[emoji_04]", R.drawable.emoji_04),
        EmojiInfo("[emoji_05]", R.drawable.emoji_05),
        EmojiInfo("[emoji_06]", R.drawable.emoji_06),
        EmojiInfo("[emoji_07]", R.drawable.emoji_07),
        EmojiInfo("[emoji_08]", R.drawable.emoji_08),
        EmojiInfo("[emoji_09]", R.drawable.emoji_09),
        EmojiInfo("[emoji_10]", R.drawable.emoji_10),
        EmojiInfo("[emoji_11]", R.drawable.emoji_11),
        EmojiInfo("[emoji_12]", R.drawable.emoji_12),
        EmojiInfo("[emoji_13]", R.drawable.emoji_13),
        EmojiInfo("[emoji_14]", R.drawable.emoji_14),
        EmojiInfo("[emoji_15]", R.drawable.emoji_15),
        EmojiInfo("[emoji_16]", R.drawable.emoji_16),
        EmojiInfo("[emoji_17]", R.drawable.emoji_17),
        EmojiInfo("[emoji_18]", R.drawable.emoji_18),
        EmojiInfo("[emoji_19]", R.drawable.emoji_19),
        EmojiInfo("[emoji_20]", R.drawable.emoji_20),
        EmojiInfo("[emoji_21]", R.drawable.emoji_21),
        EmojiInfo("[emoji_22]", R.drawable.emoji_22),
        EmojiInfo("[emoji_23]", R.drawable.emoji_23),
        EmojiInfo("[emoji_24]", R.drawable.emoji_24),
        EmojiInfo("[emoji_25]", R.drawable.emoji_25),
        EmojiInfo("[emoji_26]", R.drawable.emoji_26),
        EmojiInfo("[emoji_27]", R.drawable.emoji_27),
        EmojiInfo("[emoji_28]", R.drawable.emoji_28),
        EmojiInfo("[emoji_29]", R.drawable.emoji_29),
        EmojiInfo("[emoji_30]", R.drawable.emoji_30),
        EmojiInfo("[emoji_31]", R.drawable.emoji_31),
        EmojiInfo("[emoji_32]", R.drawable.emoji_32),
        EmojiInfo("[emoji_33]", R.drawable.emoji_33),
        EmojiInfo("[emoji_34]", R.drawable.emoji_34),
        EmojiInfo("[emoji_35]", R.drawable.emoji_35),
        EmojiInfo("[emoji_36]", R.drawable.emoji_36),
        EmojiInfo("[emoji_37]", R.drawable.emoji_37),
        EmojiInfo("[emoji_38]", R.drawable.emoji_38),
        EmojiInfo("[emoji_39]", R.drawable.emoji_39),
        EmojiInfo("[emoji_40]", R.drawable.emoji_40),
        EmojiInfo("[emoji_41]", R.drawable.emoji_41),
        EmojiInfo("[emoji_42]", R.drawable.emoji_42),
        EmojiInfo("[emoji_43]", R.drawable.emoji_43),
        EmojiInfo("[emoji_44]", R.drawable.emoji_44),
        EmojiInfo("[emoji_45]", R.drawable.emoji_45),
        EmojiInfo("[emoji_46]", R.drawable.emoji_46),
        EmojiInfo("[emoji_47]", R.drawable.emoji_47),
        EmojiInfo("[emoji_48]", R.drawable.emoji_48),
        EmojiInfo("[emoji_49]", R.drawable.emoji_49),
        EmojiInfo("[emoji_50]", R.drawable.emoji_50),
        EmojiInfo("[emoji_51]", R.drawable.emoji_51),
        EmojiInfo("[emoji_52]", R.drawable.emoji_52),
        EmojiInfo("[emoji_53]", R.drawable.emoji_53),
        EmojiInfo("[emoji_54]", R.drawable.emoji_54),
        EmojiInfo("[emoji_55]", R.drawable.emoji_55),
        EmojiInfo("[emoji_56]", R.drawable.emoji_56),
        EmojiInfo("[emoji_57]", R.drawable.emoji_57),
        EmojiInfo("[emoji_58]", R.drawable.emoji_58),
        EmojiInfo("[emoji_59]", R.drawable.emoji_59),
        EmojiInfo("[emoji_60]", R.drawable.emoji_60),
        EmojiInfo("[emoji_61]", R.drawable.emoji_61),
        EmojiInfo("[emoji_62]", R.drawable.emoji_62),
        EmojiInfo("[emoji_63]", R.drawable.emoji_63),
        EmojiInfo("[emoji_64]", R.drawable.emoji_64),
        EmojiInfo("[emoji_65]", R.drawable.emoji_65),
        EmojiInfo("[emoji_66]", R.drawable.emoji_66),
        EmojiInfo("[emoji_67]", R.drawable.emoji_67),
        EmojiInfo("[emoji_68]", R.drawable.emoji_68),
        EmojiInfo("[emoji_69]", R.drawable.emoji_69),
        EmojiInfo("[emoji_70]", R.drawable.emoji_70),
        EmojiInfo("[emoji_71]", R.drawable.emoji_71),
        EmojiInfo("[emoji_72]", R.drawable.emoji_72),
        EmojiInfo("[emoji_73]", R.drawable.emoji_73),
        EmojiInfo("[emoji_74]", R.drawable.emoji_74),
        EmojiInfo("[emoji_75]", R.drawable.emoji_75),
        EmojiInfo("[emoji_76]", R.drawable.emoji_76),
        EmojiInfo("[emoji_77]", R.drawable.emoji_77),
    )

    fun splitEmojiInfos(): ArrayList<List<EmojiInfo>> {
        val size = emojiInfos.size
        //计算可以分成多少页
        val page = (size + PAGE_COUNT - 1) / PAGE_COUNT
        val newList = ArrayList<List<EmojiInfo>>(page)
        for (i in 0 until page) {
            // 开始位置
            val fromIndex: Int = i * PAGE_COUNT
            // 结束位置
            val toIndex: Int =
                if ((i + 1) * PAGE_COUNT < size) (i + 1) * PAGE_COUNT else size
            newList.add(emojiInfos.subList(fromIndex, toIndex))
        }
        return newList
    }

    fun getImageByName(name: String): Int {
        emojiInfos.forEach {
            if (it.textContent == name) {
                return it.drawableId
            }
        }
        return -1
    }
}