package com.xq.charset.custom;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class Rot13Charset extends Charset {
    private static final String BASE_CHARSET_NAME = "UTF-8";
    Charset baseCharset;
    /**
     * Initializes a new charset with the given canonical name and alias
     * set.
     *
     * @param canonicalName The canonical name of this charset
     * @param aliases       An array of this charset's aliases, or null if it has no aliases
     */
    protected Rot13Charset(String canonicalName, String[] aliases) {
        super(canonicalName, aliases);
        baseCharset = Charset.forName(BASE_CHARSET_NAME);
    }

    @Override
    public boolean contains(Charset cs) {
        return false;
    }

    @Override
    public CharsetDecoder newDecoder() {
        return new Rot13Decode(this,baseCharset.newDecoder());
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new Rot13Encode(this,baseCharset.newEncoder());
    }

    private void rot13(CharBuffer cb) {
        for (int pos = cb.position(); pos < cb.limit(); pos++) {
            char c = cb.get(pos);
            char a = '\u0000';
            if (c>='a' && c<='z') {
                a = 'a';
            }
            if (c>='A' && c<='Z') {
                a = 'A';
            }
            if (a != '\u0000') {
                c = (char)(((c-a)+13) % 26 + a);
                cb.put(pos, c);
            }
        }

    }

    private class Rot13Encode extends CharsetEncoder {
        private CharsetEncoder baseEncoder;
        Rot13Encode(Charset cs, CharsetEncoder baseEncoder) {
            super(cs, baseEncoder.averageBytesPerChar(), baseEncoder.maxBytesPerChar());
            this.baseEncoder = baseEncoder;
        }

        @Override
        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            CharBuffer tmpcb = CharBuffer.allocate(in.remaining());
            while (in.hasRemaining()) {
                tmpcb.put(in.get());
            }
            tmpcb.rewind();
            rot13(tmpcb);
            baseEncoder.reset();
            CoderResult cr = baseEncoder.encode(tmpcb, out, true);

            in.position(in.position() - tmpcb.remaining());
            return cr;
        }
    }

    private class Rot13Decode extends CharsetDecoder {
        private CharsetDecoder baseDecoder;
        /**
         * Initializes a new decoder.  The new decoder will have the given
         * chars-per-byte values and its replacement will be the
         * string <tt>"&#92;uFFFD"</tt>.
         *
         * @param cs                  The charset that created this decoder
         * @throws IllegalArgumentException If the preconditions on the parameters do not hold
         */
        Rot13Decode(Charset cs, CharsetDecoder baseDecoder) {
            super(cs, baseDecoder.averageCharsPerByte(), baseDecoder.maxCharsPerByte());
            this.baseDecoder = baseDecoder;
        }

        @Override
        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            baseDecoder.reset();
            CoderResult result = baseDecoder.decode(in, out, true);
            rot13(out);
            return result;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader in;
        if (args.length > 0) {
            in = new BufferedReader(new FileReader(args[0]));
        } else {
            in = new BufferedReader(new InputStreamReader(System.in));
        }

        PrintStream out = new PrintStream(System.out, false, "X-ROT13");
        String s = null;
        if ((s = in.readLine())!=null) {
            out.println(s);
        }
        out.flush();
    }
}
