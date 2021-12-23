package com.example.aviatorstarter;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TESTTEST
{

    public static boolean isValidDate(String input, String formatString) {

        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString);

            format.setLenient(false);
            format.parse(input);

        } catch (ParseException e) {
            return false;

        } catch (IllegalArgumentException e) {
            return false;

        }

        return true;

    }
}
