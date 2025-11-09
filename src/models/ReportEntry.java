package models;

import java.time.LocalDateTime;

public class ReportEntry {
    private LocalDateTime timestamp;
    private String fileName;
    private String description;
    private String status;

    public ReportEntry(LocalDateTime timestamp, String fileName, String description, String status) {
        this.timestamp = timestamp;
        this.fileName = fileName;
        this.description = description;
        this.status = status;
    }

    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return timestamp + " | " + fileName + " | " + description + " | " + status;
    }
}