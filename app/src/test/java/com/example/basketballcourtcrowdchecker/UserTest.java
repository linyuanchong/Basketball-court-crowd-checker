package com.example.basketballcourtcrowdchecker;

import junit.framework.TestCase;

import java.lang.reflect.Field;

public class UserTest extends TestCase {

    public void testGetEmail() throws NoSuchFieldException, IllegalAccessException {
        //Declare and set.
        final User user = new User();
        user.setEmail("user@email.com");

        //Check.
        final Field field = user.getClass().getDeclaredField("email");
        field.setAccessible(true);
        assertEquals(field.get(user), "user@email.com");
    }

    public void testSetEmail() throws NoSuchFieldException, IllegalAccessException{
        //Declare and set.
        final User user = new User();
        final Field field = user.getClass().getDeclaredField("email");
        field.setAccessible(true);
        field.set(user, "user@email.com");

        //Check.
        final String result = user.getEmail();
        assertEquals(result, "user@email.com");
    }

    public void testGetPassword() throws NoSuchFieldException, IllegalAccessException {
        //Declare and set.
        final User user = new User();
        user.setPassword("User123");

        //Check.
        final Field field = user.getClass().getDeclaredField("password");
        field.setAccessible(true);
        assertEquals(field.get(user), "User123");
    }

    public void testSetPassword() throws NoSuchFieldException, IllegalAccessException{
        //Declare and set.
        final User user = new User();
        final Field field = user.getClass().getDeclaredField("password");
        field.setAccessible(true);
        field.set(user, "User123");

        //Check.
        final String result = user.getPassword();
        assertEquals(result, "User123");
    }

    public void testGetName() throws NoSuchFieldException, IllegalAccessException{
        //Declare and set.
        final User user = new User();
        user.setName("User");

        //Check.
        final Field field = user.getClass().getDeclaredField("name");
        field.setAccessible(true);
        assertEquals(field.get(user), "User");
    }

    public void testSetName() throws NoSuchFieldException, IllegalAccessException{
        //Declare and set.
        final User user = new User();
        final Field field = user.getClass().getDeclaredField("name");
        field.setAccessible(true);
        field.set(user, "User");

        //Check.
        final String result = user.getName();
        assertEquals(result, "User");
    }

    public void testGetPhone() throws NoSuchFieldException, IllegalAccessException {
        //Declare and set.
        final User user = new User();
        user.setPhone("1234567890");

        //Check.
        final Field field = user.getClass().getDeclaredField("phone");
        field.setAccessible(true);
        assertEquals(field.get(user), "1234567890");
    }

    public void testSetPhone() throws NoSuchFieldException, IllegalAccessException{
        //Declare and set.
        final User user = new User();
        final Field field = user.getClass().getDeclaredField("phone");
        field.setAccessible(true);
        field.set(user, "1234567890");

        //Check.
        final String result = user.getPhone();
        assertEquals(result, "1234567890");
    }

    public void testGetCurrentCourt() throws NoSuchFieldException, IllegalAccessException{
        //Declare and set.
        final User user = new User();
        user.setCurrentCourt("m0");

        //Check.
        final Field field = user.getClass().getDeclaredField("currentCourt");
        field.setAccessible(true);
        assertEquals(field.get(user), "m0");
    }

    public void testSetCurrentCourt() throws NoSuchFieldException, IllegalAccessException{
        //Declare and set.
        final User user = new User();
        final Field field = user.getClass().getDeclaredField("currentCourt");
        field.setAccessible(true);
        field.set(user, "m0");

        //Check.
        final String result = user.getCurrentCourt();
        assertEquals(result, "m0");
    }

    public void testGetPresence() throws NoSuchFieldException, IllegalAccessException{
        //Declare and set.
        final User user = new User();
        user.setPresence(true);

        //Check
        assertTrue(user.getPresence());
    }

    public void testSetPresence() throws NoSuchFieldException, IllegalAccessException{
        //Declare and set.
        final User user = new User();
        user.setPresence(true);

        //Check.
        final boolean result = user.getPresence();
        assertTrue(result);
    }
}