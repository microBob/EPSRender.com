# HTML

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
	  	* **`1`** = Premiere
	  	* **`2`** = After Effects
	  	* **`3`** = Cycles
	  	* **`4`** = EEVEE
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

		* displayed the job que
	
		* | User | Type | Time Added | Status                       |
			| ---- | ---- | ---------- | ---------------------------- |
			|      |      |            | **`x`**/**`total`** rendered |
		|      |      |            | Rendering                    |
			|      |      |            | Place **`#`** in Que         |

	* **`server-status-table`**

		* displayed the nodes and their states

		* sorted: `online` -> `rendering` -> `offline`
	
			* | Node | Status    |
				| ---- | --------- |
				|      | Offline   |
			|      | Ready     |
				|      | Rendering |

	* **`resync-server-btn`**
	
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

# Java

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

## Models

### Job Request (`.models.jobRequest`)

| Property               | Variable Name       | Type                         | Notes                                                        |
| ---------------------- | ------------------- | ---------------------------- | ------------------------------------------------------------ |
| User's email           | `useremail`         | String                       | Identity of sender                                           |
| Project Type           | `projectType`       | int (1-4)                    | `1` = Premiere Pro<br />`2` = After Effects<br />`3` = Blender Cycles<br />`4` = Blender EEVEE |
| Blender Use All Frames | `blenderUseAll`     | boolean                      | `true` = use all frames<br />`false` = use start and end frames |
| Blender Start Frame    | `blenderStartFrame` | int (`>= 1`)                 | only read if `blenderUseAll = false`.                        |
| Blender End Frame      | `blenderEndFrame`   | int (`>= blenderStartFrame`) | only read if `blenderUseAll = false`                         |
| Project Location       | `projectLocation`   | String                       | URL to location on student drive                             |

