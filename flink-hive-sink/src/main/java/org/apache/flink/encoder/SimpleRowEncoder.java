package org.apache.flink.encoder;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.flink.annotation.PublicEvolving;
import org.apache.flink.api.common.serialization.Encoder;
import org.apache.flink.types.Row;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Encoder} that uses {@code toString()} on the input elements and
 * writes them to the output bucket file separated by newline.
 *
 * @param <IN> The type of the elements that are being written by the sink.
 */
@PublicEvolving
public class SimpleRowEncoder<IN> implements Encoder<IN> {

    private static final long serialVersionUID = -6865107843734614452L;

    private static String defaultFieldDelimiter = "\u0001";

    private String fieldDelimiter;

    private String charsetName;

    private transient Charset charset;

    /**
     * Creates a new {@code StringWriter} that uses {@code "UTF-8"} charset to convert
     * strings to bytes.
     */
    public SimpleRowEncoder() {
        this(defaultFieldDelimiter);
    }

    /**
     * Creates a new {@code StringWriter} that uses {@code "UTF-8"} charset to convert
     * strings to bytes.
     */
    public SimpleRowEncoder(String fieldDelimiter) {
        this(fieldDelimiter, "UTF-8");
    }

    /**
     * Creates a new {@code StringWriter} that uses the given charset to convert
     * strings to bytes.
     *
     * @param charsetName Name of the charset to be used, must be valid input for {@code Charset.forName(charsetName)}
     */
    public SimpleRowEncoder(String fieldDelimiter, String charsetName) {
        this.fieldDelimiter = fieldDelimiter;
        this.charsetName = charsetName;
    }

    @Override
    public void encode(IN element, OutputStream stream) throws IOException {
        if (charset == null) {
            charset = Charset.forName(charsetName);
        }

        StringBuffer stringBuffer = new StringBuffer();
        if (element instanceof Row) {
            Row row = (Row) element;
            if (row.getArity()!=0) {
                Map<String,Object> columnInfo = (Map<String, Object>) row.getField(0);
                AtomicInteger counter = new AtomicInteger();
                columnInfo.forEach((k,v)->{
                    stringBuffer.append(v);
                    if (counter.get() != columnInfo.size() - 1) {
                        stringBuffer.append(StringEscapeUtils.unescapeJava(fieldDelimiter));
                    }
                    counter.getAndIncrement();
                });
            }
        }
        stream.write(stringBuffer.toString().getBytes(charset));
        stream.write('\n');
    }
}

