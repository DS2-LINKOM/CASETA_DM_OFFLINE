package customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by one on 3/12/15.
 */
public class MyTextViewNegrita extends androidx.appcompat.widget.AppCompatTextView {

    public MyTextViewNegrita(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewNegrita(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewNegrita(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/c_negrita.ttf");
            setTypeface(tf);
        }
    }

}