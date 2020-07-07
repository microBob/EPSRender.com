# HTML (Front-end)

## Baked IDs and Values

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
	  	* 3/4 (blender) will show frame selection settings
	  * **`blender-render-settings`**
	  	* div with blender frame selections settings
	  	* number input: **`start-frame-input`**
	  	* number input: **`end-frame-input`**
	  	* check-box: **`use-all-frames-check-box`**
	  		* disable above two when checked
	  		* registers all frames to be rendered
	  * URL input: **`project-location-input`**
	  	* URL string to project location
	  	* minimum URL (to Student drive): `\\drives\Students\`
	  * **`warning-error-p`**
	  	* show default alert and any form validation errors
  	* is the `<span>` that contains the error message, not the whole `<p>` itself
	  * form submit button: **`job-submit-btn`**

	* **`job-que-table`**

		* displayed the job queue
	
		* | User | Type | Time Added | Status                       |
			| ---- | ---- | ---------- | ---------------------------- |
			|      |      |            | **`x`**/**`total`** rendered |
		|      |      |            | Rendering                    |
			|      |      |            | Place **`#`** in Queue       |

	* **`server-status-table`**

		* displayed the nodes and their states

		* sorted: `online` -> `rendering` -> `offline`
	
			* | Node | Status    |
				| ---- | --------- |
				|      | Offline   |
			|      | Ready     |
				|      | Rendering |

	* **`update-server-info-btn`**
	
		* pull updates from server to repopulate the tables

### Tables example

![table example](/home/microbobu/Documents/EPS Render Server/EPRender.com/Other Sources/exampleServerInfo.png)

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
|                          |                                                              |                                                              |
|                          |                                                              |                                                              |
|                          |                                                              |                                                              |

## Session attributes

| Name            | What it does          | Expected Value           |
| --------------- | --------------------- | ------------------------ |
| **`useremail`** | holds user full email | `kyang@eastsideprep.org` |
|                 |                       |                          |
|                 |                       |                          |

## Server Enums (`.Enums.`)

| Name                 | Enum Name            | Values                                                       | Notes                                                 |
| -------------------- | -------------------- | ------------------------------------------------------------ | ----------------------------------------------------- |
| Project Type         | `ProjectType`        | `0 = PremierePro`<br />`1 = AfterEffects`<br />`2 = BlenderCycles`<br />`3 = BlenderEEVEE` |                                                       |
| Job Status           | `JobStatus`          | `0 = Unassigned`<br />`1 = Queued`<br />`2 = Rendering`      |                                                       |
| Node Power Index     | `PowerIndex`         | `0 = Ultra`<br />`1 = High`<br />`2 = Mid`<br />`3 = Low`    | Tower 1<br />VR 1<br />Corsair 1, 2<br />DELL 1, 2, 3 |
| Node Status          | `NodeStatus`         | `0 = Ready`<br />`1 = Rendering`<br />`2 = Offline`          |                                                       |
| Node Response Status | `NodeResponseStatus` | `0 = Accept`<br />`1 = Completed`<br />`2 = Reject`          |                                                       |

## Models

### Server Meta (`.models.Meta`)

| Property          | Variable Name | Type               | Notes                                                        |
| ----------------- | ------------- | ------------------ | ------------------------------------------------------------ |
| Job Queue         | `jobQueue`    | `List<JobRequest>` | holds all current requests their statuses                    |
| Action Queue      | `actionQueue` | `List<JobRequest>` | job requests that are immediately actionable.<br />pulled from and added to the render que when there are availabilities |
| Blender Job Queue | `blenderJobs` | `List<JobRequest>` | hold the frames of the Blender jobs<br />pull from here when adding frames for (distributed) rendering |
| Server nodes      | `serverNodes` | `List<Node>`       |                                                              |

### Job Request (`.models.JobRequest`)

