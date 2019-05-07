package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.Spannable;
import android.util.AttributeSet;

import com.vvsemir.kindaimageloader.ILoaderCallback;
import com.vvsemir.kindaimageloader.ImageLoader;

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

    public void setBody(String text, String imageUri) {
        //<img src="image.jpg" style="float:right" /><div>text</div>
        ImageLoaderGetter imageGetter = new ImageLoaderGetter(this, context);

        String content = "<img src=\""+ imageUri + "\" style=\"float:right\" /><div>" + text + "</div>" ;

        Spannable html = (Spannable) Html.fromHtml(content, imageGetter, null);

        setText(html);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        setMaxLines(extended? MAX_LINES_EXTENDED : MAX_LINES_NORMAL);
    }

    public class ImageLoaderGetter implements Html.ImageGetter {
        private ResizableTextView textView = null;
        Context context = null;

        public ImageLoaderGetter() {

        }

        public ImageLoaderGetter(ResizableTextView target, Context context) {
            textView = target;
            this.context = context;
        }

        @Override
        public Drawable getDrawable(String source) {
            final UriBitmapDrawable uriBitmapDrawable = new UriBitmapDrawable();
            ImageLoader.getInstance(context).loadAndReturnBitmap(source, new ILoaderCallback<Bitmap>() {

                @Override
                public void onResult(Bitmap bitmap) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
                    bitmapDrawable.setBounds(0, 0, bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());
                    uriBitmapDrawable.drawable = bitmapDrawable;
                    textView.invalidate();
                }

                @Override
                public void onError(Throwable throwable) {

                }
            });

            return uriBitmapDrawable;
        }

        private class UriBitmapDrawable extends BitmapDrawable {

            protected Drawable drawable;

            @Override
            public void draw(final Canvas canvas) {
                if (drawable != null) {
                    drawable.draw(canvas);
                }
            }

        }
    }
}
