package com.fundynamic.d2tm.game.drawing;

import com.fundynamic.d2tm.game.map.Map;
import com.fundynamic.d2tm.game.map.Perimeter;
import com.fundynamic.d2tm.game.math.Vector2D;
import com.fundynamic.d2tm.graphics.Tile;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Viewport {

    private final Map map;

    private final Graphics graphics;
    private final Image buffer;

    private final Vector2D<Integer> drawingVector;
    private final Vector2D<Integer> screenResolution;

    private final Perimeter<Float> viewingVectorPerimeter;

    private float velocityX;
    private float velocityY;

    private float moveSpeed;

    private Vector2D<Float> viewingVector;


    public Viewport(Vector2D screenResolution, Vector2D viewingVector, Graphics graphics, Map map, float moveSpeed) throws SlickException {
        this(screenResolution, Vector2D.zero(), viewingVector, graphics, map, moveSpeed);
    }

    public Viewport(Vector2D<Integer> screenResolution, Vector2D drawingVector, Vector2D viewingVector, Graphics graphics, Map map, float moveSpeed) throws SlickException {
        this.graphics = graphics;
        this.map = map;

        this.drawingVector = drawingVector;
        this.screenResolution = screenResolution;
        this.buffer = constructImage(screenResolution);

        float heightOfMapInPixels = map.getHeight() * Tile.HEIGHT;
        float widthOfMapInPixels = map.getWidth() * Tile.WIDTH;
        this.viewingVectorPerimeter = new Perimeter<>(0F,
                widthOfMapInPixels - screenResolution.getX(),
                0F,
                heightOfMapInPixels - screenResolution.getY());
        this.viewingVector = viewingVector;
        this.velocityX = 0F;
        this.velocityY = 0F;

        this.moveSpeed = moveSpeed;
    }

    public void render() throws SlickException {
        final Graphics bufferGraphics = this.buffer.getGraphics();
        if (bufferGraphics == null) return; // HACK HACK: this makes sure our tests are happy by not having to stub all the way down these methods...

        bufferGraphics.clear();

        drawViewableMapOnBuffer(viewingVector.toInt(), bufferGraphics);
        // Stefan 24-01-2015: This will get hairy once we draw other stuff on the screen.
        // this class will then probably get loads of dependencies to other sources like Units, Structures, etc. Is that what we want?
        // perhaps we can draw these things elsewhere, and then later stack images here? Like a stack of cards? 
        // determine what items are visible & draw them on image

        // add more layers
        // Units, etc

        // draw all on the big canvas
        drawBufferToGraphics(graphics, drawingVector);
        graphics.drawString("Drawing viewport at " + drawingVector.shortString() + " viewing " + viewingVector.shortString(), 10, 30);
    }

    public void update() {
        viewingVector = viewingVectorPerimeter.makeSureVectorStaysWithin(viewingVector.move(velocityX, velocityY));
    }

    public void moveLeft() {
        this.velocityX = -moveSpeed;
    }

    public void moveRight() {
        this.velocityX = moveSpeed;
    }

    public void moveUp() {
        this.velocityY = -moveSpeed;
    }

    public void moveDown() {
        this.velocityY = moveSpeed;
    }

    public void stopMovingHorizontally() {
        this.velocityX = 0F;
    }

    public void stopMovingVertically() {
        this.velocityY = 0F;
    }

    private void drawBufferToGraphics(Graphics graphics, Vector2D<Integer> drawingVector) {
        graphics.drawImage(buffer, drawingVector.getX(), drawingVector.getY());
    }

    private void drawViewableMapOnBuffer(Vector2D<Integer> viewingVector, Graphics imageGraphics) throws SlickException {
        imageGraphics.drawImage(
            map.getSubImage(
                    viewingVector.getX(),
                    viewingVector.getY(),
                    screenResolution.getX(),
                    screenResolution.getY()),
            0, 0
        );
    }

    // These methods are here mainly for (easier) testing. Best would be to remove them if possible - and at the very
    // least not the use them in the non-test code.
    public Vector2D<Float> getViewingVector() {
        return viewingVector;
    }

    protected Image constructImage(Vector2D<Integer> screenResolution) throws SlickException {
        return new Image(screenResolution.getX(), screenResolution.getY());
    }

}
