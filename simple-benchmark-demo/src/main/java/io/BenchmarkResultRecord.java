package io;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;

public class BenchmarkResultRecord {

    String message = "";

    public BenchmarkResultRecord(String arg) throws Exception {
        File f = new File( arg );
        message = new String(Files.readAllBytes( f.toPath() ));
    }

    public String asString() {
        return message;
    }
}
