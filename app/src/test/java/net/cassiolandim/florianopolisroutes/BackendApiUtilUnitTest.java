package net.cassiolandim.florianopolisroutes;

import net.cassiolandim.florianopolisroutes.backend.BackendApiUtil;

import org.junit.Test;

import static org.junit.Assert.*;

public class BackendApiUtilUnitTest {
    @Test
    public void authorization_encoding_isCorrect() throws Exception {
        String username = "WKD4N7YMA1uiM8V";
        String password = "";
        String expected = "basic V0tENE43WU1BMXVpTThWOkR0ZFR0ek1MUWxBMGhrMkMxWWk1cEx5VklsQVE2OA==";
        assertEquals(expected, BackendApiUtil.authorizationHeaderValue(username, password));
    }
}