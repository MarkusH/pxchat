package pxchat.whiteboard;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;

/**
 * <p>
 * This class is the base class for all paint objects that have a color and a
 * line width.
 * </p>
 * 
 * It provides the methods {@link #beginDraw(Graphics2D)} and
 * {@link #endDraw(Graphics2D)} that need to be called prior and after rendering
 * the actual object.
 * 
 * @author Markus Döllinger
 */
public abstract class PrimitiveObject extends PaintObject {

	private static final long serialVersionUID = -8459975068994110362L;

	/**
	 * The color of this paint object
	 */
	private Color color;

	/**
	 * The stroke of this object. Transient because it is not serializable. This
	 * object has to be kept in sync with {@link #width}.
	 */
	private transient BasicStroke stroke;

	/**
	 * The stroke width of this object. It has to be kept in sync with
	 * {@link #stroke}.
	 */
	private float width;
	
	/**
	 * A temporary variable for saving the composite of the graphics context in
	 * case the color has alpha.
	 */
	private transient Composite tmpComp;

	/**
	 * Constructs a new primitive object with the specified color and width.
	 * 
	 * @param color The color of this object
	 * @param width The width of this object
	 */
	public PrimitiveObject(Color color, float width) {
		this.color = color;
		this.width = width;
		this.stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f);
	}

	/**
	 * Sets the stroke and the color of the specified context. This method is
	 * used by subclasses of <code>PrimitiveObject</code> prior to rendering the
	 * actual object.
	 * 
	 * @param g The context to draw with
	 */
	public void beginDraw(Graphics2D g) {
		// TODO: this may be moved to readObject, we will see what's better
		if (stroke == null)
			stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f);
		g.setStroke(stroke);
		
		if (color.getAlpha() == 0) {
			tmpComp = g.getComposite();
			g.setColor(new Color(0x000000, true));
			g.setComposite(AlphaComposite.Src);
		}
		
		g.setColor(color);
	}

	/**
	 * Cleans up after rendering the object.
	 * 
	 * @param g The context used to draw the object
	 */
	public void endDraw(Graphics2D g) {
		if (color.getAlpha() == 0) {
			g.setComposite(tmpComp);
		}
	}
}
