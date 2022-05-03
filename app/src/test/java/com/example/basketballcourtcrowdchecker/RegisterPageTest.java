package com.example.basketballcourtcrowdchecker;

import junit.framework.TestCase;

public class RegisterPageTest extends TestCase {

    public void testCheckString() {
        //Declare and set.
        String testPass = "Lin123";

        //Check.
        boolean output = RegisterPage.checkString(testPass);
        assertTrue(output);

    }
}