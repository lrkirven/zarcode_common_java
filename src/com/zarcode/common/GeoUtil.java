package com.zarcode.common;

import java.util.List;

import ch.hsr.geohash.WGS84Point;

public class GeoUtil {

	/**
	 * Implements the Jordan curve theorem
	 * 
	 * @param polygon
	 * @param pt
	 * @return
	 */
	public static boolean containsPoint(List<WGS84Point> polygon, WGS84Point pt) {
        int numPoints = polygon.size();
        boolean inPoly = false;
        WGS84Point vertex1 = null;
        WGS84Point vertex2 = null;
        int i = 0;
        int j = numPoints-1;

        for (i=0; i<numPoints; i++) {
                vertex1 = polygon.get(i);
                vertex2 = polygon.get(j);

                if (vertex1.getLng() < pt.getLng() && vertex2.getLng() >= pt.getLng() || vertex2.getLng() < pt.getLng() && vertex1.getLng() >= pt.getLng()) {
                        if (vertex1.getLat() + (pt.getLng() - vertex1.getLng()) / (vertex2.getLng() - vertex1.getLng()) * (vertex2.getLat() - vertex1.getLat()) < pt.getLat()) {
                        	inPoly = !inPoly;
                        }
                }
                j = i;
        }

        return inPoly;
	}
	
	public static double getPolygonArea(List<WGS84Point> polygon) {
		int i = 0;
		int j = 0;
		double area = 0;
		int numPoints = polygon.size();
		
		for (i=0; i<numPoints; i++) {
			j = (i + 1) % numPoints;
			area += polygon.get(i).getLat() * polygon.get(j).getLng();
			area -= polygon.get(i).getLng() * polygon.get(j).getLat();
		}
		area = (area/2.0);
		return area;
	}
 

	public static WGS84Point getPolygonCentroid(List<WGS84Point> polygon) {
		double cx = 0;
		double cy = 0;
		double area = getPolygonArea(polygon);
		int i = 0;
		int j = 0;
		int numPoints = polygon.size();

	
		double factor=0;
		for (i=0; i<numPoints; i++) {
			j = (i + 1) % numPoints;
			factor = (polygon.get(i).getLat() * polygon.get(j).getLng() - polygon.get(j).getLat() * polygon.get(i).getLng());
			cx += (polygon.get(i).getLat() + polygon.get(j).getLat()) * factor;
			cy += (polygon.get(i).getLng() + polygon.get(j).getLng()) * factor;
		}
		area *= 6.0f;
		factor = 1/area;
		cx *= factor;
		cy *= factor;
		//
		// result
		//
		WGS84Point center = new WGS84Point(cx, cy);
		return center;
	}
	
	public static double distanceBtwAB(double lat1, double lon1, double lat2, double lon2) {
		double earthR = 6371; // km
		double dLat = Math.toRadians(lat2-lat1);
		double dLon = Math.toRadians(lon2-lon1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + 
			Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *  
			Math.sin(dLon/2) * Math.sin(dLon/2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double d = earthR * c;
		return d;
	}

}
