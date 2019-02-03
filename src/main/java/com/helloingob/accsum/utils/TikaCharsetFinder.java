package com.helloingob.accsum.utils;

import java.io.IOException;
import java.io.InputStream;
import org.apache.any23.encoding.TikaEncodingDetector;
import org.zkoss.zk.ui.util.CharsetFinder;

public class TikaCharsetFinder implements CharsetFinder {
    public String getCharset(String contentType, InputStream content) throws IOException {
        return new TikaEncodingDetector().guessEncoding(content);
    }
}
