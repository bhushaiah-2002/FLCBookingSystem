import java.util.*;

public class Timetable {
    private List<Lesson> lessonList = new ArrayList<>();

    public void addLesson(Lesson lesson) { lessonList.add(lesson); }

    public List<Lesson> searchByDay(String day) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson l : lessonList)
            if (l.getLessonDay().equalsIgnoreCase(day)) result.add(l);
        return result;
    }

    public List<Lesson> searchByType(String type) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson l : lessonList)
            if (l.getLessonType().equalsIgnoreCase(type)) result.add(l);
        return result;
    }

    public Lesson ById(int id) {
        for (Lesson l : lessonList)
            if (l.getLessonId() == id) return l;
        return null;
    }

    public List<Lesson> getByMonth(int month) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson l : lessonList)
            if (l.getMonth() == month) result.add(l);
        return result;
    }

    public List<Lesson> getAllLessons() { return lessonList; }
}
