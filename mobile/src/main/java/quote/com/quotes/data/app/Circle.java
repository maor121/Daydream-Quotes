package quote.com.quotes.data.app;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

/**
 * Created by Maor on 13/06/2016.
 */
public class Circle {
    public Point center;
    public float radius;

    public MatOfPoint2f contour;
    public double contourArea;

    public Circle(Point center, float radius) {
        this.center = center;
        this.radius = radius;
    }
    public Circle(MatOfPoint2f contour, Point center, float radius, double contourArea) {
        this(center, radius);

        this.contour = contour;
        this.contourArea = contourArea;
    }

    public double Distance(Circle c) {
        return Math.max(0, DistanceCenters(c))-radius-c.radius;
    }
    public double DistanceCenters(Circle c) {
        Point diff = new Point(center.x - c.center.x, center.y - c.center.y);
        return Math.sqrt(diff.dot(diff));
    }
    public double DistanceCentersRelativeToSize(Circle c) {
        return DistanceCenters(c) / ( contourArea+c.contourArea );
    }
}
