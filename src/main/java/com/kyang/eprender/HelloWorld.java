package com.kyang.eprender;

import io.javalin.Javalin;

public class HelloWorld {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public");
        }).start(7000);

        app.get("test", ctx -> ctx.result("Test passed"));
    }
}
