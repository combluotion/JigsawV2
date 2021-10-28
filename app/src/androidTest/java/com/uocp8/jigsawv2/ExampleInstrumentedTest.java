package com.uocp8.jigsawv2;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.uocp8.jigsawv2.dao.impl.ScoreDaoImpl;
import com.uocp8.jigsawv2.model.Score;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.uocp8.jigsawv2", appContext.getPackageName());

        String nombre="pepa";
        String fecha="2020-02-02";
        String time="20";

        ScoreDaoImpl insert = new ScoreDaoImpl(nombre,fecha,time);
        insert.create()
    }
}