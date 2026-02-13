package com.techLabs.nbpdcl.Utils;

import static java.lang.Math.atan2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;

import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.models.FindDeviceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResponseDataUtils {

    public static ArrayList<String> NetworkList = new ArrayList<>();

    public static boolean checkInternetConnectionAndInternetAccess(Context ctx) {
        boolean InternetAccess;
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo network = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (network != null && wifi.isConnected()) {
            InternetAccess = true;
        } else InternetAccess = network != null && network.isConnected();
        return InternetAccess;
    }

    private static boolean isExpandable = false;

    public static void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public static boolean isExpandable() {
        return isExpandable;
    }

    public static Bitmap RotateMyBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static double CalculateAngel(double fromX, double fromY, double toX, double toY) {
        double deltaX = fromX - toX;
        double deltaY = fromY - toY;
        double rad = atan2(deltaY, deltaX) * 180.0 / Math.PI;
        return -rad;
    }

    public static double calculateAngles(double startPointX, double startPointY, double endPointX, double endPointY) {
        double deltaX = endPointX - startPointX;
        double deltaY = endPointY - startPointY;
        double angleRadians = atan2(deltaY, deltaX);
        double angleDegrees = Math.toDegrees(angleRadians);
        if (angleDegrees < 0) {
            angleDegrees += 360;
        }
        return angleDegrees;
    }

    public static double calgles(double fx, double fy, double tx, double ty) {
        double cx = fx;
        double cy = fy;
        double ex = tx;
        double ey = ty;
        double dy = ey - cy;
        double dx = ex - cx;
        double theta = (Math.atan2(dy, dx) * 180.0) / Math.PI;
        return -theta;
    }

    public static Bitmap addPaddingToBitmap(Bitmap originalBitmap, int leftPadding, int rightPadding) {
        int newWidth = originalBitmap.getWidth() + leftPadding + rightPadding;
        int newHeight = originalBitmap.getHeight();
        Bitmap paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, originalBitmap.getConfig());
        Canvas canvas = new Canvas(paddedBitmap);
        canvas.drawBitmap(originalBitmap, leftPadding, 0, null);
        return paddedBitmap;
    }

    public static Bitmap DecreaseBitmap(Bitmap originalBitmap) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        int newWidth = width / 2;
        int newHeight = height / 2;
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);
        return resizedBitmap;
    }

    public static double CalculateAng(double fromX, double toX, double fromY, double toY) {
        double deltaLongitude = Double.valueOf(fromX) - Double.valueOf(toX);
        double deltaLatitude = Double.valueOf(fromY) - Double.valueOf(toY);
        double angleRadians = atan2(deltaLongitude, deltaLatitude);
        double angleDegrees = Math.toDegrees(angleRadians);
        double adjustedAngle = angleDegrees;
        return adjustedAngle;
    }

    public static Drawable createMarkerDrawable(Context context, String text) {
        final Typeface typ = ResourcesCompat.getFont(context, R.font.fonts);
        Paint paint = new Paint();
        paint.setTextSize(24);
        paint.setColor(Color.BLACK);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        Bitmap bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, bounds.left, -bounds.top, paint);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Bitmap createMarkerBitmap(Typeface typeface, String text) {
        Paint paint = new Paint();
        paint.setTextSize(18);
        paint.setTypeface(typeface);
        paint.setColor(Color.BLACK);

        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(text) + 0.5f);
        int height = (int) (baseline + paint.descent() + 0.5f);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, 0, baseline, paint);

        return bitmap;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap changeBitmapColor(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), sourceBitmap.getConfig());
        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(sourceBitmap, 0, 0, paint);
        return resultBitmap;
    }

    public static Bitmap changeBgTransparentBitmapColor(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), sourceBitmap.getConfig());
        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawText("", 2, 2, paint);
        canvas.drawBitmap(sourceBitmap, 0, 0, paint);
        return resultBitmap;
    }

    public static double CalculateSplAngel(double fromX, double toX, double fromY, double toY) {
        double delta_y = fromY - toY;
        return delta_y;
    }

    public static double calculateBearing(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);
        double deltaLon = lon2 - lon1;
        double y = Math.sin(deltaLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLon);
        double angle = Math.toDegrees(Math.atan2(y, x));
        angle = (angle + 360) % 360; // Normalize to 0-360 degrees
        return angle;
    }

    public static int getPixelColor(Bitmap bitmap, int x, int y) {
        if (x < 0 || x >= bitmap.getWidth() || y < 0 || y >= bitmap.getHeight()) {
            return 0; // Return 0 if out of bounds
        }
        return bitmap.getPixel(x, y);
    }

    public static String getHexsColor(Bitmap bitmap, int x, int y) {
        if (x < 0 || x >= bitmap.getWidth() || y < 0 || y >= bitmap.getHeight()) {
            return "";
        }
        int pixelColor = bitmap.getPixel(x, y);
        return String.format("#%06X", (0xFFFFFF & pixelColor));
    }

    public static float calculateBearings(double startLat, double startLng, double endLat, double endLng) {
        Location startLocation = new Location("start");
        startLocation.setLatitude(startLat);
        startLocation.setLongitude(startLng);
        Location endLocation = new Location("end");
        endLocation.setLatitude(endLat);
        endLocation.setLongitude(endLng);
        return startLocation.bearingTo(endLocation);
    }

    public static Bitmap changeSourceColor(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), sourceBitmap.getConfig());
        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);
        canvas.drawBitmap(sourceBitmap, 0, 0, paint);
        return resultBitmap;
    }

    public static List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));  // Convert each item to String
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            return isGpsEnabled || isNetworkEnabled;
        }

        return false;
    }

    public static float calculateTotalDistance(List<GeoPoint> geoPoints) {
        float totalDistance = 0f;

        for (int i = 0; i < geoPoints.size() - 1; i++) {
            GeoPoint start = geoPoints.get(i);
            GeoPoint end = geoPoints.get(i + 1);

            float[] result = new float[1];
            Location.distanceBetween(
                    start.getLatitude(), start.getLongitude(),
                    end.getLatitude(), end.getLongitude(),
                    result
            );

            totalDistance += result[0];
        }

        return totalDistance;
    }

    public static GeoPoint computeOffset(GeoPoint start, double distanceMeters, double bearingDegrees) {
        double radiusEarth = 6371000.0;

        double bearingRad = Math.toRadians(bearingDegrees);
        double lat1 = Math.toRadians(start.getLatitude());
        double lon1 = Math.toRadians(start.getLongitude());
        double distRatio = distanceMeters / radiusEarth;

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distRatio) +
                Math.cos(lat1) * Math.sin(distRatio) * Math.cos(bearingRad));

        double lon2 = lon1 + Math.atan2(Math.sin(bearingRad) * Math.sin(distRatio) * Math.cos(lat1),
                Math.cos(distRatio) - Math.sin(lat1) * Math.sin(lat2));

        return new GeoPoint(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }

    public static String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";

        OffsetDateTime dateTime = OffsetDateTime.parse(isoDate);
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd,MMMM, yyyy");

        return dateTime.format(formatter);
    }

    public static String formatDateTime(String inputDateTime) {

        // Input format
        DateTimeFormatter inputFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Output format: feb 18, 2025 - 09:30 am
        DateTimeFormatter outputFormatter =
                DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a", Locale.ENGLISH);

        LocalDateTime dateTime =
                LocalDateTime.parse(inputDateTime, inputFormatter);

        return dateTime.format(outputFormatter).toLowerCase();
    }

    public static String formatToZeroDot(double value) {
        int intValue = (int) value;   // decimal remove
        return String.format("0.%02d", intValue);
    }

    // KW → KVA
    public static double kwToKva(double kw, double pf) {
        if (pf <= 0) return 0;
        //return kw / 0.pf;
        return kw / pf;
    }

    // KVA → KW
    public static double kvaToKw(double kva, double pf) {
        //return kva * 0.pf;
        return kva * pf;
    }

    // PF → KVAR (using KW)
    public static double pfToKvar(double kva, double kw) {
        if (kva <= 0 || kw < 0 || kw > kva) return 0;
        return Math.sqrt((kva * kva) - (kw * kw));
    }

    public static double kvarToPf(double kw, double kvar) {
        if (kw <= 0 || kvar < 0) return 0;

        double angle = Math.atan(kvar / kw); // radians
        return Math.cos(angle);
    }

    public static int pfToInt(double pf) {
        return (int) Math.round(pf * 100);
    }

   /* public static String formatDateTime(String inputDateTime) {

        // Input format
        DateTimeFormatter inputFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Output format: feb 18, 2025 - 09:30 am
        DateTimeFormatter outputFormatter =
                DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a", Locale.ENGLISH);

        LocalDateTime dateTime =
                LocalDateTime.parse(inputDateTime, inputFormatter);

        return dateTime.format(outputFormatter).toLowerCase();
    }*/


    public static double getSafeDouble(JsonObject obj, String key) {
        try {
            if (obj != null && obj.has(key) && !obj.get(key).isJsonNull()) {
                return obj.get(key).getAsDouble();
            }
        } catch (Exception ignored) {
        }
        return 0.0;
    }

    public static double calculateKWTotalPF(double[] actualKW, double[] actualPF) {

        if (actualKW == null || actualPF == null || actualKW.length != actualPF.length) {
            return 0;
        }

        double totalKW = 0;
        double weightedPF = 0;

        for (int i = 0; i < actualKW.length; i++) {
            if (actualKW[i] > 0) {
                totalKW += actualKW[i];
                weightedPF += actualKW[i] * actualPF[i];
            }
        }

        return totalKW > 0 ? (weightedPF / totalKW) : 0;
    }

}
