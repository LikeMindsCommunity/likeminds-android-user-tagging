package com.likeminds.usertagging.util

import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.likeminds.usertagging.UserTaggingDecoderListener

object UserTaggingDecoder {
    /**
     * [REGEX_USER_TAGGING] is to encode/decode the route to be used for Tagging.
     * */
    private val REGEX_USER_TAGGING = Regex("<<([^<>]+\\|route://\\S+)>>")

    fun getRouteFromRegex(text: String?): Uri? {
        if (text.isNullOrEmpty()) {
            return null
        }
        val match = REGEX_USER_TAGGING.find(text, 0)
        if (match != null) {
            val value = match.value
            val tag = value.substring(2, value.length - 2).split("\\|".toRegex())
            val memberRoute = tag[1]
            return Uri.parse(memberRoute)
        }
        return null
    }

    fun decodeRegexIntoSpannableText(
        textView: TextView,
        text: String?,
        enableClick: Boolean,
        highlightColor: Int,
        underLineText: Boolean = false,
        isBold: Boolean = false,
        hasAtRateSymbol: Boolean = true,
        listener: UserTaggingDecoderListener? = null,
    ) {
        if (text.isNullOrEmpty()) {
            return
        }
        val matches = REGEX_USER_TAGGING.findAll(text, 0)
        textView.setText(text, TextView.BufferType.EDITABLE)
        matches.toList().asReversed().forEach { matchResult ->
            val start = matchResult.range.first
            val end = matchResult.range.last
            val value = matchResult.value
            val tag = value.substring(2, value.length - 2).split("\\|".toRegex())
            val spannableString = if (hasAtRateSymbol) {
                "@${tag[0]}"
            } else {
                tag[0]
            }
            val memberName = SpannableString(spannableString)
            if (enableClick) {
                memberName.setSpan(
                    UserTaggingClickableSpan(
                        highlightColor,
                        value,
                        underLineText
                    ) { regex ->
                        try {
                            val tagUri = getRouteFromRegex(regex)
                                ?: return@UserTaggingClickableSpan
                            textView.setOnClickListener { return@setOnClickListener }
                            listener?.onTagClick(tagUri)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, 0, memberName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                val style = if (isBold) {
                    StyleSpan(Typeface.BOLD)
                } else {
                    ForegroundColorSpan(highlightColor)
                }
                memberName.setSpan(
                    style,
                    0,
                    memberName.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            textView.setText(
                textView.editableText.replace(
                    start,
                    end + 1,
                    memberName
                ), TextView.BufferType.EDITABLE
            )
        }
    }

    /**
     * @return decoded String with member names and text instead of the whole encoded text
     * example - "Hi, <<Harsh|route://user_profile/181>>. How are you?" will return "Hi, @Harsh. How are you?"
     */
    @JvmStatic
    fun decode(text: String?): String {
        if (text.isNullOrEmpty()) {
            return ""
        }
        val matches = REGEX_USER_TAGGING.findAll(text, 0)
        if (matches.count() == 0) {
            return text
        }
        val stringBuilder = StringBuilder()
        var lastIndex = 0
        matches.forEach { matchResult ->
            val start = matchResult.range.first
            val end = matchResult.range.last
            val value = matchResult.value
            if (start > lastIndex) {
                stringBuilder.append(text.substring(lastIndex, start))
            }
            val tag = value.substring(2, value.length - 2).split("\\|".toRegex())
            val name = "@${tag.firstOrNull()}"
            stringBuilder.append(name)
            lastIndex = end + 1
        }
        if (text.length >= lastIndex) {
            stringBuilder.append(text.substring(lastIndex))
        }
        return stringBuilder.toString()
    }

    /**
     * Decodes [text] containing the regex, highlights it with [highlightColor] and sets it on [editText] with tagging spans
     */
    @JvmStatic
    fun decode(editText: EditText, text: String?, highlightColor: Int) {
        if (text.isNullOrEmpty()) {
            return
        }
        val matches = REGEX_USER_TAGGING.findAll(text, 0)
        editText.setText(text, TextView.BufferType.EDITABLE)
        matches.toList().asReversed().forEach { matchResult ->
            val start = matchResult.range.first
            val end = matchResult.range.last
            val value = matchResult.value
            val tag = value.substring(2, value.length - 2).split("\\|".toRegex())
            val memberName = SpannableString("@${tag[0]}")
            memberName.setSpan(
                UserTaggingClickableSpan(highlightColor, value),
                0,
                memberName.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            editText.setText(
                editText.editableText.replace(
                    start,
                    end + 1,
                    memberName
                ), TextView.BufferType.EDITABLE
            )
        }
    }

    /**
     * Decodes the given [text], searches for all tagged members and returns a list of Pair
     * containing member id and member name sequentially.
     * @return List<Pair<Member Id, Member Name>>
     */
    @JvmStatic
    fun decodeAndReturnAllTaggedMembers(text: String?): List<Pair<String, String>> {
        if (text.isNullOrEmpty()) {
            return emptyList()
        }
        val matches = REGEX_USER_TAGGING.findAll(text, 0)
        if (matches.count() == 0) {
            return emptyList()
        }
        val result = mutableListOf<Pair<String, String>>()
        matches.forEach { matchResult ->
            val value = matchResult.value
            val tag = value.substring(2, value.length - 2).split("\\|".toRegex())
            val memberName = "@${tag[0]}"
            val memberRoute = tag[1]
            val routeSplits = memberRoute.split("/".toRegex())
            val memberId = routeSplits[routeSplits.size - 1]
            result.add(Pair(memberId, memberName))
        }
        return result
    }
}