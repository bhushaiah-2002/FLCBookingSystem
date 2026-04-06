public class Booking {
    private int bookingId;
    private Member user;
    private Lesson lesson;
    private String status;

    public Booking(int id, Member user, Lesson lesson) {
        this.bookingId = id;
        this.user = user;
        this.lesson = lesson;
        this.status = "booked";
    }

    public void cancelBooking() {
        lesson.cancelSeat();
        status = "cancelled";
    }

    // Returns true if change succeeded
    public boolean changeLesson(Lesson newLesson) {
        if (newLesson.bookSeat()) {
            lesson.cancelSeat();
           
            status = "changed";
            return true;
        }
        return false;
    }

    public void attendLesson(String review, int rating) {
        lesson.addReview(new Review(review, rating, user, lesson));
        status = "attended";
    }

    public int getBookingId()  { return bookingId; }
    public Lesson getLesson()  { return lesson; }
    public String getStatus()  { return status; }
    public Member getMember()  { return user; }
}
