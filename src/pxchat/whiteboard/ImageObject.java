/**
 * 
 */
package pxchat.whiteboard;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import pxchat.net.protocol.frames.Frame;

/**
 * This class represents an image on the paint board. It has a position and a
 * size.
 * 
 * @author Markus Döllinger
 */
public class ImageObject extends PaintObject {

	private static final long serialVersionUID = -4590934348938821298L;

	/**
	 * The image of this image object
	 */
	private transient BufferedImage image;

	/**
	 * The image id of this image object
	 */
	private int imageID;

	/**
	 * The top left edge of the image.
	 */
	private Point topLeft;

	/**
	 * The width of the image.
	 */
	private int width;

	/**
	 * The height of the image.
	 */
	private int height;

	/**
	 * Constructs a new image object using the specified bounds and the image.
	 * 
	 * @param point1 A point on the rectangle of the image
	 * @param point2 A second point
	 * @param img The image
	 */
	public ImageObject(Point point1, Point point2, BufferedImage img) {
		this.id = Frame.ID_IMG;
		this.image = img;
		this.topLeft = new Point(Math.min(point1.x, point2.x), Math.min(point1.y, point2.y));
		this.width = Math.abs(point1.x - point2.x);
		this.height = Math.abs(point1.y - point2.y);
	}

	/**
	 * Constructs a new image object using the specified bounds and the image
	 * associated with the image id.
	 * 
	 * @param point1 A point on the rectangle of the image
	 * @param point2 A second point
	 * @param imageID The id of the image
	 */
	public ImageObject(Point point1, Point point2, int imageID) {
		this.id = Frame.ID_IMG;
		this.imageID = imageID;
		this.topLeft = new Point(Math.min(point1.x, point2.x), Math.min(point1.y, point2.y));
		this.width = Math.abs(point1.x - point2.x);
		this.height = Math.abs(point1.y - point2.y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pxchat.whiteboard.PaintObject#draw(java.awt.Graphics2D)
	 */
	@Override
	public void draw(Graphics2D g) {
		if (image == null)
			image = ImageTable.getInstance().get(imageID);
		if (image != null)
			g.drawImage(image, topLeft.x, topLeft.y, width, height, null);
	}

	/**
	 * @return The image of this image object
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Returns the resized image of this image object. If the current image is
	 * smaller or equal than the size of this image object, the current image is
	 * returned. Otherwise a resized image is returned. Returns
	 * <code>null</code> if no image is specified.
	 * 
	 * @return The resized image, or the image itself
	 */
	public BufferedImage getResizedImage() {
		if (image == null)
			return null;
		if (image.getWidth() * image.getHeight() < width * height)
			return image;
		BufferedImage result = new BufferedImage(
				width,
				height,
				image.getAlphaRaster() == null ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = result.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof ImageObject))
			return false;

		ImageObject that = (ImageObject) obj;

		return this.topLeft.equals(that.topLeft) && 
				this.width == that.width && this.height == that.height &&
				this.imageID == that.imageID;
	}
}
