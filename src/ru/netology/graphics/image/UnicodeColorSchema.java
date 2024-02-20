package ru.netology.graphics.image;

import java.util.*;

public class UnicodeColorSchema implements TextColorSchema {

    LinkedList<Character> chars = new LinkedList<>(Arrays.asList('#', '$', '@', '%', '*', '+', '-', '\''));

    @Override
    public char convert(int color) {
        return chars.get((int) Math.floor(color / 256. * chars.size()));
    }
}