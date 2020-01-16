<!DOCTYPE html>
<html lang="html">

<head>
    <title>EPRender</title>

    <!--    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css"/>-->
    <link rel="stylesheet" href="./w3.css">
    <link href="https://fonts.googleapis.com/css?family=Overpass:400,600i&display=swap" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="./styles.css"/>

    <script type="text/javascript" src="./action.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>

<?php
require_once __DIR__ . '/vendor/autoload.php';

use PhpAmqpLib\Connection\AMQPStreamConnection;
use PhpAmqpLib\Message\AMQPMessage;

$servername = "127.0.0.1";
$username = "man";
#TODO: need secure
$password = "Farm2q1D@tabaseMan";
$dbname = "renderfarm";
// echo "named vars\n";

$conn = mysqli_connect($servername, $username, $password, $dbname);
// echo "created connection\n";

if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
// echo "good connection\n";


// RabbitMQ connection
$connection = new AMQPStreamConnection('localhost', 5672, 'farmRabbitMan', 'rabbitman');
//echo "created connection\n";
$channel = $connection->channel();
//echo "located channels\n";

$channel->queue_declare('jobQue', false, true, false, false);
//echo "declared que\n";
?>

<body>
<div class="w3-container">
    <h1 class="w3-center">Welcome to <b>EPRender</b></h1>
    <h5 class="w3-center">Eastside Preparatory School's Render Farm</h5>
</div>

