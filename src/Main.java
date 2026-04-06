import java.util.*;

public class Main {

    static int nextBookingId = 1;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Timetable timetable = buildTimetable();
        List<Member> members = buildMembers();
        Report report = new Report();
        preloadData(timetable, members);
        Member member = selectMember(members);
        while (true) {
            System.out.println("\n=== FitBooker Menu ===");
            System.out.println("1. Book a lesson");
            System.out.println("2. Change/Cancel a booking");
            System.out.println("3. Attend a lesson");
            System.out.println("4. Monthly lesson report");
            System.out.println("5. Monthly champion report");
            System.out.println("6. Switch member");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            int choice = readInt();
            switch (choice) {
                case 1 -> bookLesson(timetable, member);
                case 2 -> changeOrCancel(timetable, member);
                case 3 -> attendLesson(member);
                case 4 -> {
                    System.out.print("Enter month (1 = January, 2 = February): ");
                    int m = readInt();
                    report.monthlyReport(timetable.getAllLessons(), m);
                }
                case 5 -> {
                    System.out.print("Enter month (1 = January, 2 = February): ");
                    int m = readInt();
                    report.championReport(timetable.getAllLessons(), m);
                }
                case 6 -> member = selectMember(members);
                case 0 -> { System.out.println("Goodbye!"); return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }
    static void bookLesson(Timetable timetable, Member member) {
        System.out.println("Search by: 1. Day  2. Exercise Type");
        System.out.print("Choice: ");
        int opt = readInt();
        sc.nextLine();

        List<Lesson> lessons;
        if (opt == 1) {
            System.out.print("Enter day (Saturday/Sunday): ");
            String day = sc.nextLine().trim();
            lessons = timetable.searchByDay(day);
        } else {
            System.out.println("Types: Zumba, Yoga, Box Fit, Swimming, Body Blitz, Aquacise");
            System.out.print("Enter type: ");
            String type = sc.nextLine().trim();
            lessons = timetable.searchByType(type);
        }

        if (lessons.isEmpty()) { System.out.println("No lessons found."); return; }

        System.out.println("\nAvailable lessons:");
        for (Lesson l : lessons) System.out.println(l);

        System.out.print("Enter Lesson ID to book (0 to go back): ");
        int id = readInt();
        if (id == 0) return;

        Lesson chosen = timetable.searchById(id);
        if (chosen == null) { System.out.println("Invalid ID."); return; }

        if (member.hasBooking(chosen)) {
            System.out.println("You already have this lesson booked.");
        } else if (member.hasTimeConflict(chosen)) {
            System.out.println("Time conflict with an existing booking.");
        } else if (chosen.bookSeat()) {
            Booking b = new Booking(nextBookingId++, member, chosen);
            member.addBooking(b);
            System.out.println("Booking successful! Booking ID: " + b.getBookingId());
        } else {
            System.out.println("Lesson is full. Booking unsuccessful.");
        }
    }

    static void changeOrCancel(Timetable timetable, Member member) {
        List<Booking> active = getActiveBookings(member);
        if (active.isEmpty()) { System.out.println("No active bookings."); return; }

        printBookings(active);
        System.out.print("Enter Booking ID: ");
        int bid = readInt();
        Booking booking = findBooking(active, bid);
        if (booking == null) { System.out.println("Booking not found."); return; }

        System.out.println("1. Change lesson  2. Cancel booking");
        System.out.print("Choice: ");
        int opt = readInt();
        sc.nextLine();

        if (opt == 2) {
            booking.cancelBooking();
            System.out.println("Booking " + bid + " cancelled.");
        } else if (opt == 1) {
            System.out.println("Search new lesson by: 1. Day  2. Exercise Type");
            System.out.print("Choice: ");
            int sopt = readInt();
            sc.nextLine();

            List<Lesson> lessons;
            if (sopt == 1) {
                System.out.print("Enter day (Saturday/Sunday): ");
                String day = sc.nextLine().trim();
                lessons = timetable.searchByDay(day);
            } else {
                System.out.println("Types: Zumba, Yoga, Box Fit, Swimming, Body Blitz, Aquacise");
                System.out.print("Enter type: ");
                String type = sc.nextLine().trim();
                lessons = timetable.searchByType(type);
            }

            if (lessons.isEmpty()) { System.out.println("No lessons found."); return; }
            System.out.println("\nAvailable lessons:");
            for (Lesson l : lessons) System.out.println(l);

            System.out.print("Enter new Lesson ID (0 to go back): ");
            int lid = readInt();
            if (lid == 0) return;

            Lesson newLesson = timetable.searchById(lid);
            if (newLesson == null) { System.out.println("Invalid ID."); return; }

            if (member.hasBooking(newLesson)) {
                System.out.println("You already have this lesson booked.");
                return;
            }
            if (booking.changeLesson(newLesson)) {
                System.out.println("Booking " + bid + " changed to: " + newLesson);
            } else {
                System.out.println("New lesson is full. Change unsuccessful.");
            }
        }
    }

    static void attendLesson(Member member) {
        List<Booking> attendable = new ArrayList<>();
        for (Booking b : member.getBookings())
            if (b.getStatus().equals("booked") || b.getStatus().equals("changed"))
                attendable.add(b);

        if (attendable.isEmpty()) { System.out.println("No lessons available to attend."); return; }

        printBookings(attendable);
        System.out.print("Enter Booking ID to attend: ");
        int bid = readInt();
        Booking booking = findBooking(attendable, bid);
        if (booking == null) { System.out.println("Booking not found."); return; }

        sc.nextLine();
        System.out.print("Write your review: ");
        String review = sc.nextLine().trim();

        int rating = 0;
        while (rating < 1 || rating > 5) {
            System.out.print("Rating (1-5): ");
            rating = readInt();
            if (rating < 1 || rating > 5) System.out.println("Enter a number between 1 and 5.");
        }

        booking.attendLesson(review, rating);
        System.out.println("Lesson attended. Thank you for your review!");
    }

    static Member selectMember(List<Member> members) {
        System.out.println("\n=== Select Member ===");
        for (int i = 0; i < members.size(); i++)
            System.out.println((i + 1) + ". " + members.get(i).getUserName());
        System.out.print("Choice: ");
        int idx = readInt() - 1;
        if (idx < 0 || idx >= members.size()) idx = 0;
        System.out.println("Logged in as: " + members.get(idx).getUserName());
        return members.get(idx);
    }

    static List<Booking> getActiveBookings(Member member) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : member.getBookings())
            if (!b.getStatus().equals("cancelled")) result.add(b);
        return result;
    }

