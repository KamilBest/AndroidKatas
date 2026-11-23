package katas.ex04

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IdiomsTest {

    @Test fun `value class`() {
        assertEquals(Grosze(1349), Grosze(1250) + Grosze(99))
        assertEquals("13,49 zł", Grosze(1349).toString())
        assertEquals("0,05 zł", Grosze(5).toString())
        assertTrue(Grosze(5) < Grosze(7))
        assertEquals(Grosze(7), listOf(Grosze(5), Grosze(7), Grosze(1)).maxBy { it.value })
    }

    @Test fun `counting set delegates`() {
        val set: MutableSet<String> = CountingSet()
        set.add("a"); set.add("a"); set.addAll(listOf("b", "c"))
        assertEquals(setOf("a", "b", "c"), set)
        assertEquals(4, (set as CountingSet).addAttempts)
        set.remove("a")
        assertEquals(2, set.size)
    }

    @Test fun `variance compiles`() {
        val strings: Box<String> = Box("x")
        val anys: Box<Any> = strings             // needs Box<out T>
        assertEquals("x", anys.value)
        val anySink: Sink<Any> = Sink()
        val stringSink: Sink<String> = anySink   // needs Sink<in T>
        stringSink.put("y")
        assertEquals(listOf<Any?>("y"), anySink.received)
    }

    @Test fun `reified`() {
        val items = listOf(1, "a", 2.0, "b")
        assertEquals("a", items.firstOfType<String>())
        assertEquals(2.0, items.firstOfType<Double>())
        assertNull(items.firstOfType<Long>())
    }

    @Test fun `equals and hashCode contract`() {
        val set = hashSetOf(PatientKey("440514 01359"))
        assertEquals(true, PatientKey("44051401359") in set)
        assertEquals(PatientKey("ab c"), PatientKey("ABC"))
        assertEquals(PatientKey("ab c").hashCode(), PatientKey("ABC").hashCode())
    }

    @Test fun `lazy computes once`() {
        var calls = 0
        val report = Report { calls++; "ok" }
        assertEquals(0, calls)
        repeat(3) { assertEquals("ok", report.summary) }
        assertEquals(1, calls)
    }
}
