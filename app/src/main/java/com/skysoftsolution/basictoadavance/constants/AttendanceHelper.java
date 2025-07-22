package com.skysoftsolution.basictoadavance.constants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AttendanceHelper {

    public static  String decideAttendanceType() {
        String attendance = "";
        String currentTime = new SimpleDateFormat("HH:mm").format(new Date()); // Get current time in HH:mm format
        try {
            // Define time ranges
            String morningStart = "08:00"; // Start time for morning attendance
            String morningEnd = "12:00";   // End time for morning attendance
            String eveningStart = "17:00"; // Start time for evening attendance
            String eveningEnd = "20:00";   // End time for evening attendance

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date current = sdf.parse(currentTime);
            Date morningStartTime = sdf.parse(morningStart);
            Date morningEndTime = sdf.parse(morningEnd);
            Date eveningStartTime = sdf.parse(eveningStart);
            Date eveningEndTime = sdf.parse(eveningEnd);

            if (current.after(morningStartTime) && current.before(morningEndTime)) {
                attendance = "M"; // Morning
            } else if (current.after(eveningStartTime) && current.before(eveningEndTime)) {
                attendance = "E"; // Evening
            } else {
                attendance = "N/A"; // Not in the defined morning or evening ranges
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return attendance;
    }
}
