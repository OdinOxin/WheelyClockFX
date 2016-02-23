package de.odinoxin.wheelyclock.wheel;

import de.odinoxin.wheelyclock.Editor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class WheelEditor extends Application implements Editor, ChangeListener<Boolean>
{
	//--- Fields ---
	private Scene scene;
	private Stage stage;
	
	private final Wheel wheel;
	private Wheel[] allWheels;

	//-- Wheel --
	//- Color -
	private ColorPicker clpWheelColor;
	//- NullColor -
	private ColorPicker clpWheelNullColor;
	//- Radius -
	private Slider sdrWheelRadius;
	private TextField txfWheelRadius;
	//- Thickness -
	private Slider sdrWheelThickness;
	private ToggleButton tglWheelThickness;
	private TextField txfWheelThickness;
	//- Stroked -
	private CheckBox cbxWheelStroked;
	
	//-- Value --
	//- Color -
	private ColorPicker clpValueColor;
	//- Maximum -
	private TextField txfValueMaximum;
	//- Font -
	private ComboBox<String> cbxValueFont;
	//- Size -
	private Slider sdrValueSize;
	
	//-- Text --
	//- Sg -
	private TextField txfTextSg;
	//- Pl -
	private TextField txfTextPl;
	//- Color -
	private ColorPicker clpTextColor;
	//- Font -
	private ComboBox<String> cbxTextFont;
	//- Size -
	private Slider sdrTextSize;
	//- Altitude -
	private Slider sdrTextY;
	
	//-- Footer --
	private Button btnOk;
	private CheckBox cbxUpdateAll;
	
	//--- Constructor ---
	public WheelEditor(Wheel wheel)
	{
		this.wheel = wheel;
		this.allWheels = new Wheel[]{this.wheel};
	}
	public WheelEditor(Wheel[] allWheels)
	{
		this.wheel = allWheels[0];
		this.allWheels = allWheels;
	}
	
	//--- Methods ---
	@Override
	public void start(Stage stage) throws Exception
	{
		this.stage = stage;
		this.stage.setTitle("WheelyClock - WheelEditor");
		
		VBox vbox = (VBox) FXMLLoader.load(this.getClass().getResource("wheeleditor.fxml"));
		this.cbxUpdateAll = (CheckBox) vbox.lookup("#cbxUpdateAll");
		
		this.btnOk = (Button) vbox.lookup("#btnOk");
		this.btnOk.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent ev)
			{
				WheelEditor.this.stage.close();
			}
		});
		
		ObservableList<String> fonts = FXCollections.observableList(Font.getFontNames());
		
		TabPane tabPane = (TabPane) vbox.lookup("#tabPane");
		this.initTabWheel((GridPane) tabPane.getTabs().get(0).getContent());
		this.initTabValue((GridPane) tabPane.getTabs().get(1).getContent());
		this.initTabText((GridPane) tabPane.getTabs().get(2).getContent());

		this.scene = new Scene(vbox);
		this.stage.setScene(this.scene);
		this.stage.sizeToScene();
		this.stage.show();
	}
	
	private void initTabWheel(GridPane gridWheel)
	{
		this.clpWheelColor = (ColorPicker) gridWheel.lookup("#clpWheelColor");
		this.clpWheelColor.setValue(this.wheel.getWheelColor());
		this.clpWheelColor.valueProperty().addListener(new ChangeListener<Color>()
		{
			@Override
			public void changed(ObservableValue<? extends Color> obsVal, Color oldVal, Color newVal)
			{
				Color color = newVal;
				Color nullColor = Color.color(color.getRed(), color.getGreen(), color.getBlue(), WheelEditor.this.clpWheelNullColor.getValue().getOpacity());
				WheelEditor.this.clpWheelNullColor.setValue(nullColor);
				if(WheelEditor.this.cbxUpdateAll.isSelected())
				{
					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
					{
						WheelEditor.this.allWheels[i].setWheelColor(color);
					}
				}
				else
				{
					WheelEditor.this.wheel.setWheelColor(color);
				}
			}
		});
		this.clpWheelNullColor = (ColorPicker) gridWheel.lookup("#clpWheelNullColor");
		this.clpWheelNullColor.setValue(this.wheel.getWheelNullColor());
		this.clpWheelNullColor.valueProperty().addListener(new ChangeListener<Color>()
		{
			@Override
			public void changed(ObservableValue<? extends Color> obsVal, Color oldVal, Color newVal)
			{
				if(WheelEditor.this.cbxUpdateAll.isSelected())
				{
					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
					{
						WheelEditor.this.allWheels[i].setWheelNullColor(newVal);
					}
				}
				else
				{
					WheelEditor.this.wheel.setWheelNullColor(newVal);
				}
			}
		});
		this.sdrWheelRadius = (Slider) gridWheel.lookup("#sdrWheelRadius");
		this.sdrWheelRadius.setMax(Math.min(Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight()) / 2);
		this.sdrWheelRadius.setMajorTickUnit(this.sdrWheelRadius.getMax() / 4);
		this.sdrWheelRadius.setLabelFormatter(new StringConverter<Double>()
		{
			@Override
			public String toString(Double value)
			{
				if(value == 0)
					return "0 %";
				return String.valueOf((int) ((value / WheelEditor.this.sdrWheelRadius.getMax())*100)) + " %";
			}
			
			@Override
			public Double fromString(String string)
			{
				return 0d;
			}
		});
		this.sdrWheelRadius.setValue(this.wheel.getWheelRadius());
//		this.sdrWheelRadius.valueProperty().addListener(new ChangeListener<Number>()
//		{
//			@Override
//			public void changed(ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal)
//			{
//				if(WheelEditor.this.cbxUpdateAll.isSelected())
//				{
//					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
//					{
//						WheelEditor.this.allWheels[i].setWheelRadius((double) newVal);
//					}
//				}
//				else
//				{
//					WheelEditor.this.wheel.setWheelRadius((double) newVal);
//				}
//				if(WheelEditor.this.tglWheelRadius.isSelected())
//				{
//					WheelEditor.this.txfWheelRadius.setText(String.valueOf((int) (double) newVal));
//				}
//			}
//		});
//		this.tglWheelRadius = (ToggleButton) gridWheel.lookup("#tglWheelRadius");
		this.txfWheelRadius = (TextField) gridWheel.lookup("#txfWheelRadius");
		this.txfWheelRadius.setText(String.valueOf((int) this.wheel.getWheelRadius()));
//		this.txfWheelRadius.textProperty().addListener(new ChangeListener<String>()
//		{
//			@Override
//			public void changed(ObservableValue<? extends String> obsVal, String oldVal, String newVal)
//			{
//				if(newVal.isEmpty())
//					return;
//				double wheelRadius;
//				try
//				{
//					wheelRadius = Double.parseDouble(newVal);
//					if(wheelRadius < 0)
//						throw new NumberFormatException();
//				}
//				catch(NumberFormatException ex)
//				{
//					WheelEditor.this.txfWheelRadius.setText(String.valueOf((int) WheelEditor.this.wheel.getWheelRadius()));
//					return;
//				}
//				if(WheelEditor.this.cbxUpdateAll.isSelected())
//				{
//					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
//					{
//						WheelEditor.this.allWheels[i].setWheelRadius(wheelRadius);
//					}
//				}
//				else
//				{
//					WheelEditor.this.wheel.setWheelRadius(wheelRadius);
//				}
//				if(WheelEditor.this.tglWheelRadius.isSelected())
//				{
//					WheelEditor.this.sdrWheelRadius.setValue(wheelRadius);
//				}
//			}
//		});
		
		this.sdrWheelThickness = (Slider) gridWheel.lookup("#sdrWheelThickness");
		this.sdrWheelThickness.setLabelFormatter(new StringConverter<Double>()
		{
			@Override
			public String toString(Double value)
			{
				if(value == 0)
					return "0 %";
				return String.valueOf((int) ((value / WheelEditor.this.sdrWheelThickness.getMax())*100)) + " %";
			}
			
			@Override
			public Double fromString(String string)
			{
				return 0d;
			}
		});
		this.sdrWheelThickness.setValue(this.wheel.getWheelThickness());
		this.sdrWheelThickness.valueProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal)
			{
				if(WheelEditor.this.cbxUpdateAll.isSelected())
				{
					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
					{
						WheelEditor.this.allWheels[i].setWheelThickness((double) newVal);
					}
				}
				else
				{
					WheelEditor.this.wheel.setWheelThickness((double) newVal);
				}
				if(WheelEditor.this.tglWheelThickness.isSelected())
				{
					WheelEditor.this.txfWheelThickness.setText(String.valueOf((int) (((double) newVal)*100)));
				}
			}
		});
		this.tglWheelThickness = (ToggleButton) gridWheel.lookup("#tglWheelThickness");
		this.txfWheelThickness = (TextField) gridWheel.lookup("#txfWheelThickness");
		this.txfWheelThickness.setText(String.valueOf((int) (this.wheel.getWheelThickness() * 100)));
		this.txfWheelThickness.textProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> obsVal, String oldVal, String newVal)
			{
				if(newVal.isEmpty())
					return;
				double wheelThickness;
				try
				{
					wheelThickness = Double.parseDouble(newVal) / 100;
					if(wheelThickness < 0
					|| wheelThickness > 1)
						throw new NumberFormatException();
				}
				catch(NumberFormatException ex)
				{
					WheelEditor.this.txfWheelThickness.setText(String.valueOf((int) (WheelEditor.this.wheel.getWheelThickness()*100)));
					return;
				}
				if(WheelEditor.this.cbxUpdateAll.isSelected())
				{
					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
					{
						WheelEditor.this.allWheels[i].setWheelThickness(wheelThickness);
					}
				}
				else
				{
					WheelEditor.this.wheel.setWheelThickness(wheelThickness);
				}
				if(WheelEditor.this.tglWheelThickness.isSelected())
				{
					WheelEditor.this.sdrWheelThickness.setValue(wheelThickness);
				}
			}
		});
		
		this.cbxWheelStroked = (CheckBox) gridWheel.lookup("#cbxWheelStroked");
		this.cbxWheelStroked.setSelected(this.wheel.isWheelStroked());
		this.cbxWheelStroked.selectedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal)
			{
				WheelEditor.this.wheel.setWheelStroked(newVal);
				if(WheelEditor.this.cbxUpdateAll.isSelected())
				{
					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
					{
						WheelEditor.this.allWheels[i].setWheelStroked(newVal);
					}
				}
			}
		});
	}
	private void initTabValue(GridPane gridValue)
	{
		this.clpValueColor = (ColorPicker) gridValue.lookup("#clpValueColor");
		this.clpValueColor.setValue(this.wheel.getValueColor());
		this.clpValueColor.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent ev)
			{
				if(WheelEditor.this.cbxUpdateAll.isSelected())
				{
					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
					{
						WheelEditor.this.allWheels[i].setValueColor(WheelEditor.this.clpValueColor.getValue());
						WheelEditor.this.allWheels[i].setTextColor(WheelEditor.this.clpValueColor.getValue());
					}
				}
				else
				{
					WheelEditor.this.wheel.setValueColor(WheelEditor.this.clpValueColor.getValue());
					WheelEditor.this.wheel.setTextColor(WheelEditor.this.clpValueColor.getValue());
				}
			}
		});
		
		this.txfValueMaximum = (TextField) gridValue.lookup("#txfValueMaximum");
		this.txfValueMaximum.setText(String.valueOf(this.wheel.getValueMaximum()));
		this.txfValueMaximum.focusedProperty().addListener(this);
		this.txfValueMaximum.textProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> obsVal, String oldVal, String newVal)
			{
				try
				{
					int value = Integer.parseInt(newVal);
					if(value > 0
					&& value <= 1000)
					{
						WheelEditor.this.wheel.setValueMaximum(value);
						if(WheelEditor.this.cbxUpdateAll.isSelected())
						{
							for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
							{
								WheelEditor.this.allWheels[i].setValueMaximum(value);
							}
						}
					}
				}
				catch(NumberFormatException ex){}
			}
		});
