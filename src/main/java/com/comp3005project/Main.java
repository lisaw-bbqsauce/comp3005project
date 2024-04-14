package com.comp3005project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDateTime;

public class Main {
    private final static String url = "jdbc:postgresql://localhost:5432/University";
    private final static String sqluser = "postgres";
    private final static String sqlpassword = "student";

    private static String readSqlFile(String filePath) throws IOException {
        StringBuilder sqlScript = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sqlScript.append(line).append("\n");
            }
        }
        return sqlScript.toString();
    }

    // helper functions
    public void memberOption() {
        try {
            String memberOptions = "1 Register\n2 Login\n0 Back\n";
            System.out.println("\nPick an option:");
            System.out.println(memberOptions);

            BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(System.in));
            String query = reader.readLine();
            
            switch (query) {
                case "1": // register
                    User member = new User();
                    member.setRole(UserRole.MEMBER);

                    String firstName, lastName, email, password, phone, address, password2;
                    String checkout;

                    while (true) {
                        System.out.println("Enter your first name (required):");
                        firstName = reader.readLine();
                        if (firstName.isEmpty()) {
                            continue;
                        }
                        member.setFirstName(firstName);
    
                        System.out.println("Enter your last name (required):");
                        lastName = reader.readLine();
                        if (lastName.isEmpty()) {
                            continue;
                        }
                        member.setLastName(lastName);
    
                        System.out.println("Enter phone number (optional):");
                        phone = reader.readLine();
                        member.setPhoneNumber(phone);
                        
                        System.out.println("Enter your address (optional):");
                        address = reader.readLine();
                        member.setAddress(address);
    
                        System.out.println("Enter your email (required):");
                        email = reader.readLine();
                        if (email.isEmpty()) {
                            continue;
                        }
                        member.setEmail(email);
    
                        System.out.println("Enter your password (required):");
                        password = reader.readLine();
                        if (password.isEmpty()) {
                            continue;
                        }
                        member.setPassword(password);
    
                        while (true) {
                            System.out.println("Confirm your password (required):");
                            password2 = reader.readLine();
                            if (password.equals(password2)) {
                                break;
                            } else {
                                System.out.println("Passwords do not match!");
                            }
                        }

                        System.out.println("Proceed to checkout? (y/n)");
                        checkout = reader.readLine();
                        if (checkout.equals("y")) {
                            printCheckout(member);
                        }

                        break;
                    }
                    if (checkout.equals("y")) {
                        createUser(member);
                        member = getOneUser(member.getEmail());
                        System.out.println("Pay now? (y/n)");
                        checkout = reader.readLine();
                        if (checkout.equals("n")) {
                            createBilling(member.getUserId(), 50, BillingStatus.PENDING);
                        } else {
                            createBilling(member.getUserId(), 0, BillingStatus.PAID);
                        }
                        memberLogin(member.getEmail());
                    }
                    break;
                case "2": // login
                    while (true) { 
                        System.out.println("Enter your email:");
                        email = reader.readLine();
                        System.out.println("Enter your password:");
                        password = reader.readLine();
                        if (tryLogin(email, password, UserRole.MEMBER)) {
                            memberLogin(email);
                            break;
                        }
                    }
                    break;
                case "0": // quit
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        // register, update personal info, update fitness goals, update health metric
        // display dashboard with exercise routines, fitness achievements, health statistics
        // manage schedule, book personal session, book classes
    }

    public boolean printCheckout(User member) {
        System.out.println("\nFirst Name: " + member.getFirstName());
        System.out.println("Last Name: " + member.getLastName());
        System.out.println("Email: " + member.getEmail());
        System.out.println("Phone Number: " + member.getPhoneNumber());
        System.out.println("Address: " + member.getAddress());
        System.out.println("\nCheckout:");
        
        while (true) {
            System.out.println("1 Continue to payment\n2 Cancel payment\n");

            try {
                BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(System.in));
                String query = reader.readLine();
                switch (query) {
                    case "1":
                        System.out.println("Confirmed!");
                        return true;
                    case "2":
                        System.out.println("Cancelled!");
                        return false;
                    default:
                        System.out.println("Invalid option!");
                        break;
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
    }

    public void memberLogin(String email) {
        boolean exit = false;
        
        try {
            while (!exit) {
                User member = getOneUser(email);

                int memberId = member.getUserId();
                String newFirstName, newLastName, newEmail, newPassword, newPhoneNumber, newAddress;
                
                printMemberProfile(member);
                String memberOptions = "1 Update personal information\n2 Add fitness goals\n3 Update fitness goals"
                        + "\n4 Add health metrics\n5 Update health metrics\n6 Go to dashboard\n0 Back\n";
                System.out.println("\nPick an option:");
                System.out.println(memberOptions);

                BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(System.in));
                String query = reader.readLine();

                switch (query) {
                    case "1": // update personal information
                        System.out.println("Enter your new information or press enter to skip.");
                        System.out.println("Enter your new first name:");
                        newFirstName = reader.readLine();
                        // System.out.println("test first name: " + newFirstName); // debug
                        System.out.println("Enter your new last name:");
                        newLastName = reader.readLine();
                        System.out.println("Enter your new email:");
                        newEmail = reader.readLine();
                        System.out.println("Enter your new password:");
                        newPassword = reader.readLine();
                        System.out.println("Enter your new phone number:");
                        newPhoneNumber = reader.readLine();
                        System.out.println("Enter your new address:");
                        newAddress = reader.readLine();

                        User newMember = new User(newFirstName, newLastName, newEmail, newPassword, newPhoneNumber, newAddress, UserRole.MEMBER);
                        updatePersonalInformation(memberId, newMember);
                        break;
                    case "2": // add fitness goal
                        System.out.println("Add your fitness goals.");
                        System.out.println("Enter your running goal (required):");
                        String run = reader.readLine();
                        System.out.println("Enter your swimming goal (required):");
                        String swim = reader.readLine();
                        System.out.println("Enter your biking goal (required):");
                        String bike = reader.readLine();
                        createFitnessGoal(memberId, run, swim, bike);
                        break;
                    case "3": // update fitness goals
                        System.out.println("Update your fitness goals or press enter to skip.");
                        System.out.println("Enter your new running goal:");
                        run = reader.readLine();
                        System.out.println("Enter your new swimming goal:");
                        swim = reader.readLine();
                        System.out.println("Enter your new biking goal:");
                        bike = reader.readLine();
                        if (run.isEmpty()) {
                            run = null;
                        }
                        if (swim.isEmpty()) {
                            swim = null;
                        }
                        if (bike.isEmpty()) {
                            bike = null;
                        }
                        updateFitnessGoals(memberId, run, swim, bike);
                        break;
                    case "4": // add health metrics
                        System.out.println("Enter the metric you would like to add:");
                        String metric = reader.readLine();
                        System.out.println("Enter your value:");
                        String value = reader.readLine();
                        createHealthMetric(memberId, metric, value);
                        break;
                    case "5": // update health metrics
                        System.out.println("Enter the metric you would like to update:");
                        metric = reader.readLine();
                        System.out.println("Enter your new value:");
                        value = reader.readLine();
                        updateHealthMetrics(memberId, metric, value);
                        break;
                    case "6": // go to dashboard
                        printDashboardDisplay(member.getFirstName(), member.getLastName());
                        reader.readLine();
                        break;
                    case "0": // quit
                        System.out.println("going back..."); // debug
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option!");
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void trainerOption() {
        boolean exit = false;

        try {
            System.out.println("\n1 Trainer login or 0 to go back:");

            BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(System.in));
            String query = reader.readLine();

            switch (query) {
                case "1": // login
                    // login
                    String email, password, first_name, last_name;
                    while (true) {
                        System.out.println("Enter your email:");
                        email = reader.readLine();
                        System.out.println("Enter your password:");
                        password = reader.readLine();
                        if (tryLogin(email, password, UserRole.TRAINER)) {
                            break;
                        }
                    }
                    int curr_trainer = getUserId(email);
                    // manage schedule, view member profile/search member's name
                    while (!exit) {
                        String trainerOptions = "1 Add to schedule\n2 Remove from schedule\n3 View member profile\n0 Back\n";
                        System.out.println("\nPick an option:");
                        System.out.println(trainerOptions);
                        query = reader.readLine();

                        switch (query) {
                            case "1": // add to schedule
                                while (true) {
                                    getTrainerSchedule(curr_trainer);
                                    System.out.println("Enter the date (yyyy-mm-dd) or '0' to go back:");
                                    String dateStr = reader.readLine();
                                    if (dateStr.equals("0")) {
                                        break;
                                    }
                                    System.out.println("Enter the start time (hh:mm:ss):");
                                    String startTimeStr = reader.readLine();
                                    System.out.println("Enter the end time (hh:mm:ss):");
                                    String endTimeStr = reader.readLine();

                                    LocalDateTime dateTime = LocalDateTime.parse(dateStr + "T" + startTimeStr);
                                    Timestamp startTimestamp = Timestamp.valueOf(dateTime);

                                    dateTime = LocalDateTime.parse(dateStr + "T" + endTimeStr);
                                    Timestamp endTimestamp = Timestamp.valueOf(dateTime);

                                    createTrainerSchedule(curr_trainer, startTimestamp, endTimestamp);
                                }
                                
                                break;
                            case "2": // remove from schedule
                                while (true) {
                                    getTrainerSchedule(curr_trainer);
                                    System.out.println("Enter the date (yyyy-mm-dd) or '0' to go back:");
                                    String temp = reader.readLine();
                                    if (temp.equals("0")) {
                                        break;
                                    }
                                    System.out.println("Enter the schedule id:");
                                    int scheduleId = Integer.parseInt(reader.readLine());
                                    deleteTrainerSchedule(scheduleId);
                                }
                            case "3": // view member profile
                                System.out.println("Enter member's first name:");
                                first_name = reader.readLine();
                                System.out.println("Enter member's last name:");
                                last_name = reader.readLine();
                                printViewMemberProfile(first_name, last_name);
                                break;
                            case "0": // quit
                                exit = true;
                                break;
                            default:
                                System.out.println("Invalid option!");
                                break;
                        }
                    }
                    case "0": // quit
                        break;
                    default:
                        System.out.println("Invalid option!");
                        break;
                }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void adminOption() {
        try {
            System.out.println("\n1 Admin login or 0 to go back:");

            BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(System.in));
            String query = reader.readLine();

            switch (query) {
                case "1": // login
                    String email, password;
                    while (true) {
                        System.out.println("Enter your email:");
                        email = reader.readLine();
                        System.out.println("Enter your password:");
                        password = reader.readLine();
                        if (tryLogin(email, password, UserRole.ADMIN)) {
                            break;
                        }
                    }
                    boolean exit = false;
                    int adminId = getUserId(email);

                    // manage room booking, manage class schedule, monitor equipment maintenance
                    // oversee billing and payment
                    while (!exit) {
                        String adminOptions = "1 Create room booking\n2 Update room booking\n3 Cancel room booking" 
                                + "\n4 Monitor equipment maintenance\n5 Update class schedule\n6 Update billing and payment"
                                + "\n7 View all rooms\n8 View all room bookings\n9 View billings\n0 Back\n";
                        System.out.println("Pick an option:");
                        System.out.println(adminOptions);
                        query = reader.readLine();

                        switch (query) {
                            case "1": // add room booking
                                System.out.println("Enter room name:");
                                String roomName = reader.readLine();
                                System.out.println("Enter duration of booking:");
                                String duration = reader.readLine();
                                createRoomBooking(adminId, roomName, duration);
                                updateRoomStatus(roomName, RoomStatus.BOOKED);
                                break;
                            case "2": // update room booking
                                System.out.println("Enter booking id:");
                                int booking_id = Integer.parseInt(reader.readLine());
                                // System.out.println("Enter room name:");
                                // roomName = reader.readLine();
                                System.out.println("Enter new duration of booking:");
                                duration = reader.readLine();
                                updateRoomBooking(booking_id, adminId, duration);
                                break;
                            case "3": // cancel room booking
                                System.out.println("Enter booking id to cancel:");
                                booking_id = Integer.parseInt(reader.readLine());
                                deleteRoomBooking(booking_id);
                                roomName = getRoomName(booking_id);
                                updateRoomStatus(roomName, RoomStatus.AVAILABLE);
                                break;
                            case "4": // monitor equipment maintenance
                                System.out.println("Enter equipment name:");
                                String name = reader.readLine();
                                int equipmentId = getEquipmentId(name);
                                if (equipmentId == -1) {
                                    System.out.println("Equipment not found!");
                                    break;
                                }
                                getEquipmentMaintenance(equipmentId);
                                break;
                            case "5": // update class schedule
                                System.out.println("Enter class id:");
                                int class_id = Integer.parseInt(reader.readLine());
                                System.out.println("\nEnter date (yyyy-mm-dd):");
                                String dateStr = reader.readLine();
                                Date date = Date.valueOf(dateStr);

                                // System.out.println("Enter the start time (hh:mm:ss):");
                                // String startTimeStr = reader.readLine();
                                // System.out.println("Enter the end time (hh:mm:ss):");
                                // String endTimeStr = reader.readLine();

                                // LocalDateTime dateTime = LocalDateTime.parse(dateStr + "T" + startTimeStr);
                                // Timestamp startTimestamp = Timestamp.valueOf(dateTime);

                                // dateTime = LocalDateTime.parse(dateStr + "T" + endTimeStr);
                                // Timestamp endTimestamp = Timestamp.valueOf(dateTime);

                                System.out.println("Enter new class duration:");
                                String classDuration = reader.readLine();

                                updateClassSchedule(class_id, date, classDuration);
                                break;
                            case "6": // update billing and payment
                                System.out.println("Enter member id:");
                                int member_id = Integer.parseInt(reader.readLine());
                                System.out.println("Enter new amount due:");
                                int amount_due = Integer.parseInt(reader.readLine());
                                System.out.println("Enter new status (PAID|PENDING):");
                                BillingStatus status = BillingStatus.valueOf(reader.readLine());
                                updateBilling(member_id, amount_due, status);
                                break;
                            case "7": // view all rooms
                                getAllRooms();
                                break;
                            case "8": // view all room bookings
                                getAllRoomBookings();
                                break;
                            case "9": // view billings
                                getAllBillings();
                                break;
                            case "0": // quit
                                exit = true;
                                break;
                            default:
                                System.out.println("Invalid option!");
                                break;
                        }
                    }
                case "0": // quit
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean tryLogin(String email, String password, UserRole role) {
        // check if email and password match and check role
        User user = getOneUser(email);
        if (user != null) {
            String dbPassword = user.getPassword();
            // System.out.println("role: " + role); //debug
            // System.out.println("user role: " + user.getRole()); //debug
            if (dbPassword.equals(password) && role.equals(user.getRole())){
                System.out.println("Login successful!\n");
                System.out.println("--------------------");
                System.out.println("Welcome " + user.getFirstName() + "!");
                return true;
            }
        }
        System.out.println("Login failed! Email or password is incorrect.\n");
        return false;
    }

    /**
     * Print profile of a member, view-only
     * Prints who's profile is being displayed, their fitness goal, fitness achievements, health statistics
     * @param first_name
     * @param last_name
     */
    public void printViewMemberProfile(String first_name, String last_name) {
        User member = getOneUser(first_name, last_name);
        if (member == null) {
            System.out.println("Member not found!");
            return;
        }

        int memberId = member.getUserId();

        System.out.println("--------------------");
        System.out.println(first_name + " " + last_name + "'s profile:");

        boolean hasGoal, hasAchievement, hasMetric;
        hasGoal = getFitnessGoal(memberId);
        hasAchievement = getFitnessAchievement(memberId);
        hasMetric = getHealthMetric(memberId);
        if (!hasGoal && !hasAchievement && !hasMetric) {
            System.out.println("Wow, such empty.");
        }
        // System.out.println("--------------------");
    }

    public void printDashboardDisplay(String first_name, String last_name) {
        User member = getOneUser(first_name, last_name);
        if (member == null) {
            System.out.println("Member not found!");
            return;
        }
    
        int memberId = member.getUserId();
    
        System.out.println("--------------------");
        System.out.println("Welcome " + member.getFirstName() + "! This is the dashboard:");
    
        boolean hasRoutine = getExerciseRoutine(memberId);
        boolean hasAchievement = getFitnessAchievement(memberId);
        boolean hasMetric = getHealthMetric(memberId);
    
        if (!hasRoutine && !hasAchievement && !hasMetric) {
            System.out.println("Wow, such empty.");
        }

        boolean exit = false;
        try {
            while (!exit) {
                System.out.println("--------------------");
                System.out.println("Dashboard options:");
                String dashboardOptions = "1 Book personal session\n2 Book group class" 
                        + "\n3 View all personal sessions\n4 View all group classes\n0 Back\n";
                System.out.println(dashboardOptions);
    
                System.out.println("Enter an option:");
                BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(System.in));
                String query = reader.readLine();

                switch (query) {
                    case "1": // book personal session
                        System.out.println("Enter trainer's email:");
                        String trainerEmail = reader.readLine();
                        User trainer = getOneUser(trainerEmail);
                        if (trainer == null) {
                            System.out.println("Trainer not found!");
                            break;
                        }
                        int trainerId = trainer.getUserId();
                        getTrainerSchedule(trainerId);

                        System.out.println("\nEnter date (yyyy-mm-dd):");
                        String dateStr = reader.readLine();
                        Date date = Date.valueOf(dateStr);
                        System.out.println("Enter the start time (hh:mm:ss):");
                        String startTimeStr = reader.readLine();

                        LocalDateTime dateTime = LocalDateTime.parse(dateStr + "T" + startTimeStr);
                        Timestamp timestamp = Timestamp.valueOf(dateTime);

                        System.out.println("Enter duration (in minutes):");
                        int duration = Integer.parseInt(reader.readLine());

                        registerPersonalSession(memberId, trainerId, date, timestamp, duration);
                        int sessionId = getSessionId(memberId, trainerId, timestamp);
                        updateTrainerSchedule(trainerId, sessionId);
                        break;
                    case "2": // book group class
                        System.out.println("Enter trainer's email:");
                        trainerEmail = reader.readLine();
                        trainer = getOneUser(trainerEmail);
                        if (trainer == null) {
                            System.out.println("Trainer not found!");
                            break;
                        }
                        trainerId = trainer.getUserId();
                        getTrainerSchedule(trainerId);
                        
                        System.out.println("Enter date (yyyy-mm-dd):");
                        dateStr = reader.readLine();
                        date = Date.valueOf(dateStr);
                        System.out.println("Enter the start time (hh:mm:ss):");
                        startTimeStr = reader.readLine();

                        dateTime = LocalDateTime.parse(dateStr + "T" + startTimeStr);
                        timestamp = Timestamp.valueOf(dateTime);

                        System.out.println("Enter duration (in minutes):");
                        duration = Integer.parseInt(reader.readLine());
                        System.out.println("Enter class name:");
                        String className = reader.readLine();

                        registerGroupClass(trainerId, date, timestamp, duration, className);
                        break;
                    case "3": // view all personal sessions
                        getAllMemberSessions(memberId);
                        break;
                    case "4": // view all group classes
                        getAllMemberClasses(memberId);
                        break;
                    case "0": // quit
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option!");
                        break;
                
                }
            }
            // System.out.println("Enter to go back to profile"); // debug
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void printMemberProfile(User member) {
        if (member != null) {
            int memberId = member.getUserId();
            String email = member.getEmail();
            String phoneNumber = member.getPhoneNumber();
            String address = member.getAddress();
    
            System.out.println("--------------------");
            System.out.println(member.getFirstName() + " " + member.getLastName() + "'s profile:");
            System.out.println("Member ID: " + memberId);
            System.out.println("Email: " + email);
            System.out.println("Phone Number: " + phoneNumber);
            System.out.println("Address: " + address);

            System.out.println("--------------------");
            boolean hasGoal, hasAchievement, hasMetric;
            hasGoal = getFitnessGoal(memberId);
            hasAchievement = getFitnessAchievement(memberId);
            hasMetric = getHealthMetric(memberId);
            if (!hasGoal && !hasAchievement && !hasMetric) {
                System.out.println("Wow, such empty.");
            }
            System.out.println("--------------------");
        } else {
            System.out.println("Member not found!");
        }
    }

    // CRUD functions
    // GET

    public int getUserId(String email) {
        String sql = "SELECT * FROM comp3005project.user WHERE email = ?;";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            int user_id = -1;
            while (resultSet.next()) {
                user_id = resultSet.getInt("user_id");
            }
            return user_id;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public boolean getFitnessGoal(int member_id) {
        String sql = "SELECT * FROM comp3005project.fitness_goal WHERE member_id = ?;";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, member_id);
            ResultSet resultSet = statement.executeQuery();

            boolean hasFitnessGoal = false;
            System.out.println("Fitness goals:");
            while (resultSet.next()) {
                String run = resultSet.getString("run");
                String swim = resultSet.getString("swim");
                String bike = resultSet.getString("bike");
                System.out.println("\tRun: " + run + " \tSwim: " + swim + " \tBike: " + bike);
                hasFitnessGoal = true;
            }
            return hasFitnessGoal;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean getFitnessAchievement(int member_id) {
        String sql = "SELECT * FROM comp3005project.fitness_achievement WHERE member_id = ?;";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, member_id);
            ResultSet resultSet = statement.executeQuery();

            boolean hasFitnessAchievement = false;
            System.out.println("Fitness achievements:");
            while (resultSet.next()) {
                String description = resultSet.getString("description");
                Date date = resultSet.getDate("date_achieved");
                System.out.println("\t" + description + " (" + date.toString() + ")");
                hasFitnessAchievement = true;
            }
            return hasFitnessAchievement;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean getHealthMetric(int member_id) {
        String sql = "SELECT * FROM comp3005project.health_metric WHERE member_id = ?;";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, member_id);
            ResultSet resultSet = statement.executeQuery();

            boolean hasHealthMetric = false;
            System.out.println("Health metrics:");
            while (resultSet.next()) {
                String metric = resultSet.getString("metric");
                String value = resultSet.getString("value");
                String date = resultSet.getString("date_recorded");
                System.out.println("\t" + metric + ": " + value + " (" + date + ")");
                hasHealthMetric = true;
            }
            return hasHealthMetric;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean getExerciseRoutine(int member_id) {
        // System.out.println("get Exercise routines called"); // debug
        String sql = "SELECT * FROM comp3005project.exercise_routine WHERE member_id = ?;";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, member_id);
            ResultSet resultSet = statement.executeQuery();

            boolean hasExerciseRoutine = false;
            System.out.println("Exercise routine:");
            while (resultSet.next()) {
                String description = resultSet.getString("description");
                String duration = resultSet.getString("duration");
                System.out.println("\tDescription: " + description + "\tDuration: " + duration);
                hasExerciseRoutine = true;
            }
            return hasExerciseRoutine;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public User getOneUser(String first_name, String last_name) {
        String sql = "SELECT * FROM comp3005project.user WHERE first_name = ? AND last_name = ?;";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setString(1, first_name);
            statement.setString(2, last_name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractUserFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null; // Return null if member not found or if an exception occurs
    }
    
    public User getOneUser(String email) {
        String sql = "SELECT * FROM comp3005project.user WHERE email = ?;";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractUserFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null; // Return null if member not found or if an exception occurs
    }
    
    // Helper method to extract Member object from ResultSet
    private User extractUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("user_id"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setPhoneNumber(resultSet.getString("phone_number"));
        user.setAddress(resultSet.getString("address"));
        user.setRole(UserRole.valueOf(resultSet.getString("role")));
        return user;
    }    

    public void getEquipmentMaintenance(int equipmentId) {
        String sql = "SELECT * FROM comp3005project.equipment WHERE equipment_id = ?;";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, equipmentId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                Date date = resultSet.getDate("last_maintenance");
                System.out.println("Equipment: " + name + "\nLast maintenance completed: " + date.toString());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getEquipmentId(String name) {
        String sql = "SELECT * FROM comp3005project.equipment WHERE name = ?;";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            int equipmentId = -1;
            while (resultSet.next()) {
                equipmentId = resultSet.getInt("equipment_id");
            }
            return equipmentId;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public String getRoomName(int roomId) {
        String sql = "SELECT * FROM comp3005project.room WHERE room_id = ?;";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, roomId);
            ResultSet resultSet = statement.executeQuery();
            String roomName = "";
            while (resultSet.next()) {
                roomName = resultSet.getString("name");
            }
            return roomName;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public int getRoomId(String roomName) {
        String sql = "SELECT * FROM comp3005project.room WHERE name = ?;";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, roomName);
            ResultSet resultSet = statement.executeQuery();
            int roomId = -1;
            while (resultSet.next()) {
                roomId = resultSet.getInt("room_id");
            }
            return roomId;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }
    
    public void getAllRooms() {
        String sql = "SELECT * FROM comp3005project.room;";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
    
            while (resultSet.next()) {
                String roomName = resultSet.getString("name");
                String status = resultSet.getString("status");
                System.out.println("Room: " + roomName + "\t" + status);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void getAllRoomBookings() {
        String sql = "SELECT * FROM comp3005project.room_booking;";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
    
            while (resultSet.next()) {
                int bookingId = resultSet.getInt("booking_id");
                int roomId = resultSet.getInt("room_id");
                String roomName = getRoomName(roomId);
                Date date = resultSet.getDate("date");
                String duration = resultSet.getString("duration");
                System.out.println(bookingId +".\tRoom ID: " + roomId + "\tRoom: " + roomName + "\tDate: " + date.toString() + "\tDuration: " + duration);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public boolean isTrainerAvailable(int trainerId, Date date, Timestamp startTime, int duration) {
        String sql = "SELECT COUNT(*) AS count FROM comp3005project.Trainer_schedule " +
                     "WHERE trainer_id = ? AND start_time <= ? AND end_time >= ? " +
                     "AND DATE(start_time) = ?";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            LocalDateTime localStartTime = startTime.toLocalDateTime();

            statement.setInt(1, trainerId);
            statement.setTimestamp(2, startTime);
            statement.setTimestamp(3, Timestamp.valueOf(localStartTime.plusMinutes(duration)));
            statement.setDate(4, date);
    
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count != 0; // Trainer is available if count is 0
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false; // Default to not available
    }

    public void getTrainerSchedule(int trainerId) {
        String sql = "SELECT * FROM comp3005project.Trainer_schedule WHERE trainer_id = ?;";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setInt(1, trainerId);
            ResultSet resultSet = statement.executeQuery();
    
            System.out.println("Schedule:");
            while (resultSet.next()) {
                int scheduleId = resultSet.getInt("schedule_id");
                Timestamp startTime = resultSet.getTimestamp("start_time");
                Timestamp endTime = resultSet.getTimestamp("end_time");
                System.out.println(scheduleId + ".\tStart time: " + startTime.toString() + "\n\t\tEnd time: " + endTime.toString() + "\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    
    public void getAllMemberSessions(int memberId) {
        String sql = "SELECT * FROM comp3005project.personal_session WHERE member_id = ?;";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setInt(1, memberId);
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                int trainerId = resultSet.getInt("trainer_id");
                Timestamp startTime = resultSet.getTimestamp("start_time");
                int duration = resultSet.getInt("duration");
                System.out.println("Personal session:\n");
                System.out.println("\tTrainer ID: " + trainerId + "\n\tStart time: " + startTime.toString() + "\n\tDuration: " + duration);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void getAllMemberClasses(int memberId) {
        String sql = "SELECT gc.class_id, gc.name, gc.trainer_id, gc.date, gc.duration " +
                     "FROM comp3005project.User u " +
                     "JOIN comp3005project.Group_class gc ON u.class_id = gc.class_id " +
                     "WHERE u.user_id = ?";
        
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
        
                statement.setInt(1, memberId);
                ResultSet resultSet = statement.executeQuery();
        
                while (resultSet.next()) {
                    int classId = resultSet.getInt("class_id");
                    String name = resultSet.getString("name");
                    int trainerId = resultSet.getInt("trainer_id");
                    Date date = resultSet.getDate("date");
                    int duration = resultSet.getInt("duration");
                    System.out.println("Group class:\n");
                    System.out.println("\tClass ID: " + classId + "\n\tName: " + name + "\n\tTrainer ID: " + trainerId + "\n\tDate: " + date.toString() + "\n\tDuration: " + duration);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
    }

    public int getSessionId(int memberId, int trainerId, Timestamp timestamp) {
        String sql = "SELECT session_id FROM comp3005project.personal_session WHERE member_id = ? AND trainer_id = ? AND start_time = ?;";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setInt(1, memberId);
            statement.setInt(2, trainerId);
            statement.setTimestamp(3, timestamp);
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                int sessionId = resultSet.getInt("session_id");
                return sessionId;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public void getAllBillings() {
        String sql = "SELECT * FROM comp3005project.billing;";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
    
            while (resultSet.next()) {
                int billingId = resultSet.getInt("billing_id");
                int memberId = resultSet.getInt("member_id");
                int amountDue = resultSet.getInt("amount_due");
                BillingStatus status = BillingStatus.valueOf(resultSet.getString("status"));
                System.out.println("Billing ID: " + billingId + "\tMember ID: " + memberId + "\tAmount due: " + amountDue + "\tStatus: " + status);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // CREATE
    public void createUser(User user) {
        String sql = "INSERT INTO comp3005project.user (first_name, last_name, phone_number, address, email, password, role) VALUES (?,?,?,?,?,?,?);";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getPhoneNumber());
            statement.setString(4, user.getAddress());
            statement.setString(5, user.getEmail());
            statement.setString(6, user.getPassword());
            statement.setString(7, user.getRole().toString());
            statement.executeUpdate();
            System.out.println(user.getFirstName() + " " + user.getLastName() + " registered successfully!\n");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createFitnessGoal(int member_id, String run, String swim, String bike) {
        String sql = "INSERT INTO comp3005project.fitness_goal (member_id, run, swim, bike) VALUES (?,?,?,?);";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, member_id);
            statement.setString(2, run);
            statement.setString(3, swim);
            statement.setString(4, bike);
            statement.executeUpdate();
            System.out.println("Fitness goal added successfully!\n");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createFitnessAchievement(int member_id, String achievement, String description) {
        String sql = "INSERT INTO comp3005project.fitness_achievement (member_id, achievement, description) VALUES (?,?,?);";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, member_id);
            statement.setString(2, achievement);
            statement.setString(3, description);
            statement.executeUpdate();
            System.out.println("Fitness achievement added successfully!\n");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createHealthMetric(int member_id, String metric, String value) {
        String sql = "INSERT INTO comp3005project.health_metric (member_id, metric, value) VALUES (?,?,?);";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, member_id);
            statement.setString(2, metric.toUpperCase());
            statement.setString(3, value);
            statement.executeUpdate();
            System.out.println("Health metric added successfully!\n");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createExerciseRoutine(int member_id, String routine, String description) {
        String sql = "INSERT INTO comp3005project.exercise_routine (member_id, routine, description) VALUES (?,?,?);";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, member_id);
            statement.setString(2, routine);
            statement.setString(3, description);
            statement.executeUpdate();
            System.out.println("Exercise routine added successfully!\n");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createBilling(int member_id, int amount_due, BillingStatus status) {
        String sql = "INSERT INTO comp3005project.billing (member_id, amount_due, status) VALUES (?,?,?);";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, member_id);
            statement.setInt(2, amount_due);
            statement.setString(3, status.toString());
            statement.executeUpdate();
            System.out.println("Billing added successfully!\n");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createTrainerSchedule(int trainer_id, Timestamp sTime, Timestamp eTime) {
        String sql = "INSERT INTO comp3005project.Trainer_schedule (trainer_id, start_time, end_time) VALUES (?,?,?);";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, trainer_id);
            statement.setTimestamp(2, sTime);
            statement.setTimestamp(3, eTime);
            statement.executeUpdate();
            System.out.println("Trainer availability added successfully!\n");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void createRoomBooking(int staff_id, String roomName, String duration) {
        int room_id = getRoomId(roomName);
        String sql = "INSERT INTO comp3005project.room_booking (staff_id, room_id, duration) VALUES (?, ?, ?);";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, staff_id);
            statement.setInt(2, room_id);
            statement.setString(3, duration);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // System.out.println(roomName + " booking added successfully.\n"); // debug
            } else {
                System.out.println("Failed to add room booking.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void registerPersonalSession(int memberId, int trainerId, Date date, Timestamp startTime, int duration) {
        if (!isTrainerAvailable(trainerId, date, startTime, duration)) {
            System.out.println("Trainer is not available at the specified time.");
            return;
        }
        
        String sql = "INSERT INTO comp3005project.personal_session (member_id, trainer_id, start_time, duration, status) VALUES (?,?,?,?,?);";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, memberId);
            statement.setInt(2, trainerId);
            statement.setTimestamp(3, startTime);
            statement.setInt(4, duration);
            statement.setString(5, TrainingStatus.SCHEDULED.toString());
            statement.executeUpdate();
            System.out.println("Personal session scheduled successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void registerGroupClass(int trainerId, Date date, Timestamp startTime, int duration, String className) {
        // First, check if the trainer is available
        if (!isTrainerAvailable(trainerId, date, startTime, duration)) {
            System.out.println("Trainer is not available at the specified time.");
            return;
        }

        // If trainer is available, proceed to schedule the class
        String sql = "INSERT INTO comp3005project.Group_class (name, trainer_id, date, duration) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, className);
            statement.setInt(2, trainerId);
            statement.setDate(3, date);
            statement.setInt(4, duration);

            statement.executeUpdate();
            System.out.println("Group fitness class scheduled successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // UPDATE
    public void updatePersonalInformation(int member_id, User member) {
        String sql = "UPDATE comp3005project.user SET first_name = COALESCE(?, first_name), last_name = COALESCE(?, last_name), email = COALESCE(?, email), password = COALESCE(?, password), phone_number = COALESCE(?, phone_number), address = COALESCE(?, address) WHERE user_id = ?;";
        
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, member.getFirstName());
            statement.setString(2, member.getLastName());
            statement.setString(3, member.getEmail());
            statement.setString(4, member.getPassword());
            statement.setString(5, member.getPhoneNumber());
            statement.setString(6, member.getAddress());
            statement.setInt(7, member_id);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Personal information updated successfully.");
            } else {
                System.out.println("Failed to update personal information.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void updateFitnessGoals(int member_id, String run, String swim, String bike) {
        String sql = "UPDATE comp3005project.fitness_goal SET run = COALESCE(?, run), swim = COALESCE(?, swim), bike = COALESCE(?, bike) WHERE member_id = ?;";
        
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, run);
            statement.setString(2, swim);
            statement.setString(3, bike);
            statement.setInt(4, member_id);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Fitness goals updated successfully.");
            } else {
                System.out.println("Failed to update fitness goals.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateHealthMetrics(int member_id, String metric, String value) {
        String sql = "UPDATE comp3005project.health_metric SET value = COALESCE(?, value) WHERE member_id = ? AND UPPER(metric) = ?;";
        
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setObject(1, value);
            statement.setInt(2, member_id);
            statement.setObject(3, metric.toUpperCase());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Health metrics updated successfully.");
            } else {
                System.out.println("Failed to update health metrics.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void updateExerciseRoutine(int routine_id, String routine, String description) {
        String sql = "UPDATE comp3005project.exercise_routine SET routine = COALESCE(?, routine), description = COALESCE(?, description) WHERE routine_id = ?;";
        
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, routine);
            statement.setString(2, description);
            statement.setInt(3, routine_id);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Exercise routine updated successfully.");
            } else {
                System.out.println("Failed to update exercise routine.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateTrainerAvailability(int schedule_id, String day, int duration) {
        String sql = "UPDATE comp3005project.trainer_schedule SET day = COALESCE(?, day), duration = COALESCE(?, duration) WHERE schedule_id = ?;";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setString(1, day);
            statement.setInt(2, duration);
            statement.setInt(3, schedule_id);
    
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Trainer availability updated successfully.");
            } else {
                System.out.println("Failed to update trainer availability.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void updateTrainerSchedule(int trainerId, int sessionId) {
        String sql = "UPDATE comp3005project.trainer_schedule SET session_id = COALESCE(?, session_id) WHERE trainer_id = ?;";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setInt(1, sessionId);
            statement.setInt(2, trainerId);
    
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // System.out.println("Trainer schedule updated successfully."); // debug
            } else {
                System.out.println("Failed to update trainer schedule.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateBilling(int member_id, int amount_due, BillingStatus status) {
        String sql = "UPDATE comp3005project.billing SET amount_due = COALESCE(?, amount_due), status = COALESCE(?, status) WHERE member_id = ?;";
        
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, amount_due);
            statement.setString(2, status.toString());
            statement.setInt(3, member_id);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Billing updated successfully.\n");
            } else {
                System.out.println("Failed to update billing.\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateRoomBooking(int booking_id, int staff_id, String duration) {
        // int room_id = getRoomId(roomName);
        String sql = "UPDATE comp3005project.room_booking SET staff_id = COALESCE(?, staff_id), duration = COALESCE(?, duration) WHERE booking_id = ?;";

        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, staff_id);
            // statement.setInt(2, room_id);
            statement.setString(2, duration);
            statement.setInt(3, booking_id);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // System.out.println("Booking updated successfully.\n"); // debug
            } else {
                System.out.println("Failed to update room booking.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void updateRoomStatus(String name, RoomStatus status) {
        String sql = "UPDATE comp3005project.room SET status = COALESCE(?, status) WHERE name = ?;";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setString(1, status.toString());
            statement.setString(2, name);
    
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // System.out.println(name + " status updated successfully.");
            } else {
                System.out.println("Failed to update room status.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateClassSchedule(int classId, Date date, String duration) {
        String sql = "UPDATE comp3005project.Group_class SET date = ?, duration = COALESCE(?, duration) WHERE class_id = ?;";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setDate(1, date);
            statement.setString(2, duration);
            statement.setInt(3, classId);
    
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Class schedule updated successfully.");
            } else {
                System.out.println("Failed to update class schedule.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateMemberWithClassId(int classId, int memberId) {
        String sqlUpdateMember = "UPDATE comp3005project.Member SET class_id = ? WHERE member_id = ?";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sqlUpdateMember)) {
            
            // Assuming you have a variable to hold the member_id of the logged-in member
    
            statement.setInt(1, classId);
            statement.setInt(2, memberId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // DELETE
    public void deleteHealthMetric(int metric_id) {
        String sql = "DELETE FROM comp3005project.health_metric WHERE metric_id = ?;";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setInt(1, metric_id);
    
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Health metric deleted successfully.");
            } else {
                System.out.println("Failed to delete health metric.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteTrainerSchedule(int scheduleId) {
        String sql = "DELETE FROM comp3005project.Trainer_schedule WHERE schedule_id = ?;";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setInt(1, scheduleId);
    
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Trainer schedule deleted successfully.");
            } else {
                System.out.println("Failed to delete trainer schedule.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void deleteRoomBooking(int booking_id) {
        String sql = "DELETE FROM comp3005project.room_booking WHERE booking_id = ?;";
    
        try (Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setInt(1, booking_id);
    
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Room booking deleted successfully.\n");
            } else {
                System.out.println("Failed to delete room booking.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

        try {
            // SQL script execution
            // Read SQL file
            String sqlString = readSqlFile("src/main/resources/sql/01/01.0.sql");

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, sqluser, sqlpassword);

            Statement statement = connection.createStatement();
            statement.execute(sqlString);

            // System.out.println("SQL script executed successfully!");

            // Home page, select member, trainer, or admin staff
            System.out.println("Welcome to the Health and Fitness Club Management System!");

            String options = "1 Member\n2 Trainer\n3 Administration staff\n0 Quit\n";
            boolean exit = false;
            while (!exit) {
                System.out.println("\nPick an option to register or login:");
                System.out.println(options);

                BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(System.in));
                String query = reader.readLine();

                Main main = new Main();
                switch (query) {
                    case "1": // member
                        main.memberOption();
                        break;
                    case "2": // trainer
                        main.trainerOption();
                        // manage schedule, view member profile/search member's name
                        break;
                    case "3": // admin staff
                        // login?
                        main.adminOption();
                        // manage room booking, manage class schedule, monitor equipment maintenance
                        // oversee billing and payment
                        break;
                    case "0": // quit
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option!");
                        break;
                }
            }

            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}