| Property                   | Variable Name              | Type          | Notes                                                        |
| -------------------------- | -------------------------- | ------------- | ------------------------------------------------------------ |
| User's email               | `useremail`                | `String`      | Identity of sender                                           |
| Project Type               | `projectType`              | `ProjectType` |                                                              |
| Blender Use All Frames     | `blenderUseAll`            | `boolean`     | `true` = use all frames<br />`false` = use start and end frames |
| Blender Start Frame        | `blenderStartFrame`        | `int`         | (`>= 1`)<br />only read if `blenderUseAll = false`.          |
| Blender End Frame          | `blenderEndFrame`          | `int`         | (`>= blenderStartFrame`)<br />only read if `blenderUseAll = false` |
| Project Location           | `projectLocation`          | `String`      | URL to location on student drive.<br />must include: `\\drives\Students\` |
| Job status                 | `status`                   | `JobStatus`   |                                                              |
| Blender frames rendered    | `blenderFramesRendered`    | `int`         | (`bSF <= n <= bEF`)<br />"`n` / total frames"<br />used as rendering status message |
| Blender current frame      | `blenderCurrentFrame`      | `int`         | the frame this request represents                            |
| Blender distributed amount | `blenderDistributedAmount` | `int`         | number of nodes working on this request                      |

### Server Node (`.models.Node`)

| Property       | Variable Name | Type         | Notes                               |
| -------------- | ------------- | ------------ | ----------------------------------- |
| Node name      | `nodeName`    | `String`     | Used to display<br />must be unique |
| Power Index    | `powerIndex`  | `PowerIndex` |                                     |
| Node status    | `nodeStatus`  | `NodeStatus` |                                     |
| Working on Job | `currentJob`  | `JobRequest` |                                     |

### Node Response (`.models.NodeResponse`)

| Property     | Variable Name  | Type                 | Notes                              |
| ------------ | -------------- | -------------------- | ---------------------------------- |
| Node name    | `nodeName`     | `String`             | identifier                         |
| Attached Job | `attachedJob`  | `jobRequest`         | job that this response pertains to |
| Status       | `reportStatus` | `NodeResponseStatus` |                                    |

## Serving logic

### Get new `JobRequest` (from Front-end)

1. add to `Meta.jobQueue`
2. If Adobe, immediately add to `Meta.actionQueue`
3. If Blender, create `jobRequest` class for each frame and  immediately add first to `Meta.actionQueue`
4. Add other blender frames to `Meta.blenderJobs`

### Add Jobs to RabbitMQ's `render-jobs`

1. All assumes `Meta.serverNodes` has at least one that is ready
2. If `Meta.blenderJobs.count != 0`
	1. look for in progress blender jobs
	2. if `JobRequest.blenderDistributedAmount > 0 && Meta.actionQueue.count != 0`: pop next `Meta.actionQueue` item into `render-jobs`
	3. if `JobRequest.blenderDistributedAmount == 0 && Meta.actionQueue.count == 0`: pop next blender frame from this request into `render-jobs` as <u>priority</u>
3. If nothing happened from #2: pop the top job from `Meta.actionQueue` into RabbitMQ's `render-jobs`

### On node register taking a job (from RabbitMQ)

  1. set node `currentJob` to the `attachedJob`
  2. set `nodeStatus` to rendering
  3. set related `jobRequest` in the queue listing to rendering
			   1. "rendering"
	   2. "n / total <u>rendered</u>"

### On node register completed job (from RabbitMQ)

  1. set node `currentJob` to `null`
  2. set `serverNodes.<requested node>.nodeStatus` to ready
  3. set related `jobRequest` in the queue listing
   4. remove if Adobe and email
   5. If Blender: increment/set "n" in the rendered message.
	     		1. if all rendered, remove from listing and email
6. run **Add Job**

### On node register rejected job (from RabbitMQ)

  1. if Adobe: delete and email
  2. if Blender: find all and delete, then email
  3. set `serverNodes.<requested node>.nodeStatus` to ready
  4. run **Add Job** 

## Special Items

### Get the start and end frames of a blender project

bash command: `blender -b {blender file to render} --python {path/to/}getFrames.py | grep -w 'EPRenderInterestedFrames:'`

* returns `EPRenderInterestedFrames: start,end`

# Python (Client)

## Working Logic

1. 

# RabbitMQ (Server)

## Queues

| Name            | Technical Name    | Notes                                                        |
| --------------- | ----------------- | ------------------------------------------------------------ |
| Render Jobs     | `render-jobs`     | Any immediately render-able jobs used to deliver jobs to any open/available nodes |
| Back from Nodes | `back-from-nodes` | used to send messages from nodes back to the server (like when a job is completed) |

