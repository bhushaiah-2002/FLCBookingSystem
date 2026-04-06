import java.util.*;

public class Report {

    private static String monthName(int m) {
        return m == 1 ? "January" : m == 2 ? "February" : "Month " + m;
    }

    // Monthly lesson report: each lesson in the month with attended count and avg rating
    public void monthlyReport(List<Lesson> lessons, int month) {
        System.out.println("\n=== Monthly Lesson Report - " + monthName(month) + " ===");
        System.out.printf("%-4s %-12s %-10s %-12s %-10s %-10s%n",
                "ID", "Type", "Day", "Time", "Attended", "Avg Rating");
        System.out.println("-".repeat(62));

       
        for (Lesson l : lessons) {
            if (l.getMonth() == month) {
                found = true;
                System.out.printf("%-4d %-12s %-10s %-12s %-10d %-10s%n",
                        l.getLessonId(),
                        l.getLessonType(),
                        l.getLessonDay(),
                       
                        l.getAttendedCount(),
                        l.getAttendedCount() > 0
                                ? String.format("%.2f", l.getAverageRating())
                                : "N/A");
            }
        }
        if (!found) System.out.println("No lessons found for " + monthName(month) + ".");
    }

    // Champion report: income per exercise type (price x attended count) for the month
    public void championReport(List<Lesson> lessons, int month) {
        System.out.println("\n=== Monthly Champion Report - " + monthName(month) + " ===");

        Map<String, Double> incomeMap = new LinkedHashMap<>();
        for (Lesson l : lessons) {
            if (l.getMonth() == month) {
                double income = l.getLessonPrice() * l.getAttendedCount();
                incomeMap.merge(l.getLessonType(), income, Double::sum);
            }
        }

        if (incomeMap.isEmpty()) {
            System.out.println("No data for " + monthName(month) + ".");
            return;
        }

        System.out.printf("%-14s %s%n", "Exercise Type", "Total Income");
        System.out.println("-".repeat(30));

        String champion = "";
        double max = -1;
        for (Map.Entry<String, Double> e : incomeMap.entrySet()) {
            System.out.printf("%-14s £%.2f%n", e.getKey(), e.getValue());
            if (e.getValue() > max) { max = e.getValue(); champion = e.getKey(); }
        }
        System.out.println("-".repeat(30));
        System.out.printf("Champion: %s (£%.2f)%n", champion, max);
    }
}
