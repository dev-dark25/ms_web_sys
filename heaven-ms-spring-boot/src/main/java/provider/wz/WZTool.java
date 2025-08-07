/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package provider.wz;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import tools.data.input.LittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;

/*
 * Ported Code, see WZFile.java for more info
 */
public class WZTool {
    private static byte[] encKey;
    private static byte[] tempBuffer;
    private static Charset textEncoding;

    static {
        byte[] iv = new byte[]{(byte) 0x4d, (byte) 0x23, (byte) 0xc7, (byte) 0x2b,
            (byte) 0x4d, (byte) 0x23, (byte) 0xc7, (byte) 0x2b,
            (byte) 0x4d, (byte) 0x23, (byte) 0xc7, (byte) 0x2b,
            (byte) 0x4d, (byte) 0x23, (byte) 0xc7, (byte) 0x2b,};
        byte[] key = new byte[]{(byte) 0x13, 0x00, 0x00, 0x00,
            (byte) 0x08, 0x00, 0x00, 0x00,
            (byte) 0x06, 0x00, 0x00, 0x00,
            (byte) 0xB4, 0x00, 0x00, 0x00,
            (byte) 0x1B, 0x00, 0x00, 0x00,
            (byte) 0x0F, 0x00, 0x00, 0x00,
            (byte) 0x33, 0x00, 0x00, 0x00,
            (byte) 0x52, 0x00, 0x00, 0x00
        };
        Cipher cipher = null;
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        encKey = new byte[0xFFFF];
        for (int i = 0; i < (0xFFFF / 16); i++) {
            try {
                iv = cipher.doFinal(iv);
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }
            System.arraycopy(iv, 0, encKey, (i * 16), 16);
        }
        try {
            iv = cipher.doFinal(iv);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        System.arraycopy(iv, 0, encKey, 65520, 15);
    }

    public static byte[] readListString(byte[] str) {
        for (int i = 0; i < str.length; i++) {
            str[i] = (byte) (str[i] ^ encKey[i]);
        }
        return str;
    }

    public static String readDecodedString(LittleEndianAccessor llea) {
        int size = llea.readByte(); // 使用 InputStreamByteStream 中的 readByte 方法
        String result = null;

        if (size < 0) {
            byte mask = (byte) 0xAA;
            size = (size == -128) ? llea.readInt32() : -size;

            byte[] buffer = getStringBuffer(size);
            buffer = llea.read(size);
            decrypt(buffer, 0, size);

            for (int i = 0; i < size; i++) {
                buffer[i] ^= mask;
                mask++;
            }

            java.nio.charset.Charset enc = (textEncoding != null) ? textEncoding : StandardCharsets.UTF_8;
            result = new String(buffer, 0, size, enc);
        } else if (size > 0) {
            short mask = (short) 0xAAAA;
            if (size == 127) {
                size = llea.readInt32();
            }

            byte[] buffer = llea.read(size * 2);
            decrypt(buffer, 0, size * 2);

            short[] pChar = new short[size];
            for (int i = 0; i < size; i++) {
                pChar[i] = (short) (((short) buffer[i * 2] & 0xFF) | ((short) buffer[i * 2 + 1] & 0xFF) << 8);
                pChar[i] ^= mask;
                mask++;
            }

            // 将 short 数组转换为 char 数组
            char[] charArray = new char[pChar.length];
            for (int i = 0; i < pChar.length; i++) {
                charArray[i] = (char) pChar[i];
            }

            // 将 char 数组转换为字符串
            result = new String(charArray);
        } else {
            return "";
        }

        if (result.length() <= 4) {
            for (int i = 0; i < result.length(); i++) {
                if (result.charAt(i) >= 0x80) {
                    return result;
                }
            }
            return result.intern();
        } else {
            return result;
        }
    }
    public static String readDecodedStringOld(LittleEndianAccessor llea){
        int strLength;
        byte b = llea.readByte();
        if (b == 0x00) {
            return "";
        }
        if (b >= 0) {
            if (b == 0x7F) {
                strLength = llea.readInt();
            } else {
                strLength = (int) b;
            }
            if (strLength < 0) {
                return "";
            }
            byte str[] = new byte[strLength * 2];
            for (int i = 0; i < strLength * 2; i++) {
                str[i] = llea.readByte();
            }
            return DecryptUnicodeStr(str);
        } else {
            if (b == -128) {
                strLength = llea.readInt();
            } else {
                strLength = -b;
            }
            if (strLength < 0) {
                return "";
            }
            byte str[] = new byte[strLength];
            for (int i = 0; i < strLength; i++) {
                str[i] = llea.readByte();
            }
            return DecryptAsciiStr(str);
        }
    }
    private static byte[] getStringBuffer(int size) {
        if (size <= 4096) {
            if (tempBuffer == null || tempBuffer.length < size) {
                tempBuffer = new byte[size];
            }
            return tempBuffer;
        } else {
            return new byte[size];
        }
    }

    public static void decrypt(byte[] buffer, int startIndex, int length) {

        for (int i = 0; i < length; i++) {
            buffer[startIndex + i] ^= encKey[i];
        }
    }
    public static String DecryptAsciiStr(byte[] str) {
        byte xorByte = (byte) 0xAA;
        for (int i = 0; i < str.length; i++) {
            str[i] = (byte) (str[i] ^ xorByte ^ encKey[i]);
            xorByte++;
        }
        return new String(str);
    }

    public static String DecryptUnicodeStr(byte[] str) {
        int xorByte = 0xAAAA;
        char[] charRet = new char[str.length / 2];
        for (int i = 0; i < str.length; i++) {
            str[i] = (byte) (str[i] ^ encKey[i]);
        }
        for (int i = 0; i < (str.length / 2); i++) {
            char toXor = (char) ((str[i] << 8) | str[i + 1]);
            charRet[i] = (char) (toXor ^ xorByte);
            xorByte++;
        }
        return String.valueOf(charRet);
    }

    public static String readDecodedStringAtOffset(SeekableLittleEndianAccessor slea, int offset) {
        slea.seek(offset);
        return readDecodedString(slea);
    }

    public static String readDecodedStringAtOffsetAndReset(SeekableLittleEndianAccessor slea, int offset) {
        long pos = 0;
        pos = slea.getPosition();
        slea.seek(offset);
        String ret = readDecodedString(slea);
        slea.seek(pos);
        return ret;
    }

    public static int readValue(LittleEndianAccessor lea) {
        byte b = lea.readByte();
        if (b == -128) {
            return lea.readInt();
        } else {
            return ((int) b);
        }
    }

    public static float readFloatValue(LittleEndianAccessor lea) {
        byte b = lea.readByte();
        if (b == -128) {
            return lea.readFloat();
        } else {
            return 0;
        }
    }
}