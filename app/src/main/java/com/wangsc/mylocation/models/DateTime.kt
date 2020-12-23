package com.wangsc.mylocation.models

import com.wangsc.mylocation.concat
import java.util.*

/**
 * Created by 阿弥陀佛 on 2015/6/24.
 */
class DateTime : GregorianCalendar {
    constructor() {
        this.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        //        this.setTimeInMillis(System.currentTimeMillis());
    }

    constructor(millinseconds: Long) {
        this.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        this.timeInMillis = millinseconds
    }

    constructor(year: Int, month: Int, day: Int) {
        this.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        this[year, month, day, 0, 0] = 0
        this[MILLISECOND] = 0
    }

    constructor(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int) {
        this.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        this[year, month, day, hour, minute] = second
        this[MILLISECOND] = 0
    }

    constructor(hour: Int, minute: Int) {
        this.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        this[HOUR_OF_DAY] = hour
        this[MINUTE] = minute
        this[SECOND] = 0
        this[MILLISECOND] = 0
    }

    /**
     * 返回一个时、分、秒、毫秒置零的此DateTime副本。
     *
     * @return
     */
    val date: DateTime
        get() = DateTime(this[YEAR], this[MONTH], this[DAY_OF_MONTH])

    fun addMonths(months: Int): DateTime {
        val dateTime = clone() as DateTime
        dateTime.add(MONTH, months)
        return dateTime
    }

    fun addDays(days: Int): DateTime {
        val dateTime = clone() as DateTime
        dateTime.add(DAY_OF_MONTH, days)
        return dateTime
    }

    fun addHours(hours: Int): DateTime {
        val dateTime = clone() as DateTime
        dateTime.add(HOUR_OF_DAY, hours)
        return dateTime
    }

    val year: Int
        get() = this[YEAR]
    val month: Int
        get() = this[MONTH]
    val day: Int
        get() = this[DAY_OF_MONTH]
    val hour: Int
        get() = this[HOUR_OF_DAY]
    val minite: Int
        get() = this[MINUTE]
    val second: Int
        get() = this[SECOND]
    val monthStr: String
        get() {
            val tt = month + 1
            return if (tt < 10) "0$tt" else "" + tt
        }
    val dayStr: String
        get() {
            val tt = day
            return if (tt < 10) "0$tt" else "" + tt
        }
    val hourStr: String
        get() {
            val tt = hour
            return if (tt < 10) "0$tt" else "" + tt
        }
    val miniteStr: String
        get() {
            val tt = minite
            return if (tt < 10) "0$tt" else "" + tt
        }
    val secondStr: String
        get() {
            val tt = second
            return if (tt < 10) "0$tt" else "" + tt
        }

    /**
     * 格式：****@/ **@/ **
     *
     * @return
     */
    fun toShortDateString(): String {
        return String.concat(year, "/", monthStr, "/", dayStr)
    }

    /**
     * 格式：****年**月**日
     *
     * @return
     */
    fun toShortDateString3(): String {
        return String.concat(year, "年", monthStr, "月", dayStr, "日")
    }

    /**
     * 格式：**@/ **
     *
     * @return
     */
    fun toShortDateString1(): String {
        return String.concat(monthStr, "/", dayStr)
    }

    /**
     * 格式：**@/ **  **:**
     *
     * @return
     */
    fun toLongDateString2(): String {
        return String.concat(monthStr, "/", dayStr, " ", hourStr, ":", miniteStr)
    }

    /**
     * 今天、昨天、前天
     * @return
     */
    fun toOffset1(): String {
        val now = DateTime()
        val dayOffset = dayOffset(this, now)
        return when (dayOffset) {
            0 -> "今天"
            1 -> "昨天"
            2 -> "前天"
            else -> dayOffset.toString() + "天"
        }
    }

    /**
     * 今天、昨天、前天
     * @return
     */
    fun toOffset2(): String {
        val now = DateTime()
        val dayOffset = dayOffset(this, now)
        return when (dayOffset) {
            0 -> "今天" + toShortTimeString()
            1 -> "昨天" + toShortTimeString()
            2 -> "前天" + toShortTimeString()
            else -> dayOffset.toString() + "天" + toShortTimeString()
        }
    }

    /**
     * 格式：****@/ **@/ **  **:**:**
     *
     * @return
     */
    fun toLongDateTimeString(): String {
        return String.concat(toShortDateString(), "  ", toTimeString())
    }

    /**
     * 格式：****@/ **@/ **  **:**
     *
     * @return
     */
    fun toLongDateTimeString1(): String {
        return String.concat(toShortDateString(), "  ", hourStr, ":", miniteStr)
    }

    /**
     * 格式：**:**:**
     *
     * @return
     */
    fun toTimeString(): String {
        return String.concat(hourStr, ":", miniteStr, ":", secondStr)
    }

    /**
     * 格式：**:**
     *
     * @return
     */
    fun toShortTimeString(): String {
        return String.concat(hourStr, ":", miniteStr)
    }

    val weekDayStr: String
        get() {
            when (this[DAY_OF_WEEK] - 1) {
                1 -> return "周一"
                2 -> return "周二"
                3 -> return "周三"
                4 -> return "周四"
                5 -> return "周五"
                6 -> return "周六"
                0 -> return "周日"
            }
            return ""
        }

