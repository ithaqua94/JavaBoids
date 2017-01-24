import javax.swing.*;
import java.awt.*;
import java.lang.*;
import java.util.*;


public class OutlineButton extends JButton {
  public static final long serialVersionUID = 1L;

  private Color outlineColor = Color.BLACK;
  private Color fillColor = Color.WHITE;

  public OutlineButton(String label) {
    super(label);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    this.setBackground(fillColor);
    this.setOpaque(true);
    this.setFocusPainted(false);
    this.setFont(new Font("Tahoma", Font.BOLD, 14));
    this.setBorder(BorderFactory.createLineBorder(outlineColor, 3));
  }

  public void setOutlineColor(Color g) {
    this.outlineColor = g;
  }

  public void setFillColor(Color g) {
    this.fillColor = g;
  }
}
