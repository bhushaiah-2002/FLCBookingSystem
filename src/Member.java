import java.util.*;

public class Member {
    private int userId;
    private String userName;
    private List<Booking> userBookings;

    public Member(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.userBookings = new ArrayList<>();
    }

    public void addBooking(Booking booking) { userBookings.add(booking); }

    public List<Booking> getBookings() { return userBookings; }

    public String getUserName() { return userName; }

    public boolean hasBooking(Lesson lesson) {
        for (Booking b : userBookings)
            if (b.getLesson() == lesson && !b.getStatus().equals("cancelled")) return true;
        return false;
    }

    public boolean hasTimeConflict(Lesson newLesson) {
        for (Booking b : userBookings) {
            Lesson l = b.getLesson();
            if (l.getLessonDay().equals(newLesson.getLessonDay()) &&
                l.getLessonTime().equals(newLesson.getLessonTime()) &&
                !b.getStatus().equals("cancelled")) return true;
        }
        return false;
    }
}
