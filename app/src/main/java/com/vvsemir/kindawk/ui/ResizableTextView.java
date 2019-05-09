package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.vvsemir.kindaimageloader.ILoaderCallback;
import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.R;

import org.xml.sax.XMLReader;



public class ResizableTextView extends AppCompatTextView{
    private static final int MAX_LINES_NORMAL = 10;
    private static final int MAX_LINES_EXTENDED = 300;
    private boolean extended = false;
    Context context;

    public ResizableTextView(Context context) {
        super(context);
        this.context = context;
    }

    public ResizableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ResizableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setBody(final String text) {

        /*DisplayMetrics metrics = context.getResources().getSystem().getDisplayMetrics();

        int originalWidthScaled = (int) (bitmap.getWidth() * metrics.density);
        int originalHeightScaled = (int) (bitmap.getHeight() * metrics.density);
        if (originalWidthScaled > (metrics.widthPixels * 70) / 100) {
            width = (metrics.widthPixels * 70) / 100;

            height = bitmap.getHeight() * width
                    / bitmap.getWidth();
        } else {
            height = originalHeightScaled;
            width = originalWidthScaled;
        }*/
        setText(text);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        setMaxLines(extended? MAX_LINES_EXTENDED : MAX_LINES_NORMAL);
    }
}


