package com.xq.netty.httpxml.server;

import io.netty.handler.codec.MessageToMessageDecoder;
import org.jibx.binding.generator.BindGen;

public abstract class AbstractHTTPXmlEncoder<T> extends MessageToMessageDecoder<T> {
}
