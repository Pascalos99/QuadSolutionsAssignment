package com.github.pascalos99.quad_assignment_backend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public final class TestUtils {
    public static String getResourceContent(String path) {
        try (InputStream in = TestUtils.class.getResourceAsStream(path)) {
            if (in == null) throw new FileNotFoundException(path);
            byte[] bytes = in.readAllBytes();
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