//		this.cbxValueFont = new ComboBox<String>(fonts);
//		this.cbxValueFont.getSelectionModel().select(WheelEditor.this.wheel.getValueFont().getName());
//		this.cbxValueFont.setOnAction(new EventHandler<ActionEvent>()
//		{
//			@Override
//			public void handle(ActionEvent ev)
//			{
//				WheelEditor.this.wheel.setValueFont(new Font(cbxValueFont.getSelectionModel().getSelectedItem(), WheelEditor.this.wheel.getValueFont().getSize()));
//				if(WheelEditor.this.cbxUpdateAll.isSelected())
//				{
//					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
//					{
//						WheelEditor.this.allWheels[i].setValueFont(new Font(cbxValueFont.getSelectionModel().getSelectedItem(), WheelEditor.this.wheel.getValueFont().getSize()));
//					}
//				}
//			}
//		});
//		this.sdrValueSize = new Slider(0, WheelEditor.VALUE_MAXSIZE, this.wheel.getValueFont().getSize());
//		this.sdrValueSize.valueProperty().addListener(new ChangeListener<Number>()
//		{
//			@Override
//			public void changed(ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal)
//			{
//				WheelEditor.this.wheel.setValueFont(new Font(WheelEditor.this.wheel.getValueFont().getName(), (double) newVal));
//				if(WheelEditor.this.cbxUpdateAll.isSelected())
//				{
//					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
//					{
//						WheelEditor.this.allWheels[i].setValueFont(new Font(WheelEditor.this.wheel.getValueFont().getName(), (double) newVal));
//					}
//				}
//			}
//		});
	}
	private void initTabText(GridPane gridValue)
	{
//		this.txfTextSg = new TextField(this.wheel.getTextSg());
//		this.txfTextSg.focusedProperty().addListener(this);
//		this.txfTextSg.textProperty().addListener(new ChangeListener<String>()
//		{
//			@Override
//			public void changed(ObservableValue<? extends String> obsVal, String oldVal, String newVal)
//			{
//				WheelEditor.this.wheel.setTextSg(WheelEditor.this.txfTextSg.getText());
//				if(WheelEditor.this.cbxUpdateAll.isSelected())
//				{
//					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
//					{
//						WheelEditor.this.allWheels[i].setTextSg(WheelEditor.this.txfTextSg.getText());
//					}
//				}
//			}
//		});
//		this.txfTextPl = new TextField(this.wheel.getTextPl());
//		this.txfTextPl.focusedProperty().addListener(this);
//		this.txfTextPl.textProperty().addListener(new ChangeListener<String>()
//		{
//			@Override
//			public void changed(ObservableValue<? extends String> obsVal, String oldVal, String newVal)
//			{
//				WheelEditor.this.wheel.setTextPl(WheelEditor.this.txfTextPl.getText());
//				if(WheelEditor.this.cbxUpdateAll.isSelected())
//				{
//					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
//					{
//						WheelEditor.this.allWheels[i].setTextPl(WheelEditor.this.txfTextPl.getText());
//					}
//				}
//			}
//		});
//		this.clpTextColor = new ColorPicker(this.wheel.getTextColor());
//		this.clpTextColor.setOnAction(new EventHandler<ActionEvent>()
//		{
//			@Override
//			public void handle(ActionEvent ev)
//			{
//				WheelEditor.this.wheel.setTextColor(WheelEditor.this.clpTextColor.getValue());
//				if(WheelEditor.this.cbxUpdateAll.isSelected())
//				{
//					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
//					{
//						WheelEditor.this.allWheels[i].setTextColor(WheelEditor.this.clpTextColor.getValue());
//					}
//				}
//			}
//		});
//		this.cbxTextFont = new ComboBox<String>(fonts);
//		this.cbxTextFont.getSelectionModel().select(WheelEditor.this.wheel.getTextFont().getName());
//		this.cbxTextFont.setOnAction(new EventHandler<ActionEvent>()
//		{
//			@Override
//			public void handle(ActionEvent ev)
//			{
//				WheelEditor.this.wheel.setTextFont(new Font(cbxTextFont.getSelectionModel().getSelectedItem(), WheelEditor.this.wheel.getTextFont().getSize()));
//				if(WheelEditor.this.cbxUpdateAll.isSelected())
//				{
//					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
//					{
//						WheelEditor.this.allWheels[i].setTextFont(new Font(cbxTextFont.getSelectionModel().getSelectedItem(), WheelEditor.this.wheel.getTextFont().getSize()));
//					}
//				}
//			}
//		});
//		this.sdrTextSize = new Slider(0, WheelEditor.TEXT_MAXSIZE, this.wheel.getTextFont().getSize());
//		this.sdrTextSize.valueProperty().addListener(new ChangeListener<Number>()
//		{
//			@Override
//			public void changed(ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal)
//			{
//				WheelEditor.this.wheel.setTextFont(new Font(WheelEditor.this.wheel.getTextFont().getName(), (double) newVal));
//				if(WheelEditor.this.cbxUpdateAll.isSelected())
//				{
//					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
//					{
//						WheelEditor.this.allWheels[i].setTextFont(new Font(WheelEditor.this.wheel.getTextFont().getName(), (double) newVal));
//					}
//				}
//			}
//		});
//		this.sdrTextY = new Slider(-1.2, 1.2, this.wheel.getTextY());
//		this.sdrTextY.valueProperty().addListener(new ChangeListener<Number>()
//		{
//			@Override
//			public void changed(ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal)
//			{
//				WheelEditor.this.wheel.setTextY((double) newVal);
//				if(WheelEditor.this.cbxUpdateAll.isSelected())
//				{
//					for(int i = 0; i < WheelEditor.this.allWheels.length; i++)
//					{
//						WheelEditor.this.allWheels[i].setTextY((double) newVal);
//					}
//				}
//			}
//		});
	}
	
	@Override
	public void exit()
	{
		if(this.stage != null)
		{
			this.stage.close();
			this.stage = null;
		}
	}
	
	//--- Focus Listener ---
	@Override
	public void changed(ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal)
	{
		if(newVal)
		{
			Platform.runLater(new Runnable()
			{
				@Override
				public void run()
				{
					((TextField) ((ReadOnlyBooleanProperty) obsVal).getBean()).selectAll();
				}
			});
		}
	}
}
