public class Store {
    private String name;
    private String ID;
    private String description;

    public Store(String name, String ID, String description) {
        this.name = name;
        this.ID = ID;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public String getDescription() {
        return description;
    }
}
