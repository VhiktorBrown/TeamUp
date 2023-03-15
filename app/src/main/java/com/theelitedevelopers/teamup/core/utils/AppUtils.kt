package com.theelitedevelopers.teamup.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.theelitedevelopers.teamup.R
import com.theelitedevelopers.teamup.core.data.local.SharedPref
import com.theelitedevelopers.teamup.modules.data.models.UserDetails
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AppUtils {

    companion object {

        private const val SECOND_MILLIS = 1000
        private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS = 24 * HOUR_MILLIS
        private const val WEEK_MILLIS = 7 * DAY_MILLIS

        @SuppressLint("ResourceAsColor")
        fun showSnackBar(view: View?, message: String?) {
            if (view != null) {
                val snackBar = Snackbar.make(view, message!!, Snackbar.LENGTH_LONG)
                snackBar.view
                    .setBackgroundColor(R.color.primary)
                snackBar.show()
            }
        }

        fun convertDateFromOneFormatToAnother(
            sourceFormat: String?,
            destinationFormat: String?,
            date: String
        ): String? {
            @SuppressLint("SimpleDateFormat") val sourceDateFormat = SimpleDateFormat(sourceFormat)
            @SuppressLint("SimpleDateFormat") val destinationDateFormat =
                SimpleDateFormat(destinationFormat)
            var newDate: String? = ""
            try {
                val sourceDate = sourceDateFormat.parse(date)
                newDate = destinationDateFormat.format(Objects.requireNonNull(sourceDate))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return newDate
        }


        fun getInboxDate(date: String): String? {
            @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
            var d: String? = ""
            var date1: Date? = null
            try {
                date1 = format.parse(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (date1 != null) {
                //check if date is the same with current date
                d = if (isTheSameDay(date)) {
                    getHourMinuteFormat().format(date1)
                } else if (isTheSameYear(date)) {
                    if (withinAWeek(date)) {
                        getDayFormat().format(date1)
                    } else {
                        getDayMonthFormat().format(date1)
                    }
                } else {
                    getDayMonthYearFormat().format(date1)
                }
            }
            return d
        }


        fun isTheSameDay(date: String): Boolean {
            var parsedDate: Date? = null
            var newDayToday: Date? = null
            var isSameDay = false
            try {
                val initialDate = Objects.requireNonNull(getTimeFormat().parse(date))

                //Here, I changed the Dates into the Form 'dd MMM yyyy'
                val formattedDate = getDayMonthYearFormat().format(initialDate)

                //then parsed it back to a Normal Date object.
                parsedDate = getDayMonthYearFormat().parse(formattedDate)

                //TODO Check later. Something seems off about this line.
                newDayToday = getDayMonthYearFormat().parse(getDayMonthYearFormat().format(Date()))

                //check if they are the same
                if (Objects.requireNonNull(parsedDate) == newDayToday) {
                    isSameDay = true
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return isSameDay
        }


        //Function to check if date has the same year with current date
        fun isTheSameYear(date: String): Boolean {
            @SuppressLint("SimpleDateFormat") val simpleDateFormat = SimpleDateFormat("yyyy")
            @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
            @SuppressLint("SimpleDateFormat") val sourceFormat =
                SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy")
            var parsedDate: Date? = null
            var newDayToday: Date? = null
            var isSameYear = false
            try {
                //Here, I changed the Dates into the Form 'yyyy'
                val initialDate = Objects.requireNonNull(format.parse(date))
                val formattedDate = getYearFormat().format(initialDate)
                parsedDate = getYearFormat().parse(formattedDate)
                newDayToday = getYearFormat().parse(getYearFormat().format(Date()))

                //check if they are the same
                if (Objects.requireNonNull(parsedDate) == newDayToday) {
                    isSameYear = true
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return isSameYear
        }


        //This is the Function for checking if the time passed is 1 week from current date
        fun withinAWeek(date: String): Boolean {
            var time: Long = 0
            try {
                time = Objects.requireNonNull(getTimeFormat().parse(date)).time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (time < 1000000000000L) {
                // if timestamp given in seconds, convert to millis
                time *= 1000
            }
            val now = System.currentTimeMillis()

            // TODO: localize
            val diff = now - time
            return diff < 7 * AppUtils.DAY_MILLIS
        }

        @SuppressLint("SimpleDateFormat")
        fun fromTimeStampToString(timeStamp: Long): String {
            var stamp = timeStamp;
            stamp *= 1000L;
            return try {
                val sdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
                val netDate = Date(stamp)
                sdf.format(netDate)
            } catch (ex: Exception) {
                "xx"
            }
        }

        //This function is primarily for the buzz bubbles- to handle date Manipulation
        fun getSingleInboxDate(date: String): String? {
            var buzzDate: Date? = null
            var finalDate: String? = null
            try {
                buzzDate = Objects.requireNonNull(
                    getTimeFormat().parse(date)
                )
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            finalDate = when {
                isTheSameDay(date) -> {
                    getHourMinuteFormat().format(buzzDate!!)
                }
                withinAWeek(date) -> {
                    getDayFormat()
                        .format(buzzDate!!) + ", " + getHourMinuteFormat()
                        .format(buzzDate)
                }
                isTheSameYear(date) -> {
                    getDayMonthFormat()
                        .format(buzzDate!!) + ", " + getHourMinuteFormat()
                        .format(buzzDate)
                }
                else -> {
                    getDayMonthYearFormat()
                        .format(buzzDate!!) + ", " + getHourMinuteFormat()
                        .format(buzzDate)
                }
            }
            return finalDate
        }

        @Throws(ParseException::class)
        fun convertToDateWithoutSeconds(dateInString: String): Date? {
            val date = convertDateFromOneFormatToAnother(
                "EEE, d MMM yyyy HH:mm:ss",
                "EEE, d MMM yyyy HH:mm",
                dateInString
            )
            return getTimeFormatWithoutSeconds()
                .parse(date)
        }

        @SuppressLint("SimpleDateFormat")
        fun convertDateToPresentableFormatWithOnlyDate(dateInString: String): String? {
            val simpleDateFormat = SimpleDateFormat("dd MMM yyyy")
            var date: Date? = null
            var dateInPresentableFormat: String? = null
            val format = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
            try {
                date = format.parse(dateInString)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dateInPresentableFormat = if (date != null) {
                simpleDateFormat.format(date)
            } else {
                ""
            }
            return dateInPresentableFormat
        }


        @Throws(ParseException::class)
        fun convertToDateFormat(formatType: String?, dateInString: String): Date? {
            @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat(formatType)
            return format.parse(dateInString)
        }

        fun saveDataToSharedPref(context: Context, userDetails: UserDetails) {
            SharedPref.getInstance(context).saveString(Constants.UID, userDetails.uid)
            SharedPref.getInstance(context).saveString(Constants.NAME, userDetails.name)
            SharedPref.getInstance(context).saveString(Constants.EMAIL, userDetails.email)
        }

        fun removeDataToSharedPref(context : Context) {
            SharedPref.getInstance(context).removeKeyValue(Constants.UID)
            SharedPref.getInstance(context).removeKeyValue(Constants.NAME)
            SharedPref.getInstance(context).removeKeyValue(Constants.EMAIL)
        }


        @SuppressLint("SimpleDateFormat")
        fun getTimeFormat(): SimpleDateFormat {
            return SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
        }

        @SuppressLint("SimpleDateFormat")
        fun getTimeFormatWithoutSeconds(): SimpleDateFormat {
            return SimpleDateFormat("EEE, d MMM yyyy HH:mm")
        }

        @SuppressLint("SimpleDateFormat")
        fun getDayFormat(): SimpleDateFormat {
            return SimpleDateFormat("EEE")
        }

        @SuppressLint("SimpleDateFormat")
        fun getDayMonthFormat(): SimpleDateFormat {
            return SimpleDateFormat("dd MMM")
        }

        @SuppressLint("SimpleDateFormat")
        fun getYearFormat(): SimpleDateFormat {
            return SimpleDateFormat("yyyy")
        }

        @SuppressLint("SimpleDateFormat")
        fun getDayMonthYearFormat(): SimpleDateFormat {
            return SimpleDateFormat("dd MMM yyyy")
        }

        @SuppressLint("SimpleDateFormat")
        fun getHourMinuteFormat(): SimpleDateFormat {
            return SimpleDateFormat("hh:mm aa")
        }


    }
}