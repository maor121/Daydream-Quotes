package quote.com.quotes.utils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.LinkedList;
import java.util.List;

import quote.com.quotes.data.app.Circle;
import quote.com.quotes.data.app.Cluster;

/**
 * Created by Maor on 24/06/2016.
 */
public class MotionDetector {

    private static final String TAG = MotionDetector.class.getSimpleName();

    private Scalar DEBUG_COLOR = new Scalar(255,255,255);

    public Mat proccessFrameDebug(Mat grayscale) {
        Mat diffMat = diffImg(grayscale);

        return diffMat;
    }

    public boolean proccessFrame(Mat grayscale) {
        double scaleFactor = 1280 / grayscale.width();

        Mat diffMat = diffImg(grayscale);

        if (diffMat != null) {
            Imgproc.erode(diffMat, diffMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));
            //Imgproc.dilate(diffMat, diffMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));

            Mat canny_output = new Mat();
            int thresh = 100;
            Imgproc.Canny(diffMat, canny_output, thresh, thresh * 2);

            List<MatOfPoint> contours = new LinkedList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(canny_output, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            canny_output.release();

            List<Circle> circles = new LinkedList<>();
            int MAX_CON_SIZE = (int)( 102 / scaleFactor );
            for (MatOfPoint c : contours) {
                double conArea = Imgproc.contourArea(c);
                if (conArea > MAX_CON_SIZE)
                    continue;

                //Log.d(TAG, "AREA: " + conArea);

                float[] radius = new float[1];
                Point center = new Point();
                MatOfPoint2f c2f = new MatOfPoint2f();
                c.convertTo(c2f, CvType.CV_32FC2);
                Imgproc.minEnclosingCircle(c2f, center, radius);

                circles.add( new Circle(c2f, center, radius[0], conArea) );
            }
            //Imgproc.drawContours(diffMat, contours, -1, DEBUG_COLOR, 3);
            //Log.d(TAG, "#contours: " + contours.size());

            List<List<Circle>> clusters = new LinkedList<>();
            double MAX_CIRCLE_DISTANCE = 128 / scaleFactor;
            circleLoop : for (Circle c : circles) {
                List<Circle> foundCluster = null, foundAnotherCluster = null;

                clusterLoop : for (List<Circle> cluster : clusters) {
                    for (Circle c2 : cluster) {
                        double dist = c.Distance(c2);
                        //Log.d(TAG,"distance: " + dist);
                        if (dist < MAX_CIRCLE_DISTANCE) {
                            if (foundCluster == null) {
                                foundCluster = cluster;
                                //Log.d(TAG, "Found cluster : " + foundCluster.hashCode() + " circle :"+ c.hashCode());
                                continue clusterLoop;
                            }
                            else {
                                foundAnotherCluster = cluster;
                                //Log.d(TAG, "Another cluster : " + foundAnotherCluster.hashCode() + " circle :"+ c.hashCode());
                                break clusterLoop;
                            }
                        }
                    }
                }
                if (foundCluster == null) {
                    //No cluster was fonund for c
                    List<Circle> cluster = new LinkedList<>();
                    cluster.add(c);
                    clusters.add(cluster);
                    //Log.d(TAG, "Creating cluster : " + cluster.hashCode() + " circle :" + c.hashCode());

                } else {
                    foundCluster.add(c);
                    if (foundAnotherCluster != null) {
                        clusters.remove(foundAnotherCluster);
                        foundCluster.addAll(foundAnotherCluster);
                        //Log.d(TAG, "Merge cluster : " + foundCluster.hashCode() + " cluster2 :" + foundAnotherCluster.hashCode());
                    }
                }
            }

            List<Cluster> clusterList = new LinkedList<>();
            String conArea = "{";
            for (List<Circle> c : clusters) {
                Cluster cluster = new Cluster(c);
                clusterList.add(cluster);

                //Drawing - TODO: comment out in production
                //Imgproc.circle(result,
                //        transformPoint(cluster.boundingCircle.center, scaleFactor), (int) cluster.boundingCircle.radius*scaleFactor, DEBUG_COLOR, 3);
                conArea += cluster.boundingCircle.contourArea+",";
            }
            conArea += "}";
            //Log.d(TAG, "#Clusters : " + clusters.size() + ". " + conArea);

            return clusterList.size() >= 1;
        }

        return false;
    }

    Mat t_cache = null;
    private Mat diffImg(Mat t) {
        Mat $ = null;

        if (t_cache != null) {
            $ = diffImg(t_cache, t);

            t_cache.release();
        }
        t_cache = t;

        return $;
    }

    Mat d_cache = null;
    private Mat diffImg(Mat t1, Mat t2) {
        Mat d = new Mat(), $ = null;

        Core.absdiff(t1,t2, d);
        if (d_cache != null)
        {
            $ = new Mat();
            Core.bitwise_and(d, d_cache, $);

            d_cache.release();
        }
        d_cache = d;

        return $;
    }

    public void releaseCache() {
        if (t_cache != null)
            t_cache.release();
        if (d_cache != null)
            d_cache.release();

        t_cache = d_cache = null;
    }

}
