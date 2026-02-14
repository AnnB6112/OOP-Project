/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph;

/**
 *
 * @author cherr
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.DecimalFormat;


public class AttendanceCalculator {
    private static final String ATTENDANCE_FILE_PATH = "/motorph_attendance_records.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
    private static final DecimalFormat df = new DecimalFormat("#,##0.00");
    
    private Map<Integer, EmployeeAttendance> employeeAttendanceMap;

    public AttendanceCalculator() {
        this.employeeAttendanceMap = new HashMap<>();
    }

    public void loadAttendanceData(String filePath) throws IOException {
        try (InputStream is = AttendanceCalculator.class.getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
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

    public AttendanceReport generateReport(int employeeNumber, int year, int month) {
        EmployeeAttendance attendance = employeeAttendanceMap.get(employeeNumber);
        if (attendance == null) {
            return null;
        }
        return attendance.generateReport(year, month);
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
                    
                    // Calculate late time (grace period: 10 minutes)
                    long lateMinutes = Duration.between(LocalTime.of(9, 0), record.getLogIn()).toMinutes() - 10;
                    if (lateMinutes > 0) {
                        totalLateHours += lateMinutes / 60.0;
                    }

                    // Calculate early departure time
                    long earlyDepartureMinutes = Duration.between(record.getLogOut(), LocalTime.of(17, 0)).toMinutes();
                    if (earlyDepartureMinutes > 0) {
                        totalEarlyDepartureHours += earlyDepartureMinutes / 60.0;
                    }

                    // Calculate worked hours (minus 1 hour break)
                    double hoursWorked = Duration.between(record.getLogIn(), record.getLogOut()).toMinutes() / 60.0 - 1;
                    double expectedHours = 7; // 8 hours - 1 hour break

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
            return (workingDays * 7) + overtimeHours; // 7 hours per day (8-1 break)
        }
        
        public double getAdjustedLateMinutes() {
            return Math.max(0, lateHours * 60); // Convert to minutes after grace period
        }
        
        public double getWeeklyOvertime() {
            return overtimeHours / 4; // Monthly overtime divided by 4 weeks
        }
        
        public double getWeeklyLateMinutes() {
            return getAdjustedLateMinutes() / 4; // Monthly late minutes divided by 4 weeks
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