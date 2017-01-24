import java.io.*;
import java.awt.*;
import java.util.*;

public class Ruleset {

	// RULE 1 - ATTRACTION: seek to get near the center of nearby mass
	public static AVector Rule1(Bird[] bg, Bird b) {
		AVector center = new AVector(0,0);
		int count = 0;
		for (int i = 0; i < bg.length; i++) {
			if (!bg[i].included) continue;
			double distance = AVector.dist(bg[i].getLocation(),b.getLocation());
			if (bg[i] != b && distance < b.attractDistance) {
				center.add(bg[i].getLocation());
				count++;
			}
		}

		if (count > 0) center.div(count);
		//System.out.println(center.toText());
		return center;
	}

	// RULE 2 - AVERSION: keep above minimum distance from others
	public static AVector Rule2(Bird[] bg, Bird b) {
		AVector aversion = new AVector(0,0);
		int count = 0;
		for(int i = 0; i < bg.length; i++) {
			if (!bg[i].included) continue;
			double distance = AVector.dist(bg[i].getLocation(),b.getLocation());
			if (bg[i] != b && distance < b.minimumDistance && distance > 0) {
				aversion.sub(AVector.sub(bg[i].getLocation(),b.getLocation()));
				count++;
			}
		}
		if (count > 0) aversion.div(count);
		//System.out.println(aversion.toText());
		return aversion;
	}

	// RULE 3 - ALIGNMENT: try to match velocity vectors of nearby boids
	public static AVector Rule3(Bird[] bg, Bird b) {
		AVector groupVelocity = new AVector(0,0);
		int count = 0;
		for (int i = 0; i < bg.length; i++) {
				if (!bg[i].included) continue;
				double distance = AVector.dist(bg[i].getLocation(), b.getLocation());
				if (bg[i] != b && distance < b.alignDistance) {
					groupVelocity.add(bg[i].getVelocity());
					count++;
				}
		}

		if (count > 0) groupVelocity.div(count);

		return groupVelocity;
	}

	// Keep within map
	public static AVector BoundRule(Bird b, int maxX, int maxY) {
		AVector bound = new AVector(0,0);
		AVector bpos = b.getLocation();
		if (bpos.x < 30) {
			bound.x = 4;
		} else if (bpos.x > (maxX-30)) {
			bound.x = -4;
		}

		if (bpos.y < 30) {
			bound.y = 4;
		} else if (bpos.y > (maxY - 30)) {
			bound.y = -4;
		}
		return bound;
	}
}
