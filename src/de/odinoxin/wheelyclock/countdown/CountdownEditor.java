package de.odinoxin.wheelyclock.countdown;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import de.odinoxin.wheelyclock.Editor;

public class CountdownEditor extends Application implements Editor
{
	//--- Fields ---
	private static final int[] UNITS = {
		-1,
		Calendar.DAY_OF_YEAR,
		Calendar.HOUR_OF_DAY,
		Calendar.MINUTE,
		Calendar.SECOND,
		Calendar.MILLISECOND,
	};
	private static final Priority[] COLUMN_WIDTHS = {Priority.SOMETIMES, Priority.SOMETIMES, Priority.SOMETIMES, Priority.SOMETIMES, Priority.SOMETIMES, Priority.ALWAYS, Priority.ALWAYS};
	
	private Stage stage;
	private Scene scene;
	private GridPane grid;
	private Countdown countdown;
	
	private Text lblStageHeader = new Text("Fenster");
	private Text lblStageBgColor = new Text("Hintergrundfarbe");
	private Text lblStageFullScreen = new Text("Vollbild");
	private Text lblStageRotation = new Text("Rotation");
	
	private Text lblUnitsHeader = new Text("Einheiten");
	
	private Text lblTimeHeader = new Text("Zeiteinstellungen");
	private Text lblTimeHour = new Text("Stunde(n)");
	private Text lblTimeMinute = new Text("Minute(n)");
	private Text lblTimeSecond = new Text("Sekunde(n)");
	
	private Text lblTimeControl = new Text("Steuerung");
	private Text lblTimeRate = new Text("Aktualisierungsrate");
	
	private ColorPicker clpStageBgColor;
	private CheckBox cbxStageFullScreen;
	private Slider sdrStageRotation = new Slider(0, 360, 0);
	
	private VBox boxUnits;
	
	private ToggleGroup tglGroup = new ToggleGroup();
	private RadioButton rbtCountDate = new RadioButton("Datum");
	private RadioButton rbtCountTime = new RadioButton("Tag(e)");
	private DatePicker dtpTimeDate = new DatePicker();
	private TextField txfTimeDay = new TextField();
	private TextField txfTimeHour = new TextField();
	private TextField txfTimeMinute = new TextField();
	private TextField txfTimeSecond = new TextField();
	
	private Button btnTimeStart = new Button("Start");
	private Button btnTimeStop = new Button("Stop");
	private CheckBox cbxAutoUpdate = new CheckBox("Countdown automatisch aktualisieren");
	private Slider sdrTimeRate = new Slider(1, 500, 250);
	private ProgressBar pgbTimeRefresh = new ProgressBar(0);
	
	private Button btnOk = new Button("Ok");
	
	private boolean autoUpdate;
	private int days;
	private int hours;
	private int minutes;
	private int seconds;
	
	private TimeRefreshUpdater trUpdater;
	
	//--- Methods ---
	//-- Constructor --
	public CountdownEditor(Countdown countdown)
	{
		this.countdown = countdown;
		this.trUpdater = new TimeRefreshUpdater();
	}
	
