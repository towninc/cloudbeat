package com.aimluck.model;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class CheckLogTest extends AppEngineTestCase {

    private CheckLog model = new CheckLog();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
