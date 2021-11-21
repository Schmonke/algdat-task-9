import java.util.NoSuchElementException;

public enum PointOfInterestCategory {
    PLACE(1),
    GAS_STATION(2),
    CHARGING_STATION(4);

    private final int number;
    
    PointOfInterestCategory(int number) {
        this.number = number;
    }

    public static PointOfInterestCategory fromNumber(int number) {
        for (PointOfInterestCategory category : PointOfInterestCategory.values()) {
            if (category.number == number) {
                return category;
            }
        }
        throw new NoSuchElementException("category " + number + " does not exist");
    }
}