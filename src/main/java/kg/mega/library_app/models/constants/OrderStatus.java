package kg.mega.library_app.models.constants;

public enum OrderStatus {
    TAKING("Взятие"),
    RETURNING("Возврат");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
