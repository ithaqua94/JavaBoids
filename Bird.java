import java.awt.*;
import java.awt.geom.*;
import java.lang.*;
import java.util.*;

class Bird {
	// Dynamic attributes
	boolean included = true;
	boolean selected = false;
	boolean selectView = false; // If a selected sees this
	private AVector location;
	private AVector velocity;
	private AVector acceleration;
	private int maxX = 800;
	private int maxY = 600;
	private int minX = 0;
	private int minY = 0;
	private int debugTick = 0;

	// Static attributes
	private static double maxSpeed = 4;
	private static double minSpeed = 1.5;
	private static double maxAccel = 0.4;
	private static double maxTurn = Math.toRadians(90);
	public static double sightDistance = 150;
	public static int attractDistance = 125; // Rule 1
	public static int minimumDistance = 30;  // Rule 2
	public static int alignDistance = 100;   // Rule 3
	public static double peripheryAngle = Math.toRadians(120);

	private Color fillColor = Color.WHITE;
	private Color outlineColor = Color.BLACK;
	private Color selectedColor = Color.RED;
	private Color viewedColor = Color.CYAN;
	static final Path2D icon = new Path2D.Double();
	static {
		icon.moveTo(0, -6);
		icon.lineTo(-3, 6);
		icon.lineTo(3, 6);
		icon.closePath();
	}

	public Bird(int panelWidth, int panelHeight) {
		this.maxX = panelWidth;
		this.maxY = panelHeight;
		int angle = (int) Math.random() * 360;
		this.location = new AVector(0,0);
		this.location.x = (int) (Math.random() * this.maxX);
		this.location.y = (int) (Math.random() * this.maxY);
		this.velocity = new AVector(0,0);
		this.velocity.x = minSpeed * Math.cos(Math.toRadians(angle));
		this.velocity.y = minSpeed * Math.sin(Math.toRadians(angle));
		this.acceleration = new AVector(0,0);
	}

	public AVector getLocation() { return location; }
	public AVector getVelocity() { return velocity; }

	public void move(Bird[] birds, Graphics2D g) {
			// Check for birds to calculate with
			viewable(g, birds);

			// Determine rule velocities
			AVector rule1 = Ruleset.Rule1(birds,this); // cohesion
			AVector rule2 = Ruleset.Rule2(birds,this); // separation
			AVector rule3 = Ruleset.Rule3(birds,this); // alignment
			AVector boundRule = Ruleset.BoundRule(this,maxX,maxY); // Screen boundaries
			AVector drift = new AVector(0.4, 0.5);

			// Balance rule calculations
			balanceRule(rule1);
			balanceRule(rule2);
			balanceRule(rule3);

			// Assign weights
			rule1.mult(1.7);
			rule2.mult(2.5);
			rule3.mult(1.3);
			boundRule.mult(3);

			// Setup acceleration
			acceleration.add(rule1);
			acceleration.add(rule2);
			acceleration.add(rule3);
			//acceleration.add(boundRule);
			acceleration.add(drift);

			//acceleration.limit(maxAccel);

			// Limit velocity and save old
			double prevAngle  = velocity.heading();
			AVector prevVelo = velocity;
			velocity.add(acceleration);
			velocity.limit(maxSpeed);

			// Max turn radius adjustment
			double angleDiff = velocity.heading() - prevVelo.heading();
			if (Math.abs(angleDiff) > maxTurn) {
				AVector newaccel = new AVector();
				double newangle = 0.0;
				if (angleDiff > 0) {
					newangle = prevAngle - maxTurn;
				} else {
					newangle = prevAngle + maxTurn;
				}
				newaccel.x = acceleration.mag() * Math.cos(newangle);
				newaccel.y = acceleration.mag() * Math.sin(newangle);
				acceleration = newaccel;
			}

			// Adjust location to fit velocity and boundaries
			location.add(velocity);
			if (location.x > maxX) location.x = location.x % maxX;
			if (location.x < minX) location.x = location.x + maxX;
			if (location.y > maxY) location.y = location.y % maxY;
			if (location.y < minY) location.y = location.y + maxY;
			acceleration.mult(0);
	}

	// Stops birds from considering birds they cannot see
	void viewable(Graphics2D g, Bird[] birds) {
			for(int i = 0; i < birds.length; i ++) {
				try {
					birds[i].included = false;
					if (this == birds[i]) continue;

					double dist = AVector.dist(location, birds[i].location);
					if (dist <= 0 || dist > sightDistance) continue;

					AVector lineBetween = AVector.sub(birds[i].location, location);
					double angle = AVector.angleBetween(lineBetween,velocity);

					if(angle < peripheryAngle) birds[i].included = true;
					if (selected) {
						if (birds[i].included) {
							birds[i].selectView = true;
						} else {
							birds[i].selectView = false;
						}
					}
				} catch(Exception e) {
					System.out.println("Error occured while viewing bird.");
				}
			}
	}

	public String printInfo() {
		String information = "";
		information += "Location - x:" + (int) location.x + ", y:" + (int) location.y;
		information += "\nVelocity - x:" + (int) velocity.x + ", y:" + (int) velocity.y;
		information += "\nAcceleration - x:" + (int) acceleration.x + ", y:" + (int) acceleration.y;
		return information;
	}

	public String printDetail() {
		String information = "";
		information += "Location - x:" + (int) location.x + ", y:" + (int) location.y;
		information += "\nVelocity - x:" + (int) velocity.x + ", y:" + (int) velocity.y;
		return information;
	}

	private void balanceRule(AVector rule) {
		rule.normalize();
		rule.mult(maxSpeed);
		rule.sub(velocity);
		rule.limit(maxAccel);
	}

	public void run(Graphics2D g, Bird[] birds) {
		move(birds, g);
		draw(g);
		if (selected) drawSelectInfo(g);
	}

	public void draw(Graphics2D g) {
		Color fill;
		if (selected) {
			fill = selectedColor;
		//} else if (selectView) {
			//fill = viewedColor;
		} else {
			fill = fillColor;
		}
		AffineTransform transform = g.getTransform();
		g.translate(location.x, location.y);
		g.rotate(velocity.heading() + Math.PI / 2);
		g.setColor(fill);
		g.fill(icon);
		g.setColor(outlineColor);
		g.draw(icon);
		g.setTransform(transform);
	}

	public void drawSelectInfo(Graphics2D g) {
		int x = (int) location.x;
		int y = (int) location.y;
		g.setColor(Color.BLUE);
		g.drawOval((x - minimumDistance),
							 (y - minimumDistance),
							 minimumDistance*2, minimumDistance*2);

		g.setColor(Color.GREEN);
		g.drawOval((x - attractDistance),
							 (y - attractDistance),
							 attractDistance*2, attractDistance*2);

		g.setColor(Color.YELLOW);
		g.drawOval((x - alignDistance),
							 (y - alignDistance),
							 alignDistance*2, alignDistance*2);
	}
}
