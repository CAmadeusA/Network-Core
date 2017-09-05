package com.camadeusa.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/*
 * Author CAmadeusA
 * All rights reserved. 
 */

public class VectorUtil {
    private static final Random rnd = new Random();

    /*
     * Gets a vector object in a random direction. 
     */
    public static Vector getRandomVector() {
        return VectorUtil.getRandomVector(0.1f, 10.0f, 0.1f, 10.0f, 0.1f, 10.0f);
    }

    /*
     * Gets a vector object in a random direction with declared min and max's. 
     */
    public static Vector getRandomVector(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        float x = (rnd.nextFloat() + minX) % maxX * (float)(Math.random() >= 0.5 ? 1 : -1);
        float y = (rnd.nextFloat() + minY) % maxY;
        float z = (rnd.nextFloat() + minZ) % maxZ * (float)(Math.random() >= 0.5 ? 1 : -1);
        return new Vector(x, y, z);
    }

    /*
     * List of Locations to List of Vectors.
     */
    public static List<Vector> toVector(List<Location> locations) {
        ArrayList<Vector> vectors = new ArrayList<Vector>(locations.size());
        for (Location location : locations) {
            vectors.add(location.toVector());
        }
        return vectors;
    }

    /*
     * List of Vectors to List of locations
     */
    public static List<Location> toLocation(List<Vector> vectors, World world) {
        ArrayList<Location> locations = new ArrayList<Location>(vectors.size());
        for (Vector vector : vectors) {
            locations.add(VectorUtil.toLocation(vector, world));
        }
        return locations;
    }

    
    public static Location toLocation(Vector vector, World world) {
        return new Location(world, vector.getX(), vector.getY(), vector.getZ());
    }

    public static Vector getDirection(Vector base, Vector target) {
        return target.clone().subtract(base).normalize();
    }

    public static List<Vector> getLinePoints(Vector start, Vector end, double interval) {
        ArrayList<Vector> points = new ArrayList<Vector>();
        Vector direction = end.clone().subtract(start).normalize().multiply(interval);
        Vector lastPoint = start.clone();
        double maxDistance = start.distance(end);
        while (maxDistance - interval >= 0.0) {
            points.add(lastPoint.clone());
            lastPoint.add(direction);
            maxDistance -= interval;
        }
        return points;
    }

    public static List<Vector> getLinePoints(Location start, double length, double interval) {
        ArrayList<Vector> points = new ArrayList<Vector>();
        Vector direction = start.getDirection().multiply(interval);
        Vector lastPoint = start.toVector().clone();
        double maxDistance = length;
        while (maxDistance - interval >= 0.0) {
            points.add(lastPoint.clone());
            lastPoint.add(direction);
            maxDistance -= interval;
        }
        return points;
    }

    public static List<Vector> getCirclePoints(Vector center, double radius, int total) {
        ArrayList<Vector> points = new ArrayList<Vector>(total);
        double interval = 6.283185307179586 / (double)total;
        for (double currentAngle = 6.283185307179586; currentAngle > 0.0; currentAngle -= interval) {
            points.add(new Vector(center.getX() + radius * Math.sin(currentAngle), center.getY(), center.getZ() + radius * Math.cos(currentAngle)));
        }
        return points;
    }

    public static List<Vector> rotateX(List<Vector> vectors, double degrees) {
        ArrayList<Vector> result = new ArrayList<Vector>(vectors.size());
        double cos = Math.cos(Math.toRadians(degrees));
        double sin = Math.sin(Math.toRadians(degrees));
        for (Vector target : vectors) {
            result.add(new Vector(target.getX(), cos * target.getY() - sin * target.getZ(), sin * target.getY() + cos * target.getZ()));
        }
        return result;
    }

    public static List<Vector> rotateY(List<Vector> vectors, double degrees) {
        ArrayList<Vector> result = new ArrayList<Vector>(vectors.size());
        double cos = Math.cos(Math.toRadians(degrees));
        double sin = Math.sin(Math.toRadians(degrees));
        for (Vector target : vectors) {
            result.add(new Vector(cos * target.getX() - sin * target.getZ(), target.getY(), sin * target.getX() + cos * target.getZ()));
        }
        return result;
    }

    public static List<Vector> rotateZ(List<Vector> vectors, double degrees) {
        ArrayList<Vector> result = new ArrayList<Vector>(vectors.size());
        double cos = Math.cos(Math.toRadians(degrees));
        double sin = Math.sin(Math.toRadians(degrees));
        for (Vector target : vectors) {
            result.add(new Vector(cos * target.getX() - sin * target.getY(), sin * target.getX() + cos * target.getY(), target.getZ()));
        }
        return result;
    }

    public static List<Vector> transform(List<Vector> vectors, Vector adjustment) {
        ArrayList<Vector> result = new ArrayList<Vector>(vectors.size());
        for (Vector target : vectors) {
            result.add(target.clone().add(adjustment));
        }
        return result;
    }

    public static boolean isInFront(Entity entity, Entity target) {
        Vector relative;
        Vector facing = entity.getLocation().getDirection();
        return facing.dot(relative = target.getLocation().subtract(entity.getLocation()).toVector()) >= 0.0;
    }

    public static boolean isInFront(Entity entity, Entity target, double angle) {
        Vector relative;
        if (angle <= 0.0) {
            return false;
        }
        if (angle >= 360.0) {
            return true;
        }
        Vector facing = entity.getLocation().getDirection();
        return facing.dot(relative = target.getLocation().subtract(entity.getLocation()).toVector().normalize()) >= Math.cos(angle);
    }

    public static boolean isBehind(Entity entity, Entity target) {
        return !VectorUtil.isInFront(entity, target);
    }

    public static boolean isBehind(Entity entity, Entity target, double angle) {
        Vector relative;
        if (angle <= 0.0) {
            return false;
        }
        if (angle >= 360.0) {
            return true;
        }
        Vector facing = entity.getLocation().getDirection();
        return facing.dot(relative = entity.getLocation().subtract(target.getLocation()).toVector().normalize()) >= Math.cos(angle);
    }

    public static void pushAwayFromPoint(Entity entity, Vector point, double power) {
        VectorUtil.pushAwayFromPoint(entity, point, power, new Vector(0, 0, 0));
    }

    public static void pushAwayFromPoint(Entity entity, Vector point, double power, Vector adjustment) {
        Vector velocity = VectorUtil.getDirection(entity.getLocation().toVector(), point).add(adjustment).multiply(power);
        entity.setVelocity(entity.getVelocity().subtract(velocity));
    }

    public static void pushBackwards(Entity entity, double power) {
        VectorUtil.pushBackwards(entity, power, new Vector(0, 0, 0));
    }

    public static void pushBackwards(Entity entity, double power, Vector adjustment) {
        Vector velocity = entity.getLocation().getDirection().add(adjustment).multiply(power);
        entity.setVelocity(entity.getVelocity().subtract(velocity));
    }
}

