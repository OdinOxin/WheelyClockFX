package de.odinoxin.wheelyclock.countdown;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;

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

public class Countdown extends Application implements WheelHandler
{
	//--- Fields ---
	//-- Stage --
	private Stage stage;
	private Scene scene;
	private GridPane root;
	private GridPane grid;
	
	//-- Wheels --
	private CountdownEditor countdownEditor;
	private Wheel[] wheels;
	private WheelEditor wheelEditor;
	
	//-- Time --
	private long countTime = -1;
	private GregorianCalendar countDate;
	
	//-- Threads --
	private CountdownUpdater updater;
	private int updateRate = 250;
	
	//--- Methods ---
	//-- Constructor --
	public Countdown() {}
	public Countdown(String[] args)
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
						map.get(wheelId).setWheelColor(Color.web(value));
					else if(args[i].contains("wheelnullcolor"))
						map.get(wheelId).setWheelNullColor(Color.web(value));
					else if(args[i].contains("wheelradius"))
						map.get(wheelId).setWheelRadius(Double.parseDouble(value));
					else if(args[i].contains("wheelthickness"))
						map.get(wheelId).setWheelThickness(Double.parseDouble(value));
					else if(args[i].contains("wheelstroked"))
						map.get(wheelId).setWheelStroked(Boolean.parseBoolean(value));
					else if(args[i].contains("valuecolor"))
						map.get(wheelId).setValueColor(Color.web(value));
					else if(args[i].contains("valuefont"))
						map.get(wheelId).setValueFont(Font.font(value, map.get(wheelId).getValueFont() != null ? map.get(wheelId).getValueFont().getSize() : 24));
					else if(args[i].contains("valueunit"))
						map.get(wheelId).setvalueUnit(Integer.parseInt(value));
					else if(args[i].contains("valuesize"))
						map.get(wheelId).setValueFont(Font.font(map.get(wheelId).getValueFont() != null ? map.get(wheelId).getValueFont().getName() : new Font(24).getName(), Double.parseDouble(value)));
					else if(args[i].contains("valuemaximum"))
						map.get(wheelId).setValueMaximum(Integer.parseInt(value));
					else if(args[i].contains("textcolor"))
						map.get(wheelId).setTextColor(Color.web(value));
					else if(args[i].contains("textfont"))
						map.get(wheelId).setTextFont(Font.font(value, map.get(wheelId).getTextFont() != null ? map.get(wheelId).getTextFont().getSize() : 14));
					else if(args[i].contains("textsize"))
						map.get(wheelId).setTextFont(Font.font(map.get(wheelId).getTextFont() != null ? map.get(wheelId).getTextFont().getName() : new Font(24).getName(), Double.parseDouble(value)));
					else if(args[i].contains("texty"))
						map.get(wheelId).setTextY(Double.parseDouble(value));
					else if(args[i].contains("textsg"))
						map.get(wheelId).setTextSg(value);
					else if(args[i].contains("textpl"))
						map.get(wheelId).setTextPl(value);
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
				else if(args[i].startsWith("countdown"))
				{
					if(args[i].contains("year"))
					{
						if(this.countDate == null)
							this.countDate = new GregorianCalendar(Integer.parseInt(value), 0, 1);
						else
							this.countDate = new GregorianCalendar(Integer.parseInt(value),
									this.countDate.get(Calendar.MONTH),
									this.countDate.get(Calendar.DAY_OF_MONTH),
									this.countDate.get(Calendar.HOUR_OF_DAY), 
									this.countDate.get(Calendar.MINUTE),
									this.countDate.get(Calendar.SECOND));
					}
					else if(args[i].contains("month"))
					{
						if(this.countDate == null)
							this.countDate = new GregorianCalendar(0, Integer.parseInt(value)-1, 1);
						else
							this.countDate = new GregorianCalendar(this.countDate.get(Calendar.YEAR),
									Integer.parseInt(value)-1,
									this.countDate.get(Calendar.DAY_OF_MONTH),
									this.countDate.get(Calendar.HOUR_OF_DAY), 
									this.countDate.get(Calendar.MINUTE),
									this.countDate.get(Calendar.SECOND));
					}
					else if(args[i].contains("day"))
					{
						if(this.countDate == null)
							this.countTime += 1000 * 60 * 60 * 24 * Integer.parseInt(value);
						else
							this.countDate = new GregorianCalendar(this.countDate.get(Calendar.YEAR),
									this.countDate.get(Calendar.MONTH),
									Integer.parseInt(value),
									this.countDate.get(Calendar.HOUR_OF_DAY), 
									this.countDate.get(Calendar.MINUTE),
									this.countDate.get(Calendar.SECOND));
					}
					else if(args[i].contains("hour"))
					{
						if(this.countDate == null)
							this.countTime += 1000 * 60 * 60 * Integer.parseInt(value);
						else
							this.countDate = new GregorianCalendar(this.countDate.get(Calendar.YEAR),
									this.countDate.get(Calendar.MONTH),
									this.countDate.get(Calendar.DAY_OF_MONTH),
									Integer.parseInt(value),
									this.countDate.get(Calendar.MINUTE),
									this.countDate.get(Calendar.SECOND));
					}
					else if(args[i].contains("minute"))
					{
						if(this.countDate == null)
							this.countTime += 1000 * 60 * Integer.parseInt(value);
						else
							this.countDate = new GregorianCalendar(this.countDate.get(Calendar.YEAR),
									this.countDate.get(Calendar.MONTH),
									this.countDate.get(Calendar.DAY_OF_MONTH),
									this.countDate.get(Calendar.HOUR_OF_DAY),
									Integer.parseInt(value),
									this.countDate.get(Calendar.SECOND));
					}
					else if(args[i].contains("second"))
					{
						if(this.countDate == null)
							this.countTime += 1000 * Integer.parseInt(value);
						else
							this.countDate = new GregorianCalendar(this.countDate.get(Calendar.YEAR),
									this.countDate.get(Calendar.MONTH),
									this.countDate.get(Calendar.DAY_OF_MONTH),
									this.countDate.get(Calendar.HOUR_OF_DAY),
									this.countDate.get(Calendar.MINUTE),
									Integer.parseInt(value));
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
		for(int id : map.keySet())
			wheelIds[i++] = id;
		Arrays.sort(wheelIds);
		this.wheels = new Wheel[map.keySet().size()];
		for(int j = 0; j < wheelIds.length; j++)
			this.wheels[j] = map.get(wheelIds[j]);
	}
	
	//-- Application --
	@Override
	public void start(Stage stage) throws Exception
	{
		if(this.stage == null)
			this.stage = new Stage();
		this.stage.setTitle("WheelyClock - Countdown");
		
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
			int numberOfWheels = 4;
			int space = 10;
			double wheelradius = ((screenSize - ((numberOfWheels+1) * space))/numberOfWheels/2);
			
			this.wheels = new Wheel[numberOfWheels];
			this.wheels[0] = new Wheel(
					Color.ORANGE, // wheelColor
					wheelradius, // wheelRadius
					0.125, // wheelThickness
					true, // wheelStroked
					Color.rgb(255, 165, 0, 0.25), // wheelNullColor
					Color.rgb(128, 128, 128), // valueColor
					31, // valueMaximum
					0, // value
					new Font(75), // valueFont
					Calendar.DAY_OF_YEAR, // valueUnit
					Color.rgb(128, 128, 128), // textColor
					"Tag", //textSg
					"Tage", //textPl
					new Font(20), // textFont
					0.6 // textY
					);
			this.wheels[1] = new Wheel(
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
					"Stunden", //textPl
					new Font(20), // textFont
					0.6 // textY
					);
			this.wheels[2] = new Wheel(
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
					"Minuten", //textPl
					new Font(20), // textFont
					0.6 // textY
					);
			this.wheels[3] = new Wheel(
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
					"Sekunden", //textPl
					new Font(20), // textFont
					0.6 // textY
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
					if(Countdown.this.countdownEditor != null)
					{
						Countdown.this.countdownEditor.exit();
					}
					Countdown.this.countdownEditor = new CountdownEditor(Countdown.this);
					try
					{
						Countdown.this.countdownEditor.start(new Stage());
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
					Countdown.this.exit();
			}
		});
		this.stage.setScene(this.scene);
		this.stage.sizeToScene();
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>()
		{
			@Override
			public void handle(WindowEvent ev)
			{
				Countdown.this.exit();
			}
		});
		this.stage.show();
		if(this.stage.isFullScreen())
		{
			this.stage.setFullScreen(false);
			this.stage.setFullScreen(true);
		}
		if(this.countDate == null
		&& this.countTime == -1)
		{
			this.countDate = new GregorianCalendar();
			this.countDate = new GregorianCalendar(this.countDate.get(Calendar.YEAR)+1, Calendar.JANUARY, 1, 0, 0);
		}
		
