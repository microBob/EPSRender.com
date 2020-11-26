# HTML (Front-end)

## Baked IDs and Values

* **`new-job-request-form`**

	* form element that holds all new request elements
	
* **`login-row`**

	* shown when logged out
	* contains "sign in with Microsoft" button and help

* **`logged-in-row`**

	* shown when logged in

	* can show rest of job form when displayed

	* contains "Currently logged in as: " + "not you?"

	* **`current-user-lbl`**

	  * just the user part of the label
  * "Currently logged in as **user**"
	
* **`post-login-form`**
	
	* div with the rest of the job request form
	* shown when logged in
	* project type drop-down: **`type-select`**
	  	* **`0`** = Premiere
	  	* **`1`** = After Effects
	  	* **`2`** = Cycles
	  	* **`3`** = EEVEE
	  	* 2/3 (blender) will show frame selection settings
	* **`blender-render-settings`**
	  	* div with blender frame selections settings
	  	* number input: **`start-frame-input`**
	  	* number input: **`end-frame-input`**
	  	* check-box: **`use-all-frames-check-box`**
	  		* disable above two when checked
	  		* registers all frames to be rendered
	* **`project-folder-name-input`**
	  	* project folder name
	  	* needs to be greater than 1 character
	* **`warning-error-p`**
		* show default alert and any form validation errors
			* it is the `<span>` that contains the error message, not the whole `<p>` itself
	  
	* form submit button: **`job-submit-btn`**
	
	* **`job-que-table`**
	
	  * displayed the job queue
	
	  * | User | Type | Time Added | Status                       |
	  	| ---- | ---- | ---------- | ---------------------------- |
	  	|      |      |            | **`x`**/**`total`** rendered |
	  	|      |      |            | Verifying                    |
	  	|      |      |            | Place **`#`** in Queue       |
	  	
	  * CSS colors
	
	    * rendering: `text-primary`
	    * Verifying: `text-warning`
	    * Queued: `text-info`
	
	* **`server-status-table`**
	
	  * displayed the nodes and their states
	
	  * sorted `ready -> rendering -> offline`
	
	  	* | Node | Status    |
	  		| ---- | --------- |
	  		|      | Offline   |
	  		|      | Ready     |
	  		|      | Rendering |
	  	
	  * CSS colors
	
	    * Offline: `text-danger`
	    * Ready: `text-success`
	    * Rendering: `text-warning`
	
	* **`update-server-info-btn`**
	
	  * pull updates from server to repopulate the tables

## Useful CSS class definitions

| Use                | Notes                            | CSS                |
| ------------------ | -------------------------------- | ------------------ |
| Hide elements      |                                  | **`d-none`**       |
| Warning Background | yellow (use `bg` for background) | **`bg-warning`**   |
| primary            | blue                             | **`text-primary`** |
| info               | purple                           | **`text-info`**    |
| warning            | yellow                           | **`text-warning`** |
| danger             | red                              | **`text-danger`**  |
| success            | green                            | **`text-success`** |

# Java (Server)

## Routes

| Name                     | What it does                                                 | Return                                                       |
| ------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **`/test`**              | simple text return test                                      | "Test again"                                                 |
| **`/login`**             | redirects to EPSAuth                                         | "Running login"<br />redirect to completion route            |
| **`/complete_login`**    | return redirect from `/login`. <br />Attaches the user to a session attribute | redirect to `/`                                              |
| **`/update_login_stat`** | checks for current logged in user                            | session user *name* <br />"!invalid!" if invalid for some reason<br />"" if null |
| **`/update_stat`**       | updates displayed info on Job Que and Server Status          | `ServerUpdateInfo` JSON                                      |
|                          |                                                              |                                                              |
|                          |                                                              |                                                              |

## Session attributes

| Name            | What it does          | Expected Value           |
| --------------- | --------------------- | ------------------------ |
| **`useremail`** | holds user full email | `kyang@eastsideprep.org` |
|                 |                       |                          |
|                 |                       |                          |

## Server Enums (`.Enums.`)

| Name             | Enum Name     | Values                                                       | Notes                                                        |
| ---------------- | ------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Project Type     | `ProjectType` | `0 = PremierePro`<br />`1 = AfterEffects`<br />`2 = BlenderCycles`<br />`3 = BlenderEEVEE` |                                                              |
| Job Status       | `JobStatus`   | `0 = Verifying`<br />`1 = Queued`<br />`2 = Rendering`<br />`3 = Necro` | Necro is for jobs from died Nodes                            |
| Node Power Index | `PowerIndex`  | `0 = Ultra`<br />`1 = High`<br />`2 = Mid`<br />`3 = Low`    | Tower 1<br />VR 1<br />Corsair 1, 2<br />DELL 1, 2, 3        |
| Node Status      | `NodeStatus`  | `0 = Ready`<br />`1 = Rendering`<br />`2 = Offline`          |                                                              |
| Message Type     | `MessageType` | `0 = VerifyBlender`<br />`1 = VerifyPremiere`<br />`2 = VerifyAE`<br />`3 = RenderBlender`<br />`4 = RenderME`<br />`5 = NewNodeHandshake` | Verify primarily is used to identify if a file can be found<br />Blender verify includes getting frames<br /><br />messages received from node means "done"<br /><br /> |

## Models

### Server Meta (`.models.Meta`)

| Property        | Variable Name    | Type                    | Notes                                       |
| --------------- | ---------------- | ----------------------- | ------------------------------------------- |
| Job Queue       | `jobQueue`       | `ArrayList<JobRequest>` | holds all current requests their statuses   |
| Verifying Queue | `verifyingQueue` | `ArrayList<JobRequest>` | copy of an unverified request in `jobQueue` |
| Blender Queue   | `blenderQueue`   | `ArrayList<JobRequest>` | holds verified blender frames               |
| Server nodes    | `serverNodes`    | `ArrayList<Node>`       |                                             |
### Server Node (`.models.Node`)

| Property           | Variable Name  | Type         | Notes                                   |
| ------------------ | -------------- | ------------ | --------------------------------------- |
| Node name          | `nodeName`     | `String`     | Used to display<br />must be unique     |
| Context session ID | `ctxSessionID` | `String`     | acquired when first connected to Server |
| Power Index        | `powerIndex`   | `PowerIndex` |                                         |
| Node status        | `nodeStatus`   | `NodeStatus` |                                         |
| Working on Job     | `currentJob`   | `JobRequest` |                                         |

### Message (`.models.Message`)

| Properties       | Variable Name | Type          | Notes                                      |
| ---------------- | ------------- | ------------- | ------------------------------------------ |
| Message Type     | `type`        | `MessageType` |                                            |
| Job Request Data | `jobRequest`  | `JobRequest`  | use type to determine what to do with data |

### Job Request (`.models.JobRequest`)

| Property                                                     | Variable Name       | Type                 | Notes                                |
| ------------------------------------------------------------ | ------------------- | -------------------- | ------------------------------------ |
| *<u>{Input parameter}</u>*<br />User's email                 | `useremail`         | `String`             | Identity of sender                   |
| *<u>{Input parameter}</u>*<br />Project Type                 | `projectType`       | `ProjectType`        |                                      |
| <u>*{Input parameter}*</u><br />Project Folder Name          | `projectFolderName` | `String`             |                                      |
| <u>*{Input parameter}*</u><br />Blender project rendering info | `blenderInfo`       | `BlenderProjectInfo` | combination data for blender renders |
| Verified                                                     | `verified`          | `boolean`            | Only verified requests can be queued |
| Verification Error Msg                                       | `errorMsg`          | `String`             | built by node                        |
| Time Added                                                   | `timeAdded`         | `String`             | Java Date converted to string        |
| Job Status                                                   | `jobStatus`         | `JobStatus`          |                                      |

### Blender Project Info (`.models.BlenderProjectInfo`)

| Property            | Variable Name     | Type      | Notes                                         |
| ------------------- | ----------------- | --------- | --------------------------------------------- |
| Start Frame         | `startFrame`      | `int`     |                                               |
| End Frame           | `endFrame`        | `int`     |                                               |
| Use all frames?     | `useAllFrames`    | `boolean` |                                               |
| Project Filename    | `fileName`        | `String`  | acquired after verification                   |
| Frame Number        | `frameNumber`     | `int`     | Frame number this job represents<br />        |
| Frames completed    | `framesCompleted` | `int`     | `= (endFrame - startFrame) - `frames in queue |
| Number of renderers | `renderers`       | `int`     | number of nodes working on this               |

### Server Update Info (`.models.ServerUpdateInfo`)

| Property      | Variable Name | Type                    | Notes                                    |
| ------------- | ------------- | ----------------------- | ---------------------------------------- |
| Job Queue     | `jobQueue`    | `ArrayList<JobRequest>` | merge of `jobQueue` and `verifyingQueue` |
| Server Status | `serverStat`  | `ArrayList<Node>`       | sorted `ready -> rendering -> offline`   |

### Node handshake Info (`.models.NodeHandshakeInfo`)

| Property    | Variable Name | Type         | Notes |
| ----------- | ------------- | ------------ | ----- |
| Node name   | `nodeName`    | `String`     |       |
| Power index | `powerIndex`  | `PowerIndex` |       |



## Server logic

### Get new Job Request (from Front-end)

1. parse data into a `JobRequest`
	1. set `timeAdded` to format `MM/dd/yyyy hh:mm:ss aa`
2. copy `JobRequest` to `verifyingQueue`

### If a node is Ready

1. pull the oldest item from `verifyingQueue` and send to node to verify
	1. mark node rendering with job
2. check `jobQueue`
	1. check `0` index for necro jobs (jobs from dead nodes)
	2. if there is a rendering blender job with 0 working nodes, send next frame of this project from `blenderQueue` to node
		1. mark node rendering with job
	3. pick the next ME job
		1. mark node rendering with job
	4. pick the next frame in the oldest blender job
		1. mark node rendering with job

### Received verify (node completed verification)

1. if bad
	1. send email to user and copy `errorMsg`
	2. remove `JobRequest` from queues
2. if good
	1. if blender
		1. generate other frames (`JobRequests`) based on new data into `blenderQueue`
3. set `JobRequest` `jobStatus` to queued
4. check if a node is ready to render

### Received render (node completed render)

1. mark node as ready
2. if blender
	1. `++` to `framesCompleted`
	2. if `framesCompleted = (endFrame - startFrame + 1)`
		1. send email to user on completion
		2. remove `JobRequest` from queues
	3. else, remove frame from `blenderQueue`
3. if ME
	1. send email to user on completion
	2. remove `JobRequest` from queues

### New node handshake (when a new node connects)

1. create a new (empty) node with `ctxSessionID`
2. send `NewNodeIntro` message to node
3. parse response and fill out rest of `Node`

# NodeJS (Client)

## Special Items

### Get the start and end frames of a blender project

bash command: `blender -b {blender file to render} --python {path/to/}getFrames.py | grep -w 'EPRenderInterestedFrames:'`

* returns `EPRenderInterestedFrames: start,end`
