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
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EPSRenderCore {

    //// SECTION: Global vars
    private static final Meta serverMeta = new Meta();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String mailgunDomain = "sandbox502a3c858cc64b8a963c2a3286770a7b.mailgun.org";
    private static final String mailgunAPIKey = "3f767055f38819fcd64755f7faea6adf-4d640632-a33fff72";
    //// SECTION ^: Global vars


    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> config.addStaticFiles("/public")).start(7000);

        app.get("/test", ctx -> ctx.result("Test again"));


        //// SECTION: Demo items
//        BlenderProjectInfo iconInfo = new BlenderProjectInfo(1, 30);
//        BlenderProjectInfo marbleInfo = new BlenderProjectInfo(24, 44);
//        JobRequest icon = new JobRequest("kyang@eastsideprep.org", ProjectType.BlenderCycles, "EPS Render Server " +
//                "Icon", iconInfo);
//        JobRequest marbles = new JobRequest("kyang@eastsideprep.org", ProjectType.BlenderCycles, "Marbles",
//        marbleInfo);

//        JobRequest create_edit_sequence = new JobRequest("kyang@eastsideprep.org", ProjectType.PremierePro, "create" +
//                "-edit-sequence", null);

//        serverMeta.addToVerifyingQueue(icon);
//        serverMeta.addToVerifyingQueue(marbles);
//        serverMeta.addToVerifyingQueue(create_edit_sequence);

        //// SECTION ^: Demo items

        //// SECTION: Node ping
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        Runnable pingTask = () -> {
            for (Node n : serverMeta.getAliveNodes()) {
                serverMeta.getCtxIdHash().get(n.getCtxSessionID()).send("KeepAlive");
            }

            // Push node to do work if idle and somehow didn't pick up a job
            if (serverMeta.getServerNodes().stream().filter(node -> node.getNodeStatus().equals(NodeStatus.Ready)).findFirst().orElse(null) != null) {
                try {
                    InitNextJob();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        };
        executorService.scheduleAtFixedRate(pingTask, 0, 55, TimeUnit.SECONDS);
        //// SECTION ^: Node ping

        //// SECTION: Communication
        app.ws("/communication", ws -> {
            ws.onConnect(ctx -> { /// initial connection with node
                System.out.println("[WS Connection]:\t" + ctx.getSessionId());

                // Create new ctx hash with ctx id
                serverMeta.addToCtxIdHash(ctx.getSessionId(), ctx);

                // create blank handshake and send ID
                Message handshake = new Message(MessageType.NewNodeHandshake, null);
                ctx.send(mapper.writeValueAsString(handshake));
                ctx.send(ctx.getSessionId());
            });
            ws.onClose(ctx -> { /// when a node dies
                // get node from ctx ID
                Node disNode = serverMeta.getServerNodeWithID(ctx.getSessionId());
                // print disconnect
                System.out.println("[Node Disconnected]:\t" + disNode.getNodeName() + " disconnected!");
                // set status to offline
                disNode.setNodeStatus(NodeStatus.Offline);

                // handle job (if died with job)
                JobRequest currentJob = disNode.getCurrentJob();
                if (currentJob != null) {
                    currentJob.setJobStatus(JobStatus.Necro);
                    serverMeta.addToJobQueueBeginning(currentJob);
                    disNode.setCurrentJob(null);
                    InitNextJob();
                }
            });
            ws.onMessage(ctx -> {
                System.out.println("[WS Message]:\t" + ctx.message());

                if (!ctx.message().equals("StayingAlive")) {
                    if (ctx.message().contains("{\"ctxSessionID\"")) { // check if it's a new node handshake
                        try {
                            // try to read the handshake
                            NodeHandshakeInfo handshakeInfo = mapper.readValue(ctx.message(), NodeHandshakeInfo.class);

                            // try to grab a node with name (check for preexistence)
                            Node preExisting = serverMeta.getServerNodeWithName(handshakeInfo.getNodeName());
                            if (preExisting != null) { /// it's a returning node (by name)
                                System.out.println("[Returning Node]:\t" + preExisting.getNodeName());
                                serverMeta.getCtxIdHash().remove(preExisting.getCtxSessionID());
                                preExisting.setCtxSessionID(handshakeInfo.getCtxSessionID());
                                preExisting.setNodeStatus(NodeStatus.Ready);
                            } else { /// brand new node? create a new node and register
                                Node newNode = new Node(handshakeInfo.getNodeName(), handshakeInfo.getCtxSessionID(),
                                        handshakeInfo.getPowerIndex());
                                serverMeta.addServerNode(newNode);
                                System.out.println("[New Node]:\t" + newNode.getNodeName() + "!");
                            }
                        } catch (Error error) { /// wot
                            System.out.println("[WS Message Error]:\t" + error.getMessage());
                        }
                    } else { /// more typically, it's a job message
                        try {
                            Message inMessage = mapper.readValue(ctx.message(), Message.class);
                            JobRequest refJob =
                                    serverMeta.getJobFromVerifyingQueueWithName(inMessage.getData().getProjectFolderName());
                            Node refNode = serverMeta.getServerNodeWithID(ctx.getSessionId());

                            switch (inMessage.getType()) {
                                case VerifyBlender: // returned a verified blender job
                                    if (inMessage.getData().getVerified()) { // and it's good
                                        System.out.println("[Blender Verify]:\tProject " + inMessage.getData().getProjectFolderName() + " Verified!");

                                        // update info
                                        refJob.setVerified(true);
                                        if (refJob.getBlenderInfo().getUseAllFrames()) {
                                            refJob.getBlenderInfo().setStartFrame(inMessage.getData().getBlenderInfo().getStartFrame());
                                            refJob.getBlenderInfo().setEndFrame(inMessage.getData().getBlenderInfo().getEndFrame());
                                        }

                                        refJob.getBlenderInfo().setFileName(inMessage.getData().getBlenderInfo().getFileName());

                                        refJob.setJobStatus(JobStatus.Queued);
                                        refJob.getBlenderInfo().setFramesCompleted(0);
                                        refJob.getBlenderInfo().clearRenderers();

                                        // add to Job Queue and remove from verify
                                        serverMeta.addToJobQueue(refJob);

                                        /// duplicate out into blender render queue
                                        for (int frame = refJob.getBlenderInfo().getStartFrame(); frame <= refJob.getBlenderInfo().getEndFrame(); frame++) {
                                            BlenderProjectInfo newJobBlender =
                                                    new BlenderProjectInfo(refJob.getBlenderInfo().getStartFrame(),
                                                            refJob.getBlenderInfo().getEndFrame());
                                            newJobBlender.setFrameNumber(frame);
                                            newJobBlender.setFileName(refJob.getBlenderInfo().getFileName());

                                            JobRequest newJob = new JobRequest(refJob.getUserEmail(),
                                                    refJob.getProjectType(), refJob.getProjectFolderName(),
                                                    newJobBlender);
                                            newJob.setJobStatus(JobStatus.Queued);
                                            newJob.setVerified(true);

                                            serverMeta.addToBlenderQueue(newJob);
                                        }
                                    } else { /// and if it's not
                                        System.out.println("[Blender Verify]:\tProject \"" + inMessage.getData().getProjectFolderName() + "\" Failed!");
                                    }

                                    /// Complete verification
                                    serverMeta.removeJobFromVerifyingQueueWithName(refJob.getProjectFolderName());
                                    // reset node that did verifying
                                    refNode.setCurrentJob(null);
                                    refNode.setNodeStatus(NodeStatus.Ready);

                                    JsonNode mailResponse = sendMessage(inMessage.getData());

                                    System.out.println("[Email Response]:\t" + mailResponse.toString());
                                    break;
                                case VerifyPremiere:
                                    handleVerifiedME(inMessage, refJob, refNode, "[Premiere Verify]:\tProject \"");
                                    break;
                                case VerifyAE:
                                    handleVerifiedME(inMessage, refJob, refNode, "[After Effects Verify]:\tProject \"");
                                    break;
                                case RenderBlender:
                                    // get correct ref-job
                                    refJob =
                                            serverMeta.getJobFromJobQueueWithName(inMessage.getData().getProjectFolderName());

                                    boolean stillWorking = true;

                                    if (inMessage.getData().getVerified()) { /// finish out a blender render
                                        System.out.println("[Blender Render]:\t\"" + refJob.getProjectFolderName() +
                                                "\" " +
                                                "Frame completed! " + (refJob.getBlenderInfo().getFramesCompleted() + 1) + " / "
                                                + ((refJob.getBlenderInfo().getEndFrame() - refJob.getBlenderInfo().getStartFrame()) + 1));
                                        refJob.getBlenderInfo().addFramesCompleted();
                                        refJob.getBlenderInfo().removeRenderers();
                                        serverMeta.removeBlenderJobWithNameAndFrame(refJob.getProjectFolderName(),
                                                inMessage.getData().getBlenderInfo().getFrameNumber());

                                        stillWorking =
                                                refJob.getBlenderInfo().getFramesCompleted() < (refJob.getBlenderInfo().getEndFrame() - refJob.getBlenderInfo().getStartFrame()) + 1;
                                    } else { /// kill job if render failed
                                        System.out.println("[Blender Render]:\t\"" + inMessage.getData().getProjectFolderName() + "\" Render failed!");
                                        serverMeta.removeJobFromJobQueWithName(refJob.getProjectFolderName());
                                        serverMeta.removeBlenderJobWithName(refJob.getProjectFolderName());
                                    }

                                    refNode.setCurrentJob(null);
                                    refNode.setNodeStatus(NodeStatus.Ready);

                                    if (!stillWorking) {
                                        serverMeta.removeJobFromJobQueWithName(refJob.getProjectFolderName());
                                        mailResponse = sendMessage(inMessage.getData());
                                        System.out.println("[Email Response]:\t" + mailResponse.toString());
                                    }
                                    break;
                                case RenderME:
                                    if (inMessage.getData().getVerified()) {
                                        System.out.println("[ME Render]:\t\"" + inMessage.getData().getProjectFolderName() + "\" Completed!");
                                    } else {
                                        System.out.println("[ME Render]:\t\"" + inMessage.getData().getProjectFolderName() + "\" Failed!");
                                    }

                                    // remove job from queue
                                    serverMeta.removeJobFromJobQueWithName(inMessage.getData().getProjectFolderName());

                                    // reset node
                                    refNode.setCurrentJob(null);
                                    refNode.setNodeStatus(NodeStatus.Ready);

                                    mailResponse = sendMessage(inMessage.getData());
                                    System.out.println("[Email Response]:\t" + mailResponse.toString());
                                    break;
                                default:
                                    System.out.println("[Message Parsing Error]:\tUnknown type " + inMessage.getType().toString());
                                    break;
                            }
                        } catch (Error error) {
                            System.out.println("[WS Message Error]:\t" + error.getMessage());
                        }
                    }

                    InitNextJob();
                }
            });
        });
        //// SECTION ^: Communication


        ////SECTION: Login
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
            /// If it is a valid EPS email
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
        //// SECTION ^: Login

        //// SECTION: Adding a new Job
        app.put("/add_new_job", ctx -> {
            System.out.println("[Route Requested]:\t/add_new_job");

            /// Read in param data
            String userEmail = getUserEmail(ctx);
            int projectTypeInt = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("projectType")));
            String projectFolderName = ctx.queryParam("projectFolderName");

            // For blender
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

            /// Create job and add to verify
            JobRequest newJobRequest = new JobRequest(userEmail, ProjectType.values()[projectTypeInt],
                    projectFolderName, blenderProjectInfo);

            serverMeta.addToVerifyingQueue(newJobRequest);

            // Prompt nodes to pick it up
            InitNextJob();

            System.out.println("[Sent Job to verification]:\t" + newJobRequest.getProjectFolderName());
        });
        //// SECTION ^: Adding a new Job

        //// SECTION: Server Status
        app.get("/update_stat", ctx -> {
            System.out.println("[Route Requested]:\t/update_stat");
            // Get info
            StatusUpdateInfo updateInfo = new StatusUpdateInfo(serverMeta.getJobQueue(),
                    serverMeta.getVerifyingQueue(), serverMeta.getServerNodes());
            // Send it over
            String resultString = mapper.writeValueAsString(updateInfo);
            ctx.result(resultString);
        });
        //// SECTION ^: Server Status

    }

    //// SECTION: Internal methods
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

    private static void handleVerifiedME(Message inMessage, JobRequest refJob, Node refNode, String s) throws UnirestException {
        JsonNode mailResponse;
        if (inMessage.getData().getVerified()) {
            System.out.println(s + inMessage.getData().getProjectFolderName() + "\" Verified!");

            // update job info
            refJob.setVerified(true);
            refJob.setJobStatus(JobStatus.Queued);

            // add to Job Queue and remove from verify
            serverMeta.addToJobQueue(refJob);
        } else {
            System.out.println(s + inMessage.getData().getProjectFolderName() + "\" Failed!");
        }

        serverMeta.removeJobFromVerifyingQueueWithName(refJob.getProjectFolderName());

        /// reset node that did verifying
        refNode.setCurrentJob(null);
        refNode.setNodeStatus(NodeStatus.Ready);

        mailResponse = sendMessage(inMessage.getData());
        System.out.println("[Email Response]:\t" + mailResponse.toString());
    }

    private static JsonNode sendMessage(JobRequest job) throws UnirestException {
        String subject = "";
        String html = "<html>";
        String tag = "";
        if (job.getJobStatus().equals(JobStatus.Queued)) {
            if (job.getVerified()) {
                subject = "\"" + job.getProjectFolderName() + "\" Verified!";

                html += "<h2>Hello! This message is to let you know <u>\"" + job.getProjectFolderName() + "\"</u" +
                        "> has <u>successfully been verified</u>!</h2>";
                html += "<p>Your project has been placed in the render queue. You will be notified again when " +
                        "rendering " +
                        "has completed.</p>";

                tag = "Verify ";
            } else {
                subject = "\"" + job.getProjectFolderName() + "\" Failed Verification!";

                html += "<h2>Hello! This message is to let you know <u>\"" + job.getProjectFolderName() + "\"</u" +
                        "> has <u>failed verification</u>!</h2>";
                html += "<h3>Failure reason: " + job.getErrorMsg() + "</h3>";
                html += "<p>Your project has been removed from the server. Please make appropriate changes and " +
                        "resubmit" +
                        ".</p>";
                html += "<p>If you believe there has been a mistake, please <a href=\"mailto:kyang@eastsideprep" +
                        ".org\">contact support</a> for assistance.</p>";

                if (job.getErrorMsg().contains("A server node")) {
                    tag = "Verifying Error ";
                } else {
                    tag = "Verified Reject ";
                }
            }
        } else if (job.getJobStatus().equals(JobStatus.Rendering)) {
            if (job.getVerified()) {
                subject = "\"" + job.getProjectFolderName() + "\" Render Complete!";

                html += "<h2>Hello! This message is to let you know <u>\"" + job.getProjectFolderName() + "\"</u" +
                        "> has <u>successfully been rendered</u>!</h2>";
                html += "<p>Your render is in the folder \"EPSRenderServerOutput\" located in your uploaded project " +
                        "folder.</p>";

                tag = "Rendered ";
            } else {
                subject = "\"" + job.getProjectFolderName() + "\" Render Failed!";

                html += "<h2>Hello! This message is to let you know <u>\"" + job.getProjectFolderName() + "\"</u" +
                        "> has <u>failed to render</u>!</h2>";
                html += "<h3>Failure reason: " + job.getErrorMsg() + "</h3>";
                html += "<p>Your project has been removed from the server. Please make appropriate changes and " +
                        "resubmit.</p>";
                html += "<p>If you believe there has been a mistake, please <a href=\"mailto:kyang@eastsideprep" +
                        ".org\">contact support</a> for assistance.</p>";

                tag = "Failed Render ";
            }
        }

        switch (job.getProjectType()) {
            case BlenderCycles:
                tag += "Blender Cycles";
                break;
            case BlenderEEVEE:
                tag += "Blender EEVEE";
                break;
            case AfterEffects:
                tag += "After Effects";
                break;
            case PremierePro:
                tag += "Premiere Pro";
                break;
            default:
                break;
        }

        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + mailgunDomain + "/messages")
                .basicAuth("api", mailgunAPIKey)
                .field("from", "EPSRender Server Notification <kyang@eastsideprep.org>")
                .field("to", job.getUserEmail())
                .field("subject", subject)
                .field("html", html)
                .field("o:tag", tag)
                .asJson();

        return request.getBody();
    }
    //// SECTION ^: Internal methods

    //// SECTION: Session management
    private static void registerUser(Context ctx, String userEmail) {
        if (ctx.sessionAttribute("userEmail") == null) {
            ctx.sessionAttribute("userEmail", userEmail);
        }
    }

    // return the stored userEmail attribute
    private static String getUserEmail(Context ctx) {
        return ctx.sessionAttribute("userEmail");
    }
    //// SECTION ^: Session management


    //// SECTION: Delegate Methods
    public static Meta getServerMeta() {
        return serverMeta;
    }
    //// SECTION ^: Delegate Methods
}
