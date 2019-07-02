package com.invengo.lib.util;

import java.nio.ByteOrder;

import com.invengo.lib.diagnostics.InvengoLog;

public class BitConvert {

	private static final String TAG = BitConvert.class.getSimpleName();

	public static short toShort(byte[] buf, int offset) throws Exception {
		return toShort(buf, offset, ByteOrder.LITTLE_ENDIAN);
	}

	public static short toShort(byte[] buf, int offset, ByteOrder order) throws Exception {
		short value = 0;

		if (buf == null)
			throw new NullPointerException();
		if (buf.length < offset + (Short.SIZE / Byte.SIZE))
			throw new IndexOutOfBoundsException();

		try {
			if (order.equals(ByteOrder.BIG_ENDIAN)) {
				value |= (((short) buf[offset + 0] << 8) & 0xFF00);
				value |= (((short) buf[offset + 1] << 0) & 0x00FF);

			} else if (order.equals(ByteOrder.LITTLE_ENDIAN)) {
				value |= (((short) buf[offset + 0] << 0) & 0x00FF);
				value |= (((short) buf[offset + 1] << 8) & 0xFF00);
			}
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. toShort([%s], %d, %s", HexUtil.dump(buf, offset, 2), offset, order);
			throw e;
		}

		return value;
	}

	public static int toInteger(byte[] buf, int offset) throws Exception {
		return toInteger(buf, offset, ByteOrder.LITTLE_ENDIAN);
	}

	public static int toInteger(byte[] buf, int offset, ByteOrder order) throws Exception {
		int value = 0;

		if (buf == null)
			throw new NullPointerException();
		if (buf.length < offset + (Integer.SIZE / Byte.SIZE))
			throw new IndexOutOfBoundsException();

		try {
			if (order.equals(ByteOrder.BIG_ENDIAN)) {
				value |= (((int) buf[offset + 0] << 24) & 0xFF000000);
				value |= (((int) buf[offset + 1] << 16) & 0x00FF0000);
				value |= (((int) buf[offset + 2] << 8) & 0x0000FF00);
				value |= (((int) buf[offset + 3] << 0) & 0x000000FF);
			} else if (order.equals(ByteOrder.LITTLE_ENDIAN)) {
				value |= (((int) buf[offset + 0] << 0) & 0x000000FF);
				value |= (((int) buf[offset + 1] << 8) & 0x0000FF00);
				value |= (((int) buf[offset + 2] << 16) & 0x00FF0000);
				value |= (((int) buf[offset + 3] << 24) & 0xFF000000);
			}
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. toInteger([%s], %d, %s", HexUtil.dump(buf, offset, 4), offset, order);
			throw e;
		}

		return value;
	}

	public static void fromShort(short value, byte[] buf, int offset) throws Exception {
		fromShort(value, buf, offset, ByteOrder.LITTLE_ENDIAN);
	}

	public static void fromShort(short value, byte[] buf, int offset, ByteOrder order) throws Exception {
		if (buf == null)
			throw new NullPointerException();
		if (buf.length < offset + (Short.SIZE / Byte.SIZE))
			throw new IndexOutOfBoundsException();

		try {
			if (order.equals(ByteOrder.BIG_ENDIAN)) {
				buf[offset + 0] = (byte) ((value & 0xFF00) >> 8);
				buf[offset + 1] = (byte) ((value & 0x00FF) >> 0);
			} else if (order.equals(ByteOrder.LITTLE_ENDIAN)) {
				buf[offset + 0] = (byte) ((value & 0x00FF) >> 0);
				buf[offset + 1] = (byte) ((value & 0xFF00) >> 8);
			}
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. fromShort(0x%04X, %d)", value, offset);
			throw e;
		}
	}

	public static void fromInteger(int value, byte[] buf, int offset) throws Exception {
		fromInteger(value, buf, offset, ByteOrder.LITTLE_ENDIAN);
	}

	public static void fromInteger(int value, byte[] buf, int offset, ByteOrder order) throws Exception {
		if (buf == null)
			throw new NullPointerException();
		if (buf.length < offset + (Integer.SIZE / Byte.SIZE))
			throw new IndexOutOfBoundsException();

		try {
			if (order.equals(ByteOrder.BIG_ENDIAN)) {
				buf[offset + 0] = (byte) ((value & 0xFF000000) >> 24);
				buf[offset + 1] = (byte) ((value & 0x00FF0000) >> 16);
				buf[offset + 2] = (byte) ((value & 0x0000FF00) >> 8);
				buf[offset + 3] = (byte) ((value & 0x000000FF) >> 0);
			} else if (order.equals(ByteOrder.LITTLE_ENDIAN)) {
				buf[offset + 0] = (byte) ((value & 0x000000FF) >> 0);
				buf[offset + 1] = (byte) ((value & 0x0000FF00) >> 8);
				buf[offset + 2] = (byte) ((value & 0x00FF0000) >> 16);
				buf[offset + 3] = (byte) ((value & 0xFF000000) >> 24);
			}
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. fromInteger(0x%08X, %d)", value, offset);
			throw e;
		}
	}
}
