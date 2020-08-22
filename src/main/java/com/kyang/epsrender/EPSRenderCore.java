package com.kyang.epsrender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyang.epsrender.Enums.MessageType;
import com.kyang.epsrender.Enums.NodeStatus;
import com.kyang.epsrender.models.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Objects;

public class EPSRenderCore {

    // Global vars
    private static final Meta serverMeta = new Meta();

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> config.addStaticFiles("/public")).start(7000);
        ObjectMapper mapper = new ObjectMapper();

        app.get("/test", ctx -> ctx.result("Test again"));


//        // SECTION: Register nodes
//        //noinspection SpellCheckingInspection
//        serverMeta.addServerNode(new Node("Tester 1", "kjasdhf9ia768927huisdaf9", 3));


        // SECTION: Open communication
        app.ws("/communication", ws -> {
            ws.onConnect(ctx -> {
                System.out.println("[WS Connection]: " + ctx.getSessionId());

                // create new blank node with ctx
                Node unknownNode = new Node(ctx.getSessionId());
                serverMeta.addServerNode(unknownNode);

                // create blank handshake and send
                Message handshake = new Message(MessageType.NewNodeHandshake, null);
                ctx.send(mapper.writeValueAsString(handshake));
            });
            ws.onClose(ctx -> {
                // get node fro ctx ID
                Node disNode = serverMeta.getServerNodeWithID(ctx.getSessionId());
                // print disconnect
                System.out.println("[Node Disconnected]: " + disNode.getNodeName() + " disconnected!");
                // set status to offline
                disNode.setNodeStatus(NodeStatus.Offline);

                // handle job (if died with job)
                if (disNode.getCurrentJob() != null) {
                    serverMeta.addToJobQueueBeginning(disNode.getCurrentJob());
                }
            });
            ws.onMessage(ctx -> {
                System.out.println("[WS Message]: " + ctx.message());

                try { // try to unpack new message as a Job related message (typical)
                    Message inMessage = mapper.readValue(ctx.message(), Message.class);

                    switch (inMessage.getType()) {
                        case VerifyBlender:
//                        BlenderFrames blenderFrames = mapper.readValue(inMessage.getPayload(), BlenderFrames.class);
                            BlenderProjectInfo blenderProjectInfo = (BlenderProjectInfo) inMessage.getData();
                            System.out.println("[Blender Frames]: " + blenderProjectInfo.getStartFrame() + ", " + blenderProjectInfo.getEndFrame());

                            break;
                        case VerifyPremiere:
                            break;
                        case VerifyAE:
                            break;
                        case RenderBlender:
                            break;
                        case RenderME:
                            break;
                        default:
                            System.out.println("[Message Parsing Error] Unknown type " + inMessage.getType().toString());
                            break;
                    }
                } catch (Error potentialError) { // if not, try to see if it's a new node handshake
                    try {
                        NodeHandshakeInfo handshakeInfo = mapper.readValue(ctx.message(), NodeHandshakeInfo.class);

                        // fill in rest of node handshake
                        Node newNode = serverMeta.getServerNodeWithID(handshakeInfo.getCtxSessionID());
                        newNode.setNodeName(handshakeInfo.getNodeName());
                        newNode.setPowerIndex(handshakeInfo.getPowerIndex());

                        System.out.println("[New Node]: " + newNode.getNodeName() + "!");
                    } catch (Error realError) { // now we actually have a problem
                        System.out.println("[WS Message Error]: " + realError.getMessage());
                    }
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
            // Get info
            ServerUpdateInfo updateInfo = new ServerUpdateInfo(serverMeta.getJobQueue(), serverMeta.getVerifyingQueue(), serverMeta.getServerNodes());
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
