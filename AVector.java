/*
  Custom vector class for real vector usage
*/

import java.lang.*;
import java.awt.*;

public class AVector {
  double x;
  double y;

  AVector() {  }

  AVector(double x, double y) {
    this.x = x;
    this.y = y;
  }

  void add(AVector av) {
    this.x += av.x;
    this.y += av.y;
  }

  void sub(AVector av) {
    this.x -= av.x;
    this.y -= av.y;
  }

  void div(double val) {
    this.x /= val;
    this.y /= val;
  }

  void mult(double val) {
    this.x *= val;
    this.y *= val;
  }

  void limit(double limit) {
    double mag = mag();
    if (mag != 0 && mag > limit) {
      y *= limit / mag;
      x *= limit / mag;
    }
  }

  double heading() {
    return Math.atan2(y,x);
  }

  double mag() {
    return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
  }

  double dot(AVector v) {
    return x * v.x + y * v.x;
  }

  String toText() {
    return "Vector - x: " + x + ", y: " + y;
  }

  void normalize() {
    double mag = mag();
    if (mag != 0) {
      x /= mag;
      y /= mag;
    }
  }

  static AVector sub(AVector v1, AVector v2) {
    return new AVector(v1.x - v2.x, v1.y - v2.y);
  }
  static AVector add(AVector v1, AVector v2) {
    return new AVector(v1.x + v2.x, v1.y + v2.y);
  }
  static double dist(AVector v1, AVector v2) {
    return Math.sqrt(Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2));
  }
  static double angleBetween(AVector v1, AVector v2) {
    return Math.acos(v1.dot(v2) / (v1.mag() * v2.mag()));
  }
}