    companion object {
        @JvmStatic
        val today: DateTime
            get() {
                val today = DateTime()
                return today.date
            }
        /**
         * 格式：*天*小时*分钟*秒
         *
         * @return
         */
        //    public static String toSpanStringSecond(long timeInMillis) {
        //        int second = (int) (timeInMillis / 1000 % 60);
        //        int minite = (int) (timeInMillis / 60000 % 60);
        //        int hour = (int) (timeInMillis / 60000 / 60 % 24);
        //        int day = (int) (timeInMillis / 60000 / 60 / 24);
        //        if (day == 0 && hour == 0 && minite == 0) {
        //            return second + "秒";
        //        }
        //        return _String.concat(day > 0 ? day + "天" : "", hour > 0 ? hour + "小时" : "", minite > 0 ? minite + "分钟" : "", second > 0 ? second + "秒" : "");
        //    }
        /**
         * 格式：*天*小时*分钟
         *
         * @return
         */
        //    public static String toSpanStringMin(long timeInMillis) {
        //        int minite = (int) (timeInMillis / 60000 % 60);
        //        int hour = (int) (timeInMillis / 60000 / 60 % 24);
        //        int day = (int) (timeInMillis / 60000 / 60 / 24);
        //        if (day == 0 && hour == 0) {
        //            return minite + "分钟";
        //        }
        //        return _String.concat(day > 0 ? day + "天" : "", hour > 0 ? hour + "小时" : "", minite > 0 ? minite + "分钟" : "");
        //    }

        /**
         * 格式：最大显示xx:xx:xx  最小显示xx:xx
         *
         * @param timeInMillis
         * @return
         */
        @JvmStatic
        fun toSpanString(timeInMillis: Long): String {
            val hour = (timeInMillis / 60000 / 60).toInt()
            val minite = (timeInMillis / 60000 % 60).toInt()
            val second = (timeInMillis / 1000 % 60).toInt()
            return if (hour == 0) {
                minite.toString() + ":" + if (second < 10) "0$second" else second.toString() + ""
            } else {
                hour.toString() + ":" + (if (minite < 10) "0$minite" else minite.toString() + "") + ":" + if (second < 10) "0$second" else second.toString() + ""
            }
        }
        /**
         * 格式：*天*小时*分钟*秒
         *
         * @param timeInMillis
         * @param startTag     开始标志 1：秒；2：分；3：时；4：天
         * @param endTag       开始标志 1：秒；2：分；3：时；4：天
         * @return
         */
        @JvmStatic
        fun toSpanString(timeInMillis: Long, startTag: Int, endTag: Int): String {

            if (startTag < endTag) throw Exception("开始标志必须大于等于结束标志")
            var resutl = ""
            val day = (timeInMillis / 60000 / 60 / 24).toInt()

            var hour = (timeInMillis / 60000 / 60 % 24).toInt()
            var minite = (timeInMillis / 60000 % 60).toInt()
            var second = (timeInMillis / 1000 % 60).toInt()

            when (startTag) {
                3 -> hour += day * 24
                2 -> minite += day * 24 + hour * 60
                1 -> second += day * 24 + hour * 60 + minite * 60
            }

            val sday = if (day > 0) day.toString() + "天" else ""
            val shour = if (hour > 0) hour.toString() + "小时" else ""
            val smin = if (minite > 0) minite.toString() + "分钟" else ""
            val ssecond = if (second > 0) second.toString() + "秒" else ""

            when (startTag) {
                4 -> {
                    when (endTag) {
                        4 -> {
                            resutl += sday
                        }
                        3 -> {
                            resutl += sday + shour
                        }
                        2 -> {
                            resutl += sday + shour + smin
                        }
                        1 -> {
                            resutl += sday + shour + smin + ssecond
                        }
                    }
                }
                3 -> {
                    when (endTag) {
                        3 -> {
                            resutl += sday + shour
                        }
                        2 -> {
                            resutl += sday + shour + smin
                        }
                        1 -> {
                            resutl += sday + shour + smin + ssecond
                        }
                    }
                }
                2 -> {
                    when (endTag) {
                        2 -> {
                            resutl += sday + shour + smin
                        }
                        1 -> {
                            resutl += sday + shour + smin + ssecond
                        }
                    }
                }
                1 -> {
                    resutl += sday + shour + smin + ssecond
                }
            }
            return resutl
        }


        /**
         * date2比date1多的天数
         *
         * @param date1
         * @param date2
         * @return
         */
        @JvmStatic
        fun dayOffset(date1: DateTime, date2: DateTime): Int {
            val date1l = date1.timeInMillis - date1.hour * 3600000 - date1.minite * 60000 - date1.second * 1000
            val date2l = date2.timeInMillis - date2.hour * 3600000 - date2.minite * 60000 - date2.second * 1000
            val date1Days = date1l / (3600 * 1000 * 24)
            val date2Days = date2l / (3600 * 1000 * 24)
            return (date2Days - date1Days).toInt()
        }

        /**
         * date2比date1多的周数
         *
         * @param startTime
         * @param endTime
         * @return
         */
        fun calcWeekOffset(startTime: DateTime, endTime: DateTime): Int {
            var dayOfWeek = startTime[DAY_OF_WEEK]
            dayOfWeek = dayOfWeek - 1
            if (dayOfWeek == 0) dayOfWeek = 7
            val dayOffset = dayOffset(startTime, endTime)
            var weekOffset = dayOffset / 7
            val a: Int
            a = if (dayOffset > 0) {
                if (dayOffset % 7 + dayOfWeek > 7) 1 else 0
            } else {
                if (dayOfWeek + dayOffset % 7 < 1) -1 else 0
            }
            weekOffset = weekOffset + a
            return weekOffset
        }
    }
}