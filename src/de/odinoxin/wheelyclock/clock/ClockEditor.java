package de.odinoxin.wheelyclock.clock;

import java.util.Calendar;

import de.odinoxin.wheelyclock.Editor;
import javafx.application.Application;
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
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClockEditor extends Application implements Editor
{
	//--- Fields ---
	private static final int[] UNITS = {
		-1,
		Calendar.YEAR,
		Calendar.MONTH,
		Calendar.DAY_OF_MONTH,
		Calendar.HOUR_OF_DAY,
		Calendar.MINUTE,
		Calendar.SECOND,
		Calendar.MILLISECOND,
	};
	private static final Priority[] COLUMN_WIDTHS = {Priority.SOMETIMES, Priority.ALWAYS, Priority.SOMETIMES};
	
	private Stage stage;
	private Scene scene;
	private GridPane grid;
	private Clock clock;
	
	private Text lblStageHeader = new Text("Fenster");
	private Text lblStageBgColor = new Text("Hintergrundfarbe");
	private Text lblStageFullScreen = new Text("Vollbild");
	private Text lblStageRotation = new Text("Rotation");
	
	private Text lblUnitsHeader = new Text("Einheiten");

	private ColorPicker clpStageBgColor;
	private CheckBox cbxStageFullScreen;
	private Slider sdrStageRotation = new Slider(0, 360, 0);
	
	private VBox boxUnits;
	
	private Button btnOk = new Button("Ok");
	
	//--- Constructor ---
	public ClockEditor(Clock clock)
	{
		this.clock = clock;
	}
	
	//--- Methods ---
	//- Application -
	@Override
	public void start(Stage stage) throws Exception
	{
		this.stage = stage;
		this.stage.setTitle("WheelyClock - Clock Editor");
		
		this.grid = new GridPane();
		this.grid.setAlignment(Pos.CENTER);
		this.grid.setHgap(10);
		this.grid.setVgap(10);
		this.grid.setPadding(new Insets(10, 10, 10, 10));
		
		for(int i = 0; i < ClockEditor.COLUMN_WIDTHS.length; i++)
		{
			ColumnConstraints cc = new ColumnConstraints();
			cc.setHgrow(ClockEditor.COLUMN_WIDTHS[i]);
			this.grid.getColumnConstraints().add(cc);
		}
		
		this.lblStageHeader.setFont(Font.font(this.lblStageHeader.getFont().getName(), FontWeight.BOLD, this.lblStageHeader.getFont().getSize()));
		this.lblUnitsHeader.setFont(Font.font(this.lblUnitsHeader.getFont().getName(), FontWeight.BOLD, this.lblUnitsHeader.getFont().getSize()));
		
		this.clpStageBgColor = new ColorPicker(this.clock.getBgColor());
		this.clpStageBgColor.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent ev)
			{
				ClockEditor.this.clock.setBgColor(ClockEditor.this.clpStageBgColor.getValue());
			}
		});
		this.cbxStageFullScreen = new CheckBox();
		this.cbxStageFullScreen.setSelected(this.clock.isFullScreen());
		this.cbxStageFullScreen.selectedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal)
			{
				ClockEditor.this.clock.setFullScreen(newVal);
			}
		});
		this.sdrStageRotation.setMinorTickCount(0);
		this.sdrStageRotation.setMajorTickUnit(45);
		this.sdrStageRotation.setSnapToTicks(true);
		this.sdrStageRotation.setShowTickMarks(true);
		this.sdrStageRotation.setShowTickLabels(true);
		this.sdrStageRotation.setPrefWidth(200);
		this.sdrStageRotation.setValue(this.clock.getRotation());
		this.sdrStageRotation.valueProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal)
			{
				ClockEditor.this.clock.setRotation((double) newVal);
			}
		});
		
		this.boxUnits = new VBox(5);
		for(int i = 0; i < this.clock.getUnits().length; i++)
		{
			ComboBox<ComboBoxItem> cbo = this.createUnitCbo();
			int selection = 0;
			switch(this.clock.getUnits()[i])
			{
			case Calendar.YEAR:
				selection = 1;
				break;
			case Calendar.MONTH:
				selection = 2;
				break;
			case Calendar.DAY_OF_MONTH:
				selection = 3;
				break;
			case Calendar.HOUR_OF_DAY:
				selection = 4;
				break;
			case Calendar.MINUTE:
				selection = 5;
				break;
			case Calendar.SECOND:
				selection = 6;
				break;
			case Calendar.MILLISECOND:
				selection = 7;
				break;
			}
			cbo.getSelectionModel().select(selection);
			this.addListenerToComboBox(cbo);
			this.boxUnits.getChildren().add(cbo);
		}
		ComboBox<ComboBoxItem> emptyCbo = this.createUnitCbo();
		this.addListenerToComboBox(emptyCbo);
		this.boxUnits.getChildren().add(emptyCbo);
		
		GridPane.setHalignment(btnOk, HPos.RIGHT);
		this.btnOk.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent ev)
			{
				ClockEditor.this.exit();
			}
		});
		
		this.grid.add(this.lblStageHeader, 0, 0, 2, 1);
		this.grid.add(this.lblStageBgColor, 0, 1);
		this.grid.add(this.lblStageFullScreen, 0, 2);
		this.grid.add(this.lblStageRotation, 0, 3);
		
		this.grid.add(this.lblUnitsHeader, 2, 0);
		
		this.grid.add(this.clpStageBgColor, 1, 1);
		this.grid.add(this.cbxStageFullScreen, 1, 2);
		this.grid.add(this.sdrStageRotation, 1, 3);
		
		this.grid.add(this.boxUnits, 2, 1, 1, 4);
		
		this.grid.add(new Separator(Orientation.HORIZONTAL), 0, 5, 3, 1);
		this.grid.add(this.btnOk, 2, 6);
		
		this.scene = new Scene(this.grid);
		this.stage.setScene(this.scene);
		this.stage.sizeToScene();
		this.stage.show();
	}
	
	//-- ClockEditor --
	private ComboBox<ComboBoxItem> createUnitCbo()
	{
		final ComboBox<ComboBoxItem> cbo = new ComboBox<>();
		cbo.getItems().addAll(
		new ComboBoxItem(ClockEditor.UNITS[0], ""),
		new ComboBoxItem(ClockEditor.UNITS[1], "Jahr"),
		new ComboBoxItem(ClockEditor.UNITS[2], "Monat"),
		new ComboBoxItem(ClockEditor.UNITS[3], "Tag"),
		new ComboBoxItem(ClockEditor.UNITS[4], "Stunde"),
		new ComboBoxItem(ClockEditor.UNITS[5], "Minute"),
		new ComboBoxItem(ClockEditor.UNITS[6], "Sekunde"),
		new ComboBoxItem(ClockEditor.UNITS[7], "Millisekunde")
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
					for(int i = 0; i < ClockEditor.this.boxUnits.getChildren().size(); i++)
					{
						if(ClockEditor.this.boxUnits.getChildren().get(i) == cbo)
						{
							for(int j = i+1; j < ClockEditor.this.boxUnits.getChildren().size();)
							{
								ClockEditor.this.boxUnits.getChildren().remove(j);
							}
							break;
						}
					}
				}
				else
				{
					//- Add new empty ComboBox, if necessary -
					if(ClockEditor.this.boxUnits.getChildren().size() > 0)
					{
						if(((ComboBox<ComboBoxItem>) ClockEditor.this.boxUnits.getChildren().get(ClockEditor.this.boxUnits.getChildren().size()-1)).getSelectionModel().getSelectedItem().getValue() != -1)
						{
							ComboBox<ComboBoxItem> emptyCbo = ClockEditor.this.createUnitCbo();
							ClockEditor.this.addListenerToComboBox(emptyCbo);
							ClockEditor.this.boxUnits.getChildren().add(emptyCbo);
							ClockEditor.this.stage.sizeToScene();
						}
					}
				}
				
				//- Update Clock -
				if(ClockEditor.this.boxUnits.getChildren().size() > 0)
				{
					int[] units = new int[ClockEditor.this.boxUnits.getChildren().size()-1];
					for(int i = 0; i < units.length; i++)
					{
						units[i] = ((ComboBox<ComboBoxItem>) ClockEditor.this.boxUnits.getChildren().get(i)).getSelectionModel().getSelectedItem().getValue();
					}
					ClockEditor.this.clock.setUnits(units);
				}
				else
				{
					ClockEditor.this.clock.setUnits(new int[0]);
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
