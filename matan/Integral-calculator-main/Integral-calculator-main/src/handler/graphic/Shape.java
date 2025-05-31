package handler.graphic;

import java.awt.Graphics2D;

import output.GraphicPanel;

public interface Shape {

    void draw(Graphics2D g, GraphicPanel p);
}
