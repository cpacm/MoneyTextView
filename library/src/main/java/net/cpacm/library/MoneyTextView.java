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
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用自定义字体的文本显示框<br/>
 *
 * @Auther: cpacm
 * @Date: 2015/10/26 0026-下午 3:59
 */
public class MoneyTextView extends TextView {

    private Context mContext;
    CustomTypefaceSpan customTypefaceSpan;
    private float moneyRate;
    private int moneyColor;
    private MoneyFormat moneyFormat;
    private MoneyMode moneyMode;
    private String moneyText;
    private String moneyFont;
    private String symbol;
    private float symbolRate;
    private float decimalRate;

    private List<SplitText> textList;

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
        mContext = context;
        textList = new ArrayList<>();
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.MoneyTextView);
                 /*这里从集合里取出相对应的属性值,第二参数是如果使用者没用配置该属性时所用的默认值*/
        moneyColor = typeArray.getColor(R.styleable.MoneyTextView_moneyColor, getTextColors().getDefaultColor());
        moneyRate = typeArray.getFloat(R.styleable.MoneyTextView_moneyRate, 1f);
        moneyMode = MoneyMode.getModeById(typeArray.getInteger(R.styleable.MoneyTextView_moneyMode, 0));
        moneyFormat = MoneyFormat.getFormatById(typeArray.getInteger(R.styleable.MoneyTextView_moneyFormat, 0));
        moneyText = typeArray.getString(R.styleable.MoneyTextView_moneyText);
        moneyFont = typeArray.getString(R.styleable.MoneyTextView_moneyFont);
        symbol = typeArray.getString(R.styleable.MoneyTextView_symbol);
        if (TextUtils.isEmpty(symbol)) symbol = "";
        symbolRate = typeArray.getFloat(R.styleable.MoneyTextView_symbolRate, moneyRate);
        decimalRate = typeArray.getFloat(R.styleable.MoneyTextView_decimalRate, moneyRate);
        if (TextUtils.isEmpty(moneyText)) moneyText = getText().toString();
        typeArray.recycle();
        setMoneyText(moneyText);
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
        String str = dealText(text.toString());
        switch (moneyMode) {
            case ALL:
                richStr = new SpannableString(str);
                for (SplitText splitText : textList) {
                    richStr = setTextFontFamilySpan(richStr, splitText.getStartPosition(), splitText.getStartPosition() + splitText.length());
                    richStr = setTextColorSpan(richStr, splitText.getStartPosition(), splitText.getStartPosition() + splitText.length(), moneyColor);
                    int length = symbol.length();
                    if (length > 0 && splitText.isNumber()) {
                        richStr = setRelativeSpan(richStr, splitText.getStartPosition(), splitText.getStartPosition() + length, symbolRate);
                        richStr = setRelativeSpan(richStr, splitText.getStartPosition() + length, splitText.getStartPosition() + splitText.length(), moneyRate);
                    } else
                        richStr = setRelativeSpan(richStr, splitText.getStartPosition(), splitText.getStartPosition() + splitText.length(), moneyRate);
                    if (splitText.getPointPosition() >= 0 && splitText.length() > 0 && splitText.isNumber()) {
                        int dec = splitText.getPointPosition();
                        richStr = setRelativeSpan(richStr, splitText.getStartPosition() + dec, splitText.getStartPosition() + splitText.length(), decimalRate);
                    }
                }
                break;
            case DIGIT:
                richStr = new SpannableString(str);
                for (SplitText splitText : textList) {
                    if (!splitText.isNumber()) continue;
                    richStr = setTextFontFamilySpan(richStr, splitText.getStartPosition(), splitText.getStartPosition() + splitText.length());
                    richStr = setTextColorSpan(richStr, splitText.getStartPosition(), splitText.getStartPosition() + splitText.length(), moneyColor);
                    int length = symbol.length();
                    if (length > 0) {
                        richStr = setRelativeSpan(richStr, splitText.getStartPosition(), splitText.getStartPosition() + length, symbolRate);
                        richStr = setRelativeSpan(richStr, splitText.getStartPosition() + length, splitText.getStartPosition() + splitText.length(), moneyRate);
                    } else
                        richStr = setRelativeSpan(richStr, splitText.getStartPosition(), splitText.getStartPosition() + splitText.length(), moneyRate);
                    if (splitText.getPointPosition() >= 0 && splitText.length() > 0 && splitText.isNumber()) {
                        int dec = splitText.getPointPosition();
                        richStr = setRelativeSpan(richStr, splitText.getStartPosition() + dec, splitText.getStartPosition() + splitText.length(), decimalRate);
                    }
                }
                break;
        }
        return richStr;
    }

    private String dealText(String text) {
        textList.clear();
        String str = "";
        boolean isNum = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                if (!isNum) {
                    isNum = true;
                    if (!TextUtils.isEmpty(str)) {
                        textList.add(new SplitText(str, false));
                    }
                    str = "";
                }
                str += String.valueOf(c);
            } else {
                if (isNum) {
                    isNum = false;
                    if (!TextUtils.isEmpty(str)) {
                        textList.add(new SplitText(str));
                    }
                    str = "";
                }
                str += String.valueOf(c);
            }
        }
        if (!TextUtils.isEmpty(str)) {
            textList.add(new SplitText(str));
        }
        str = "";
        int startPosition = 0;
        for (SplitText splitText : textList) {
            splitText.format(moneyFormat.getFormat());
            splitText.setStartPosition(startPosition);
            if (splitText.isNumber()) {
                splitText.setText(symbol + splitText.getText());
            }
            str += splitText.getText();
            startPosition += splitText.length();
        }
        return str;
    }

    /**
     * 设置文字的显示模式<br/>
     *
     * @param mode ALL:改变所有文字, DIGIT;只改变数字
     * @see MoneyMode
     */
    public void setMoneyMode(MoneyMode mode) {
        this.moneyMode = mode;
        setMoneyText(this.moneyText);
    }

    /**
     * 设置金钱文字前的显示符号<br/>
     *
     * @param symbol 金钱符号
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
        setMoneyText(this.moneyText);
    }

    /**
     * 设置金钱文字的颜色
     *
     * @param color
     */
    public void setMoneyColor(int color) {
        moneyColor = color;
        setMoneyText(this.moneyText);
    }

    /**
     * 设置金钱的相对大小
     *
     * @param rate
     */
    public void setMoneyRate(float rate) {
        moneyRate = rate;
        setMoneyText(this.moneyText);
    }

    /**
     * 设置金钱符号的相对大小
     *
     * @param rate
     */
    public void setSymbolRate(float rate) {
        symbolRate = rate;
        setMoneyText(this.moneyText);
    }

    /**
     * 设置小数点后面的小数相对大小(只在FORMAT_FLOAT下有用)
     *
     * @param rate
     */
    public void setDecimalRate(float rate) {
        decimalRate = rate;
        setMoneyText(this.moneyText);
    }

    /**
     * 金钱数字的转换方式
     *
     * @param moneyFormat
     * @see MoneyFormat
     */
    public void setMoneyFormat(MoneyFormat moneyFormat) {
        this.moneyFormat = moneyFormat;
        setMoneyText(this.moneyText);
    }

    /**
     * 设置金钱文字的字体，从assets读取
     *
     * @param moneyFont
     */
    public void setMoneyFont(String moneyFont) {
        this.moneyFont = moneyFont;
        setMoneyText(this.moneyText);
    }

    public void setMoneyText(float text) {
        this.moneyText = String.valueOf(text);
        setText(moneyText);
    }

    public void setMoneyText(int text) {
        this.moneyText = String.valueOf(text);
        setText(moneyText);
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
        if (!TextUtils.isEmpty(moneyFont)) {
            AssetManager assertMgr = mContext.getAssets();
            try {
                Typeface fontType = Typeface.createFromAsset(assertMgr, moneyFont);
                customTypefaceSpan = new CustomTypefaceSpan("sans-serif", fontType);
                spannable.setSpan(customTypefaceSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                System.gc();
            } catch (RuntimeException e) {
                Log.e("MoneyTextView", "can't find font file from assets");
            }

        }
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

    public enum MoneyFormat {
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

    public enum MoneyMode {
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