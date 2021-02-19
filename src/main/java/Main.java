package main.java;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import main.java.gui.Font;

public class Main 
{
	public static World world = new World();
	public static TextureRenderer textureRenderer = new TextureRenderer();
	public static long window;
	public static Font font;
	public static TexturesInit textures;
	
	public static void main(String[] args)
	{
		if (!glfwInit()) 
		{
			throw new IllegalStateException("Failed to initialize GLFW!");
		}
		
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		window = glfwCreateWindow(710, 710, "Forrest Game", 0, 0);
		if (window == 0)
		{
			throw new IllegalStateException("Failed to create window!");
		}
		
		GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (videoMode.width() - 710)/2, (videoMode.height() -  710)/2);
		
		glfwShowWindow(window);
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		textures = new TexturesInit();
		world.generate();
		font = new Font("iljaFont");
		
		while (!glfwWindowShouldClose(window))
		{
			glfwPollEvents(); 
			glClear(GL_COLOR_BUFFER_BIT);
			
			World.worldPlayer.tick();
			
			Main.textureRenderer.renderBoard();
			Main.textureRenderer.renderPlayer(World.worldPlayer.getTexture());
			Main.textureRenderer.renderViewport();

			font.drawString("energy: "+World.worldPlayer.hungerStat, 0f, 0.85f, 0.7f);
			font.drawString("health: "+World.worldPlayer.healthStat, -1f, 0.85f, 0.7f);
			
			glfwSwapBuffers(window);
		}
		
		glfwTerminate();
		System.out.println("thx for playing,bye.");
		System.exit(0);
	}

}
