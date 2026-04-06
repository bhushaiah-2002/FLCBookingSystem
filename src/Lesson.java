import java.util.*;

public class Lesson {
    private int lessonId;
    private String lessonType;
    private String lessonDay;
    private String lessonTime;
    private int month;
    private int maxSeats;
    private int bookedSeats;
    private double lessonPrice;
    private List<Review> reviews;

    public Lesson(int id, String type, String day, String time, int month, int maxSeats, double price) {
        this.lessonId = id;
        this.lessonType = type;
        this.lessonDay = day;
        this.lessonTime = time;
        
        this.maxSeats = maxSeats;
        this.lessonPrice = price;
        this.bookedSeats = 0;
        this.reviews = new ArrayList<>();
    }

    public boolean bookSeat() {
        if (bookedSeats < maxSeats) { bookedSeats++; return true; }
        return false;
    }

    public void cancelSeat() {
        if (bookedSeats > 0) bookedSeats--;
    }

    public void addReview(Review r) { reviews.add(r); }

    public double getAverageRating() {
        if (reviews.isEmpty()) return 0;
        double total = 0;
        for (Review r : reviews) total += r.getRatingValue();
        return total / reviews.size();
    }

    public int getAttendedCount() { return reviews.size(); }

    public int getLessonId()     { return lessonId; }
    public String getLessonType(){ return lessonType; }
    public String getLessonDay() { return lessonDay; }
    public String getLessonTime(){ return lessonTime; }
    public int getMonth()        { return month; }
    public double getLessonPrice(){ return lessonPrice; }
    public int getBookedSeats()  { return bookedSeats; }
    public int getAvailableSeats(){ return maxSeats - bookedSeats; }

    @Override
    public String toString() {
        return String.format("[%d] %s | %s %s | £%.2f | Spaces: %d",
                lessonId, lessonType, lessonDay, lessonTime, lessonPrice, getAvailableSeats());
    }
}
