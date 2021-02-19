package main.java;

import main.java.Texture;

import java.util.Random;

public class TexturesInit
{
    public TexturesInit()
    {
        initialization();
    }

    public static Texture air;
    public static Texture ground;
    public static Texture wall;
    public static Texture mushroom;

    private void initialization()
    {
        air = new Texture("air");
        ground = new Texture("ground");
        wall = new Texture("wall");
        mushroom = new Texture("mushroom");
    }
}
