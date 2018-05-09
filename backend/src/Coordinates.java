public class Coordinates {
    String type;
    double[] coordinates;

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Coordinates (" + type + "): <");
        for(double item : coordinates) {
            s.append(item + ", ");
        }
        s.append(">");
        return s.toString();
    }
}
