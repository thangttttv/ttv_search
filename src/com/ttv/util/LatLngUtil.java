package com.ttv.util;

public class LatLngUtil {
	
	  public static double toRad(double fDeg) {
	         return fDeg * Math.PI / 180;
	    }

	  public static double toDeg(double fRad) {
	         return fRad * 180 / Math.PI;
	   }
	      
	  public static double getMaxLatitude(double lat1,double lon1,double dist){
	         double brng = 180;
	         dist = dist / 6371;  
	         brng = toRad(brng);//($brng * pi())/ 180;
	         lat1 = toRad(lat1);//($lat1 * pi())/ 180;
	         lon1 =toRad(lon1);// ($lon1 * pi())/ 180;

	         double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) + Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
	         double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dist) * Math.cos(lat1),Math.cos(dist) - Math.sin(lat1) *Math.sin(lat2));

	         if (Double.isNaN(lat2) || Double.isNaN(lon2)) return 0;
	         else  return toDeg(lat2);
	    }
	    
	  public static double getMinLatitude(double lat1,double lon1,double dist){
	         double brng = 0;
	         dist =  dist / 6371;  
	         brng = toRad(brng);//($brng * pi())/ 180;
	         lat1 = toRad(lat1);//($lat1 * pi())/ 180;
	         lon1 =toRad(lon1);// ($lon1 * pi())/ 180;

	         double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) + Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
	         double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dist) * Math.cos(lat1),Math.cos(dist) - Math.sin(lat1) *Math.sin(lat2));

	         if (Double.isNaN(lat2) || Double.isNaN(lon2)) return 0;
	         else  return toDeg(lat2);
	    }
	    
	  public static double getMaxLongitude(double lat1,double lon1,double dist){
	         double brng = 90;
	         dist = dist / 6371;  
	         brng = toRad(brng);//($brng * pi())/ 180;
	         lat1 = toRad(lat1);//($lat1 * pi())/ 180;
	         lon1 =toRad(lon1);// ($lon1 * pi())/ 180;

	         double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) + Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
	         double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dist) * Math.cos(lat1),Math.cos(dist) - Math.sin(lat1) *Math.sin(lat2));

	         if (Double.isNaN(lat2) || Double.isNaN(lon2)) return 0;
	         else return toDeg(lon2);
	    }
	    
	  public static double getMinLongitude(double lat1,double lon1,double dist){
	         double brng = -90;
	         dist = dist / 6371;  
	         brng = toRad(brng);//($brng * pi())/ 180;
	         lat1 = toRad(lat1);//($lat1 * pi())/ 180;
	         lon1 =toRad(lon1);// ($lon1 * pi())/ 180;

	         double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) + Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
	         double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dist) * Math.cos(lat1),Math.cos(dist) - Math.sin(lat1) *Math.sin(lat2));

	         if (Double.isNaN(lat2) || Double.isNaN(lon2)) return 0;
	         else 
	          return toDeg(lon2);
	    }
	  
	  public static double getDistance(double lat1,double lng1,double lat2,double lng2){
		  	int r = 6378137;
		  	double dLat  ;
		  	double dLong ;
		  	double a,c,d ;
		     dLat = toRad(lat2 - lat1);
		     dLong = toRad(lng2 - lng1);
		    
		     a =  Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
		     c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		     d = r * c;
		    
		    return d;
	  }
	    
}
