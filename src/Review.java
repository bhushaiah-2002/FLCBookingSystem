public class Review {
    private String reviewText;
    private int ratingValue;
    private Member user;
    private Lesson lesson;

    public Review(String text, int rating, Member user, Lesson lesson) {
        this.reviewText = text;
        this.ratingValue = rating;
        this.user = user;
        this.lesson = lesson;
    }

    public int getRatingValue() {
        return ratingValue;
    }
}