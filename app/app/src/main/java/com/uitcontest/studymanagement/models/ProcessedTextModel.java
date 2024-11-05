package com.uitcontest.studymanagement.models;

public class ProcessedTextModel {
    private String id;
    private String text;
    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {

        // Check if date is already formatted
        if (date.contains("T") || date.contains("-")) {
            date = formatDate(date);
        }

        return date;
    }

    private String formatDate(String date) {
        // Format date
        String[] dateParts = date.split("T");

        // Remove milliseconds
        date = dateParts[0] + " " + dateParts[1].substring(0, dateParts[1].length() - 5);
        // Remove seconds
        date = date.substring(0, date.length() - 5);

        // Remove '.'
        date = date.replace(".", "");

        // Format date to dd/MM/yyyy
        dateParts = date.split(" ");
        date = dateParts[0].substring(8, 10) + "/" + dateParts[0].substring(5, 7) + "/" + dateParts[0].substring(0, 4) + " " + dateParts[1];

        // Arrange time before date
        String[] dateTime = date.split(" ");
        date = dateTime[1] + " " + dateTime[0];
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
