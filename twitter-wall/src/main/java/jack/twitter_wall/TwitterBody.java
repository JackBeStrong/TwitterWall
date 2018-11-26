package jack.twitter_wall;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

public class TwitterBody extends Body {

	private Color colour;
	private String string;

	public TwitterBody(String string, int width, int height) {
		this.colour = new Color((float) Math.random() * 0.5f + 0.5f,
				(float) Math.random() * 0.5f + 0.5f,
				(float) Math.random() * 0.5f + 0.5f);
		this.string = string;
	}

	public TwitterBody(Color colour) {
		this.colour = colour;
		this.string = "";
	}

	public void setString(String string) {
		this.string = string;
	}

	public void render(Graphics2D g) {
		// save the original transform
		AffineTransform ot = g.getTransform();

		// transform the coordinate system from world coordinates to local coordinates
		AffineTransform lt = new AffineTransform();
		lt.translate(this.transform.getTranslationX(),
				this.transform.getTranslationY());
		lt.rotate(this.transform.getRotation());

		// apply the transform
		g.transform(lt);

		renderRect(g);

		// set the original transform
		g.setTransform(ot);
	}

	public void renderRect(Graphics2D g) {
		Vector2[] vertices = ((Rectangle) (this.fixtures.iterator().next()
				.getShape())).getVertices();
		int l = vertices.length;

		Path2D.Double p = new Path2D.Double();
		p.moveTo(vertices[0].x, vertices[0].y);
		for (int i = 1; i < l; i++) {
			p.lineTo(vertices[i].x, vertices[i].y);
		}
		p.closePath();

		g.setColor(colour);
		// put the string to normal orientation instead of upside down
		g.transform(AffineTransform.getScaleInstance(1, -1));
		g.drawString(string, (float) vertices[3].x, (float) vertices[3].y);
		g.transform(AffineTransform.getScaleInstance(1, -1));
	}

}
