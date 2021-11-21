import java.util.HashMap;

public class PointsOfInterest {
    private final HashMap<Integer, PointOfInterest> pointsOfInterest;

    public PointsOfInterest(int capacity) {
        pointsOfInterest = new HashMap<>(capacity);
    }

    public void addPointOfInterest(int nodeNumber, PointOfInterestCategory category, String name) {
        pointsOfInterest.put(nodeNumber, new PointOfInterest(category, name));
    }

    public PointOfInterest getPointOfInterest(int nodeNumber) {
        return pointsOfInterest.get(nodeNumber);
    }
}
