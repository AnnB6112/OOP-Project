package com.mycompany.motorph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceCalculator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter TIMECARD_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd");
    private static final DateTimeFormatter TIMECARD_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DecimalFormat df = new DecimalFormat("#,##0.00");

    private final Map<Integer, EmployeeAttendance> employeeAttendanceMap;

    public AttendanceCalculator() {
        this.employeeAttendanceMap = new HashMap<>();
    }

    public void loadAttendanceData(String filePath) throws IOException {
        try (InputStream is = AttendanceCalculator.class.getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IOException("Attendance file not found in classpath: " + filePath);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                boolean headerSkipped = false;

                while ((line = br.readLine()) != null) {
                    if (!headerSkipped) {
                        headerSkipped = true;
                        continue;
                    }

                    String[] values = line.split(",");
                    int employeeNumber = Integer.parseInt(values[0].trim());
                    LocalDate date = LocalDate.parse(values[3].trim(), DATE_FORMATTER);
                    LocalTime logIn = LocalTime.parse(values[4].trim(), TIME_FORMATTER);
                    LocalTime logOut = LocalTime.parse(values[5].trim(), TIME_FORMATTER);

                    employeeAttendanceMap
                        .computeIfAbsent(employeeNumber, k -> new EmployeeAttendance())
                        .addRecord(date, logIn, logOut);
                }
            }
        }
    }

    public AttendanceReport generateReport(int employeeNumber, int year, int month) {
        EmployeeAttendance attendance = employeeAttendanceMap.get(employeeNumber);
        if (attendance == null) {
            return null;
        }
        return attendance.generateReport(year, month);
    }

    public AttendanceReport generateWeeklyReport(int employeeNumber, int year, int month, int weekNumber) {
        if (weekNumber < 1 || weekNumber > 4) {
            throw new IllegalArgumentException("Week number must be 1 to 4.");
        }

        EmployeeAttendance attendance = employeeAttendanceMap.get(employeeNumber);
        if (attendance == null) {
            return null;
        }
        return attendance.generateWeeklyReport(year, month, weekNumber);
    }

    public String generateTimecard(Employee employee, int year, int month) {
        int periodEndDay = Math.min(15, YearMonth.of(year, month).lengthOfMonth());
        LocalDate periodEndDate = LocalDate.of(year, month, periodEndDay);

        EmployeeAttendance attendance = employeeAttendanceMap.get(employee.getEmpNumber());
        Map<LocalDate, AttendanceRecord> recordsByDate = new HashMap<>();
        if (attendance != null) {
            for (AttendanceRecord record : attendance.records) {
                if (record.getDate().getYear() == year && record.getDate().getMonthValue() == month) {
                    recordsByDate.put(record.getDate(), record);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("MotorPH\n");
        sb.append("7 Jupiter Avenue cor. F. Sandoval Jr., Bagong Nayon, Quezon City\n");
        sb.append("Phone: (028) 911-5073 | Email: corpcom@motorph.com\n");
        sb.append("================================================================================================================\n");
        sb.append("                                                 EMPLOYEE TIMECARD\n");
        sb.append("================================================================================================================\n");
        sb.append(String.format("%-18s: %-20s %-20s: %s%n", "EMPLOYEE ID", employee.getEmpNumber(), "PERIOD END DATE", periodEndDate));
        sb.append(String.format("%-18s: %-20s %-20s: %s%n", "EMPLOYEE NAME", employee.getName(), "POSITION/DEPARTMENT", employee.getPosition()));
        sb.append("----------------------------------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-8s %-6s %-10s %-10s %-10s %-10s %-18s %-25s%n",
            "Date", "Day", "Time In", "Break Out", "Break In", "Time Out", "Total Hours Worked", "Remarks"));
        sb.append("----------------------------------------------------------------------------------------------------------------\n");

        for (int day = 1; day <= periodEndDay; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            DayOfWeek dow = date.getDayOfWeek();
            String dayLabel = dow.toString().substring(0, 1) + dow.toString().substring(1, 3).toLowerCase();
            AttendanceRecord record = recordsByDate.get(date);

            if (record != null) {
                double hoursWorked = Math.max(0, Duration.between(record.getLogIn(), record.getLogOut()).toMinutes() / 60.0 - 1.0);
                String remarks = record.getLogIn().isAfter(LocalTime.of(9, 10)) ? "Was late" : "Present";
                sb.append(String.format("%-8s %-6s %-10s %-10s %-10s %-10s %-18s %-25s%n",
                    date.format(TIMECARD_DATE_FORMATTER),
                    dayLabel,
                    record.getLogIn().format(TIMECARD_TIME_FORMATTER),
                    "12:00 PM",
                    "01:00 PM",
                    record.getLogOut().format(TIMECARD_TIME_FORMATTER),
                    df.format(hoursWorked) + " Hrs",
                    remarks));
            } else {
                String remark = (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) ? "Weekend" : "Absent";
                sb.append(String.format("%-8s %-6s %-10s %-10s %-10s %-10s %-18s %-25s%n",
                    date.format(TIMECARD_DATE_FORMATTER),
                    dayLabel,
                    "-",
                    "-",
                    "-",
                    "-",
                    "0 Hrs",
                    remark));
            }
        }

        sb.append("================================================================================================================\n");
        return sb.toString();
    }

    private static class EmployeeAttendance {
        private final List<AttendanceRecord> records = new ArrayList<>();

        public void addRecord(LocalDate date, LocalTime logIn, LocalTime logOut) {
            records.add(new AttendanceRecord(date, logIn, logOut));
        }

        public AttendanceReport generateReport(int year, int month) {
            double totalLateHours = 0;
            double totalEarlyDepartureHours = 0;
            double totalShortHours = 0;
            double totalOvertimeHours = 0;
            int workingDays = 0;

            for (AttendanceRecord record : records) {
                if (record.getDate().getYear() == year && record.getDate().getMonthValue() == month) {
                    workingDays++;

                    long lateMinutes = Duration.between(LocalTime.of(9, 0), record.getLogIn()).toMinutes() - 10;
                    if (lateMinutes > 0) {
                        totalLateHours += lateMinutes / 60.0;
                    }

                    long earlyDepartureMinutes = Duration.between(record.getLogOut(), LocalTime.of(17, 0)).toMinutes();
                    if (earlyDepartureMinutes > 0) {
                        totalEarlyDepartureHours += earlyDepartureMinutes / 60.0;
                    }

                    double hoursWorked = Duration.between(record.getLogIn(), record.getLogOut()).toMinutes() / 60.0 - 1;
                    double expectedHours = 7;

                    if (hoursWorked < expectedHours) {
                        totalShortHours += expectedHours - hoursWorked;
                    } else if (hoursWorked > expectedHours) {
                        totalOvertimeHours += hoursWorked - expectedHours;
                    }
                }
            }

            return new AttendanceReport(year, month, workingDays, totalLateHours,
                totalEarlyDepartureHours, totalShortHours,
                totalOvertimeHours);
        }

        public AttendanceReport generateWeeklyReport(int year, int month, int weekNumber) {
            double totalLateHours = 0;
            double totalEarlyDepartureHours = 0;
            double totalShortHours = 0;
            double totalOvertimeHours = 0;
            int workingDays = 0;

            for (AttendanceRecord record : records) {
                LocalDate date = record.getDate();
                if (date.getYear() != year || date.getMonthValue() != month) {
                    continue;
                }

                int payrollWeek = calculatePayrollWeek(date.getDayOfMonth());
                if (payrollWeek != weekNumber) {
                    continue;
                }

                workingDays++;

                long lateMinutes = Duration.between(LocalTime.of(9, 0), record.getLogIn()).toMinutes() - 10;
                if (lateMinutes > 0) {
                    totalLateHours += lateMinutes / 60.0;
                }

                long earlyDepartureMinutes = Duration.between(record.getLogOut(), LocalTime.of(17, 0)).toMinutes();
                if (earlyDepartureMinutes > 0) {
                    totalEarlyDepartureHours += earlyDepartureMinutes / 60.0;
                }

                double hoursWorked = Duration.between(record.getLogIn(), record.getLogOut()).toMinutes() / 60.0 - 1;
                double expectedHours = 7;

                if (hoursWorked < expectedHours) {
                    totalShortHours += expectedHours - hoursWorked;
                } else if (hoursWorked > expectedHours) {
                    totalOvertimeHours += hoursWorked - expectedHours;
                }
            }

            return new AttendanceReport(year, month, workingDays, totalLateHours,
                totalEarlyDepartureHours, totalShortHours, totalOvertimeHours);
        }

        private int calculatePayrollWeek(int dayOfMonth) {
            if (dayOfMonth <= 7) {
                return 1;
            }
            if (dayOfMonth <= 14) {
                return 2;
            }
            if (dayOfMonth <= 21) {
                return 3;
            }
            return 4;
        }
    }

    private static class AttendanceRecord {
        private final LocalDate date;
        private final LocalTime logIn;
        private final LocalTime logOut;

        public AttendanceRecord(LocalDate date, LocalTime logIn, LocalTime logOut) {
            this.date = date;
            this.logIn = logIn;
            this.logOut = logOut;
        }

        public LocalDate getDate() { return date; }
        public LocalTime getLogIn() { return logIn; }
        public LocalTime getLogOut() { return logOut; }
    }

    public static class AttendanceReport {
        private final int year;
        private final int month;
        private final int workingDays;
        private final double lateHours;
        private final double earlyDepartureHours;
        private final double shortHours;
        private final double overtimeHours;

        public AttendanceReport(int year, int month, int workingDays,
                                double lateHours, double earlyDepartureHours,
                                double shortHours, double overtimeHours) {
            this.year = year;
            this.month = month;
            this.workingDays = workingDays;
            this.lateHours = lateHours;
            this.earlyDepartureHours = earlyDepartureHours;
            this.shortHours = shortHours;
            this.overtimeHours = overtimeHours;
        }

        public int getYear() { return year; }
        public int getMonth() { return month; }
        public int getWorkingDays() { return workingDays; }
        public double getLateHours() { return lateHours; }
        public double getEarlyDepartureHours() { return earlyDepartureHours; }
        public double getShortHours() { return shortHours; }
        public double getOvertimeHours() { return overtimeHours; }

        public double getTotalWorkedHours() {
            return (workingDays * 7) + overtimeHours;
        }

        public double getAdjustedLateMinutes() {
            return Math.max(0, lateHours * 60);
        }

        public double getWeeklyOvertime() {
            return overtimeHours / 4;
        }

        public double getWeeklyLateMinutes() {
            return getAdjustedLateMinutes() / 4;
        }

        @Override
        public String toString() {
            return String.format(
                "\n=====================================\n" +
                    "       DETAILED ATTENDANCE REPORT\n" +
                    "=====================================\n" +
                    "Period: %d-%02d\n" +
                    "Working Days: %d\n" +
                    "-------------------------------------\n" +
                    "Total Worked Hours: %s\n" +
                    "Total Late Minutes: %s (after grace period)\n" +
                    "Total Early Departures: %s hours\n" +
                    "Total Short Hours: %s hours\n" +
                    "Total Overtime Hours: %s\n" +
                    "-------------------------------------\n" +
                    "Weekly Averages:\n" +
                    "Overtime Hours: %s\n" +
                    "Late Minutes: %s\n" +
                    "=====================================\n",
                year, month,
                workingDays,
                df.format(getTotalWorkedHours()),
                df.format(getAdjustedLateMinutes()),
                df.format(earlyDepartureHours),
                df.format(shortHours),
                df.format(overtimeHours),
                df.format(getWeeklyOvertime()),
                df.format(getWeeklyLateMinutes())
            );
        }
    }
}