<div class="w3-container w3-cell-row">
    <div class="w3-container w3-half w3-cell w3-mobile">
        <fieldset>
            <legend>
                <div class="w3-bar">
                    <button class="w3-bar-item w3-black" onclick="document.location.reload();">New Job Request
                    </button>
                </div>
            </legend>
            <form method="post" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]); ?>">
                <?php
                $emailErr = $pathErr = "";
                $startFErr = $endFErr = "";

                $blenderFramesDivVis = 'none';

                if ($_SERVER["REQUEST_METHOD"] == "POST") {
                    $email = $_POST["email"];
                    $type = $_POST["projectType"];
                    $path = $_POST["projectPath"];

                    $bStartFrame = $_POST["blenderStartFrame"];
                    $bEndFrame = $_POST["blenderEndFrame"];

                    $go = true;

                    if (empty($email)) {
                        $emailErr = "Please provide your EPS email.";
                        $go = false;
                    } else if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
                        $emailErr = "Please provide a valid EPS email.";
                        $go = false;
                    } else if (substr($email, -17) != "@eastsideprep.org") {
                        $emailErr = "Please provide your EPS email.";
                        $go = false;
                    }

                    if ($type == 2) {
                        $blenderFramesDivVis = 'block';
                        if (empty($bStartFrame)) {
                            $startFErr = "Please specify where to start rendering";
                            $go = false;
                        } else if ($bStartFrame < 0) {
                            $startFErr = "Please specify a positive frame number";
                            $go = false;
                        } else if ($bStartFrame > $bEndFrame) {
                            $startFErr = "Please choose a frame less than " . $bEndFrame;
                            $bEndFrame = "Please choose a frame greater than " . $bStartFrame;
                            $go = false;
                        }

                        if (empty($bEndFrame)) {
                            $endFErr = "Please specify where to end rendering";
                            $go = false;
                        } else if ($bEndFrame < 0) {
                            $endFErr = "Please specify a positive frame number";
                            $go = false;
                        }
                    }

                    if (empty($path)) {
                        $pathErr = "Please Provide a Project Path.";
                        $go = false;
                    } else {
                        //check if job already exists
                        $sql = "SELECT * FROM jobQ WHERE jobLoc=\"" . addslashes($path) . "\"";
                        $request = mysqli_query($conn, $sql);
                        if (mysqli_num_rows($request) > 0) {
                            $pathErr = "Job for this project already in que!";
                            $go = false;
                        }
                    }
                }
                ?>
                <p class="error">All fields are required</p>
                <!-- <br> -->
                <label>
                    E-mail:
                    <input id="emailInput" type="text" name="email" placeholder="@eastsideprep.org"
                           value="<?php echo $email; ?>"
                           class="textInput">
                    <span class="error"><?php echo $emailErr; ?></span>
                </label>

                <br><br>

                <label>
                    Type:
                    <select id="projectTypeInput" name="projectType" class="selectInput"
                            onchange="showRenderingFrames()">
                        <option <?php if ($type == 0) {
                            echo "selected";
                        } ?> value=0>Adobe Premiere Pro (.prproj)
                        </option>
                        <option <?php if ($type == 1) {
                            echo "selected";
                        } ?> value=1>Adobe After Effects (.aep, .aepx)
                        </option>
                        <option <?php if ($type == 2) {
                            echo "selected";
                        } ?> value=2>Blender 2.81a Cycles (.blend)
                        </option>
                    </select>
                </label>

                <div id="blenderRenderingFrames" style="display: <?php echo $blenderFramesDivVis ?>;">
                    <label>
                        Start Frame:
                        <input id="blenderStartFrameInput" type="text" name="blenderStartFrame" class="textInput"
                               placeholder="1" value="<?php echo $bStartFrame ?>">
                        <span class="error"><?php echo $startFErr; ?></span>
                    </label>
                    <br>
                    <label>
                        End Frame:
                        <input id="blenderEndFrameInput" type="text" name="blenderEndFrame" class="textInput"
                               placeholder="250" value="<?php echo $bEndFrame ?>">
                        <span class="error"><?php echo $endFErr ?></span>
                    </label>
                </div>

                <br><br>

                <label>
                    Project Path:
                    <input id="projectPathInput" type="text" name="projectPath" placeholder="Make@EPS"
                           value="<?php echo $path ?>"
                           class="textInput">
                    <input id="whatIsThisBtn" type="button" value="?"
                           onclick="toggleWhatIsThisText(); return false;"/>
                    <div id="whatIsThisText" class="w3-modal" onclick="this.style.display='none'">
                        <div class="w3-modal-content w3-animate-zoom">
                            <header class="w3-container">
                                <span onclick="document.getElementById('whatIsThisText').style.display='none'"
                                      class="w3-button w3-display-topright">&times;</span>
                            </header>
                            <div class="w3-container">
                                <p>A bunch of text that will be something</p>
                            </div>
                        </div>
                    </div>
                    <span class="error"><?php echo $pathErr; ?></span>
                </label>

                <br><br>

                <input type="submit" value="Send">
                <?php
                if ($go) {
                    if ($type === '2') {
                        for ($l = $bStartFrame; $l <= $bEndFrame; $l++) {
                            $notifyFrame = 0;
                            if ($l == $bStartFrame) {
                                $notifyFrame = 1;
                            } else if ($l == $bEndFrame) {
                                $notifyFrame = 2;
                            }
                            $sql = "INSERT INTO jobQ (user, jobLoc, type, blenderNFrame, notifyingFrame) VALUES (\"" . $email . "\",\"" . addslashes($path) . "\"," . $type . "," . $l . "," . $notifyFrame . ");";
                            if (mysqli_query($conn, $sql)) {
                                echo "Frame Added to Que!\n";
                                //Sending Job
                                $jobR = new AMQPMessage($email . "," . $type . "," . $path . "," . $l . "," . $notifyFrame, array('delivery_mode' => AMQPMessage::DELIVERY_MODE_PERSISTENT));
                                $channel->basic_publish($jobR, '', 'jobQue');
                                echo "Frame published to farm system!";
                            } else {
                                echo "Error: " . $sql . "<br>" . mysqli_error($conn);
                            }
                        }
                    } else {
                        $sql = "INSERT INTO jobQ (user, jobLoc, type) VALUES (\"" . $email . "\",\"" . addslashes($path) . "\"," . $type . ");";
                        if (mysqli_query($conn, $sql)) {
                            echo "Job successfully added to que!\n";
                            //Sending Job
                            $jobR = new AMQPMessage($email . "," . $type . "," . $path, array('delivery_mode' => AMQPMessage::DELIVERY_MODE_PERSISTENT));
                            $channel->basic_publish($jobR, '', 'jobQue');

                            echo "Job published to farm system!";
                        } else {
                            echo "Error: " . $sql . "<br>" . mysqli_error($conn);
                        }
                    }
                    $channel->close();
                    try {
                        $connection->close();
                    } catch (Exception $e) {
                    }
                    ?>
                    <script type="text/javascript">
                        clearForm();
                    </script>
                    <?php
                }
                ?>
            </form>
        </fieldset>
    </div>

    <div class="w3-container w3-half w3-cell w3-mobile">
        <?php
        $ipToCOMName = ["10.68.68.200" => "Tower1",
            "10.20.30.121" => "VR1",
            "10.20.85.231" => "Box1",
            "10.20.85.250" => "Box2",
            "10.20.85.233" => "DELL1",
            "10.20.85.224" => "DELL2",
            "10.20.85.234" => "DELL3",];
        $statToString = [0 => "Ready",
            1 => "Rendering",
            2 => "Offline"];
        $jobTypeToString = [0 => "Adobe Premiere Pro",
            1 => "Adobe After Effects",
            2 => "Blender 2.81a Cycles",];
        ?>

        <fieldset>
            <legend>
                <!-- <select>
                    <option selected="selected" value="serverStat">Server Status</option>
                    <option value="jobQue">Job Que</option>
                </select> -->
                <div class="w3-bar">
                    <button class="w3-bar-item w3-hover-black w3-black" onclick="serverDataTabs(true)"
                            id="serverStatusDataBtn">Server Status
                    </button>
                    <button class="w3-bar-item w3-hover-black" onclick="serverDataTabs(false)"
                            id="jobQueDataBtn">Job Que
                    </button>
                </div>
            </legend>

            <div class="w3-container" id="serverStatusData">
                <table class="w3-table w3-table-all">
                    <tr>
                        <th>Node Name</th>
                        <th>Status</th>
                        <th>Render Start Time</th>
                    </tr>
                    <?php
                    $sql = "SELECT ip, status, renderStart FROM nodeStat ORDER BY status ASC, renderStart ASC;";
                    $result = mysqli_query($conn, $sql);

                    if (mysqli_num_rows($result) > 0) {
                        while ($row = mysqli_fetch_assoc($result)) {
                            echo "<tr>";
                            echo "<td>" . $ipToCOMName[$row["ip"]] . "</td>";
                            // echo "<td class=\"";
                            // if ($row["status"] == 0) {
                            //     echo "greenText";
                            // } elseif ($row["status"] == 1) {
                            //     echo "blueText";
                            // } else {
                            //     echo "redText";
                            // }
                            // echo "\">";
                            echo "<td>" . $statToString[$row["status"]] . "</td>";
                            echo "<td>" . $row["renderStart"] . "</td>";
                            echo "</tr>";
                        }
                    }
                    ?>
                </table>
            </div>
            <div class="w3-container" id="jobQueData" style="display: none;">
                <table class="w3-table w3-table-all">
                    <tr>
                        <th>User</th>
                        <th>Time Added</th>
                        <th>Type</th>
                        <th>Status</th>
                    </tr>
                    <?php
                    $sql = "SELECT user, timeAdded, type, rendering FROM jobQ ORDER BY rendering DESC, timeAdded ASC;";
                    $result = mysqli_query($conn, $sql);

                    if (mysqli_num_rows($result) > 0) {
                        while ($row = mysqli_fetch_assoc($result)) {
                            echo "<tr>";
                            echo "<td>" . substr($row["user"], 0, -17) . "</td>";
                            echo "<td>" . $row["timeAdded"] . "</td>";
                            echo "<td>" . $jobTypeToString[$row["type"]] . "</td>";
                            echo "<td>" . ($row["rendering"] == 0 ? "Queued" : "Rendering") . "</td>";
                            echo "</tr>";
                        }
                    }
                    ?>
                </table>
            </div>
        </fieldset>
    </div>
</div>
</body>

</html>
