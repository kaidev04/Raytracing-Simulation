package lumen2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

// Kai TE20B
public class Main extends JPanel implements KeyListener {
	
	JFrame frame;
	int panel_width, panel_height; // frame and panel size
	
	// obj = object, ls = light source
	Ellipse2D.Double obj, ls;
	static int obj_x, obj_y;
	static int ls_x, ls_y;
	static int obj_radius, ls_radius;
	static int obj_center_x, obj_center_y, obj_center_z;
	static int ls_center_x, ls_center_y, ls_center_z;
	
	static int ls_size_difference;
	static boolean ls_size_increase;
	
	static int depth; // room depth
	
	static double inverse_square_exponent; // affects light intensity
	
	static boolean bounce_mode;
	static int x_bounce_velocity, y_bounce_velocity;
	int x_lower_limit, x_upper_limit, y_lower_limit, y_upper_limit;
	
	MidWall mid_wall;
	LeftWall left_wall;
	RightWall right_wall;
	TopWall top_wall;
	BottomWall bottom_wall;
	
	ClickListener clickListener;
	DragListener dragListener;
	
	JPanel control_panel;
	
	Main() {
		frame = new JFrame();
		control_panel = new JPanel();
		panel_width = 700;
		panel_height = 600;
		
		this.setSize(panel_width, panel_height);
		
		bounce_mode = false;
		x_bounce_velocity = 55;
		y_bounce_velocity = 55;
		
		x_lower_limit = 70;
		x_upper_limit = 550;
		y_lower_limit = 70;
		y_upper_limit = 400;
		
		depth = 175;
		inverse_square_exponent = 1.3;
		
		obj_radius = 80;
		ls_radius = 40;
		ls_size_difference = 0;
		
		obj_x = this.getWidth()/2 - obj_radius;
		obj_y = this.getHeight()/2 - obj_radius + 50;
		
		ls_x = this.getWidth() - 150;
		ls_y = this.getHeight() - 450;
		
		obj_center_x = obj_x + obj_radius;
		obj_center_y = obj_y + obj_radius;
		obj_center_z = depth / 2;
		
		ls_center_x = ls_x + ls_radius;
		ls_center_y = ls_y + ls_radius;
		ls_center_z = depth / 2;
		
		mid_wall = new MidWall();
		left_wall = new LeftWall();
		right_wall = new RightWall();
		top_wall = new TopWall();
		bottom_wall = new BottomWall();
		
		// Listener
		clickListener = new ClickListener();
		dragListener = new DragListener();
		this.addMouseListener(clickListener);
		this.addMouseMotionListener(dragListener);
		this.addKeyListener(this);
		
		this.setFocusable(true);
		
		// Build
		frame.add(this);
		
		// Frame properties
		frame.setTitle("Lumen2D");
		frame.setSize(panel_width, panel_height);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		new Main();
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		// Walls
		bottom_wall.paint(g2d);
		left_wall.paint(g2d);
		right_wall.paint(g2d);
		top_wall.paint(g2d);
		mid_wall.paint(g2d);
		
		obj = new Ellipse2D.Double(obj_x, obj_y, obj_radius*2, obj_radius*2);		
		ls = new Ellipse2D.Double(ls_x, ls_y, ls_radius*2, ls_radius*2);
		
		obj_center_x = obj_x + obj_radius;
		obj_center_y = obj_y + obj_radius;
		
		ls_center_x = ls_x + ls_radius;
		ls_center_y = ls_y + ls_radius;
		
		if(ls_center_z >= obj_center_z) {
			g2d.setColor(Color.BLACK);
			g2d.fill(obj);
			castLight(g2d);
			g2d.setColor(new Color(235, 237, 235));
			g2d.fill(ls);
		}
		else {
			g2d.setColor(new Color(235, 237, 235));
			g2d.fill(ls);
			g2d.setColor(Color.BLACK);
			g2d.fill(obj);
		}
		
		if(bounce_mode) {
			if(ls_x > x_upper_limit || ls_x < x_lower_limit) {
				x_bounce_velocity *= -1;
			}		
			ls_x += x_bounce_velocity;
				
			if(ls_y > y_upper_limit || ls_y < y_lower_limit) {
				y_bounce_velocity *= -1;
			}
			ls_y += y_bounce_velocity;
			
			repaint();
		}
		
		
	}
	
	
	public static void castLight(Graphics2D g2d) {
		for(int x = obj_x; x <= obj_x + obj_radius*2; x++) {
			for(int y = obj_y; y <= obj_y + obj_radius*2; y++) {
				if(Math.pow(obj_center_x - x, 2) + Math.pow(obj_center_y - y, 2) <= Math.pow(obj_radius, 2)) {
					int dist_x = Math.abs(ls_center_x - x);
					int dist_y = Math.abs(ls_center_y - y);
					double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2)) / 100;
					
					double intensity;
					if(Math.pow(dist, inverse_square_exponent) >= 1) {
						intensity = 200 / (Math.pow(dist, inverse_square_exponent));
					}
					else {
						intensity = 200;
					}
					
					int lightShade = (int) Math.round(intensity);
					g2d.setColor(new Color(lightShade, lightShade, lightShade));
					g2d.fillRect(x, y, 1, 1);
					
				}
			}	
		}
	}
	
	
	public class MidWall {
		int wall_width, wall_height;
		int x_start, y_start, z;
		
		
		MidWall() {
			wall_width = 350;
			wall_height = 250;
			
			x_start = panel_width/2 - wall_width/2;
			y_start = panel_height/2 - wall_height/2;
			z = 0;
			
		}
		
		public void paint(Graphics2D g2d) {		
			
			for(int x = x_start; x <= x_start + wall_width; x++) {
				for(int y = y_start; y <= y_start + wall_height; y++) {
					int dist_x = Math.abs(ls_center_x - x);
					int dist_y = Math.abs(ls_center_y - y);
					int dist_z = ls_center_z;
					double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2) + Math.pow(dist_z, 2)) / 100;
					
					double intensity;
					if(Math.pow(dist, inverse_square_exponent) >= 1) {
						intensity = 200 / (Math.pow(dist, inverse_square_exponent));
					}
					else {
						intensity = 200;
					}
					
					int lightShade = (int) Math.round(intensity);
					g2d.setColor(new Color(lightShade, lightShade, lightShade));
					g2d.fillRect(x, y, 1, 1);
				}
			}
			
		}
		
		public int getWidth() {
			return wall_width;
		}
		
		public int getHeight() {
			return wall_height;
		}
		
		public int getX() {
			return x_start;
		}
		
		public int getY() {
			return y_start;
		}
	}
	
	public class RightWall {		
		
		int rect_x = mid_wall.getX() + mid_wall.getWidth(); 
		int rect_y = mid_wall.getY();
		
		int rect_width = panel_width - (mid_wall.getX() + mid_wall.getWidth());
		int rect_height = mid_wall.getHeight();
		
		public void paint(Graphics2D g2d) {
			// draw rect
			int rect_z = 0;
			for(int x = rect_x; x <= panel_width; x++) {
				for(int y = rect_y; y <= rect_y + rect_height; y++) {
					int dist_x = Math.abs(ls_center_x - panel_width);
					int dist_y = Math.abs(ls_center_y - y);
					int dist_z = Math.abs(ls_center_z -  rect_z);
					double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2) + Math.pow(dist_z, 2)) / 100;
					
					double intensity;
					if(Math.pow(dist, inverse_square_exponent) >= 1) {
						intensity = 200 / (Math.pow(dist, inverse_square_exponent));
					}
					else {
						intensity = 200;
					}
					
					int lightShade = (int) Math.round(intensity);
					g2d.setColor(new Color(lightShade, lightShade, lightShade));
					g2d.fillRect(x, y, 1, 1);
				}
				rect_z++;
			}
			
			// draw top triangle
			int z = 0;
			int i = 1;
			for(int x = rect_x; x <= panel_width; x++) {
				for(int y = rect_y; y >= rect_y - i; y--) {
					int dist_x = Math.abs(ls_center_x - panel_width);
					int dist_y = Math.abs(ls_center_y - y);
					int dist_z = Math.abs(ls_center_z -  z);
					double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2) + Math.pow(dist_z, 2)) / 100;
					
					double intensity;
					if(Math.pow(dist, inverse_square_exponent) >= 1) {
						intensity = 200 / (Math.pow(dist, inverse_square_exponent));
					}
					else {
						intensity = 200;
					}
					
					int lightShade = (int) Math.round(intensity);
					g2d.setColor(new Color(lightShade, lightShade, lightShade));
					g2d.fillRect(x, y, 1, 1);
					
				}
				z++;
				i++;
			}
			
			// draw bottom triangle
			int z2 = 0;
			int i2 = 1;
			for(int x = rect_x; x <= panel_width; x++) {
				for(int y = rect_y + rect_height; y <= (rect_y + rect_height) + i2; y++) {
					int dist_x = Math.abs(ls_center_x - panel_width);
					int dist_y = Math.abs(ls_center_y - y);
					int dist_z = Math.abs(ls_center_z -  z);
					double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2) + Math.pow(dist_z, 2)) / 100;
					
					double intensity;
					if(Math.pow(dist, inverse_square_exponent) >= 1) {
						intensity = 200 / (Math.pow(dist, inverse_square_exponent));
					}
					else {
						intensity = 200;
					}
					
					int lightShade = (int) Math.round(intensity);
					g2d.setColor(new Color(lightShade, lightShade, lightShade));
					g2d.fillRect(x, y, 1, 1);
					
				}
				z2++;
				i2++;
			}
			
			
					
			
		}
		
	}
	
	
	public class LeftWall {
		Rectangle2D.Double rect;
		
		int rect_x = 0; 
		int rect_y = mid_wall.getY();
		
		int rect_width = mid_wall.getX();
		int rect_height = mid_wall.getHeight();
		
		public void paint(Graphics2D g2d) {
			// draw rect
			int rect_z = depth;
			for(int x = rect_x; x <= rect_width; x++) {
				for(int y = rect_y; y <= rect_y + rect_height; y++) {
					int dist_x = ls_center_x;
					int dist_y = Math.abs(ls_center_y - y);
					int dist_z = Math.abs(ls_center_z -  rect_z);
					double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2) + Math.pow(dist_z, 2)) / 100;
								
					double intensity;
					if(Math.pow(dist, inverse_square_exponent) >= 1) {
						intensity = 200 / (Math.pow(dist, inverse_square_exponent));
					}
					else {
						intensity = 200;
					}
								
					int lightShade = (int) Math.round(intensity);
					g2d.setColor(new Color(lightShade, lightShade, lightShade));
					g2d.fillRect(x, y, 1, 1);
				}
				rect_z--;
			}
			
			// draw top triangle
			int z = 0;
			int i = 1;
			for(int x = rect_width; x >= 0; x--) {
				for(int y = rect_y; y >= rect_y - i; y--) {
					int dist_x = ls_center_x;
					int dist_y = Math.abs(ls_center_y - y);
					int dist_z = Math.abs(ls_center_z -  z);
					double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2) + Math.pow(dist_z, 2)) / 100;
								
					double intensity;
					if(Math.pow(dist, inverse_square_exponent) >= 1) {
						intensity = 200 / (Math.pow(dist, inverse_square_exponent));
					}
					else {
						intensity = 200;
					}
								
					int lightShade = (int) Math.round(intensity);
					g2d.setColor(new Color(lightShade, lightShade, lightShade));
					g2d.fillRect(x, y, 1, 1);
								
				}
				z++;
				i++;
			}
			
			// draw bottom triangle
			int z2 = 0;
			int i2 = 1;
			for(int x = rect_width; x >= 0; x--) {
				for(int y = rect_y + rect_height; y <= (rect_y + rect_height) + i2; y++) {
					int dist_x = ls_center_x;
					int dist_y = Math.abs(ls_center_y - y);
					int dist_z = Math.abs(ls_center_z -  z);
					double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2) + Math.pow(dist_z, 2)) / 100;
								
					double intensity;
					if(Math.pow(dist, inverse_square_exponent) >= 1) {
						intensity = 200 / (Math.pow(dist, inverse_square_exponent));
					}
					else {
						intensity = 200;
					}
								
					int lightShade = (int) Math.round(intensity);
					g2d.setColor(new Color(lightShade, lightShade, lightShade));
					g2d.fillRect(x, y, 1, 1);
								
				}
				z2++;
				i2++;
			}
			
		}
	}
	
	public class TopWall {
		
		int rect_x, rect_y;
		int rect_width, rect_height;
		
		TopWall() {
			rect_x = mid_wall.getX();
			rect_y = 0;
			rect_width = mid_wall.getWidth();
			rect_height = mid_wall.getHeight();
		}
		
		public void paint(Graphics2D g2d) {
			// draw rect
			for(int x = rect_x; x <= rect_x + rect_width; x++) {
				int z = depth;
				for(int y = rect_y; y <= mid_wall.getY(); y++) {
					int dist_x = Math.abs(ls_center_x - x);
					int dist_y = ls_center_y;
					int dist_z = z;
					double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2) + Math.pow(dist_z, 2)) / 100;
					
					double intensity;
					if(Math.pow(dist, inverse_square_exponent) >= 1) {
						intensity = 200 / (Math.pow(dist, inverse_square_exponent));
					}
					else {
						intensity = 200;
					}
					
					int lightShade = (int) Math.round(intensity);
					g2d.setColor(new Color(lightShade, lightShade, lightShade));
					g2d.fillRect(x, y, 1, 1);
					z--;
				}
			}
			
			// draw left triangle
			int i = 1;
			for(int x = 0; x <= rect_x; x++) {
				int z = depth;
				for(int y = 0; y <= i; y++) {
					int dist_x = Math.abs(ls_center_x - x);
					int dist_y = ls_center_y;
					int dist_z = z;
					double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2) + Math.pow(dist_z, 2)) / 100;
					
					double intensity;
					if(Math.pow(dist, inverse_square_exponent) >= 1) {
						intensity = 200 / (Math.pow(dist, inverse_square_exponent));
					}
					else {
						intensity = 200;
					}
					
					int lightShade = (int) Math.round(intensity);
					g2d.setColor(new Color(lightShade, lightShade, lightShade));
					g2d.fillRect(x, y, 1, 1);
					z--;
				}
				i++;
			}
			
			// draw bottom triangle
			int i2 = 1;
			for(int x = panel_width; x >= rect_x + rect_width; x--) {
				int z = depth;
				for(int y = 0; y <= i2; y++) {
					int dist_x = Math.abs(ls_center_x - x);
					int dist_y = ls_center_y;
					int dist_z = z;
					double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2) + Math.pow(dist_z, 2)) / 100;
										
					double intensity;
					if(Math.pow(dist, inverse_square_exponent) >= 1) {
						intensity = 200 / (Math.pow(dist, inverse_square_exponent));
					}
					else {
						intensity = 200;
					}
											
					int lightShade = (int) Math.round(intensity);
					g2d.setColor(new Color(lightShade, lightShade, lightShade));
					g2d.fillRect(x, y, 1, 1);
					z--;		
				}
				i2++;
			}
			
		}
		
	}
	
	public class BottomWall {
		int rect_x, rect_y;
		int rect_width, rect_height;
		
		BottomWall() {
			rect_width = mid_wall.getWidth();
			rect_height = panel_height - (mid_wall.getX() + mid_wall.getHeight());
			rect_x = mid_wall.getX();
			rect_y = rect_x + mid_wall.getHeight();
		}
		
		public void paint(Graphics2D g2d) {
			
			for(int x = rect_x; x <= rect_x + rect_width; x++) {
				int z = depth;
				for(int y = rect_y; y <= panel_height; y++) {
					int dist_x = Math.abs(ls_center_x - x);
					int dist_y = Math.abs(panel_height - ls_center_y);
					int dist_z = Math.abs(ls_center_z - z);
					double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2) + Math.pow(dist_z, 2)) / 100;
					
					double intensity;
					if(Math.pow(dist, inverse_square_exponent) >= 1) {
						intensity = 200 / (Math.pow(dist, inverse_square_exponent));
					}
					else {
						intensity = 200;
					}
					
					int lightShade = (int) Math.round(intensity);
					g2d.setColor(new Color(lightShade, lightShade, lightShade));
					g2d.fillRect(x, y, 1, 1);
					z--;
				}
			}
			
			
			// draw left triangle
			int i = 1;
			for(int x = 0; x <= rect_x; x++) {
				int z = depth;
				for(int y = panel_height; y >= panel_height - i; y--) {
					int dist_x = Math.abs(ls_center_x - x);
					int dist_y = Math.abs(panel_height - ls_center_y);
					int dist_z = Math.abs(ls_center_z -  z);
					double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2) + Math.pow(dist_z, 2)) / 100;
								
					double intensity;
					if(Math.pow(dist, inverse_square_exponent) >= 1) {
						intensity = 200 / (Math.pow(dist, inverse_square_exponent));
					}
					else {
						intensity = 200;
					}
								
					int lightShade = (int) Math.round(intensity);
					g2d.setColor(new Color(lightShade, lightShade, lightShade));
					g2d.fillRect(x, y, 1, 1);
					z--;			
				}
				i++;
			}
			
			int i2 = 1;
			for(int x = panel_width; x >= rect_x + rect_width; x--) {
				int z = depth;
				for(int y = panel_height; y >= panel_height - i; y--) {
					int dist_x = Math.abs(ls_center_x - x);
					int dist_y = Math.abs(panel_height - ls_center_y);
					int dist_z = Math.abs(ls_center_z -  z);
					double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2) + Math.pow(dist_z, 2)) / 100;
								
					double intensity;
					if(Math.pow(dist, inverse_square_exponent) >= 1) {
						intensity = 200 / (Math.pow(dist, inverse_square_exponent));
					}
					else {
						intensity = 200;
					}
								
					int lightShade = (int) Math.round(intensity);
					g2d.setColor(new Color(lightShade, lightShade, lightShade));
					g2d.fillRect(x, y, 1, 1);
					z--;
				}
				i2++;
			}
			
		}
	}
	
	Point ls_prevPt;
	private class ClickListener extends MouseAdapter {
		
		public void mousePressed(MouseEvent e) {
			Point point = e.getPoint();
			ls_prevPt = point;
			
		}
	}

	private class DragListener extends MouseMotionAdapter {
		
		public void mouseDragged(MouseEvent e) {
			Point point = e.getPoint();
			int point_x  = (int) point.getX();
			int point_y = (int) point.getY();
			ls_x += point_x - (int) ls_prevPt.getX();
			ls_y += point_y - (int) ls_prevPt.getY();
			ls_prevPt = point;
			repaint();
			
		}
		
	}
	int z_bottom_limit = 65;
	int z_upper_limit = 150;
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		ls_size_difference++;
		if(e.getKeyCode() == KeyEvent.VK_UP && ls_center_z < z_upper_limit) {
			ls_radius++;
			ls_center_z++;
			x_lower_limit -= 2;
			x_upper_limit += 2;
			y_lower_limit -= 2;
			y_upper_limit += 2;
			repaint();
		}	
		if(e.getKeyCode() == KeyEvent.VK_DOWN && ls_center_z > z_bottom_limit) {
			ls_radius--;
			ls_center_z--;
			x_lower_limit += 2;
			x_upper_limit -= 2;
			y_lower_limit += 2;
			y_upper_limit -= 2;
			repaint();
		}
		if(e.getKeyCode() == KeyEvent.VK_1) {
			bounce_mode = true;
			repaint();
		}
		else if(e.getKeyCode() == KeyEvent.VK_2) {
			bounce_mode = false;
			repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		ls_size_difference = 0;
	}

}
