package com.kyang.eprender;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class EPRenderCore {

    // Global vars

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> config.addStaticFiles("/public")).start(7000);

        app.get("/test", ctx -> ctx.result("Test again"));

        //SECTION: Login
        app.get("/login", ctx -> {
            System.out.println("[Route Requested]: /login");

            String returnAddress = "http://" + ctx.host() + "/complete_login";
            String url = "http://epsauth.azurewebsites.net/login?url=" + returnAddress + "&loginparam=useremail";

            ctx.redirect(url);
            ctx.result("Running login");
        });
        app.get("/complete_login", ctx -> {
            System.out.println("[Route Requested]: /complete_login");

            registerUser(ctx, ctx.queryParam("useremail"));

            ctx.redirect("/");
        });

        app.get("/update_login_stat", ctx -> {
            System.out.println("[Route Requested]: /update_login_stat");
            // If it is a valid EPS email
            String useremail = getUserEmail(ctx);
            if (useremail != null) {
                if (useremail.endsWith("@eastsideprep.org")) {
                    ctx.result(useremail.substring(0, useremail.length() - 17));
                } else {
                    ctx.result("!invalid!");
                }
            } else {
                // returns blank if not
                ctx.result("");
            }
        });

        // SECTION ^: Login

    }

    // SECTION: Session management
    private static void registerUser(Context ctx, String useremail) {
        if (ctx.sessionAttribute("useremail") == null) {
            ctx.sessionAttribute("useremail", useremail);
        }
    }

    // return the stored useremail attribute
    private static String getUserEmail(Context ctx) {
        return ctx.sessionAttribute("useremail");
    }
    // SECTION ^: Session management
}
