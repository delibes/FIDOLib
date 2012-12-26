/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.data;

/**
 *
 * @author Steen
 */
public class Position {

    /**
     * The latitude in decimal degrees
     */
    public double lat = 0.0;
    /**
     * The longitude in decimal degrees
     */
    public double lon = 0.0;
    /**
     * GPS altitude in meters
     */
    public int GPSAltitude = 0;
    /**
     * Good latitude reading?
     */
    public boolean latitudeGood = false;
    /**
     * Good longitude reading?
     */
    public boolean longitudeGood = false;
    /**
     * The latitude in pixels
     */
    public int latPixels = 0;
    /**
     * The longitude in pixels
     */
    public int lonPixels = 0;
    
    /**
     * Constructor
     */
    public Position() {
        
    }
    /**
     * Constructor taking latitude and longitude as decimal degrees 
     * as well as the altitude measured in meter.
     * @param lat latitude in decimal degrees 
     * @param lon longitude in decimal degrees
     * @param altitude altutude in meters above ground level
     */
    public Position(double lat, double lon, int altitude) {
        this.lat = lat;
        this.lon = lon;
        this.GPSAltitude = altitude;
    }
}
