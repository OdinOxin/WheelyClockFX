package de.odinoxin.wheelyclock.clock;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;

import de.odinoxin.wheelyclock.Menu;
import de.odinoxin.wheelyclock.wheel.Wheel;
import de.odinoxin.wheelyclock.wheel.WheelEditor;
import de.odinoxin.wheelyclock.wheel.WheelHandler;
import de.odinoxin.wheelyclock.wheel.WheelUpdater;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Clock extends Application implements Runnable, WheelHandler
{
	//--- Fields ---
	//-- Layout --
	private Stage stage;
	private Scene scene;
	private GridPane root;
	private GridPane grid;
	
	//-- Wheels --
	private ClockEditor clockEditor;
	private Wheel[] wheels;
	private WheelEditor wheelEditor;
	
	//-- Threads --
	private boolean running = true;

	//--- Methods ---
	//-- Constructor --
	public Clock() {}
	public Clock(String[] args)
	{
		Hashtable<Integer, Wheel> map = new Hashtable<>();
		int wheelId = 0;
		String value;
		for(int i = 0; i < args.length; i++)
		{
			try
			{
				value = args[i].split("=")[1];
				args[i] = args[i].toLowerCase();
				if(args[i].startsWith("wheel:"))
				{
					wheelId = Integer.parseInt(args[i].split(":")[1]);
					if(map.get(wheelId) == null)
						map.put(wheelId, new Wheel());
					
					if(args[i].contains("wheelcolor"))
					{
						map.get(wheelId).setWheelColor(Color.web(value));
					}
					else if(args[i].contains("wheelnullcolor"))
					{
						map.get(wheelId).setWheelNullColor(Color.web(value));
					}
					else if(args[i].contains("wheelradius"))
					{
						map.get(wheelId).setWheelRadius(Double.parseDouble(value));
					}
					else if(args[i].contains("wheelthickness"))
					{
						map.get(wheelId).setWheelThickness(Double.parseDouble(value));
					}
					else if(args[i].contains("wheelstroked"))
					{
						map.get(wheelId).setWheelStroked(Boolean.parseBoolean(value));
					}
					else if(args[i].contains("valuecolor"))
					{
						map.get(wheelId).setValueColor(Color.web(value));
					}
					else if(args[i].contains("valuefont"))
					{
						map.get(wheelId).setValueFont(Font.font(value, map.get(wheelId).getValueFont() != null ? map.get(wheelId).getValueFont().getSize() : 24));
					}
					else if(args[i].contains("valueunit"))
					{
						map.get(wheelId).setvalueUnit(Integer.parseInt(value));
					}
					else if(args[i].contains("valuesize"))
					{
						map.get(wheelId).setValueFont(Font.font(map.get(wheelId).getValueFont() != null ? map.get(wheelId).getValueFont().getName() : new Font(24).getName(), Double.parseDouble(value)));
					}
					else if(args[i].contains("valuemaximum"))
					{
						map.get(wheelId).setValueMaximum(Integer.parseInt(value));
					}
					else if(args[i].contains("textcolor"))
					{
						map.get(wheelId).setTextColor(Color.web(value));
					}
					else if(args[i].contains("textfont"))
					{
						map.get(wheelId).setTextFont(Font.font(value, map.get(wheelId).getTextFont() != null ? map.get(wheelId).getTextFont().getSize() : 14));
					}
					else if(args[i].contains("textsize"))
					{
						map.get(wheelId).setTextFont(Font.font(map.get(wheelId).getTextFont() != null ? map.get(wheelId).getTextFont().getName() : new Font(24).getName(), Double.parseDouble(value)));
					}
					else if(args[i].contains("texty"))
					{
						map.get(wheelId).setTextY(Double.parseDouble(value));
					}
					else if(args[i].contains("textsg"))
					{
						map.get(wheelId).setTextSg(value);
					}
					else if(args[i].contains("textpl"))
					{
						map.get(wheelId).setTextPl(value);
					}
				}
				else if(args[i].startsWith("stage"))
				{
					if(args[i].contains("color"))
					{
						this.root = new GridPane();
						this.root.setBackground(new Background(new BackgroundFill(Color.web(value), null, null)));
					}
					else if(args[i].contains("fullscreen"))
					{
						this.stage = new Stage();
						this.stage.setFullScreen(Boolean.parseBoolean(value));
					}
					else if(args[i].contains("rotation"))
					{
						this.grid = new GridPane();
						this.grid.setRotate(Double.parseDouble(value));
					}
				}
			}
			catch(Exception ex)
			{
				continue;
			}
		}
		int[] wheelIds = new int[map.keySet().size()];
		int i = 0;
		for(Iterator<Integer> iterator = map.keySet().iterator(); iterator.hasNext();)
		{
			wheelIds[i++] = (int) iterator.next();
		}
		Arrays.sort(wheelIds);
		this.wheels = new Wheel[map.keySet().size()];
		for(int j = 0; j < wheelIds.length; j++)
		{
			this.wheels[j] = map.get(wheelIds[j]);
		}
	}
	
	//-- Application --
	@Override
	public void start(Stage stage) throws Exception
	{
		if(this.stage == null)
			this.stage = new Stage();
		this.stage.setTitle("WheelyClock - Clock");
		
		if(this.root == null)
			this.root = new GridPane();
		this.root.setAlignment(Pos.CENTER);
		if(this.root.getBackground() == null)
			this.setBgColor(Color.BLACK);
		
		if(this.grid == null)
			this.grid = new GridPane();
		this.grid.setAlignment(Pos.CENTER);
		this.grid.setHgap(10);
		
		if(this.wheels == null)
		{
			Rectangle2D screenRect = Screen.getPrimary().getVisualBounds();
			if(screenRect.getWidth() < screenRect.getHeight())
				this.setRotation(90);
			double screenSize = Math.max(screenRect.getWidth(), screenRect.getHeight());
			int numberOfWheels = 3;
			int space = 10;
			double wheelradius = ((screenSize - ((numberOfWheels+1) * space))/numberOfWheels/2);
			
			this.wheels = new Wheel[3];
			this.wheels[0] = new Wheel(
					Color.GREEN, // wheelColor
					wheelradius, // wheelRadius
					0.125, // wheelThickness
					true, // wheelStroked
					Color.rgb(0, 128, 0, 0.25), // wheelNullColor
					Color.rgb(128, 128, 128), // valueColor
					23, // valueMaximum
					0, // value
					new Font(75), // valueFont
					Calendar.HOUR_OF_DAY, // valueUnit
					Color.rgb(128, 128, 128), // textColor
					"Stunde", //textSg
					"Stunde", //textPl
					new Font(20), // textFont
					-0.45 // textY
					);
			this.wheels[1] = new Wheel(
					Color.BLUE, // wheelColor
					wheelradius, // wheelRadius
					0.125, // wheelThickness
					true, // wheelStroked
					Color.rgb(0, 0, 255, 0.25), // wheelNullColor
					Color.rgb(128, 128, 128), // valueColor
					59, // valueMaximum
					0, // value
					new Font(75), // valueFont
					Calendar.MINUTE, // valueUnit
					Color.rgb(128, 128, 128), // textColor
					"Minute", //textSg
					"Minute", //textPl
					new Font(20), // textFont
					-0.45 // textY
					);
			this.wheels[2] = new Wheel(
					Color.RED, // wheelColor
					wheelradius, // wheelRadius
					0.125, // wheelThickness
					true, // wheelStroked
					Color.rgb(255, 0, 0, 0.25), // wheelNullColor
					Color.rgb(128, 128, 128), // valueColor
					59, // valueMaximum
					0, // value
					new Font(75), // valueFont
					Calendar.SECOND, // valueUnit
					Color.rgb(128, 128, 128), // textColor
					"Sekunde", //textSg
					"Sekunde", //textPl
					new Font(20), // textFont
					-0.45 // textY
					);
		}
		for(int i = 0; i < this.wheels.length; i++)
		{
			this.wheels[i].setOnMouseClicked(this.wheels[i].getWheelEditorEventHandler(this.wheels, i, this));
			this.grid.add(this.wheels[i], i, 0);
		}
		this.root.add(this.grid, 0, 0);
		this.scene = new Scene(this.root);
		this.scene.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent ev)
			{
				if(ev.getButton() == MouseButton.SECONDARY)
				{
					if(Clock.this.clockEditor != null)
					{
						Clock.this.clockEditor.exit();
					}
					Clock.this.clockEditor = new ClockEditor(Clock.this);
					try
					{
						Clock.this.clockEditor.start(new Stage());
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		});
		this.scene.setOnKeyPressed(new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent ev)
			{
				if(ev.getCode() == KeyCode.ESCAPE)
					Clock.this.exit();
			}
		});
		this.stage.setScene(this.scene);
		this.stage.sizeToScene();
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>()
		{
			@Override
			public void handle(WindowEvent ev)
			{
				Clock.this.exit();
			}
		});
		this.stage.show();
		if(this.stage.isFullScreen())
		{
			this.stage.setFullScreen(false);
			this.stage.setFullScreen(true);
		}
		new Thread(this).start();
	}

	//-- Clock --
	public Color getBgColor()
	{
		return (Color) this.root.getBackground().getFills().get(0).getFill();
	}
	public boolean isFullScreen()
	{
		return this.stage.isFullScreen();
	}
	public double getRotation()
	{
		return this.grid.getRotate();
	}
	
	public int[] getUnits()
	{
		int[] units = new int[this.wheels.length];
		for(int i = 0; i < units.length; i++)
			units[i] = this.wheels[i].getValueUnit();
		return units;
	}
	
	@Override
	public WheelEditor getWheelEditor()
	{
		return this.wheelEditor;
	}
	
	public void setBgColor(Color bg)
	{
		this.root.setBackground(new Background(new BackgroundFill(bg, null, null)));
	}
	public void setFullScreen(boolean fullScreen)
	{
		this.stage.setFullScreen(fullScreen);
	}
	public void setRotation(double rotation)
	{
		this.grid.setRotate(rotation);
	}
	
	public void setUnits(int[] units)
	{
		this.grid.getChildren().clear();
		
		this.wheels = new Wheel[units.length];
		
		int maxValue = 0;
		String text = "";
		for(int i = 0; i < units.length; i++)
		{
			switch(units[i])
			{
			case Calendar.YEAR:
				maxValue = 5;
				text = "Jahr";
				break;
			case Calendar.MONTH:
				maxValue = 11;
				text = "Monat";
				break;
			case Calendar.DAY_OF_MONTH:
				maxValue = 31;
				text = "Tag";
				break;
			case Calendar.HOUR_OF_DAY:
				maxValue = 23;
				text = "Stunde";
				break;
			case Calendar.MINUTE:
				maxValue = 59;
				text = "Minute";
				break;
			case Calendar.SECOND:
				maxValue = 59;
				text = "Sekunde";
				break;
			case Calendar.MILLISECOND:
				maxValue = 1000;
				text = "Millisekunde";
				break;
			default:
				maxValue = 5;
				text = "Unbekannt";
				break;
			}
			this.wheels[i] = new Wheel(
					Color.BLACK, // wheelColor
					100, // wheelRadius
					0.125, // wheelThickness
					true, // wheelStroked
					Color.rgb(0, 0, 0, 0.25), // wheelNullColor
					Color.BLACK, // valueColor
					maxValue, // valueMaximum
					0, // value
					new Font(75), // valueFont
					units[i], // valueUnit
					Color.BLACK, // textColor
					text, //textSg
					text, //textPl
					new Font(20), // textFont
					0.6 // textY
					);
			this.wheels[i].setOnMouseClicked(this.wheels[i].getWheelEditorEventHandler(this.wheels, i, this));
			this.grid.add(this.wheels[i], i, 0);
		}
	}
	
	@Override
	public void setWheelEditor(WheelEditor wheelEditor)
	{
		this.wheelEditor = wheelEditor;
	}
	
	//-- Runnable --
	@Override
	public void run()
	{
		Calendar gc = GregorianCalendar.getInstance();
		while(this.running)
		{
			gc.setTimeInMillis(System.currentTimeMillis());
			for(int i = 0; i < Clock.this.wheels.length; i++)
			{
				Platform.runLater(new WheelUpdater(this.wheels[i], gc.get(Clock.this.wheels[i].getValueUnit())));
			}
			try
			{
				Thread.sleep(333);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	//-- Exit --
	private void exit()
	{
		this.running = false;
		if(this.wheelEditor != null)
		{
			this.wheelEditor.exit();
			this.wheelEditor = null;
		}
		if(this.clockEditor != null)
		{
			this.clockEditor.exit();
			this.clockEditor = null;
		}
		if(this.stage != null)
			this.stage.close();
		try
		{
			new Menu().start(new Stage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
