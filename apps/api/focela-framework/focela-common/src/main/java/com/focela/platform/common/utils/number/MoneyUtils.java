package com.focela.platform.common.utils.number;

import cn.hutool.core.math.Money;
import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Money utility class
 */
public class MoneyUtils {

    /**
     * Decimal scale for money amounts
     */
    private static final int PRICE_SCALE = 2;

    /**
     * BigDecimal value representing 100 percent
     */
    public static final BigDecimal PERCENT_100 = BigDecimal.valueOf(100);

    /**
     * Calculate percentage amount, rounded half up
     *
     * @param price amount
     * @param rate  percentage, e.g. pass 56.77 for 56.77%
     * @return percentage amount
     */
    public static Integer calculateRatePrice(Integer price, Double rate) {
        return calculateRatePrice(price, rate, 0, RoundingMode.HALF_UP).intValue();
    }

    /**
     * Calculate percentage amount, rounded down (floor)
     *
     * @param price amount
     * @param rate  percentage, e.g. pass 56.77 for 56.77%
     * @return percentage amount
     */
    public static Integer calculateRatePriceFloor(Integer price, Double rate) {
        return calculateRatePrice(price, rate, 0, RoundingMode.FLOOR).intValue();
    }

    /**
     * Calculate percentage amount
     *
     * @param price   amount (in cents)
     * @param count   quantity
     * @param percent discount (in cents), e.g. pass 6020 for 60.2%
     * @return total price of the items
     */
    public static Integer calculator(Integer price, Integer count, Integer percent) {
        price = price * count;
        if (percent == null) {
            return price;
        }
        return MoneyUtils.calculateRatePriceFloor(price, (double) (percent / 100));
    }

    /**
     * Calculate percentage amount
     *
     * @param price        amount
     * @param rate         percentage, e.g. pass 56.77 for 56.77%
     * @param scale        number of decimal places to keep
     * @param roundingMode rounding mode
     */
    public static BigDecimal calculateRatePrice(Number price, Number rate, int scale, RoundingMode roundingMode) {
        return NumberUtil.toBigDecimal(price).multiply(NumberUtil.toBigDecimal(rate)) // multiply
                .divide(BigDecimal.valueOf(100), scale, roundingMode); // divide by 100
    }

    /**
     * Convert cents (fen) to yuan
     *
     * @param fen cents
     * @return yuan
     */
    public static BigDecimal fenToYuan(int fen) {
        return new Money(0, fen).getAmount();
    }

    /**
     * Convert cents (fen) to yuan as string
     *
     * For example, when fen is 1, the result is 0.01
     *
     * @param fen cents
     * @return yuan
     */
    public static String fenToYuanStr(int fen) {
        return new Money(0, fen).toString();
    }

    /**
     * Multiply amounts, rounded half up by default
     *
     * Scale: {@link #PRICE_SCALE}
     *
     * @param price amount
     * @param count quantity
     * @return product of the amounts
     */
    public static BigDecimal priceMultiply(BigDecimal price, BigDecimal count) {
        if (price == null || count == null) {
            return null;
        }
        return price.multiply(count).setScale(PRICE_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Multiply amount by percentage, rounded half up by default
     *
     * Scale: {@link #PRICE_SCALE}
     *
     * @param price  amount
     * @param percent percentage
     * @return product of amount and percentage
     */
    public static BigDecimal priceMultiplyPercent(BigDecimal price, BigDecimal percent) {
        if (price == null || percent == null) {
            return null;
        }
        return price.multiply(percent).divide(PERCENT_100, PRICE_SCALE, RoundingMode.HALF_UP);
    }

}
