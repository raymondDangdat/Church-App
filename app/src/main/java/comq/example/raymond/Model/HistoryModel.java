package comq.example.raymond.Model;

public class HistoryModel {
    private String history;
    private long dateUpdated;

    public HistoryModel() {
    }

    public HistoryModel(String history, long dateUpdated) {
        this.history = history;
        this.dateUpdated = dateUpdated;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public long getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(long dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
