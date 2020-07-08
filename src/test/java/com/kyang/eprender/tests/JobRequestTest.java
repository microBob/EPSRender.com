package com.kyang.eprender.tests;

import com.kyang.eprender.models.JobRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobRequestTest {

    @Test
    void blenderShouldRetrieveCorrectStartAndEndFrames() {
        String useremail = "kyang@eastsideprep.org";
        int projectType = 3;
        String projectLocation = "/home/microbobu/Documents/EPS Render Server/EPRender.com/src/test/resources/Sample Blender Files";

        JobRequest blenderJob = new JobRequest(useremail, projectType, projectLocation);

        // start frame
        assertEquals(1, blenderJob.getBlenderStartFrame(), "Start frame should be 1");

        // end frame
        assertEquals(30, blenderJob.getBlenderEndFrame(), "End frame should be 30");
    }
}