package com.example.mynotes.widget

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetUpdateQueueTest {
    @Test
    fun combinesIdsWithoutDuplicatesOrRetainingInputArray() = runBlocking {
        val queue = WidgetUpdateQueue()
        val first = intArrayOf(1, 2)
        queue.enqueue(first)
        first[0] = 99
        queue.enqueue(intArrayOf(2, 3))
        val batches = mutableListOf<List<Int>>()
        repeat(3) { queue.drain { batches += it.toList() } }
        assertEquals(listOf(listOf(1, 2, 3)), batches)
    }

    @Test
    fun emptyQueueDoesNotLoadOrRender() = runBlocking {
        val queue = WidgetUpdateQueue()
        queue.enqueue(intArrayOf())
        var renders = 0
        queue.drain { renders++ }
        assertEquals(0, renders)
    }

    @Test
    fun serializesRunningUpdateAndCoalescesLaterRequests() = runBlocking {
        val queue = WidgetUpdateQueue()
        val finish = CompletableDeferred<Unit>()
        val batches = mutableListOf<List<Int>>()
        var rendering = false
        queue.enqueue(intArrayOf(1))
        val first = launch(start = CoroutineStart.UNDISPATCHED) {
            queue.drain {
                rendering = true
                batches += it.toList()
                finish.await()
                rendering = false
            }
        }
        queue.enqueue(intArrayOf(1, 2))
        val second = launch(start = CoroutineStart.UNDISPATCHED) {
            queue.drain {
                assertTrue(!rendering)
                batches += it.toList()
            }
        }
        queue.enqueue(intArrayOf(3, 2))
        val third = launch(start = CoroutineStart.UNDISPATCHED) {
            queue.drain {
                assertTrue(!rendering)
                batches += it.toList()
            }
        }
        finish.complete(Unit)
        first.join()
        second.join()
        third.join()
        assertEquals(listOf(listOf(1), listOf(1, 2, 3)), batches)
    }

    @Test
    fun renderingFailureDoesNotBlockNextRequest() = runBlocking {
        val queue = WidgetUpdateQueue()
        queue.enqueue(intArrayOf(1))
        assertTrue(runCatching { queue.drain { error("Render failed") } }.isFailure)
        queue.enqueue(intArrayOf(1, 2))
        var rendered = emptyList<Int>()
        queue.drain { rendered = it.toList() }
        assertEquals(listOf(1, 2), rendered)
    }

    @Test
    fun cancelledWaiterLeavesItsIdsPending() = runBlocking {
        val queue = WidgetUpdateQueue()
        val finish = CompletableDeferred<Unit>()
        queue.enqueue(intArrayOf(1))
        val first = launch(start = CoroutineStart.UNDISPATCHED) { queue.drain { finish.await() } }
        queue.enqueue(intArrayOf(2))
        val waiting = launch(start = CoroutineStart.UNDISPATCHED) { queue.drain { error("Cancelled request ran") } }
        waiting.cancelAndJoin()
        finish.complete(Unit)
        first.join()
        var rendered = emptyList<Int>()
        queue.drain { rendered = it.toList() }
        assertEquals(listOf(2), rendered)
    }
}