	//-- Application --
	@Override
	public void start(Stage stage) throws Exception
	{
		this.stage = stage;
		this.stage.setTitle("WheelyClock - Countdown Editor");
		
		this.grid = new GridPane();
		this.grid.setAlignment(Pos.CENTER);
		this.grid.setHgap(10);
		this.grid.setVgap(10);
		this.grid.setPadding(new Insets(10, 10, 10, 10));
		
		for(int i = 0; i < CountdownEditor.COLUMN_WIDTHS.length; i++)
		{
			ColumnConstraints cc = new ColumnConstraints();
			cc.setHgrow(CountdownEditor.COLUMN_WIDTHS[i]);
			this.grid.getColumnConstraints().add(cc);
		}
		
		this.lblStageHeader.setFont(Font.font(this.lblStageHeader.getFont().getName(), FontWeight.BOLD, this.lblStageHeader.getFont().getSize()));
		this.lblUnitsHeader.setFont(Font.font(this.lblUnitsHeader.getFont().getName(), FontWeight.BOLD, this.lblUnitsHeader.getFont().getSize()));
		this.lblTimeHeader.setFont(Font.font(this.lblTimeHeader.getFont().getName(), FontWeight.BOLD, this.lblTimeHeader.getFont().getSize()));
		this.lblTimeControl.setFont(Font.font(this.lblTimeControl.getFont().getName(), FontWeight.BOLD, this.lblTimeControl.getFont().getSize()));
		
		this.clpStageBgColor = new ColorPicker(this.countdown.getBgColor());
		this.clpStageBgColor.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent ev)
			{
				CountdownEditor.this.countdown.setBgColor(CountdownEditor.this.clpStageBgColor.getValue());
			}
		});
		this.cbxStageFullScreen = new CheckBox();
		this.cbxStageFullScreen.setSelected(this.countdown.isFullScreen());
		this.cbxStageFullScreen.selectedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal)
			{
				CountdownEditor.this.countdown.setFullScreen(newVal);
			}
		});
		this.sdrStageRotation.setMinorTickCount(0);
		this.sdrStageRotation.setMajorTickUnit(45);
		this.sdrStageRotation.setSnapToTicks(true);
		this.sdrStageRotation.setShowTickMarks(true);
		this.sdrStageRotation.setShowTickLabels(true);
		this.sdrStageRotation.setPrefWidth(200);
		this.sdrStageRotation.setValue(this.countdown.getRotation());
		this.sdrStageRotation.valueProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal)
			{
				CountdownEditor.this.countdown.setRotation((double) newVal);
			}
		});
		
		this.boxUnits = new VBox(5);
		for(int i = 0; i < this.countdown.getUnits().length; i++)
		{
			ComboBox<ComboBoxItem> cbo = this.createUnitCbo();
			int selection = 0;
			switch(this.countdown.getUnits()[i])
			{
			case Calendar.DAY_OF_YEAR:
				selection = 1;
				break;
			case Calendar.HOUR_OF_DAY:
				selection = 2;
				break;
			case Calendar.MINUTE:
				selection = 3;
				break;
			case Calendar.SECOND:
				selection = 4;
				break;
			case Calendar.MILLISECOND:
				selection = 5;
				break;
			}
			cbo.getSelectionModel().select(selection);
			this.addListenerToComboBox(cbo);
			this.boxUnits.getChildren().add(cbo);
		}
		ComboBox<ComboBoxItem> emptyCbo = this.createUnitCbo();
		this.addListenerToComboBox(emptyCbo);
		this.boxUnits.getChildren().add(emptyCbo);
		
		this.rbtCountDate.setToggleGroup(this.tglGroup);
		this.rbtCountTime.setToggleGroup(this.tglGroup);
		this.rbtCountDate.selectedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal)
			{
				CountdownEditor.this.txfTimeDay.setDisable(newVal);
				if(CountdownEditor.this.autoUpdate)
					CountdownEditor.this.updateCountdown();
			}
		});
		this.rbtCountTime.selectedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal)
			{
				CountdownEditor.this.dtpTimeDate.setDisable(newVal);
				if(CountdownEditor.this.autoUpdate)
					CountdownEditor.this.updateCountdown();
			}
		});
		
		long countTime = this.countdown.getCountTime();
		GregorianCalendar cal = this.countdown.getCountDate();
		if(countTime >= 0)
		{
			LocalDate nowDate = LocalDate.now();
			countTime /= 1000;
			this.seconds = (int) (countTime % 60); countTime /= 60;
			this.minutes = (int) (countTime % 60); countTime /= 60;
			this.hours = (int) (countTime % 24); countTime /= 24;
			this.days = (int) countTime;
			this.dtpTimeDate.setValue(LocalDate.of(nowDate.getYear(), nowDate.getMonth(), nowDate.getDayOfMonth()+1));
			this.rbtCountTime.setSelected(true);
		}
		else if(cal != null)
		{
			this.dtpTimeDate.setValue(LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH)));
			this.days = 1;
			this.hours = cal.get(Calendar.HOUR_OF_DAY);
			this.minutes = cal.get(Calendar.MINUTE);
			this.seconds = cal.get(Calendar.SECOND);
			this.rbtCountDate.setSelected(true);
		}
		this.txfTimeDay.setText(String.valueOf(this.days));
		this.txfTimeHour.setText(String.valueOf(this.hours));
		this.txfTimeMinute.setText(String.valueOf(this.minutes));
		this.txfTimeSecond.setText(String.valueOf(this.seconds));
		
		this.cbxAutoUpdate.selectedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal)
			{
				CountdownEditor.this.autoUpdate = newVal;
			}
		});
		this.cbxAutoUpdate.setSelected(true);
		
		this.dtpTimeDate.valueProperty().addListener(new ChangeListener<LocalDate>()
		{
			@Override
			public void changed(ObservableValue<? extends LocalDate> obsVal, LocalDate oldVal, LocalDate newVal)
			{
				if(CountdownEditor.this.autoUpdate)
					CountdownEditor.this.updateCountdown();
			}
		});
		this.txfTimeDay.textProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> obsVal, String oldVal, String newVal)
			{
				try
				{
					int days = Integer.parseInt(newVal);
					if(days >= 0)
					{
						CountdownEditor.this.days = days;
						if(CountdownEditor.this.autoUpdate)
							CountdownEditor.this.updateCountdown();
					}
					else
					{
						CountdownEditor.this.txfTimeDay.setText(String.valueOf(CountdownEditor.this.days));
					}
				}
				catch(NumberFormatException ex)
				{
					CountdownEditor.this.txfTimeDay.setText(String.valueOf(CountdownEditor.this.days));
				}
			}
		});
		this.txfTimeHour.textProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> obsVal, String oldVal, String newVal)
			{
				try
				{
					int hours = Integer.parseInt(newVal);
					if(hours >= 0
					&& hours <= 23)
					{
						CountdownEditor.this.hours = hours;
						if(CountdownEditor.this.autoUpdate)
							CountdownEditor.this.updateCountdown();
					}
					else
					{
						CountdownEditor.this.txfTimeHour.setText(String.valueOf(CountdownEditor.this.hours));
					}
				}
				catch(NumberFormatException ex)
				{
					CountdownEditor.this.txfTimeHour.setText(String.valueOf(CountdownEditor.this.hours));
				}
			}
		});
		this.txfTimeMinute.textProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> obsVal, String oldVal, String newVal)
			{
				try
				{
					int minutes = Integer.parseInt(newVal);
					if(minutes >= 0
					&& minutes <= 59)
					{
						CountdownEditor.this.minutes = minutes;
						if(CountdownEditor.this.autoUpdate)
							CountdownEditor.this.updateCountdown();
					}
					else
					{
						CountdownEditor.this.txfTimeMinute.setText(String.valueOf(CountdownEditor.this.minutes));
					}
				}
				catch(NumberFormatException ex)
				{
					CountdownEditor.this.txfTimeMinute.setText(String.valueOf(CountdownEditor.this.minutes));
				}
			}
		});
		this.txfTimeSecond.textProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> obsVal, String oldVal, String newVal)
			{
				try
				{
					int seconds = Integer.parseInt(newVal);
					if(seconds >= 0
					&& seconds <= 59)
					{
						CountdownEditor.this.seconds = seconds;
						if(CountdownEditor.this.autoUpdate)
							CountdownEditor.this.updateCountdown();
					}
					else
					{
						CountdownEditor.this.txfTimeSecond.setText(String.valueOf(CountdownEditor.this.seconds));
					}
				}
				catch(NumberFormatException ex)
				{
					CountdownEditor.this.txfTimeSecond.setText(String.valueOf(CountdownEditor.this.seconds));
				}
			}
		});
		
		GridPane.setHalignment(this.btnTimeStart, HPos.RIGHT);
		this.btnTimeStart.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent ev)
			{
				if(CountdownEditor.this.rbtCountDate.isSelected())
					CountdownEditor.this.updateCountdown();
				else
					CountdownEditor.this.updateCountdown();
			}
		});
		this.btnTimeStart.setTextFill(Color.GREEN);
		this.btnTimeStop.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent ev)
			{
				CountdownEditor.this.countdown.stopCountdown();
			}
		});
		this.btnTimeStop.setTextFill(Color.RED);
		this.sdrTimeRate.setValue(this.countdown.getUpdateRate());
		this.sdrTimeRate.valueProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal)
			{
				CountdownEditor.this.countdown.setUpdateRate((int) ((double) newVal));
				if(CountdownEditor.this.autoUpdate)
					CountdownEditor.this.updateCountdown();
			}
		});
		
		this.pgbTimeRefresh.prefWidthProperty().bind(this.sdrTimeRate.widthProperty());
		this.pgbTimeRefresh.setTooltip(new Tooltip("Füllt sich, wenn die Anzeige aktualisiert wird."));

		GridPane.setHalignment(this.btnOk, HPos.RIGHT);
		this.btnOk.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent ev)
			{
				CountdownEditor.this.exit();
			}
		});
		
		this.grid.add(this.lblStageHeader, 0, 0, 2, 1);
		this.grid.add(this.lblStageBgColor, 0, 1);
		this.grid.add(this.lblStageFullScreen, 0, 2);
		this.grid.add(this.lblStageRotation, 0, 3);
		
		this.grid.add(this.lblUnitsHeader, 2, 0);
		
		this.grid.add(this.lblTimeHeader, 3, 0, 2, 1);
		this.grid.add(this.lblTimeHour, 3, 3);
		this.grid.add(this.lblTimeMinute, 3, 4);
		this.grid.add(this.lblTimeSecond, 3, 5);
		
		this.grid.add(this.lblTimeControl, 5, 0, 2, 1);
		this.grid.add(this.lblTimeRate, 5, 3, 2, 1);
		
		this.grid.add(this.clpStageBgColor, 1, 1);
		this.grid.add(this.cbxStageFullScreen, 1, 2);
		this.grid.add(this.sdrStageRotation, 1, 3);
		
		this.grid.add(this.boxUnits, 2, 1, 1, 6);
		
		this.grid.add(this.rbtCountDate, 3, 1, 2, 1);
		this.grid.add(this.rbtCountTime, 3, 2, 2, 1);
		this.grid.add(this.dtpTimeDate, 4, 1);
		this.grid.add(this.txfTimeDay, 4, 2);
		this.grid.add(this.txfTimeHour, 4, 3);
		this.grid.add(this.txfTimeMinute, 4, 4);
		this.grid.add(this.txfTimeSecond, 4, 5);
		
		this.grid.add(this.btnTimeStart, 5, 1);
		this.grid.add(this.btnTimeStop, 6, 1);
		this.grid.add(this.cbxAutoUpdate, 5, 2, 2, 1);
		this.grid.add(this.sdrTimeRate, 5, 4, 2, 1);
		this.grid.add(this.pgbTimeRefresh, 5, 5, 2, 1);
		
		this.grid.add(new Separator(Orientation.HORIZONTAL), 0, 7, 7, 1);
		this.grid.add(this.btnOk, 6, 8);
		
		this.scene = new Scene(this.grid);
		this.stage.setScene(this.scene);
		this.stage.sizeToScene();
		this.stage.show();
	}
	
	//-- Countdown --
	private void updateCountdown()
	{
		if(this.rbtCountDate.isSelected())
		{
			GregorianCalendar gc = new GregorianCalendar();
			LocalDate selectedDate = this.dtpTimeDate.getValue(); 
			gc.set(selectedDate.getYear(), selectedDate.getMonth().getValue()-1, selectedDate.getDayOfMonth(), this.hours, this.minutes, this.seconds);
			this.countdown.setCountDate(gc);
		}
		else
		{
			this.countdown.setCountTime(
				1000 * this.seconds +
				1000 * 60 * this.minutes +
				1000 * 60 * 60 * this.hours +
				1000 * 60 * 60 * 24 * this.days);
		}
		this.countdown.setUpdateRate((int) this.sdrTimeRate.getValue());
		this.countdown.startCountdown();
	}
	
	//-- Refresh --
	public void refreshBar()
	{
		Platform.runLater(new Thread(this.trUpdater));
	}
	
	//-- ComboBoxes --
	private ComboBox<ComboBoxItem> createUnitCbo()
	{
		final ComboBox<ComboBoxItem> cbo = new ComboBox<>();
		cbo.getItems().addAll(
		new ComboBoxItem(CountdownEditor.UNITS[0], ""),
		new ComboBoxItem(CountdownEditor.UNITS[1], "Tag"),
		new ComboBoxItem(CountdownEditor.UNITS[2], "Stunde"),
		new ComboBoxItem(CountdownEditor.UNITS[3], "Minute"),
		new ComboBoxItem(CountdownEditor.UNITS[4], "Sekunde"),
		new ComboBoxItem(CountdownEditor.UNITS[5], "Millisekunde")
		);
		cbo.getSelectionModel().select(0);
		return cbo;
	}
	private void addListenerToComboBox(final ComboBox<ComboBoxItem> cbo)
	{
		cbo.valueProperty().addListener(new ChangeListener<ComboBoxItem>()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void changed(ObservableValue<? extends ComboBoxItem> obsVal, ComboBoxItem oldVal, ComboBoxItem newVal)
			{
				if(newVal.getValue() == -1)
				{
					//- Remove all following ComboBoxes -
					for(int i = 0; i < CountdownEditor.this.boxUnits.getChildren().size(); i++)
					{
						if(CountdownEditor.this.boxUnits.getChildren().get(i) == cbo)
						{
							for(int j = i+1; j < CountdownEditor.this.boxUnits.getChildren().size();)
							{
								CountdownEditor.this.boxUnits.getChildren().remove(j);
							}
							break;
						}
					}
				}
				else
				{
					//- Add new empty ComboBox, if necessary -
					if(CountdownEditor.this.boxUnits.getChildren().size() > 0)
					{
						if(((ComboBox<ComboBoxItem>) CountdownEditor.this.boxUnits.getChildren().get(CountdownEditor.this.boxUnits.getChildren().size()-1)).getSelectionModel().getSelectedItem().getValue() != -1)
						{
							ComboBox<ComboBoxItem> emptyCbo = CountdownEditor.this.createUnitCbo();
							CountdownEditor.this.addListenerToComboBox(emptyCbo);
							CountdownEditor.this.boxUnits.getChildren().add(emptyCbo);
							CountdownEditor.this.stage.sizeToScene();
						}
					}
				}
				
				//- Update Clock -
				if(CountdownEditor.this.boxUnits.getChildren().size() > 0)
				{
					int[] units = new int[CountdownEditor.this.boxUnits.getChildren().size()-1];
					for(int i = 0; i < units.length; i++)
					{
						units[i] = ((ComboBox<ComboBoxItem>) CountdownEditor.this.boxUnits.getChildren().get(i)).getSelectionModel().getSelectedItem().getValue();
					}
					CountdownEditor.this.countdown.setUnits(units);
				}
				else
				{
					CountdownEditor.this.countdown.setUnits(new int[0]);
				}
			}
		});
	}
	
	//-- Editor --
	@Override
	public void exit()
	{
		if(this.stage != null)
		{
			this.stage.close();
			this.stage = null;
		}
	}

	//--- Classes ---
	private class TimeRefreshUpdater implements Runnable
	{
		@Override
		public void run()
		{
			double d = CountdownEditor.this.pgbTimeRefresh.getProgress() + 0.01;
			if(d >= 1)
				d = 0;
			CountdownEditor.this.pgbTimeRefresh.setProgress(d);
		}
	}

	private class ComboBoxItem
	{
		private final int value;
		private final String text;
		
		public ComboBoxItem(int value, String text)
		{
			this.value = value;
			this.text = text;
		}
		
		public int getValue()
		{
			return this.value;
		}
		
		@Override
		public String toString()
		{
			return this.text;
		}
	}
}
