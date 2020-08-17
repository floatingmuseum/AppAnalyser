package com.floatingmuseum.app.analyser

import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun test1() {
        val list = ArrayList<App>()
        for (i in 0 until 500000) {
            list.add(App("pkg-$i", "name-$i"))
        }

        val map = HashMap<String, App>()
        for (i in 0 until 500000) {
            map["pkg-$i"] = App("pkg-$i", "name-$i")
        }
        println("list长度:${list.size}")
        println("map长度:${list.size}")
        val start1 = System.currentTimeMillis()
        var result1: App? = null
        for (app in list) {
            if (app.pkg == "pkg-499999") {
                result1 = app
                break
            }
        }
        println("result1:$result1...time:${System.currentTimeMillis() - start1}")

        val start2 = System.currentTimeMillis()
        val result2 = map["pkg-499999"]
        println("result2:$result2...time:${System.currentTimeMillis() - start2}")

        val start3 = System.currentTimeMillis()
        for (app in list) {

        }
        println("list loop...time:${System.currentTimeMillis() - start3}")

        val start4 = System.currentTimeMillis()
        for (mutableEntry in map) {

        }
        println("map loop...time:${System.currentTimeMillis() - start4}")
    }
}

data class App(
    val pkg: String,
    val name: String
)
