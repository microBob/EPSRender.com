package com.kyang.epsrender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyang.epsrender.Enums.JobStatus;
import com.kyang.epsrender.Enums.MessageType;
import com.kyang.epsrender.Enums.NodeStatus;
import com.kyang.epsrender.Enums.ProjectType;
import com.kyang.epsrender.models.messages.*;
import com.kyang.epsrender.models.server.Meta;
import com.kyang.epsrender.models.server.Node;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Objects;

public class EPSRenderCore {

    // Global vars
    private static final Meta serverMeta = new Meta();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> config.addStaticFiles("/public")).start(7000);

        app.get("/test", ctx -> ctx.result("Test again"));


        // SECTION: demo items
//        serverMeta.addServerNode(new Node("Tester 1", "kjasdhf9ia768927huisdaf9", 3));
        BlenderProjectInfo blenderInfo = new BlenderProjectInfo(true);
        blenderInfo.setFramesCompleted(5);
        JobRequest right_here = new JobRequest("kyang@eastsideprep.org", ProjectType.BlenderCycles, "Right_Here",
                blenderInfo);
//        right_here.setJobStatus(JobStatus.Rendering);
        serverMeta.addToVerifyingQueue(right_here);
//        JobRequest another = new JobRequest("kyang@eastsideprep.org", ProjectType.BlenderCycles, "another",
//        blenderInfo);
//        another.setJobStatus(JobStatus.Queued);
//        serverMeta.addToJobQueue(another);


        // SECTION: Open communication
        app.ws("/communication", ws -> {
            ws.onConnect(ctx -> {
                System.out.println("[WS Connection]:\t" + ctx.getSessionId());

                // create new ctx hash with ctx id
                serverMeta.addToCtxIdHash(ctx.getSessionId(), ctx);

                // create blank handshake and send ID
                Message handshake = new Message(MessageType.NewNodeHandshake, null);
                ctx.send(mapper.writeValueAsString(handshake));
                ctx.send(ctx.getSessionId());
            });
            ws.onClose(ctx -> {
                // get node fro ctx ID
                Node disNode = serverMeta.getServerNodeWithID(ctx.getSessionId());
                // print disconnect
                System.out.println("[Node Disconnected]:\t" + disNode.getNodeName() + " disconnected!");
                // set status to offline
                disNode.setNodeStatus(NodeStatus.Offline);

                // handle job (if died with job)
                if (disNode.getCurrentJob() != null) {
                    disNode.setCurrentJob(null);
                }
            });
            ws.onMessage(ctx -> {
                System.out.println("[WS Message]:\t" + ctx.message());

                if (ctx.message().contains("{\"ctxSessionID\"")) { // check if it's a new node handshake
                    try {
                        // try to read the handshake
                        NodeHandshakeInfo handshakeInfo = mapper.readValue(ctx.message(), NodeHandshakeInfo.class);

                        // try to grab a node with name (check for preexistence)
                        Node preExisting = serverMeta.getServerNodeWithName(handshakeInfo.getNodeName());
                        if (preExisting != null) { // it's a returning node (by name)
                            System.out.println("[Returning Node]:\t" + preExisting.getNodeName());
                            serverMeta.getCtxIdHash().remove(preExisting.getCtxSessionID());
                            preExisting.setCtxSessionID(handshakeInfo.getCtxSessionID());
                            preExisting.setNodeStatus(NodeStatus.Ready);
                        } else { // brand new node? create a new node and register
                            Node newNode = new Node(handshakeInfo.getNodeName(), handshakeInfo.getCtxSessionID(),
                                    handshakeInfo.getPowerIndex());
                            serverMeta.addServerNode(newNode);
                            System.out.println("[New Node]:\t" + newNode.getNodeName() + "!");
                        }

                        InitNextJob(); // prompt next job
                    } catch (Error error) { // wot
                        System.out.println("[WS Message Error]:\t" + error.getMessage());
                    }
                } else { // more typically, it's a job message
                    try {
                        Message inMessage = mapper.readValue(ctx.message(), Message.class);

                        switch (inMessage.getType()) {
                            case VerifyBlender: // returned a verified blender job
                                if (inMessage.getData().getVerified()) { // and it's good
                                    System.out.println("[Blender Verify]:\tProject " + inMessage.getData().getProjectFolderName() + " Verified!");
                                    JobRequest refJob =
                                            serverMeta.getJobFromVerifyingQueueWithName(inMessage.getData().getProjectFolderName());

                                    // update info
                                    refJob.setVerified(true);
                                    if (refJob.getBlenderInfo().getUseAllFrames()) {
                                        refJob.getBlenderInfo().setStartFrame(inMessage.getData().getBlenderInfo().getStartFrame());
                                        refJob.getBlenderInfo().setEndFrame(inMessage.getData().getBlenderInfo().getEndFrame());
                                    }

                                    refJob.setJobStatus(JobStatus.Queued);
                                    refJob.getBlenderInfo().setFramesCompleted(0);
                                    refJob.getBlenderInfo().setRenderers(0);

                                    // add to Job Queue and remove from verify
                                    serverMeta.addToJobQueue(refJob);
                                    serverMeta.removeJobFromVerifyingQueueWithName(refJob.getProjectFolderName());

                                    // ready to branch out to render que
                                    for (int frame = refJob.getBlenderInfo().getStartFrame(); frame <= refJob.getBlenderInfo().getEndFrame(); frame++) {
                                        refJob.getBlenderInfo().setFrameNumber(frame);
                                        serverMeta.addToBlenderQueue(refJob);
                                    }

                                    // reset node that did verifying
                                    Node incomingNode = serverMeta.getServerNodeWithID(ctx.getSessionId());
                                    incomingNode.setCurrentJob(null);
                                    incomingNode.setNodeStatus(NodeStatus.Ready);

                                    System.out.println("[Verify Queue Count]:\t" + serverMeta.getVerifyingQueue().size());
                                    System.out.println("[Job Queue Count]:\t" + serverMeta.getJobQueue().size());
                                    System.out.println("[Blender Queue Count]:\t"+serverMeta.getBlenderQueue().size());
                                } else { // and it's not
                                    System.out.println("[Blender Verify]:\tProject " + inMessage.getData().getProjectFolderName() + " Failed!");
                                    // send emails

                                }

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
                                System.out.println("[Message Parsing Error]:\tUnknown type " + inMessage.getType().toString());
                                break;
                        }
                    } catch (Error error) {
                        System.out.println("[WS Message Error]:\t" + error.getMessage());
                    }
                }
            });
        });


        //SECTION: Login
        app.get("/login", ctx -> {
            System.out.println("[Route Requested]:\t/login");

            String returnAddress = "http://" + ctx.host() + "/complete_login";
            String url = "http://epsauth.azurewebsites.net/login?url=" + returnAddress + "&loginparam=userEmail";

            ctx.redirect(url);
            ctx.result("Running login");
        });
        app.get("/complete_login", ctx -> {
            System.out.println("[Route Requested]:\t/complete_login");

            registerUser(ctx, ctx.queryParam("userEmail"));

            ctx.redirect("/");
        });

        app.get("/update_login_stat", ctx -> {
            System.out.println("[Route Requested]:\t/update_login_stat");
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
            System.out.println("[Route Requested]:\t/add_new_job");

            String userEmail = getUserEmail(ctx);
            int projectTypeInt = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("projectType")));
            String projectFolderName = ctx.queryParam("projectFolderName");

            BlenderProjectInfo blenderProjectInfo = null;
            if (projectTypeInt > 1) {
                boolean blenderUseAllFrames = Boolean.parseBoolean(ctx.queryParam("blenderUseAll"));
                if (!blenderUseAllFrames) {
                    int blenderStartFrame = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("blenderStartFrame"
                    )));
                    int blenderEndFrame = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("blenderEndFrame")));

                    blenderProjectInfo = new BlenderProjectInfo(blenderStartFrame, blenderEndFrame);
                } else {
                    blenderProjectInfo = new BlenderProjectInfo(true);
                }
            }

            JobRequest newJobRequest = new JobRequest(userEmail, ProjectType.values()[projectTypeInt],
                    projectFolderName, blenderProjectInfo);

            serverMeta.addToVerifyingQueue(newJobRequest);

            InitNextJob();

            System.out.println("[Sent Job to verification]:\t" + newJobRequest.getProjectFolderName());
        });
        // SECTION ^: Adding a new Job

        // SECTION: Server Status
        app.get("/update_stat", ctx -> {
            System.out.println("[Route Requested]:\t/update_stat");
            // Get info
            StatusUpdateInfo updateInfo = new StatusUpdateInfo(serverMeta.getJobQueue(),
                    serverMeta.getVerifyingQueue(), serverMeta.getServerNodes());
            // Send it over
            String resultString = mapper.writeValueAsString(updateInfo);
            ctx.result(resultString);
        });
        // SECTION ^: Server Status

    }

    // SECTION: Internal methods
    private static void InitNextJob() throws JsonProcessingException {
        Node readyServerNode = serverMeta.getReadyServerNode();
        if (readyServerNode != null) {
            Message jobMsg = readyServerNode.getNextJob();
            if (jobMsg != null) {
                String jsonMsg = mapper.writeValueAsString(jobMsg);

                serverMeta.getCtxIdHash().get(readyServerNode.getCtxSessionID()).send(jsonMsg);
            } else {
                System.out.println("[Init Next Job]:\tNo jobs available for now");
            }
        }
    }
    // SECTION ^: Internal methods

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
