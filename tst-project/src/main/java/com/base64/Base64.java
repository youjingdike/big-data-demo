package com.base64;

import sun.misc.BASE64Decoder;

import java.io.IOException;

public class Base64 {
    public static void main(String[] args) {
        BASE64Decoder base64Decoder = new BASE64Decoder();
        String ideConfigJson = "eyJmdW5jdGlvbnMiOltdLCJmbG93cHJvcHMiOlt7InJlc3RhcnRDb25maWdzIjp7ImRlbGF5IjoiIiwicmVzdGFydFN0cmF0ZWd5IjoiZmFpbHVyZS1yYXRlIiwibWF4LWZhaWx1cmVzLXBlci1pbnRlcnZhbCI6IjMiLCJmYWlsdXJlcy1kZWxheSI6IjYiLCJhdHRlbXB0cyI6IiIsImZhaWx1cmUtcmF0ZS1pbnRlcnZhbCI6IjE4MCJ9LCJkYXRhQ29uc2lzdGVuY3kiOiJBVF9MRUFTVF9PTkNFIiwiYWxsb3dMYXRlbmVzcyI6IiIsInVzZUV4Y2VwdGlvbkRhdGFTaW5rVG9waWMiOiJmYWxzZSIsImV2ZW50VGltZUZpZWxkTmFtZSI6IiIsInRyaWdnZXJJbnRlcnZhbCI6MTAsIm9wdGlvbmFsUGFyYW1zIjp7fSwiZXhjZXB0aW9uRHNOYW1lIjoiIiwiZGF0YVNhbXBsaW5nIjoiZmFsc2UiLCJzYW1wbGluZ1JhdGlvIjoiMSIsImV4Y2VwdGlvblRvcGljIjoiIiwibWF4UnVubmluZ0Zsb3dzIjoxLCJwcm9jZXNzaW5nVGltZUZpZWxkTmFtZSI6IiIsImZsaW5rVXNlVGltZSI6InByb2Nlc3NpbmdUaW1lIn1dLCJwYWNrYWdlcyI6W10sImZsb3ciOnsiY29tbW9uU2lkZXByb3BzIjpbXSwiZWRnZXMiOlt7InNvdXJjZSI6IkthZmthU291cmNlXzE2Njg1ODczMjEzMzciLCJ0YXJnZXQiOiJTcWxfMjAyMjA5MDExOTA4MTk2MDcifSx7InNvdXJjZSI6IlNxbF8yMDIyMDkwMTE5MDgxOTYwNyIsInRhcmdldCI6IkhpdmVTaW5rXzE2Njc1NDYwNzkwNTMifV0sImVudiI6Im9ubGluZSIsImZsb3dfbmFtZSI6InRzdF9zcWxfanNvbiIsIm5vZGVzIjpbeyJpZCI6IjIyMTEyMjE0NDI0NTUyNjJAMSIsImpvYlR5cGUiOiJIaXZlU2luayIsIm5hbWUiOiJIaXZlU2lua18xNjY3NTQ2MDc5MDUzIiwicGFyYW1zIjoie1wiZmFpbHVyZUhhbmRsZXJQcm94eVwiOlt7XCJzaHV0ZG93bldpdGhGYWlsQ291bnRcIjpcIjFcIixcInNodXRkb3duSWZGYWlsXCI6XCJmYWxzZVwiLFwic2h1dGRvd25Db25kaXRpb25cIjpcIm9uZUZhaWx1cmVcIixcInNodXRkb3duSW5UaW1lSWZGYWlsXCI6XCIxXCIsXCJsb2dGaWVsZHNJZkZhaWxcIjpcIlwiLFwibG9nSWZGYWlsXCI6XCJmYWxzZVwiLFwibG9nUGVyY2VudElmRmFpbFwiOlwiMTAwXCJ9XSxcInNtYWxsRmlsZVNpemVcIjpcIjEyOFwiLFwiZHNOYW1lXCI6XCJkZWZhdWx0SGl2ZVwiLFwic21hbGxGaWxlQ29tcGFjdGlvblwiOlwiZmFsc2VcIixcInByb2Nlc3NvclR5cGVcIjpcInNpbmtcIixcInBhcmFsbGVsaXNtXCI6XCJcIixcInVzZURhdGFDaGVja1wiOlwiZmFsc2VcIixcIl90eXBlXCI6XCJIaXZlU2lua1wiLFwiZGJcIjpcImhpdmVfZGIxMTE0XCIsXCJ0YWJsZVwiOlwiaGl2ZV90YjExMTRcIn0ifSx7ImlkIjoiMjIxMTIyMTQ0MjQ1NTI5NUAxIiwiam9iVHlwZSI6IkthZmthU291cmNlIiwibmFtZSI6IkthZmthU291cmNlXzE2Njg1ODczMjEzMzciLCJwYXJhbXMiOiJ7XCJrYWZrYVRpbWVzdGFtcE5hbWVcIjpcIlwiLFwiZHNOYW1lXCI6XCJkZ0thZmthXCIsXCJyZW1vdmVEdXBsaWNhdGVVcGRhdGVUeXBlXCI6XCJPbkNyZWF0ZUFuZFdyaXRlXCIsXCJwcm9jZXNzb3JUeXBlXCI6XCJzb3VyY2VcIixcImNyZWF0ZVRvcGljUHJvamVjdE5hbWVcIjpcImtzY0F1dG9UZXN0XCIsXCJwYXJhbGxlbGlzbVwiOlwiXCIsXCJ1c2VEYXRhQ2hlY2tcIjpcImZhbHNlXCIsXCJfdHlwZVwiOlwiS2Fma2FTb3VyY2VcIixcImNvbnN1bWVyTG9jYXRpb25cIjpcIkxhdGVzdFwiLFwiYWRkS2Fma2FUaW1lc3RhbXBcIjpcImZhbHNlXCIsXCJ3YXRlcm1hcmtUaW1lXCI6XCJcIixcInN0cmVhbWluZy50aW1lc3RhbXBzXCI6XCJcIixcIm1heEJ5dGVzUGVyU2Vjb25kXCI6XCJcIixcInJlbW92ZUR1cGxpY2F0ZUtlZXBUaW1lXCI6XCJcIixcImV2ZW50VGltZUZpZWxkXCI6XCJcIixcInVzZVJlbW92ZUR1cGxpY2F0ZVwiOlwiZmFsc2VcIixcInRvcGljXCI6XCJ0ZXN0MVwiLFwidGFibGVcIjpcInRcIn0ifSx7ImlkIjoiMjIxMTIyMTQ0MjQ1NTMwNkAxIiwiam9iVHlwZSI6IlNxbCIsIm5hbWUiOiJTcWxfMjAyMjA5MDExOTA4MTk2MDciLCJwYXJhbXMiOiJ7XCJkYXRhU3RyZWFtc1wiOltdLFwicGVybWlzc2lvbkxldmVsXCI6XCJcIixcInNlbGVjdE1vZGVcIjpcInNpbmdsZVwiLFwicHJvY2Vzc29yVHlwZVwiOlwib3BlcmF0b3JcIixcIl90eXBlXCI6XCJTcWxcIixcImlzVXNlVGVtcGxhdGVcIjpcIm5vdXNlXCIsXCJ0ZW1wbGF0ZUlkXCI6XCJcIixcInRhYmxlXCI6XCJcIixcInNxbFwiOlwic2VsZWN0XFxuICBpZCxcXG4gICd7XFxcIiBpZCBcXFwiOjEsXFxcIiBuYW1lIFxcXCI6XFxcIiB0c3QgXFxcIn0nIGFzIGpzb24xLFxcbiAgXFxcIiB7XFxcImlkXFxcIjpcXFwiMVxcXCJ9IFxcXCIgYXMgajEsXFxuICBcXFwiIHtcXFwiaWRcXFwiOlxcXCIxXFxcIiB9XFxcIiBhcyBqMixcXG4gIFxcXCJ7XFxcImlkXFxcIjpcXFwiMVxcXCJ9XFxcIiBhcyBqM1xcbmZyb21cXG4gIHQ7XCIsXCJ3YXRlcm1hcmtlckNvbmZpZ1wiOltdfSJ9XSwicHJvamVjdCI6ImtzY0F1dG9UZXN0IiwicHJvamVjdF9pZCI6MSwic2lkZXByb3BzIjpbXX19=";
        try {
            ideConfigJson = new String(base64Decoder.decodeBuffer(ideConfigJson));
        } catch (IOException e) {
            throw new RuntimeException(String.format("base64 decode IOException: %s", e));
        }
        String formatJson = ideConfigJson.replaceAll("\t", "")
                .replace("\"{", "{")
                .replace("}\"", "}")
                .replace("\"[", "[")
                .replace("]\"", "]")
                .replace("\\\"", "\"");

        String s = formatJson.replaceAll("\\\\\"", "\\\"")
                .replaceAll("\\\\\\\\\\\\\\{","\\\\\"{");
        System.out.println(ideConfigJson);
        System.out.println(formatJson);
        System.out.println(s);

        String s1 = "a\\\\\\{b";
        System.out.println(s1);
        s1 = s1.replaceAll("\\\\\\\\\\\\\\{","\\\\\"{");
        System.out.println(s1);
    }
}
