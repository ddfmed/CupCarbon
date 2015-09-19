/*----------------------------------------------------------------------------------------------------------------
 * CupCarbon: OSM based Wireless Sensor Network design and simulation tool
 * www.cupcarbon.com
 * ----------------------------------------------------------------------------------------------------------------
 * Copyright (C) 2013 Ahcene Bounceur
 * ----------------------------------------------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *----------------------------------------------------------------------------------------------------------------*/

package device;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import map.Layer;
import solver.SensorGraph;
import utilities.MapCalc;
import actions_ui.DeleteDevice;
import flying_object.FlyingGroup;

/**
 * @author Ahcene Bounceur
 * @author Kamal Mehdi
 * @author Lounis Massinissa
 * @version 1.0
 */
public class DeviceList {

	public static List<Device> nodes = new ArrayList<Device>();
	public static boolean drawLinks = true;
	//private static boolean displayConnectionDistance = false;
	public static LinkedList<LinkedList<Integer>> envelopeList = new LinkedList<LinkedList<Integer>>();
	public static int number = 1;
	
	/**
	 * 
	 */
	public DeviceList() {
		reset();
		// Thread th = new Thread(this);
		// th.start();
	}
	
	public static void reset() {
		for(Device node : nodes) {
			Layer.getMapViewer().removeMouseListener(node);
			Layer.getMapViewer().removeMouseMotionListener(node);
			Layer.getMapViewer().removeKeyListener(node);
			node = null;
		}
		nodes = new ArrayList<Device>();
		drawLinks = true;
		//displayConnectionDistance = false;
		envelopeList = new LinkedList<LinkedList<Integer>>();
	}

	/**
	 * @return the nodes
	 */
	public static List<Device> getNodes() {
		return nodes;
	}
	
	/**
	 * @return a node by its id
	 */
	public static Device getNodeById(int id) {
		for(Device device : nodes) {
			if(device.getId() == id) return device;
		}
		return null;
	}
	
	/**
	 * @return a sensor node by its id
	 */
	public static SensorNode getSensorNodeById(int id) {		
		for(SensorNode snode : DeviceList.getSensorNodes()) {
			if(snode.getId() == id) return snode;
		}
		return null;
	}
	
	/**
	 * @return a sensor node by its my
	 */
	public static SensorNode getSensorNodeByMy(int my) {		
		for(SensorNode snode : DeviceList.getSensorNodes()) {
			if(snode.getMy() == my) return snode;
		}
		return null;
	}
	
	/**
	 * @return the sensor nodes
	 */
	public static List<SensorNode> getSensorNodes() {
		List<SensorNode> snodes = new ArrayList<SensorNode>();
		for(Device n : nodes) {
			if(n.getType() == Device.SENSOR || n.getType()==Device.BASE_STATION)
				snodes.add((SensorNode) n);
		}
		return snodes;
	}
	
	/**
	 * @return the sensor and mobile nodes
	 */
	public static List<Device> getSensorAndMobileNodes() {
		List<Device> nodes = new ArrayList<Device>();
		for(Device n : nodes) {
			if((n.getType() == Device.SENSOR) || (n.getType() == Device.MOBILE))
				nodes.add((SensorNode) n);
		}
		return nodes;
	}
	
	/**
	 * @return the mobile nodes
	 */
	public static List<Device> getMobileNodes() {
		List<Device> snodes = new ArrayList<Device>();
		for(Device n : nodes) {
			if(n.getType() == Device.MOBILE)
				snodes.add(n);
		}
		return snodes;
	}