    static void printBookings(List<Booking> bookings) {
        System.out.println("\nBookings:");
        for (Booking b : bookings) {
            Lesson l = b.getLesson();
            System.out.printf("  Booking %d -> %s | %s %s | £%.2f | Status: %s%n",
                    b.getBookingId(), l.getLessonType(), l.getLessonDay(),
                    l.getLessonTime(), l.getLessonPrice(), b.getStatus());
        }
    }

    static Booking findBooking(List<Booking> bookings, int id) {
        for (Booking b : bookings) if (b.getBookingId() == id) return b;
        return null;
    }

    static int readInt() {
        while (!sc.hasNextInt()) { System.out.print("Please enter a number: "); sc.next(); }
        return sc.nextInt();
    }

    static Timetable buildTimetable() {
        Timetable t = new Timetable();
        int[] months = {
            1,1,1,1,1,1,1,1,
            2,2,2,2,2,2,2,2
        };
        String[] dayNames = {
            "Saturday","Sunday","Saturday","Sunday",
            "Saturday","Sunday","Saturday","Sunday",
            "Saturday","Sunday","Saturday","Sunday",
            "Saturday","Sunday","Saturday","Sunday"
        };
        String[] times  = {"Morning", "Afternoon", "Evening"};
        String[] types  = {"Zumba", "Yoga", "Box Fit", "Swimming", "Body Blitz", "Aquacise"};
        double[] prices = {8.00,    9.50,   7.00,      7.00,       8.50,         9.75};

        int id = 1;
        for (int w = 0; w < dayNames.length; w++) {
            String dayName = dayNames[w];
            for (int s = 0; s < times.length; s++) {
                int typeIdx = (id - 1) % types.length;
                t.addLesson(new Lesson(id++, types[typeIdx], dayName, times[s],
                        months[w], 4, prices[typeIdx]));
            }
        }
        return t;
    }

    static List<Member> buildMembers() {
        List<Member> list = new ArrayList<>();
        String[] names = {
            "Aarav Sharma", "Ananya Iyer", "Ishaan Malhotra", "Myra Kapoor",
            "Kabir Joshi", "Kiara Reddy", "Advait Verma", "Zoya Khan",
            "Reyansh Gupta", "Saanvi Nair"
        };
        for (int i = 0; i < names.length; i++)
            list.add(new Member(i + 1, names[i]));
        return list;
    }

    static void preloadData(Timetable t, List<Member> members) {
        Object[][] data = {
            {0,  1, 5, "Zumba was so energetic, loved every minute!"},
            {1,  2, 4, "Great yoga session, very calming and well-paced."},
            {2,  3, 4, "Box Fit was intense but really fun."},
            {3,  4, 5, "Swimming lesson was excellent, instructor was brilliant."},
            {4,  5, 3, "Body Blitz was tough but rewarding."},
            {5,  6, 5, "Aquacise was fantastic, will definitely come back."},
            {6,  7, 4, "Zumba on Sunday morning was a great start to the day."},
            {7,  8, 4, "Yoga in the afternoon was very relaxing."},
            {8,  9, 5, "Box Fit afternoon session was high energy and brilliant."},
            {9, 10, 3, "Swimming was good but the pool was a bit cold."},
            {0, 11, 4, "Body Blitz evening session really pushed my limits."},
            {1, 12, 5, "Aquacise evening was so much fun, great instructor."},
            {2, 13, 4, "Zumba on the second Saturday was even better."},
            {3, 14, 5, "Yoga session was deeply relaxing and well structured."},
            {4, 25, 5, "First February Zumba was amazing, great atmosphere."},
            {5, 26, 4, "Yoga in February was very peaceful and refreshing."},
            {6, 27, 3, "Box Fit was challenging but I enjoyed it overall."},
            {7, 28, 5, "Swimming in February was superb, best session yet."},
            {8, 29, 4, "Body Blitz was a great full-body workout."},
            {9, 30, 5, "Aquacise was brilliant, loved the water exercises."},
            {0, 31, 4, "Zumba Sunday morning in February was so lively."},
            {1, 32, 5, "Yoga afternoon was the most relaxing session I have had."},
        };

        for (Object[] row : data) {
            int memberIdx = (int) row[0];
            int lessonId  = (int) row[1];
            int rating    = (int) row[2];
            String review = (String) row[3];

            Member member = members.get(memberIdx);
            Lesson lesson = t.searchById(lessonId);
            if (lesson == null) continue;
            if (member.hasBooking(lesson)) continue;
            if (!lesson.bookSeat()) continue;

            Booking b = new Booking(nextBookingId++, member, lesson);
            member.addBooking(b);
            b.attendLesson(review, rating);
        }
    }
}
