import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.Timer;

public class Driver implements Runnable {
	private JFrame mainFrame;
	private DrawPanel mainPanel;
	private JPanel controlPanel;
	private OutlineTextArea detailTextArea;

	private Bird[] birds;
	private int width = 800;
	private int height = 600;

	private Thread thread;
	private int threadCycle = 0;
	private boolean oneSelected = false;

	private boolean exitProgram = false;
	private boolean fullScreen = true;

	//private Color fillColor = new Color(68,68,68);
	private Color fillColor = Color.WHITE;

	public Driver() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		this.width = gs.getDisplayMode().getWidth();
		this.height = gs.getDisplayMode().getHeight();
		Dimension mainPanelDim = new Dimension(this.width - 100, this.height);
		Dimension controlPanelDim = new Dimension(100, this.height);

		MouseHandler mh = new MouseHandler();

		if (!gs.isFullScreenSupported()) {
			fullScreen = false;
			this.width = 800;
			this.height = 600;
		}

		mainFrame = new JFrame();
		mainPanel = new DrawPanel();
		mainPanel.addMouseListener(mh);
		mainPanel.setBackground(fillColor);

		mainPanelDim = new Dimension(this.width,this.height);

		OutlineButton exitButton = new OutlineButton("EXIT");
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitProgram = true;
			}
		});
		exitButton.setBounds(10,10,90,40);
		//exitButton.setPreferredSize(new Dimension(100,50));
		mainPanel.setLayout(null);
		mainPanel.add(exitButton);

		detailTextArea = new OutlineTextArea("Location - \nVelocity - ");
		detailTextArea.setBounds(10,70,210,40);
		detailTextArea.setEditable(false);
		mainPanel.add(detailTextArea);

		mainFrame.add(mainPanel);

		mainFrame.setPreferredSize(new Dimension(this.width,this.height));

		if (fullScreen) {
			mainFrame.setUndecorated(true);
			mainFrame.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent arg0) {mainFrame.setAlwaysOnTop(true);}
				@Override
				public void focusLost(FocusEvent arg0) {mainFrame.setAlwaysOnTop(false);}
			});
			//gs.setFullScreenWindow(mainFrame);
			//mainFrame.setLocation(gs.getDefaultConfiguration().getBounds().x, mainFrame.getY());
			mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}

		//mainFrame.pack();
		mainFrame.setVisible(true);
		mainFrame.setResizable(false);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				System.exit(0);
			}
		});

		birds = new Bird[300];
		for (int i = 0; i < birds.length; i++) {
			birds[i] = new Bird(this.width, this.height);
		}
		this.run();
	}

	public void run() {
		int i = 0;
		new Timer(17, (ActionEvent e) -> {
			mainPanel.repaint();
			if (exitProgram) System.exit(0);
		}).start();
	}

	private class MouseHandler implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			boolean firstFound = false;
			int x = e.getX();
			int y = e.getY();
			for (int i = 0; i < birds.length; i++) {
				AVector pos = birds[i].getLocation();
				boolean inRadius = ((Math.abs(pos.x - x) < 15) &&
														(Math.abs(pos.y - y) < 15));

				if (inRadius && !firstFound) {
					firstFound = true;
					oneSelected = true;
					birds[i].selected = true;
				} else {
					birds[i].selected = false;
				}
			}
			if (!firstFound) {
				oneSelected = false;
			}
		}
		@Override
		public void mouseExited(MouseEvent e) {	}
		@Override
		public void mouseEntered(MouseEvent e) { }
		@Override
		public void mouseReleased(MouseEvent e) {	}
		@Override
		public void mousePressed(MouseEvent e) {	}
	}

	public class DrawPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			int i = 0;
			try {
				for (i = 0; i < birds.length; i++) {
					birds[i].run(g2, birds);
					if (birds[i].selected && threadCycle == 10) {
						detailTextArea.setText(birds[i].printDetail());
					}
				}
				if (!oneSelected) {
					detailTextArea.setText("Location - \nVelocity - ");
				}
			} catch(Exception e) {
				System.out.println("error computing bird run on: " + i);
				System.out.println(birds[i].printInfo());
			}
			if (threadCycle > 10) {
				threadCycle = 0;
			}
			threadCycle++;
		}
	}

	public static void main(String[] args) {
		Driver d = new Driver();
	}
}