	/**
	 * @param fileName
	 */
	public static void save(String fileName) {
		try {
			PrintStream fos = new PrintStream(new FileOutputStream(fileName));
			Device node;
			for (Iterator<Device> iterator = nodes.iterator(); iterator.hasNext();) {
				node = iterator.next();
				//System.out.println(node.getGPSFileName());
				fos.print(node.getType());
				fos.print(" " + node.getId());
				fos.print(" " + node.getMy()+"#"+node.getCh()+"#"+node.getNId());
				fos.print(" " + node.getLongitude());
				fos.print(" " + node.getLatitude());
				fos.print(" " + node.getRadius());

				if (node.getType() == Device.SENSOR || node.getType() == Device.BASE_STATION || node.getType() == Device.BRIDGE || node.getType() == Device.MOBILE_WR)
					fos.print(" " + node.getRadioRadius());

				if (node.getType() == Device.SENSOR || node.getType() == Device.BASE_STATION )
					fos.print(" " + node.getSensorUnitRadius());

				if (node.getType() == Device.FLYING_OBJECT)
					fos.print(" " + ((FlyingGroup) node).getflyingObjectNumber());

				if (node.getType() == Device.SENSOR || node.getType() == Device.BASE_STATION 
						|| node.getType() == Device.FLYING_OBJECT
						|| node.getType() == Device.MOBILE
						|| node.getType() == Device.MOBILE_WR) {
					//System.out.println("----> " + node.getGPSFileName());
					fos.print(" "
							+ ((node.getGPSFileName() == "") ? "#" : node
									.getGPSFileName()));
				}

				if (node.getType() == Device.SENSOR || node.getType() == Device.BASE_STATION ) {
					fos.print(" "+ ((node.getScriptFileName() == "") ? "#" : node.getScriptFileName()));
				}

				fos.println();

			}
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param fileName
	 */
	public static void open(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			String[] str=null;
			int idMax = 0 ;
			while ((line = br.readLine()) != null) {
				str = line.split(" ");				
				switch (str.length) {
				case 6:
					addNodeByType(str[0], str[1], str[2], str[3], str[4],
							str[5]);
					break;
				case 7:
					addNodeByType(str[0], str[1], str[2], str[3], str[4],
							str[5], str[6]);
					break;
				case 8:
					addNodeByType(str[0], str[1], str[2], str[3], str[4],
							str[5], str[6], str[7]);
					break;
				case 9:
					addNodeByType(str[0], str[1], str[2], str[3], str[4],
							str[5], str[6], str[7], str[8]);
					break;
				case 10:
					addNodeByType(str[0], str[1], str[2], str[3], str[4],
							str[5], str[6], str[7], str[8], str[9]);
					break;
				}
				int v = Integer.valueOf(str[1]);
				if (v>idMax)
					idMax = v ;
			}
			if(str!=null)
				DeviceList.number = idMax+1 ;//Integer.valueOf(str[1])+1;
			br.close();
			
			//for(Device device : DeviceList.getNodes()) device.calculateNeighbours();
			
			Layer.getMapViewer().repaint();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the number of the nodes
	 */
	public static int size() {
		return nodes.size();
	}

	/**
	 * Create a node from a set of values (table type)
	 * 
	 * @param type
	 *            table that contains information about a node to add
	 */
	public static void addNodeByType(String... type) {
		int id = Integer.valueOf(type[1]);
		switch (Integer.valueOf(type[0])) {
		case 1:
			add(new SensorNode(type[1], type[2], type[3], type[4], type[5], type[6], type[7], type[8], type[9]));
			break;
		case 2:
			add(new Gas(type[3], type[4], type[5], id));
			break;
		case 3:
			add(new FlyingGroup(type[3], type[4], type[5], type[6], type[7]));
			break;
		case 4:
			add(new BaseStation(type[1], type[2], type[3], type[4], type[5], type[6], type[7], type[8], type[9]));
			break;
		case 6:
			add(new Mobile(type[3], type[4], type[5], type[6], id));
			break;
		case 7:
			add(new MobileWithRadio(type[3], type[4], type[5], type[6], type[7], id));
			break;
		case 8:
			add(new Marker(type[3], type[4], type[5]));
			break;
		}
	}

	/**
	 * @param node
	 */
	public static void add(Device node) {
		nodes.add(node);
	}

	// public void drawDistance(int x, int y, int x2, int y2, int d, Graphics g)
	// {
	// g.setColor(UColor.WHITED_TRANSPARENT);
	// g.drawString(""+d,(x2-x)/2,(y2-y)/2);
	// }

	
	/**
	 * Draw Links between
	 * 
	 * @param g
	 *            Graphics
	 */
	public void draw(Graphics g) {
		for (Device n : nodes) {
			n.drawRadioRange(g);
		}
		
		for (Device n : nodes) {
			n.drawSensorUnit(g);
		}
		
		for (Device n : nodes) {			
			n.drawMarked(g);
			n.draw(g);
		}
		
		for (Device n : nodes) {
			n.drawRadioLinks(g);
		}
		
		for (Device n : nodes) {
			if(n.displayInfos()) n.drawInfos(g);
		}
	}

	public Device get(int idx) {
		return nodes.get(idx);
	}

	public void setDrawLinks(boolean b) {
		drawLinks = b;
	}

	public boolean getDrawLinks() {
		return drawLinks;
	}

//	public void setDisplayDistance(boolean b) {
//		displayConnectionDistance = b;
//	}
//
//	public boolean getDisplayDistance() {
//		return displayConnectionDistance;
//	}

	public static void delete(int idx) {
		Device node = nodes.get(idx);
		Layer.getMapViewer().removeMouseListener(node);
		Layer.getMapViewer().removeMouseMotionListener(node);
		Layer.getMapViewer().removeKeyListener(node);
		nodes.remove(idx);
		node = null;
	}
	
	public void simulateMobiles() {
		for (Device node : nodes) {
			if(node.getType()==Device.MOBILE || node.getType()==Device.MOBILE_WR) {
				node.setSelection(true);
				node.start();
			}
		}
	}

	public static StringBuilder displaySensorGraph() {
		return SensorGraph.toSensorGraph(nodes, nodes.size()).displayNames();
	}

	public static StringBuilder displaySensorTargetGraph() {
		return SensorGraph.toSensorTargetGraph(nodes, nodes.size()).displayNames();
	}

	public void selectInNodeSelection(int cadreX1, int cadreY1, int cadreX2,
			int cadreY2) {
		Device node;
		for (Iterator<Device> iterator = nodes.iterator(); iterator.hasNext();) {
			node = iterator.next();
			node.setMove(false);
			node.setSelection(false);
			if (Layer.inMultipleSelection(node.getLongitude(), node.getLatitude(), cadreX1,
					cadreX2, cadreY1, cadreY2)) {
				node.setSelection(true);
			}
		}
	}

	public void deleteIfSelected() {
		Device node;
		for (Iterator<Device> iterator = nodes.iterator(); iterator.hasNext();) {
			node = iterator.next();
			if (node.isSelected() && node.getHide()==0) {
				Layer.getMapViewer().removeMouseListener(node);
				Layer.getMapViewer().removeMouseMotionListener(node);
				Layer.getMapViewer().removeKeyListener(node);
				iterator.remove();
				/* Tanguy */
				DeleteDevice action = new DeleteDevice(node, "Device deleted");
				action.exec();
				/* ------ */
				node = null;
			}
		}
	}

	public static void setGpsFileName(String gpsFileName) {
		Device node;
		for (Iterator<Device> iterator = nodes.iterator(); iterator.hasNext();) {
			node = iterator.next();
			if (node.isSelected()) {
				node.setGPSFileName(gpsFileName);
			}
		}
	}

	public static void setScriptFileName(String scriptFileName) {
		Device node;
		for (Iterator<Device> iterator = nodes.iterator(); iterator.hasNext();) {
			node = iterator.next();
			if (node.isSelected()) {
				node.setScriptFileName(scriptFileName);
			}
		}
	}

	public static void updateFromMap(String xS, String yS, String radiusS,
			String radioRadiusS, String captureRadiusS, String gpsFileName,
			String eMax, String eTx, String eRx, String eS, String beta, String targetName
			) {
		Device node;
		for (Iterator<Device> iterator = nodes.iterator(); iterator.hasNext();) {
			node = iterator.next();
			if (node.isSelected()) {
				node.setLongitude(Double.valueOf(xS));
				node.setLatitude(Double.valueOf(yS));
				node.setRadius(Double.valueOf(radiusS));
				node.setRadioRadius(Double.valueOf(radioRadiusS));
				node.setSensorUnitRadius(Double.valueOf(captureRadiusS));
				node.setGPSFileName(gpsFileName);
				node.getBattery().setLevel(Integer.valueOf(eMax));
				node.setETx(Double.valueOf(eTx));
				node.setERx(Double.valueOf(eRx));
				node.setES(Double.valueOf(eS));
				node.setBeta(Double.valueOf(beta));
				node.setTrgetName(targetName);				
			}
		}
		Layer.getMapViewer().repaint();
	}

	public static void initAll() {
		envelopeList = new LinkedList<LinkedList<Integer>>();
		for (Device device : nodes) {
			device.setMarked(false);
			device.setVisited(false);
			device.setDead(false);			
			device.setLedColor(0);
			if(device.getType()==Device.SENSOR) {
				device.setSending(false);
				device.setReceiving(false);
			}
		}
		Layer.getMapViewer().repaint();
	}

	public static void initAlgoSelectedNodes() {
		for (Device device : nodes) {
			if (device.isSelected()) {
				device.setMarked(false);
				device.setVisited(false);
				device.setLedColor(0);
			}
		}
		Layer.getMapViewer().repaint();
	}

	public static void setAlgoSelect(boolean b) {
		for (Device node : nodes) {
			node.setMarked(false);
		}
		Layer.getMapViewer().repaint();
	}

	public void setSelectionOfAllNodes(boolean selection, int type,
			boolean addSelect) {
		for (Device dev : nodes) {
			if (!addSelect)
				dev.setSelection(false);
			if (dev.getType() == type || type == -1)
				dev.setSelection(selection);
		}
		Layer.getMapViewer().repaint();
	}

	public void invertSelection() {
		for (Device dev : nodes) {
			dev.invSelection();
		}
		Layer.getMapViewer().repaint();
	}

	public Point[] getCouple(Device n1, Device n2) {
		int[] coord = MapCalc.geoToIntPixelMapXY(n1.getLongitude(), n1.getLatitude());
		int lx1 = coord[0];
		int ly1 = coord[1];
		coord = MapCalc.geoToIntPixelMapXY(n2.getLongitude(), n2.getLatitude());
		int lx2 = coord[0];
		int ly2 = coord[1];
		Point[] p = new Point[2];
		p[0] = new Point(lx1, ly1);
		p[1] = new Point(lx2, ly2);
		return p;
	}
	
	
	// Note: This method is not correct for a project because it changes 
	// the id of each sensor. It is used to validate simulation in simbox_simulation
	// package
	public void initId() {
		int k = 0;
		Device.initNumber() ;
		for(Device d : nodes) {
			d.setId(k++);
			Device.incNumber();
		}
		Layer.getMapViewer().repaint();
	}
	//---------
	
	public void loadRoutesFromFiles() {
		for(Device d : nodes) {
			d.loadRouteFromFile();
		}
	}

	public void simulate() {
		Device node;
		for (Iterator<Device> iterator = nodes.iterator(); iterator.hasNext();) {
			node = iterator.next();
			if (node.isSelected())
				node.start();
		}
	}
	
	public void simulateAll() {	
		for (Device node : nodes) {
			node.setSelection(true);
			node.start();
		}
	}
	
	public void simulateSensors() {
		for (Device node : nodes) {
			if(node.getType()==Device.SENSOR) {
				node.setSelection(true);
				node.start();
			}
		}
	}
	
	public static void stopSimulation() {
		for (Device node : nodes) {
			node.setSelection(false);
			node.stopSimulation();
		}
	}
	
	//------
	public static int getLastEnvelopeSize() {
		return envelopeList.getLast().size();
	}
	
	public static void initLastEnvelope() {
		envelopeList.getLast().clear();
	}
	
	public static void addEnvelope() {
		envelopeList.add(new LinkedList<Integer>());
	}
	
	public static void addToLastEnvelope(Integer d) {
		envelopeList.getLast().add(d);
	}
	
	public static LinkedList<Integer> getLastEnvelope() {
		return envelopeList.getLast();
	}
	
	public void drawEnvelope(LinkedList<Integer> envelope, Graphics2D g) {
		if(envelope.size()>0) {
			double x = nodes.get(envelope.get(0)).getLongitude();
			double y = nodes.get(envelope.get(0)).getLatitude();
			int lx1=0;
			int ly1=0;
			int lx2=0;
			int ly2=0;
			int[] coord ;
			for(int i=1; i<envelope.size(); i++) {
				coord = MapCalc.geoToIntPixelMapXY(x, y);
				lx1 = coord[0];
				ly1 = coord[1];
				coord = MapCalc.geoToIntPixelMapXY(nodes.get(envelope.get(i)).getLongitude(), nodes.get(envelope.get(i)).getLatitude());
				lx2 = coord[0];
				ly2 = coord[1];
				g.setColor(Color.BLUE);
				g.drawLine(lx1, ly1, lx2, ly2);
				x = nodes.get(envelope.get(i)).getLongitude();
				y = nodes.get(envelope.get(i)).getLatitude();		
			}
			coord = MapCalc.geoToIntPixelMapXY(nodes.get(envelope.get(0)).getLongitude(), nodes.get(envelope.get(0)).getLatitude());
			lx1 = coord[0];
			ly1 = coord[1];
			g.drawLine(lx2, ly2, lx1, ly1);
		}
	}
	
	public void drawEnvelopeList(Graphics2D g) {
		for(LinkedList<Integer> envelope : envelopeList) {
			drawEnvelope(envelope, g);
		}
	}
	
	public static void selectWitoutScript() {
		for(Device d : nodes) {
			if((d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION) && (d.getScriptFileName().equals(""))) {
				d.setSelection(true);
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void selectWitoutGps() {
		for(Device d : nodes) {
			if((d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION) && (d.getGPSFileName().equals(""))) {
				d.setSelection(true);
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void selectMarkedSensors() {
		for(Device d : nodes) {
			if((d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION) && (d.isMarked())) {
				d.setSelection(true);
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void setMy(String my) {
		for (Device d : nodes) {
			if (d.isSelected() && (d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION)) {
				d.setMy(Integer.valueOf(my));
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void setId(String id) {
		for (Device d : nodes) {
			if (d.isSelected() && (d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION)) {
				d.setId(Integer.valueOf(id));
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void setCh(String ch) {
		for (Device d : nodes) {
			if (d.isSelected() && (d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION)) {
				d.setCh(Integer.valueOf(ch));
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void setNId(String NId) {
		for (Device d : nodes) {
			if (d.isSelected() && (d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION)) {
				d.setNId(Integer.valueOf(NId));
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void setLongitude(String value) {
		for (Device d : nodes) {
			if (d.isSelected() && (d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION)) {
				d.setLongitude(Double.valueOf(value));
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void setLatitude(String value) {
		for (Device d : nodes) {
			if (d.isSelected() && (d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION)) {
				d.setLatitude(Double.valueOf(value));
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void setRadius(String value) {
		for (Device d : nodes) {
			if (d.isSelected() && (d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION)) {
				d.setRadius(Double.valueOf(value));
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void setRadioRadius(String value) {
		for (Device d : nodes) {
			if (d.isSelected() && (d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION)) {
				d.setRadioRadius(Double.valueOf(value));
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void setSensorUnitRadius(String value) {
		for (Device d : nodes) {
			if (d.isSelected() && (d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION)) {
				d.setSensorUnitRadius(Double.valueOf(value));
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void setEMax(String value) {
		for (Device d : nodes) {
			if (d.isSelected() && (d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION)) {
				d.getBattery().setLevel(Integer.valueOf(value));
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void setTx(String value) {
		for (Device d : nodes) {
			if (d.isSelected() && (d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION)) {
				d.setETx(Double.valueOf(value));
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void setRx(String value) {
		for (Device d : nodes) {
			if (d.isSelected() && (d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION)) {
				d.setERx(Double.valueOf(value));
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void setSensingEnergy(String value) {
		for (Device d : nodes) {
			if (d.isSelected() && (d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION)) {
				d.setERx(Double.valueOf(value));
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void setBeta(String value) {
		for (Device d : nodes) {
			if (d.isSelected() && (d.getType()==Device.SENSOR || d.getType()==Device.BASE_STATION)) {
				d.setBeta(Double.valueOf(value));
			}
		}
		Layer.getMapViewer().repaint();
	}	
	

	public static void selectById(String id) {
		String [] ids = id.split(" ");
		int k=0;
		for (Device d : nodes) {
			d.setSelection(false);
			if(k<ids.length)
				if (d.getId()==Integer.valueOf(ids[k])) {
					d.setSelection(true);
					k++;
				}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void selectByMy(String my) {
		String [] mys = my.split(" ");
		for (Device d : nodes) {
			d.setSelection(false);
		}
		for(int k=0; k<mys.length; k++) {
			for (Device d : nodes) {
				if (d.getMy()==Integer.valueOf(mys[k])) {
					d.setSelection(true);
				}			
			}
		}
		Layer.getMapViewer().repaint();
	}
	
	public static void selectOneFromSelected() {
		for(Device d : nodes) {
			if(d.isSelected()) {
				deselectAll();
				d.setSelection(true);
			}
		}
	}
	
	public static void deselectAll() {
		for(Device d : nodes) {
			d.setSelection(false);
		}
	}
	
//	public void calculateLinks() {
//		neighbors = new LinkedList<Device> () ;
//		for(Device device : DeviceList.getNodes()) {
//			if((!isDead() || !device.isDead()) && this!=device) {
//				if (!isDead() && !device.isDead()) 
//					neighbors.add(device);
//				if (device.radioDetect(this))
//					if (!this.isDead())
//						device.addNeighbor(this);
//					else
//						device.removeNeighbor(this);				
//			}
//		}
//			//Layer.getMapViewer().repaint();
//	}
	
}