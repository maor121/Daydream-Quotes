package quote.com.quotes.data.app;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Maor on 14/06/2016.
 */
public class Cluster {
    public int id = this.hashCode();
    public List<Point> points;
    public Circle boundingCircle;

    public Cluster(List<Circle> cluster) {
        points = new LinkedList<>();
        double area = 0;
        for (Circle c : cluster) {
            points.addAll(c.contour.toList());
            area += c.contourArea;
        }

        MatOfPoint2f contour = new MatOfPoint2f();
        contour.fromList(points);
        Point center = new Point();
        float[] radius = new float[1];
        Imgproc.minEnclosingCircle(contour, center, radius);
        boundingCircle = new Circle(contour, center, radius[0], area);
    }
}
