package com.tobot.common.util;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * 手机号与固定电话的验证
 *
 * @author houdeming
 * @date 2018/8/28
 */
public class PhoneValidatorUtils {
    /**
     * 手机号
     */
    private static final String MOBILE_REGEX = "^((13[0-9])|(15[^4])|(166)|(17[0-8])|(18[0-9])|(19[8-9])|(147,145))\\d{8}$";
    /**
     * 固定电话
     */
    private static final String TELEPHONE_REGEX = "^((0[0-9]{2,3})|(852)|(853))-?([0-9]{6,9})$";

    /**
     * 匹配电话号码
     *
     * @param number
     * @return
     */
    public static boolean matchPhone(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        if (Pattern.matches(MOBILE_REGEX, number) || Pattern.matches(TELEPHONE_REGEX, number)) {
            return true;
        }
        return false;
    }

    /**
     * 匹配手机号
     *
     * @param number
     * @return
     */
    public static boolean matchMobilePhone(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        return Pattern.matches(MOBILE_REGEX, number);
    }

    /**
     * 匹配座机电话
     *
     * @param number
     * @return
     */
    public static boolean matchTelePhone(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        return Pattern.matches(TELEPHONE_REGEX, number);
    }

    /**
     * 手机号隐藏前7位
     *
     * @param phone
     * @return
     */
    public static String getHideFront7Phone(String phone) {
        if (matchMobilePhone(phone)) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0, length = phone.length(); i < length; i++) {
                if (i <= 6) {
                    builder.append("*");
                } else {
                    builder.append(phone.charAt(i));
                }
            }
            return builder.toString();
        }
        return phone;
    }
}
