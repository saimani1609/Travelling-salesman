import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;


public class MapPanel extends JPanel {
	
	private final int BORDER = 10;
	private final int BOTTOM = 100;
	
	private int width;
	private int height;
	private int fieldWidth;
	private int fieldHeight;
	
	private ArrayList<Event> events = new ArrayList<>();
	private ArrayList<RefillingPoint> refillingPoints;
	private ArrayList<Point> taskPoints;
	
	private int planeX = -100;
	private int planeY = -100;
	private float planeAngle;
	
	private float remainingFuel;
	private float remainingChem;
	
	private boolean dusting = false;
	private float dustingRadius;
	
	private JButton btnStart;
	private JButton btnStop;
	
	private AnimationThread animationThread;
	
	private boolean running;
	
	public MapPanel() {
		super();
		initUI();
	}
	
	private void initUI(){
		setLayout(null);
		btnStart = new JButton("Start");
		btnStart.setBounds(400, 490, 70, 30);
		add(btnStart);
		
		btnStop = new JButton("Stop");
		btnStop.setBounds(400, 530, 70, 30);
		add(btnStop);
		btnStop.setEnabled(false);
		
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				initValue();
				running = true;
				animationThread = new AnimationThread();
				animationThread.start();
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
			}
		});
		
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				running = false;
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
			}
		});
	}
	
	private void initValue(){
		planeX = -100;
		planeY = -100;
		planeAngle = 0;
		
		remainingFuel = 0;
		remainingChem = 0;
		
		dusting = false;
		dustingRadius = 0;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		width = getWidth();
		height = getHeight();
		
		drawField(g);
		drawEvents(g);
		drawPoints(g);
		drawDust(g);
		drawPlane(g);
		drawBars(g);
		
		
	}
	
	private void drawField(Graphics g){
		fieldWidth = width - 2 * BORDER;
		fieldHeight = fieldWidth;
		
		g.setColor(new Color(63, 134, 0));
		g.fillRect(BORDER, BORDER, fieldWidth, fieldHeight);
		
		g.setColor(Color.BLACK);
		for(int i = 0; i <= 10; i++){
			int x = (int) (BORDER + i * (fieldWidth / 10f));
			g.drawLine(x, BORDER, x, BORDER + fieldHeight);
		}
		
		for(int i = 0; i <= 10; i++){
			int y = (int) (BORDER + i * (fieldHeight / 10f));
			g.drawLine(BORDER, y, BORDER + fieldWidth, y);
		}
	}
	
	private void drawPoints(Graphics g){
		if(taskPoints != null){
			g.setColor(Color.DARK_GRAY);
			for(Point p : taskPoints){
				int x = (int) (BORDER + (p.x / 100f * fieldWidth));
				int y = (int) (BORDER + (fieldHeight - p.y / 100f * fieldHeight));
				g.fillOval(x - 4, y - 4, 8, 8);
			}
		}
		
		if(refillingPoints != null){
			g.setColor(Color.RED);
			for(Point p : refillingPoints){
				int x = (int) (BORDER + (p.x / 100f * fieldWidth));
				int y = (int) (BORDER + (fieldHeight - p.y / 100f * fieldHeight));
				g.fillOval(x - 4, y - 4, 8, 8);
			}
		}
	}
	
	private void drawPlane(Graphics g){
		g.setColor(Color.CYAN);
		int bodyLen = 20;
		int bodyWing = 8;
		int wingLen = 12;
		int tailWingLen = 6;
		
		int body1x = planeX;
		int body1y = planeY;
		int body2x = (int) Math.round(planeX + bodyLen * Math.cos(planeAngle));
		int body2y = (int) Math.round(planeY + bodyLen * Math.sin(planeAngle));
		
		int bodyMidx = (int) Math.round(planeX + bodyWing * Math.cos(planeAngle));
		int bodyMidy = (int) Math.round(planeY + bodyWing * Math.sin(planeAngle));
		
		int lWingX = (int) Math.round(bodyMidx + wingLen * Math.sin(planeAngle));
		int lWingY = (int) Math.round(bodyMidy - wingLen * Math.cos(planeAngle));
		
		int rWingX = (int) Math.round(bodyMidx - wingLen * Math.sin(planeAngle));
		int rWingY = (int) Math.round(bodyMidy + wingLen * Math.cos(planeAngle));
		
		int lTailWingX = (int) Math.round(body2x + tailWingLen * Math.sin(planeAngle));
		int lTailWingY = (int) Math.round(body2y - tailWingLen * Math.cos(planeAngle));
		
		int rTailWingX = (int) Math.round(body2x - tailWingLen * Math.sin(planeAngle));
		int rTailWingY = (int) Math.round(body2y + tailWingLen * Math.cos(planeAngle));
		
		g.drawLine(body1x, body1y, body2x, body2y);
		g.drawLine(bodyMidx, bodyMidy, lWingX, lWingY);
		g.drawLine(bodyMidx, bodyMidy, rWingX, rWingY);
		g.drawLine(body2x, body2y, lTailWingX, lTailWingY);
		g.drawLine(body2x, body2y, rTailWingX, rTailWingY);
	}
	
	
	private void drawBars(Graphics g){
		g.setColor(Color.BLACK);
		g.setFont(new Font(null, Font.BOLD, 20));
		final int ULCornerX = BORDER;
		final int URCornerY = BORDER + fieldHeight + BORDER;
		
		g.drawString("FUEL", ULCornerX + 51, URCornerY + 25);
		g.drawString("CHEMICAL", ULCornerX, URCornerY + 55);
		
		g.setColor(getColorFromRatio(remainingFuel / Plane.MAX_FUEL));
		g.fillRect(ULCornerX + 117, URCornerY + 7, (int)(197f * remainingFuel / Plane.MAX_FUEL) , 17);
		g.setColor(getColorFromRatio(remainingChem / Plane.MAX_CHEMICAL));
		g.fillRect(ULCornerX + 117, URCornerY + 37, (int)(197f * remainingChem / Plane.MAX_CHEMICAL), 17);
		
		g.setColor(Color.BLACK);
		g.drawRect(ULCornerX + 115, URCornerY + 5, 200, 20);
		g.drawRect(ULCornerX + 115, URCornerY + 35, 200, 20);
		
	}
	
	private void drawDust(Graphics g){
		if(dusting){
			final int MAX_R = 40;
			g.setColor(Color.WHITE);
			for(int i = 0; i < 100; i++){
				float random = (float) Math.random();
				float random2 = (float) Math.random();
				float angle = (float) (Math.PI * 2 * random);
				float r = dustingRadius * random2;
				int x = (int) (planeX + r * Math.cos(angle));
				int y = (int) (planeY + r * Math.sin(angle));
				g.drawLine(x, y, x, y);
			}
		}
		
		
		
		
	}
	
	private Color getColorFromRatio(float ratio){
		int red;
		int green;
		int ratioInt = (int) (ratio * 100f);
		
		if(ratioInt > 50){
			red = (int) (255 * (200 - ratioInt * 2) / 100f);
			green = 255;
		} else {
			red = 255;
			green = (int) (255 * ratioInt * 2 / 100f);
		}
		
		return new Color(red, green, 0);
	}
	
	private void drawEvents(Graphics g){
		if(events != null){
			for(Event e : events){
				g.setColor(Color.WHITE);
				if(e instanceof FlyingEvent){
					FlyingEvent fe = (FlyingEvent) e;
					int[] s = getConvertedCoordinate(fe.from);
					int[] d = getConvertedCoordinate(fe.to);
					g.drawLine(s[0], s[1], d[0], d[1]);
					
				} else if(e instanceof RefillEvent){
					
				} else if(e instanceof DustEvent){
					
				}
			}
			
		}
	}
	
	private int[] getConvertedCoordinate(Point p){
		int[] co = new int[2];
		co[0] = (int) (BORDER + (p.x / 100f * fieldWidth));
		co[1] = (int) (BORDER + (fieldHeight - p.y / 100f * fieldHeight));
		return co;
	}
	
	public void setTaskPoints(ArrayList<Point> task){
		taskPoints = task;
		repaint();
	}
	
	public void setRefillingPoints(ArrayList<RefillingPoint> points){
		refillingPoints = points;
		repaint();
	}
	
	public void setEvents(ArrayList<Event> events){
		this.events = events;
		repaint();
	}
	

	
	private class AnimationThread extends Thread{
		@Override
		public void run() {
			for(Event e : events){
				if(!running)break;
				if(e instanceof FlyingEvent){
					final float SPEED = 1.5f;
					FlyingEvent fe = (FlyingEvent) e;
					final int[] start = getConvertedCoordinate(fe.from);
					final int[] dest = getConvertedCoordinate(fe.to);
					
					planeAngle = (float) Math.atan2(start[1] - dest[1],start[0] - dest[0]);
					
					int distance = (int) Math.sqrt(Math.pow(start[0] - dest[0], 2) + Math.pow(start[1] - dest[1], 2));
					int frameNum = (int) (distance / SPEED);
					float tmpFuel = remainingFuel;
					for(int i = 1; i <= frameNum; i++){
						float ratio = i / (float)frameNum;
						planeX = (int) Math.round(start[0] - ratio * distance * Math.cos(planeAngle));
						planeY = (int) Math.round(start[1] - ratio * distance * Math.sin(planeAngle));
						
						remainingFuel = tmpFuel - ratio * fe.fuelCost;
						repaint();
						try {
							Thread.sleep(20);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						if(!running)break;
					}
					
					
				} else if(e instanceof RefillEvent){
					RefillEvent re = (RefillEvent) e;
					final float FILLING_SPEED = 1f;
					
					int[] curr = getConvertedCoordinate(re.where);
					planeX = curr[0];
					planeY = curr[1];
					
					float longerAddingVolume = Math.max(re.fuelAdded, re.ChemAdded);
					int frameNum = (int) (longerAddingVolume / FILLING_SPEED);
					for(int i = 1; i <= frameNum; i++){
						float ratio = i / (float)frameNum;
						remainingFuel += FILLING_SPEED;
						remainingFuel = Math.min(remainingFuel, Plane.MAX_FUEL);
						remainingChem += FILLING_SPEED;
						remainingChem = Math.min(remainingChem, Plane.MAX_CHEMICAL);
						
						repaint();
						try {
							Thread.sleep(20);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						if(!running)break;
					}
					
				} else if(e instanceof DustEvent){
					dusting = true;
					DustEvent de = (DustEvent) e;
					int timeNeeded = 50;
					float tmpFuel = remainingFuel;
					float tmpChem = remainingChem;
					final int MAX_DUSTING_RADIUS = 40;
					
					for(int i = 1; i <= timeNeeded; i++){
						float ratio = i / (float)timeNeeded;
						dustingRadius = MAX_DUSTING_RADIUS * ratio;
						remainingFuel = tmpFuel - ratio * de.fuelCost;
						remainingChem = tmpChem - ratio * de.ChemCost;
						
						repaint();
						try {
							Thread.sleep(20);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						if(!running)break;
					}
					dusting = false;
				}
			}
			btnStart.setEnabled(true);
		}
	}
}
