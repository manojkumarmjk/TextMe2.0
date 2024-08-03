package com.bymjk.txtme.Components;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.TypefaceSpan;
import android.text.util.Linkify;
import android.graphics.Color;
import android.text.style.BulletSpan;

import androidx.core.content.res.ResourcesCompat;

import com.bymjk.txtme.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFormatter {

    private static Typeface extraBoldTypeface;

    public static Spannable formatText(Context context, String inputText) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(inputText);

        try {

            if (extraBoldTypeface == null) {
                extraBoldTypeface = ResourcesCompat.getFont(context, R.font.poppins_bold);
            }

            // Format links
            Linkify.addLinks(spannable, Linkify.WEB_URLS);

            // Format numbers
            Pattern numberPattern = Pattern.compile("\\b\\d{8,}\\b");
            Matcher numberMatcher = numberPattern.matcher(spannable);
            while (numberMatcher.find()) {
                spannable.setSpan(new ForegroundColorSpan(Color.BLUE), numberMatcher.start(), numberMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            // Format italics
            Pattern italicPattern = Pattern.compile("_(.*?)_");
            Matcher italicMatcher = italicPattern.matcher(spannable);
            while (italicMatcher.find()) {
                spannable.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), italicMatcher.start(), italicMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.delete(italicMatcher.start(), italicMatcher.start() + 1);
                spannable.delete(italicMatcher.end() - 2, italicMatcher.end() - 1);
            }

            // Format extra bold
            Pattern extraBoldPattern = Pattern.compile("\\*\\^(.*?)\\^\\*");
            Matcher extraBoldMatcher = extraBoldPattern.matcher(spannable);
            while (extraBoldMatcher.find()) {
                int start = extraBoldMatcher.start();
                int end = extraBoldMatcher.end();

                // Remove the special characters first to adjust indices
                spannable.delete(start, start + 2);
                spannable.delete(end - 4, end - 2);
                end -= 4;  // Adjust end index after deletion

                // Apply the custom typeface for extra bold
                spannable.setSpan(new CustomTypefaceSpan(extraBoldTypeface), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            // Format bold
            Pattern boldPattern = Pattern.compile("\\*(.*?)\\*");
            Matcher boldMatcher = boldPattern.matcher(spannable);
            while (boldMatcher.find()) {
                spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), boldMatcher.start(), boldMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.delete(boldMatcher.start(), boldMatcher.start() + 1);
                spannable.delete(boldMatcher.end() - 2, boldMatcher.end() - 1);
            }

            // Format strikethrough
            Pattern strikethroughPattern = Pattern.compile("~(.*?)~");
            Matcher strikethroughMatcher = strikethroughPattern.matcher(spannable);
            while (strikethroughMatcher.find()) {
                spannable.setSpan(new StrikethroughSpan(), strikethroughMatcher.start(), strikethroughMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.delete(strikethroughMatcher.start(), strikethroughMatcher.start() + 1);
                spannable.delete(strikethroughMatcher.end() - 2, strikethroughMatcher.end() - 1);
            }

            // Format monospace
            Pattern monospacePattern = Pattern.compile("```(.*?)```");
            Matcher monospaceMatcher = monospacePattern.matcher(spannable);
            while (monospaceMatcher.find()) {
                spannable.setSpan(new TypefaceSpan("monospace"), monospaceMatcher.start(), monospaceMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.delete(monospaceMatcher.start(), monospaceMatcher.start() + 3);
                spannable.delete(monospaceMatcher.end() - 6, monospaceMatcher.end() - 3);
            }

            // Format inline code
            Pattern inlineCodePattern = Pattern.compile("`(.*?)`");
            Matcher inlineCodeMatcher = inlineCodePattern.matcher(spannable);
            while (inlineCodeMatcher.find()) {
                spannable.setSpan(new TypefaceSpan("monospace"), inlineCodeMatcher.start(), inlineCodeMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.delete(inlineCodeMatcher.start(), inlineCodeMatcher.start() + 1);
                spannable.delete(inlineCodeMatcher.end() - 2, inlineCodeMatcher.end() - 1);
            }

            // Format multiline quotes
            // Format multiline quotes
            Pattern quotePattern = Pattern.compile("^> (.*)");
            Matcher quoteMatcher = quotePattern.matcher(spannable);
            while (quoteMatcher.find()) {
                int start = quoteMatcher.start();
                int end = quoteMatcher.end();

                // Remove the leading '> '
                spannable.delete(start, start + 2);
                end -= 2;

                // Add space at the start and end
                spannable.insert(start, "\u2728 ");
                spannable.insert(end + 2, " ");
                end += 3; // Adjust end index to account for added spaces

                // Apply the BackgroundColorSpan to the entire line
                spannable.setSpan(new BackgroundColorSpan(0x11000000), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }


            Pattern bulletPattern = Pattern.compile("^[*\\-] (.*?)$", Pattern.MULTILINE);
            Matcher bulletMatcher = bulletPattern.matcher(spannable);
            while (bulletMatcher.find()) {
                int end =  bulletMatcher.end();
                spannable.delete(bulletMatcher.start(), bulletMatcher.start() + 1);
                end = end-1;
                spannable.setSpan(new BulletSpan(10, 0xD5007FFF), bulletMatcher.start(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            // Format numbered list
            Pattern numberedPattern = Pattern.compile("^\\d+\\. (.*?)$", Pattern.MULTILINE);
            Matcher numberedMatcher = numberedPattern.matcher(spannable);
            while (numberedMatcher.find()) {
                spannable.setSpan(new BulletSpan(10, 0xD5007FFF), numberedMatcher.start(), numberedMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return spannable;
    }
}

