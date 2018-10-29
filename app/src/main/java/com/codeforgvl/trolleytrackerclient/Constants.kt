package com.codeforgvl.trolleytrackerclient

import android.content.Context
import android.support.v4.content.ContextCompat

class Constants {
    companion object {
        const val LOG_TAG = "TROLLEYTRACKER"
        const val ROUTE_UPDATE_INTERVAL = 15
        const val SLEEP_INTERVAL = 5000
        const val LOCATION_PERMISSION_REQUEST_ID = 1

        val HOST =
            if (BuildConfig.DEBUG) "yeahthattrolley.azurewebsites.net" else "api.yeahthattrolley.com"
        val API_PATH = "/api/v1/"
//        var ALL_TROLLEYS_ENDPOINT =
//        var RUNNING_TROLLEYS_ENDPOINT =
//        var ACTIVE_ROUTES_ENDPOINT =
//        var ROUTE_SCHEDULE_ENDPOINT =

        private val ROUTE_DETAILS_ENDPOINT = "http://" + HOST + API_PATH + "Routes/"

        fun getRouteDetailsEndpoint(routeId: Int): String {
            return ROUTE_DETAILS_ENDPOINT + routeId
        }

        fun getAllTrolleysEndpoint(): String {
            return """http://$HOST${API_PATH}Trolleys"""  // Complete trolley record - all trolleys
        }

        fun getRunningTrolleysEndpoint(): String {
            return """http://$HOST${API_PATH}Trolleys/Running"""
        }

        fun getActiveRoutesEndpoint(): String {
            return """http://$HOST${API_PATH}Routes/Active"""
        }

        fun getRouteScheduleEndpoint(): String {
            return """http://$HOST${API_PATH}RouteSchedules"""
        }

        fun getRouteColorForRouteNumber(context: Context, ndx: Int): Int {

            val routeNo = ndx % 5 + 1
            return when (routeNo) {
                1 -> ContextCompat.getColor(context, R.color.route1)
                2 -> ContextCompat.getColor(context, R.color.route2)
                3 -> ContextCompat.getColor(context, R.color.route3)
                4 -> ContextCompat.getColor(context, R.color.route4)
                else -> ContextCompat.getColor(context, R.color.route5)
            }
        }

        fun getStopColorForRouteNumber(context: Context, ndx: Int): Int {
            val routeNo = ndx % 5 + 1
            return when (routeNo) {
                1 -> ContextCompat.getColor(context, R.color.stop1)
                2 -> ContextCompat.getColor(context, R.color.stop2)
                3 -> ContextCompat.getColor(context, R.color.stop3)
                4 -> ContextCompat.getColor(context, R.color.stop4)
                else -> ContextCompat.getColor(context, R.color.stop5)
            }
        }
    }

    enum class DayOfWeek {
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday,
        Sunday
    }
}