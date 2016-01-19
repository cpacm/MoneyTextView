package net.cpacm.library;

import android.text.TextUtils;

import java.text.DecimalFormat;

/**
 * A class used to store the split texts
 *
 * Auther: cpacm
 * Date: 2016/1/18
 */
public class SplitText {
    private String text;
    private boolean isNumber;
    private int startPosition;
    private int pointPosition;

    public SplitText(String text) {
        this.text = text;
        isNumber = judgeNumber();
    }

    public SplitText(String text, boolean isNumber) {
        this.text = text;
        this.isNumber = isNumber;
    }

    public int length() {
        if (TextUtils.isEmpty(text)) return 0;
        return text.length();
    }

    public boolean judgeNumber() {
        pointPosition = -1;
        int point = 0;
        for (int i = 0; i < length(); i++) {
            if (text.charAt(i) == '.') {
                pointPosition = i;
                point++;
                continue;
            }
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        if (point > 1) {
            pointPosition = -1;
            return false;
        }
        return true;
    }

    public int getPointPosition() {
        pointPosition = -1;
        for (int i = 0; i < length(); i++) {
            if (text.charAt(i) == '.') {
                pointPosition = i;
                break;
            }
        }
        return pointPosition;
    }

    /**
     * Format numbers in a given format
     *
     * @param format
     * @return String
     */
    public String format(String format) {
        if (isNumber && !TextUtils.isEmpty(format)) {
            DecimalFormat decimalFormat = new DecimalFormat(format);
            decimalFormat.applyPattern(format);
            text = decimalFormat.format(Double.valueOf(text));
        }
        return text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setIsNumber(boolean isNumber) {
        this.isNumber = isNumber;
    }

    public boolean isNumber() {
        return isNumber;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }
}
