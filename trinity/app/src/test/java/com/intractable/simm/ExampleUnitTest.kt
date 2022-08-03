package com.intractable.simm

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_1() {
        val input = 1
        printValue(input)
    }

    @Test
    fun test_2() {
        val flowExample: Flow<Int> = flow {
            val input = 1
            emit(input)
        }

        runBlocking {
            flowExample.collect() {
                printValue(it)
            }
        }
    }

    fun printValue(i: Int) {
        println("i: " + i)
    }

    @Test
    fun coroutine_1() {
        runBlocking {
            val job1: Job
            val job2: Job
                job1 = launch {
                    println("A1")
                    delay(3000)
                    println("A2")
                }
                job2 = launch {
                    println("B1")
                    yield()
                    println("B2")
                    println("B3")
                }
        }
        println("finished")
    }

    @Test
    fun flow_1() {
        val flowExample: Flow<Int> = flow {
            println("emit thread: " + Thread.currentThread().id)
            while (true) {
                emit(System.currentTimeMillis().toInt())
                delay(1000)
            }
        }

        runBlocking {
            flowExample.flowOn(Dispatchers.IO).collect() {
                println("collect thread: " + Thread.currentThread().id)
                println("item = " + it)
            }
        }
    }
}