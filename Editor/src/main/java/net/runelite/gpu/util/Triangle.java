package net.runelite.gpu.util;

import lombok.Value;

@Value
public class Triangle
{
	private final Vertex a;
	private final Vertex b;
	private final Vertex c;

	/**
	 * Rotates the triangle by the given orientation.
	 *
	 * @param orientation passed orientation
	 * @return new instance
	 */
	public Triangle rotate(int orientation)
	{
		return new Triangle(
				a.rotate(orientation),
				b.rotate(orientation),
				c.rotate(orientation)
		);
	}

}