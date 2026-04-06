import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FitBookerTest {

    @Test
    public void testLessonCapacity() {
        Lesson lesson = new Lesson(1, "Yoga", "Saturday", "Morning", 1, 4, 9.5);
        assertTrue(lesson.bookSeat());
        assertTrue(lesson.bookSeat());
        assertTrue(lesson.bookSeat());
        assertTrue(lesson.bookSeat());
        assertFalse(lesson.bookSeat()); // 5th should fail
    }

    @Test
    public void testHasSpace() {
        Lesson l = new Lesson(1, "Yoga", "Saturday", "Morning", 1, 4, 9.5);
        assertTrue(l.hasSpace());
        l.bookSeat(); l.bookSeat(); l.bookSeat(); l.bookSeat();
        assertFalse(l.hasSpace()); // full
    }

    @Test
    public void testDuplicateBooking() {
        Member m = new Member(1, "User1");
        Lesson l = new Lesson(1, "Yoga", "Saturday", "Morning", 1, 4, 9.5);
        m.addBooking(new Booking(1, m, l));
        assertTrue(m.hasBooking(l));
    }

    @Test
    public void testTimeConflict() {
        Member m = new Member(1, "User1");
        Lesson l1 = new Lesson(1, "Yoga",  "Saturday", "Morning", 1, 4, 9.5);
        Lesson l2 = new Lesson(2, "Zumba", "Saturday", "Morning", 1, 4, 8.0);
        m.addBooking(new Booking(1, m, l1));
        assertTrue(m.hasTimeConflict(l2));
    }

    @Test
    public void testNoTimeConflictDifferentTime() {
        Member m = new Member(1, "User1");
        Lesson l1 = new Lesson(1, "Yoga",  "Saturday", "Morning",   1, 4, 9.5);
        Lesson l2 = new Lesson(2, "Zumba", "Saturday", "Afternoon", 1, 4, 8.0);
        m.addBooking(new Booking(1, m, l1));
        assertFalse(m.hasTimeConflict(l2));
    }

    // Issue 3 fix: change should check time conflict with OTHER bookings, not itself
    @Test
    public void testChangeLessonNoFalseConflict() {
        Member m = new Member(1, "User1");
        Lesson l1 = new Lesson(1, "Yoga",  "Saturday", "Morning", 1, 4, 9.5);
        Lesson l2 = new Lesson(2, "Zumba", "Saturday", "Morning", 1, 4, 8.0);
        l1.bookSeat();
        Booking b = new Booking(1, m, l1);
        m.addBooking(b);
        // l2 is same day/time as l1 — but since we're changing b itself, should NOT be a conflict
        assertFalse(m.hasTimeConflictExcluding(l2, b));
    }

    @Test
    public void testAttendLesson() {
        Member m = new Member(1, "User1");
        Lesson l = new Lesson(1, "Yoga", "Saturday", "Morning", 1, 4, 9.5);
        Booking b = new Booking(1, m, l);
        b.attendLesson("Good session", 5);
        assertEquals("attended", b.getStatus());
        assertEquals(5.0, l.getAverageRating());
    }

    // Issue 1 + 4 fix: changeLesson is fully encapsulated, old seat released, new seat claimed
    @Test
    public void testChangeLesson() {
        Member m = new Member(1, "User1");
        Lesson l1 = new Lesson(1, "Yoga",  "Saturday", "Morning", 1, 4, 9.5);
        Lesson l2 = new Lesson(2, "Zumba", "Sunday",   "Morning", 1, 4, 8.0);
        l1.bookSeat();
        Booking b = new Booking(1, m, l1);
        assertTrue(b.changeLesson(l2));
        assertEquals("changed", b.getStatus());
        assertEquals(l2, b.getLesson());
        assertEquals(0, l1.getBookedSeats()); // old seat released
        assertEquals(1, l2.getBookedSeats()); // new seat claimed
    }

    // Issue 1 fix: changeLesson fails cleanly when new lesson is full
    @Test
    public void testChangeLessonFailsWhenFull() {
        Member m = new Member(1, "User1");
        Lesson l1 = new Lesson(1, "Yoga",  "Saturday", "Morning", 1, 4, 9.5);
        Lesson l2 = new Lesson(2, "Zumba", "Sunday",   "Morning", 1, 4, 8.0);
        l1.bookSeat();
        l2.bookSeat(); l2.bookSeat(); l2.bookSeat(); l2.bookSeat(); // fill l2
        Booking b = new Booking(1, m, l1);
        assertFalse(b.changeLesson(l2));
        assertEquals("booked", b.getStatus());    // status unchanged
        assertEquals(l1, b.getLesson());           // lesson unchanged
        assertEquals(1, l1.getBookedSeats());      // old seat NOT released
    }

    @Test
    public void testCancelBooking() {
        Member m = new Member(1, "User1");
        Lesson l = new Lesson(1, "Yoga", "Saturday", "Morning", 1, 4, 9.5);
        l.bookSeat();
        Booking b = new Booking(1, m, l);
        b.cancelBooking();
        assertEquals("cancelled", b.getStatus());
        assertEquals(0, l.getBookedSeats()); // seat released
    }

    // Issue 2 fix: income uses only attended count, not booked count
    @Test
    public void testIncomeOnlyFromAttended() {
        Member m = new Member(1, "User1");
        Lesson l = new Lesson(1, "Box Fit", "Saturday", "Evening", 1, 4, 7.0);
        // Book 2 seats but only attend 1
        l.bookSeat(); l.bookSeat();
        Booking b1 = new Booking(1, m, l);
        Booking b2 = new Booking(2, m, l);
        b1.attendLesson("Great!", 4);
        // b2 not attended
        // income should be price * attendedCount = 7.0 * 1 = 7.0
        double income = l.getLessonPrice() * l.getAttendedCount();
        assertEquals(7.0, income);
    }

    @Test
    public void testAverageRating() {
        Member m = new Member(1, "User1");
        Lesson l = new Lesson(1, "Box Fit", "Saturday", "Evening", 1, 4, 7.0);
        new Booking(1, m, l).attendLesson("Great!", 4);
        new Booking(2, m, l).attendLesson("Loved it", 2);
        assertEquals(3.0, l.getAverageRating());
    }
}
