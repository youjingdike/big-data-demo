package com.xq.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_0;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;

    private final String url;
    private final String root;

    public HttpFileServerHandler(String url,String root) {
        this.url = url;
        this.root = root;
    }



    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        final String uri = req.getUri();
        System.out.println("uri:"+uri);
        if (!req.getDecoderResult().isSuccess()) {
            sendError(ctx,HttpResponseStatus.BAD_REQUEST);
            return;
        }
        if (req.getMethod() != HttpMethod.GET) {
            sendError(ctx,HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        final String path = sanitizeUri(uri);
        if (path == null) {
            sendError(ctx,HttpResponseStatus.FORBIDDEN);
            return;
        }
        File file = new File(path);
        if (file.isHidden()||!file.exists()) {
            sendError(ctx,HttpResponseStatus.NOT_FOUND);
            return;
        }
        if (file.isDirectory()) {
            if (uri.endsWith("/")) {
                sendListing(ctx, file);
            } else {
                sendRedirect(ctx,uri+"/");
            }
            return;
        }

        if (!file.isFile()) {
            sendError(ctx,HttpResponseStatus.FORBIDDEN);
            return;
        }

        /*CharSequence ifModifiedSince = req.headers().get(HttpHeaders.Names.IF_MODIFIED_SINCE);
        if (ifModifiedSince != null && !ifModifiedSince.toString().isEmpty()) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
            Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince.toString());

            // Only compare up to the second because the datetime format we send to the client
            // does not have milliseconds
            long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
            long fileLastModifiedSeconds = file.lastModified() / 1000;
            if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                sendNotModified(ctx,req);
                return;
            }
        }*/

        RandomAccessFile raf = null;
        try {
            //以只读的方式打开文件
            raf = new RandomAccessFile(file,"r");// 以只读的方式打开文件
        } catch (FileNotFoundException e) {
            sendError(ctx,HttpResponseStatus.NOT_FOUND);
            return;
        }
        long fileLength = raf.length();

        //一定要注意这个response对象不要用错了
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
        setContentLength(response,fileLength);
        setContentTypeHander(response,file);
//        setDateAndCacheHeaders(response, file);
        if (HttpHeaders.isKeepAlive(req)) {
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.write(response);

        ChannelFuture sendFileFuture = null;
        ChannelFuture lastContentFuture = null;

        sendFileFuture = ctx.write(new ChunkedFile(raf, 0, fileLength, 8192),ctx.newProgressivePromise());

        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
                //progress累积传输的字节,total总共要传输的字节,如果没有传完total为-1
                if (total < 0) {
                    System.err.println(future.channel() + ",Transfer progress-1:" + progress+"/"+total);
                } else {
                    System.err.println(future.channel() + ",Transfer progress-2:" + progress+"/"+total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                System.err.println(future.channel() + ",Transfer complete.");
            }
        });


        lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!HttpHeaders.isKeepAlive(req)) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx,HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    private String sanitizeUri(String uri) {
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException ex) {
                throw new Error();
            }
        }

        if (!uri.startsWith(url)) {
            return null;
        }

        if (!uri.startsWith("/")) {
            return null;
        }
        uri = uri.replace('/', File.separatorChar);
        if (uri.contains(File.separator+'.') || uri.contains('.'+File.separator) ||
                uri.startsWith(".") || uri.endsWith(".") || INSECURE_URI.matcher(uri).matches()) {
            return  null;
        }

//        return System.getProperty("user.dir")+File.separator+uri;
        return root+File.separator+uri;
    }

    private static final Pattern ALLOWND_FILE_NAME = Pattern.compile("[^-\\._]?[^<>&\\\"]*");

    private static void sendListing(ChannelHandlerContext ctx,File dir) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/html;charset=UTF-8");
        StringBuffer sb = new StringBuffer();
        String dirPath = dir.getPath();
        sb.append("<!DOCTYPE html>\r\n");
        sb.append("<html><head><title>");
        sb.append(dirPath);
        sb.append(" 目录: ");
        sb.append("</title></head><body>\r\n");
        sb.append("<h3>\r\n");
        sb.append(dirPath).append(" 目录: ");
        sb.append("</h3>\r\n");
        sb.append("<ul>");
        sb.append("<li>链接:<a href=\"../\">..</a></li>\r\n");
        for (File file : dir.listFiles()) {
            if (file.isHidden() || !file.canRead()) {
                continue;
            }

            String name = file.getName();
            if (!ALLOWND_FILE_NAME.matcher(name).matches()) {
                continue;
            }

            sb.append("<li>链接:<a href=\"");
            sb.append(name);
            sb.append("\">");
            sb.append(name);
            sb.append("</a></li>\r\n");
        }
        sb.append("</ul></body><html>\r\n");
        ByteBuf byteBuf = Unpooled.copiedBuffer(sb, CharsetUtil.UTF_8);
        response.content().writeBytes(byteBuf);
        byteBuf.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendRedirect(ChannelHandlerContext ctx,String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaders.Names.LOCATION,newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendError(ChannelHandlerContext ctx,HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status,Unpooled.copiedBuffer("Failure:"+status.toString()+"\r\n",CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/plain;chartset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendNotModified(ChannelHandlerContext ctx,FullHttpRequest request) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED, Unpooled.EMPTY_BUFFER);
        setDateHeader(response);

        sendAndCleanupConnection(ctx, response,request);
    }

    private void sendAndCleanupConnection(ChannelHandlerContext ctx, FullHttpResponse response,FullHttpRequest request) {
        final boolean keepAlive = HttpHeaders.isKeepAlive(request);
        HttpHeaders.setContentLength(response, response.content().readableBytes());
        if (!keepAlive) {
            // We're going to close the connection as soon as the response is sent,
            // so we should also make it clear for the client.
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
        } else if (request.getProtocolVersion().equals(HTTP_1_0)) {
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        ChannelFuture flushPromise = ctx.writeAndFlush(response);

        if (!keepAlive) {
            // Close the connection as soon as the response is sent.
            flushPromise.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaders.Names.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaders.Names.EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(HttpHeaders.Names.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(
                HttpHeaders.Names.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    private static void setDateHeader(FullHttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaders.Names.DATE, dateFormatter.format(time.getTime()));
    }

    private static void setContentTypeHander(HttpResponse httpResponse, File file) {
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        String contentType = mimetypesFileTypeMap.getContentType(file.getPath());
        System.out.println("contentType:"+contentType);
        httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
    }

    private static void setContentLength(HttpResponse httpResponse, long length) {
        HttpHeaders.setContentLength(httpResponse,length);
//        httpResponse.headers().setLong(HttpHeaderNames.CONTENT_LENGTH, length);
    }
}
