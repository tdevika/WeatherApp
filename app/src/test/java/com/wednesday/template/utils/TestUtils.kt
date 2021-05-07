package com.wednesday.template.utils

import com.wednesday.template.model.City

object TestUtil {
    val city = City(2459115, "New York", "City")
    val cities =
        object : ArrayList<City>() {
            init {
                add(city)
                add(City(2295412, "Pune", "City"))
            }
        }
}