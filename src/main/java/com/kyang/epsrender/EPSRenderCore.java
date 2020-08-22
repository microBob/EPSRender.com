package com.kyang.epsrender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyang.epsrender.models.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Objects;

public class EPSRenderCore {

    // Global vars
    private static final Meta serverMeta = new Meta();

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> config.addStaticFiles("/public")).start(7000);

        app.get("/test", ctx -> ctx.result("Test again"));


        // SECTION: Register nodes
        //noinspection SpellCheckingInspection
        serverMeta.addServerNode(new Node("Tester 1", "10.68.68.111", "kjasdhf9ia768927huisdaf9", 3));


        // SECTION: Open communication
        app.ws("/communication", ws -> {
            ws.onConnect(ctx -> {
                System.out.println("[WS Connection]: " + ctx.getSessionId());
            });
            ws.onClose(ctx -> {
                System.out.println("[WS Disconnected]: " + ctx.getSessionId());
            });
            ws.onMessage(ctx -> {
                System.out.println("[WS Message]: " + ctx.message());
                ObjectMapper mapper = new ObjectMapper();

                Message inMessage = mapper.readValue(ctx.message(), Message.class);

                switch (inMessage.getType()) {
                    case VerifyBlender:
//                        BlenderFrames blenderFrames = mapper.readValue(inMessage.getPayload(), BlenderFrames.class);
                        BlenderProjectInfo blenderProjectInfo = (BlenderProjectInfo) inMessage.getData();
                        System.out.println("[Blender Frames]: " + blenderProjectInfo.getStartFrame() + ", " + blenderProjectInfo.getEndFrame());

                        break;
                    default:
                        System.out.println("[Message Parsing Error] Unknown type " + inMessage.getType().toString());
                        break;
                }
            });
        });


        //SECTION: Login
        app.get("/login", ctx -> {
            System.out.println("[Route Requested]: /login");

            String returnAddress = "http://" + ctx.host() + "/complete_login";
            String url = "http://epsauth.azurewebsites.net/login?url=" + returnAddress + "&loginparam=userEmail";

            ctx.redirect(url);
            ctx.result("Running login");
        });
        app.get("/complete_login", ctx -> {
            System.out.println("[Route Requested]: /complete_login");

            registerUser(ctx, ctx.queryParam("userEmail"));

            ctx.redirect("/");
        });

        app.get("/update_login_stat", ctx -> {
            System.out.println("[Route Requested]: /update_login_stat");
            // If it is a valid EPS email
            String userEmail = getUserEmail(ctx);
            if (userEmail != null) {
                if (userEmail.endsWith("@eastsideprep.org")) {
                    ctx.result(userEmail.substring(0, userEmail.length() - 17));
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

            String userEmail = getUserEmail(ctx);
            int projectTypeInt = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("projectType")));
            String projectFolderName = ctx.queryParam("projectFolderName");

            if (projectTypeInt > 1) {
                boolean blenderUseAllFrames = Boolean.parseBoolean(ctx.queryParam("blenderUseAll"));
                if (!blenderUseAllFrames) {
                    int blenderStartFrame = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("blenderStartFrame")));
                    int blenderEndFrame = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("blenderEndFrame")));

                }
            }

            System.out.println("[Debug]: JobQueue count: " + serverMeta.getJobQueue().size());
            System.out.println("[Debug]: ActionQueue count: " + serverMeta.getVerifyingQueue().size());
            System.out.println("[Debug]: BlenderJobs count: " + serverMeta.getBlenderQueue().size());
        });
        // SECTION ^: Adding a new Job

        // SECTION: Server Status
        app.get("/update_server_stat", ctx -> {
            
        });
        // SECTION ^: Server Status

    }

    // SECTION: Session management
    private static void registerUser(Context ctx, String userEmail) {
        if (ctx.sessionAttribute("userEmail") == null) {
            ctx.sessionAttribute("userEmail", userEmail);
        }
    }

    // return the stored userEmail attribute
    private static String getUserEmail(Context ctx) {
        return ctx.sessionAttribute("userEmail");
    }
    // SECTION ^: Session management


    // SECTION: Delegate Methods

    public static Meta getServerMeta() {
        return serverMeta;
    }
}