		this.startCountdown();
	}
	
	//-- Getters --
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
	
	public long getCountTime()
	{
		return this.countTime;
	}
	public GregorianCalendar getCountDate()
	{
		return this.countDate;
	}
	
	public int getUpdateRate()
	{
		return this.updateRate;
	}
	
	@Override
	public WheelEditor getWheelEditor()
	{
		return this.wheelEditor;
	}
	
	//-- Setters --
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
		String textSg = "";
		String textPl = "";
		for(int i = 0; i < units.length; i++)
		{
			switch(units[i])
			{
			case Calendar.DAY_OF_YEAR:
				maxValue = 31;
				textSg = "Tag";
				textPl = "Tage";
				break;
			case Calendar.HOUR_OF_DAY:
				maxValue = 23;
				textSg = "Stunde";
				textPl = "Stunden";
				break;
			case Calendar.MINUTE:
				maxValue = 59;
				textSg = "Minute";
				textPl = "Minuten";
				break;
			case Calendar.SECOND:
				maxValue = 59;
				textSg = "Sekunde";
				textPl = "Sekunden";
				break;
			case Calendar.MILLISECOND:
				maxValue = 1000;
				textSg = "Millisekunde";
				textPl = "Millisekunden";
				break;
			default:
				maxValue = 5;
				textSg = "Unbekannt";
				textPl = "Unbekannte";
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
					textSg, //textSg
					textPl, //textPl
					new Font(20), // textFont
					0.6 // textY
					);
			this.wheels[i].setOnMouseClicked(this.wheels[i].getWheelEditorEventHandler(this.wheels, i, this));
			this.grid.add(this.wheels[i], i, 0);
		}
	}
	
	public void setCountTime(long countDown)
	{
		this.countTime = countDown;
		this.countDate = null;
	}
	public void setCountDate(GregorianCalendar countDate)
	{
		this.countDate = countDate;
		this.countTime = -1;
	}
	
	public void setUpdateRate(int updateRate)
	{
		this.updateRate = updateRate;
	}
	
	@Override
	public void setWheelEditor(WheelEditor wheelEditor)
	{
		this.wheelEditor = wheelEditor;
	}

	//-- Countdown Starter --
	public void startCountdown()
	{
		this.stopCountdown();
		if(this.countDate != null
		&& this.countTime < 0)
			this.updater = new CountdownUpdater(this, this.countDate, this.updateRate);
		else
			this.updater = new CountdownUpdater(this, this.countTime, this.updateRate);
		new Thread(this.updater).start();
	}
	public void stopCountdown()
	{
		if(this.updater != null)
			this.updater.cancel();
	}
	
	//-- Exit --
	private void exit()
	{
		this.stopCountdown();
		if(this.wheelEditor != null)
		{
			this.wheelEditor.exit();
			this.wheelEditor = null;
		}
		if(Countdown.this.countdownEditor != null)
		{
			this.countdownEditor.exit();
			this.countdownEditor = null;
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
	
	//--- Classes ---
	private class CountdownUpdater implements Runnable
	{
		private final Countdown countdown;
		private final GregorianCalendar dateTo;
		private final long downFrom;
		private boolean running = true;
		private final int updateRate;
		
		public CountdownUpdater(Countdown countdown, GregorianCalendar dateTo, int updateRate)
		{
			this.countdown = countdown;
			this.dateTo = (GregorianCalendar) dateTo.clone();
			this.updateRate = updateRate;
			
			this.downFrom = -1;
		}
		public CountdownUpdater(Countdown countdown, long downFrom, int updateRate)
		{
			this.countdown = countdown;
			this.downFrom = downFrom;
			this.updateRate = updateRate;
			
			this.dateTo = null;
		}
		
		//- Runnable -
		@Override
		public void run()
		{
			long timeLeft = 0;
			
			long time;
			int days;
			int hours;
			int minutes;
			int seconds;
			int millies;
			
			if(this.downFrom >= 0)
			{
				timeLeft = this.downFrom;
			}
			
			GregorianCalendar lastLoop = new GregorianCalendar();
			while(this.running
			&& timeLeft >= 0)
			{
				if(this.countdown.countdownEditor != null)
					this.countdown.countdownEditor.refreshBar();
				if(this.dateTo != null)
				{
					timeLeft = this.dateTo.getTimeInMillis() - System.currentTimeMillis();
				}
				else
				{
					timeLeft -= System.currentTimeMillis() - lastLoop.getTimeInMillis();
					lastLoop = new GregorianCalendar();
				}
				
				time = timeLeft;
				millies = (int) (time % 1000); time /= 1000;
				seconds = (int) (time % 60); time /= 60;
				minutes = (int) (time % 60); time /= 60;
				hours = (int) (time % 24); time /= 24;
				days = (int) time;
				for(int i = 0; i < Countdown.this.wheels.length; i++)
				{
					switch(Countdown.this.wheels[i].getValueUnit())
					{
					case Calendar.DAY_OF_YEAR:
						Platform.runLater(new WheelUpdater(this.countdown.wheels[i], days));
						break;
					case Calendar.HOUR_OF_DAY:
						Platform.runLater(new WheelUpdater(this.countdown.wheels[i], hours));
						break;
					case Calendar.MINUTE:
						Platform.runLater(new WheelUpdater(this.countdown.wheels[i], minutes));
						break;
					case Calendar.SECOND:
						Platform.runLater(new WheelUpdater(this.countdown.wheels[i], seconds));
						break;
					case Calendar.MILLISECOND:
						Platform.runLater(new WheelUpdater(this.countdown.wheels[i], millies));
						break;
					}
				}
				try
				{
					Thread.sleep(this.updateRate);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			for(int i = 0; i < Countdown.this.wheels.length; i++)
			{
				Platform.runLater(new WheelUpdater(this.countdown.wheels[i], 0));
			}
		}
		
		public void cancel()
		{
			this.running = false;
		}
	}
}
