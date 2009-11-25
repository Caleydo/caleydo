package daemon;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlRootElement
public class BoundingBox {
	private int x;
	private int y;
	private int width;
	private int height;

	public BoundingBox() {
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
	}
	
	public BoundingBox(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public int getX() {
		return x;
	}
	@XmlAttribute
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	@XmlAttribute
	public void setY(int y) {
		this.y = y;
	}
	public int getWidth() {
		return width;
	}
	@XmlAttribute
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	@XmlAttribute
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public String toString() {
		return "BoundingBox [("+x+":"+y+")-("+height+":"+width+")]";
	}
	
}
