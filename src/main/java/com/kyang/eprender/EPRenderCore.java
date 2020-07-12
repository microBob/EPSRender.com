package com.kyang.eprender;

import com.kyang.eprender.Enums.JobStatus;
import com.kyang.eprender.Enums.ProjectType;
import com.kyang.eprender.models.JobRequest;
import com.kyang.eprender.models.Meta;
import com.kyang.eprender.models.Node;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Objects;

public class EPRenderCore {

    // Global vars
    private static final Meta serverMeta = new Meta();

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> config.addStaticFiles("/public")).start(7000);

        app.get("/test", ctx -> ctx.result("Test again"));


        // SECTION: Register nodes
        serverMeta.addServerNode(new Node("Tester 1", "10.68.68.111", 3));


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

        // SECTION: Adding a new Job
        app.put("/add_new_job", ctx -> {
            System.out.println("[Route Requested]: /add_new_job");

            String useremail = getUserEmail(ctx);
            int projectTypeInt = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("projectType")));
            String projectLocation = ctx.queryParam("projectLocation");

            if (projectTypeInt > 1) {
                boolean blenderUseAllFrames = Boolean.parseBoolean(ctx.queryParam("blenderUseAll"));
                if (!blenderUseAllFrames) {
                    int blenderStartFrame = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("blenderStartFrame")));
                    int blenderEndFrame = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("blenderEndFrame")));

                    for (int i = blenderStartFrame; i <= blenderEndFrame; i++) {
                        JobRequest jobRequest = new JobRequest(useremail, projectTypeInt, projectLocation, blenderStartFrame, blenderEndFrame);
                        jobRequest.setBlenderCurrentFrame(i);

                        if (i == blenderStartFrame) {
                            serverMeta.addToActionQueue(jobRequest);
                        } else {
                            serverMeta.addToBlenderJobs(jobRequest);
                        }
                    }
                } else {
                    JobRequest jobRequest = new JobRequest(useremail, projectTypeInt, projectLocation);
                    jobRequest.setBlenderCurrentFrame(-1);

                    serverMeta.addToActionQueue(jobRequest);
                }
            } else {
                JobRequest jobRequest = new JobRequest(useremail, projectTypeInt, projectLocation);

                serverMeta.addToActionQueue(jobRequest);
            }

            System.out.println("[Debug]: JobQueue count: "+serverMeta.getJobQueue().size());
            System.out.println("[Debug]: ActionQueue count: "+serverMeta.getActionQueue().size());
            System.out.println("[Debug]: BlenderJobs count: "+serverMeta.getBlenderJobs().size());
        });
        // SECTION ^: Adding a new Job

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


    // SECTION: Delegate Methods

    public static Meta getServerMeta() {
        return serverMeta;
    }
}
