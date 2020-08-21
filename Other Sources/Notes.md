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
	  |      |      |            | Rendering                    |
	  	|      |      |            | Place **`#`** in Queue       |
	
	* **`server-status-table`**
	
	  * displayed the nodes and their states
	
	  * sorted: `ready` -> `rendering` -> `offline`
	
	  	* | Node | Status    |
	  		| ---- | --------- |
	  		|      | Offline   |
	  	|      | Ready     |
	  		|      | Rendering |
	
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

| Name             | Enum Name     | Values                                                       | Notes                                                 |
| ---------------- | ------------- | ------------------------------------------------------------ | ----------------------------------------------------- |
| Project Type     | `ProjectType` | `0 = PremierePro`<br />`1 = AfterEffects`<br />`2 = BlenderCycles`<br />`3 = BlenderEEVEE` |                                                       |
| Job Status       | `JobStatus`   | `0 = Unassigned`<br />`1 = Queued`<br />`2 = Rendering`      |                                                       |
| Node Power Index | `PowerIndex`  | `0 = Ultra`<br />`1 = High`<br />`2 = Mid`<br />`3 = Low`    | Tower 1<br />VR 1<br />Corsair 1, 2<br />DELL 1, 2, 3 |
| Node Status      | `NodeStatus`  | `0 = Ready`<br />`1 = Rendering`<br />`2 = Offline`          |                                                       |
| Message Type     | `MessageType` | `"Blender Frames"` = `BlenderFrames`                         |                                                       |

## Models

### Server Meta (`.models.Meta`)

| Property          | Variable Name | Type               | Notes                                                        |
| ----------------- | ------------- | ------------------ | ------------------------------------------------------------ |
| Job Queue         | `jobQueue`    | `List<JobRequest>` | holds all current requests their statuses                    |
| Action Queue      | `actionQueue` | `List<JobRequest>` | job requests that are immediately actionable.<br />pulled from and added to the render que when there are availabilities |
| Blender Job Queue | `blenderJobs` | `List<JobRequest>` | hold the frames of the Blender jobs<br />pull from here when adding frames for (distributed) rendering |
| Server nodes      | `serverNodes` | `List<Node>`       |                                                              |

### Job Request (`.models.JobRequest`)

| Property                                               | Variable Name              | Type          | Notes                                                        |
| ------------------------------------------------------ | -------------------------- | ------------- | ------------------------------------------------------------ |
| *<u>{Input parameter}</u>*<br />User's email           | `useremail`                | `String`      | Identity of sender                                           |
| *<u>{Input parameter}</u>*<br />Project Type           | `projectType`              | `ProjectType` |                                                              |
| *<u>{Input parameter}</u>*<br />Blender Use All Frames | `blenderUseAll`            | `boolean`     | `true` = use all frames<br />`false` = use start and end frames |
| *<u>{Input parameter}</u>*<br />Blender Start Frame    | `blenderStartFrame`        | `int`         | (`>= 1`)<br />only read if `blenderUseAll = false`.          |
| *<u>{Input parameter}</u>*<br />Blender End Frame      | `blenderEndFrame`          | `int`         | (`>= blenderStartFrame`)<br />only read if `blenderUseAll = false` |
| Project File                                           | `projectFile`              | String        | URL to specific project file<br />generated after received   |
| *<u>{Input parameter}</u>*<br />Project Location       | `projectLocation`          | `String`      | URL to location on student drive.<br />must include: `\\drives\Students\`<br />generated from `projectFile` |
| Job status                                             | `status`                   | `JobStatus`   |                                                              |
| Blender frames rendered                                | `blenderFramesRendered`    | `int`         | (`bSF <= n <= bEF`)<br />"`n` / total frames"<br />used as rendering status message |
| Blender current frame                                  | `blenderCurrentFrame`      | `int`         | the frame this request represents                            |
| Blender distributed amount                             | `blenderDistributedAmount` | `int`         | number of nodes working on this request                      |

### Server Node (`.models.Node`)

| Property       | Variable Name | Type         | Notes                               |
| -------------- | ------------- | ------------ | ----------------------------------- |
| Node name      | `nodeName`    | `String`     | Used to display<br />must be unique |
| IP Address     | `ipAddress`   | `String`     | unique identifier                   |
| Power Index    | `powerIndex`  | `PowerIndex` |                                     |
| Node status    | `nodeStatus`  | `NodeStatus` |                                     |
| Working on Job | `currentJob`  | `JobRequest` |                                     |

## Server logic

### Get new `JobRequest` (from Front-end)

1. 

## Special Items

### Get the start and end frames of a blender project

bash command: `blender -b {blender file to render} --python {path/to/}getFrames.py | grep -w 'EPRenderInterestedFrames:'`

* returns `EPRenderInterestedFrames: start,end`

# NodeJS (Client)

## Working Logic

1. 
