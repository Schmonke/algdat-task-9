import java.util.Objects;

public class PointOfInterest {
    private final PointOfInterestCategory category;
    private final String name;

    public PointOfInterest(PointOfInterestCategory category, String name) {
        this.category = Objects.requireNonNull(category);
        this.name = Objects.requireNonNull(name);
    }

    public PointOfInterestCategory getCategory() {
        return category;
    }
    
    public String getName() {
        return name;
    }
}
