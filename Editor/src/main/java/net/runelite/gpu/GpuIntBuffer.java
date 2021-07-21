/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.gpu;


import lombok.var;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;


public class GpuIntBuffer
{
	private IntBuffer buffer = allocateDirect(65536);

	public void put(int x, int y, int z)
	{
		buffer.put(x).put(y).put(z);
	}

	public void put(int x, int y, int z, int c)
	{
		buffer.put(x).put(y).put(z).put(c);
	}

	public void flip()
	{
		buffer.flip();
	}

	public void clear()
	{
		buffer.clear();
	}

	public void ensureCapacity(int size)
	{
		while (buffer.remaining() < size)
		{
			IntBuffer newB = allocateDirect(buffer.capacity() * 2);
			buffer.flip();
			newB.put(buffer);
			buffer = newB;
		}
	}

	public IntBuffer getBuffer()
	{
		return buffer;
	}

	public static IntBuffer allocateDirect(int size)
	{
		return ByteBuffer.allocateDirect(size * Integer.BYTES)
				.order(ByteOrder.nativeOrder())
				.asIntBuffer();
	}

    public void put(GpuIntBuffer buffer) {
		this.buffer.put(buffer.buffer);
    }

	public ByteBuffer toByteBuffer() {
		var byteBuffer = ByteBuffer.allocateDirect(buffer.capacity() * Integer.BYTES)
				.order(ByteOrder.nativeOrder());

		for(int i : buffer.asReadOnlyBuffer().array()) {
			byteBuffer.putInt(i);
		}

		return byteBuffer;
	}

	public int getOffset() {
		return buffer.limit();
	}
}
