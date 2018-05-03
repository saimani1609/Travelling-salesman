import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class CropDuster {
	
	private Plane plane;
	private MapPanel mapPanel;
	private JTextArea textArea = new JTextArea();
	private JButton btn1;
	private JButton btn2;
	private JButton btnRandomGen;
	
	
	private ArrayList<RefillingPoint> refillingPoints = new ArrayList<>();
	
	public CropDuster(){
		refillingPoints.add(new RefillingPoint(25, 20, 5, 16));
		refillingPoints.add(new RefillingPoint(80, 70, 6, 15));
		mapPanel = new MapPanel();
	}
	
	public void loadTask(String filename){
		ArrayList<Point> taskPoints = loadTaskFromFile(filename);
		loadTask(taskPoints);
	}
	
	public void loadTaskByRandom(int taskNum){
		ArrayList<Point> taskPoints = new ArrayList<>(taskNum);
		for(int i = 0; i < taskNum; i++){
			taskPoints.add(new Point((int)(100 * Math.random()), (int)(100 * Math.random())));
		}
		loadTask(taskPoints);
	}
	
	private void loadTask(ArrayList<Point> taskPoints){
		textArea.setText("");
		ArrayList<Point> taskPoints2 = (ArrayList<Point>) taskPoints.clone();
		
		plane = new Plane(this);
		plane.setRefillingPoints(refillingPoints);
		plane.setTaskPoints(taskPoints);
		
		mapPanel.setRefillingPoints(refillingPoints);
		
		ArrayList<Event> events = plane.startWork();
		
		mapPanel.setTaskPoints(taskPoints2);
		mapPanel.setEvents(events);
	}
	
	public static ArrayList<Point> loadTaskFromFile(String filename){
		ArrayList<Point> points = new ArrayList<>();
		File taskFile = new File(filename);
		
		String line;
		String[] xy;
		try {
			BufferedReader in = new BufferedReader(new FileReader(taskFile));
			while ((line = in.readLine()) != null){
				line = line.trim();
				if(line.length() < 2){
					continue;
				}
				line = line.substring(1, line.length() - 1);
				xy = line.split(",");
				if(xy.length == 2){
					int x = Integer.parseInt(xy[0].trim());
					int y = Integer.parseInt(xy[1].trim());
					points.add(new Point(y, x));
				}
			}
			
			
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return points;
	}
	
	public void outputToTextArea(String t){
		textArea.append(t + "\n");
	}
	
	private void initMap(){
		JFrame frame = new JFrame();
		
		frame.setTitle("CropDuster");
		
		frame.setResizable(false);
		
		Container container = frame.getContentPane();
		container.setLayout(new BorderLayout());
		mapPanel.setPreferredSize(new Dimension(500, 600));
		container.add(mapPanel, BorderLayout.CENTER);
		
		JPanel info = new JPanel(); 
		info.setLayout(new BorderLayout());
		
		info.setPreferredSize(new Dimension(300, 0));
		container.add(info, BorderLayout.EAST);
		
		JScrollPane jsp = new JScrollPane(textArea);
		
		info.add(jsp, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2, 2));
		info.add(buttonPanel, BorderLayout.SOUTH);
		
		btn1 = new JButton("1st Day");
		btn2 = new JButton("2nd Day");
		btnRandomGen = new JButton("Random");
		
		JPanel genNumInputPanel = new JPanel(new BorderLayout());
		genNumInputPanel.add(new JLabel("point:"), BorderLayout.WEST);
		final JTextField tfRandomNum = new JTextField("30");
		genNumInputPanel.add(tfRandomNum, BorderLayout.CENTER);
		
		buttonPanel.add(btn1);
		buttonPanel.add(btn2);
		buttonPanel.add(genNumInputPanel);
		buttonPanel.add(btnRandomGen);
		
		
		btn1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadTask("firstdaytask.txt");
			}
		});
		
		btn2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadTask("seconddaytask.txt");
			}
		});
		
		btnRandomGen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String numStr = tfRandomNum.getText().trim();
				try {
					int num = Integer.valueOf(numStr);
					loadTaskByRandom(num);
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				
			}

		});
		frame.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		CropDuster cd = new CropDuster();
		
		cd.loadTask("firstday.txt");
		
		cd.initMap();
	}

}
