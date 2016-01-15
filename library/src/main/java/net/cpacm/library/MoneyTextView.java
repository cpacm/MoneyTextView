package net.cpacm.library;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * 使用自定义字体的文本显示框<br/>
 * A textView whose use custom font type
 *
 * @Auther: cpacm
 * @Date: 2015/10/26 0026-下午 3:59
 */
public class MoneyTextView extends TextView {

    private final static String DEFAULT_FONT = "money.otf";
    CustomTypefaceSpan customTypefaceSpan;
    private float moneySize;
    private int moneyColor;
    private MoneyFormat moneyFormat;
    private MoneyMode moneyMode;
    private String moneyText;
    private String moneyFont;

    private MoneyTextView(Context context) {
        super(context);
        init(context, null);
    }

    public MoneyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MoneyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        init(context);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.MoneyTextView);
                 /*这里从集合里取出相对应的属性值,第二参数是如果使用者没用配置该属性时所用的默认值*/
        moneyColor = typeArray.getColor(R.styleable.MoneyTextView_moneyColor, getTextColors().getDefaultColor());
        moneySize = typeArray.getDimension(R.styleable.MoneyTextView_moneySize, getTextSize());
        moneyMode = MoneyMode.getModeById(typeArray.getInteger(R.styleable.MoneyTextView_moneyMode, 0));
        moneyFormat = MoneyFormat.getFormatById(typeArray.getInteger(R.styleable.MoneyTextView_moneyFormat, 0));
        moneyText = typeArray.getString(R.styleable.MoneyTextView_moneyText);
        moneyFont = typeArray.getString(R.styleable.MoneyTextView_moneyFont);
        if (TextUtils.isEmpty(moneyFont)) moneyFont = DEFAULT_FONT;
        if (TextUtils.isEmpty(moneyText)) moneyText = getText().toString();
        AssetManager assertMgr = context.getAssets();
        Typeface fontType = Typeface.createFromAsset(assertMgr, moneyFont);
        customTypefaceSpan = new CustomTypefaceSpan("sans-serif", fontType);
        typeArray.recycle();
        setMoneyText(moneyText);
    }

    public void init(Context context) {

        moneySize = getTextSize();
        moneyColor = getTextColors().getDefaultColor();
        moneyMode = MoneyMode.ALL;
        moneyFormat = MoneyFormat.FORMAT_DISABLE;
        moneyText = getText().toString();
    }

    public void setText(String text) {
        SpannableString spanText = dealMoney(text);
        super.setText(spanText);
    }

    public void setMoneyText(String moneyText) {
        this.moneyText = moneyText;
        setText(moneyText);
    }


    private SpannableString dealMoney(CharSequence text) {
        SpannableString richStr = new SpannableString("");
        if (TextUtils.isEmpty(text))
            return richStr;
        switch (moneyMode) {
            case ALL:
                text = format(text.toString(), moneyFormat.getFormat());
                richStr = new SpannableString(text);
                richStr = setTextFontFamilySpan(richStr, 0, richStr.length());
                richStr = setTextColorSpan(richStr, 0, richStr.length(), moneyColor);
                break;
            case DIGIT:
                text = format(text.toString(), moneyFormat.getFormat());
                richStr = new SpannableString(text);
                break;
        }
        return richStr;
    }

    public boolean dealNumber(String text) {
        String reg = "[\\d]+\\.[\\d]+";
        return Pattern.compile(reg).matcher(text).find();
    }

    /**
     * 按给定格式格式化数字
     *
     * @param text   需要格式化的数字
     * @param format 格式
     * @return String
     */
    public String format(String text, String format) {
        if (TextUtils.isEmpty(format)) return text;
        if (dealNumber(text)) {
            DecimalFormat decimalFormat = new DecimalFormat(format);
            decimalFormat.applyPattern(format);
            return decimalFormat.format(Double.valueOf(text));
        } else return text;

    }

    /**
     * 设置文字的相对大小，默认为1f
     *
     * @param spannable
     * @param start
     * @param end
     * @param rate
     * @return
     */
    public SpannableString setRelativeSpan(SpannableString spannable, int start, int end, float rate) {
        spannable.setSpan(new RelativeSizeSpan(rate), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     * 设置文字字体
     *
     * @param spannable 需要设置的文字
     * @param start     开始的位置
     * @param end       结束的位置
     * @return spannable
     */
    public SpannableString setTextFontFamilySpan(SpannableString spannable, int start, int end) {
        spannable.setSpan(customTypefaceSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     * 设置文字颜色
     *
     * @param spannable 需要设置的文字
     * @param start     开始位置
     * @param end       结束位置
     * @param color     颜色
     * @return spannable
     */
    public SpannableString setTextColorSpan(SpannableString spannable, int start, int end, int color) {
        spannable.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    enum MoneyFormat {
        FORMAT_DISABLE(0, ""), FORMAT_INTEGER(1, "#,###"), FORMAT_FLOAT(2, "###,##0.00");
        private int id;
        private String format;

        MoneyFormat(int id, String format) {
            this.id = id;
            this.format = format;
        }

        public static MoneyFormat getFormatById(int id) {
            switch (id) {
                case 0:
                    return FORMAT_DISABLE;
                case 1:
                    return FORMAT_INTEGER;
                case 2:
                    return FORMAT_FLOAT;
                default:
                    return FORMAT_DISABLE;
            }
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }

    enum MoneyMode {
        ALL(0), DIGIT(1);
        private int id;

        MoneyMode(int id) {
            this.id = id;
        }

        public static MoneyMode getModeById(int id) {
            switch (id) {
                case 0:
                    return ALL;
                case 1:
                    return DIGIT;
                default:
                    return ALL;
            }
        }
    }


    public class CustomTypefaceSpan extends TypefaceSpan {
        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        private void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }
            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }
            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }
            paint.setTypeface(tf);
        }
    }

}
