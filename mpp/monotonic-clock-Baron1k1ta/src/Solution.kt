class Solution : MonotonicClock {
    private var left1 by RegularInt(0)
    private var left2 by RegularInt(0)
    private var mid3  by RegularInt(0)
    private var right2 by RegularInt(0)
    private var right1 by RegularInt(0)

    override fun write(time: Time) {
        left1  = time.d1
        left2  = time.d2
        mid3   = time.d3
        right2 = time.d2
        right1 = time.d1
    }

    override fun read(): Time {
        val r1 = right1
        val r2 = right2
        val m3 = mid3
        val l2 = left2
        val l1 = left1

        return if (r1 == l1) {
            if (r2 == l2) {
                Time(l1, l2, m3)
            } else {
                Time(l1, l2, 0)
            }
        } else {
            Time(l1, 0, 0)
        }
    }
}