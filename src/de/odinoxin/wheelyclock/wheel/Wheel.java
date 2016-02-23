package de.odinoxin.wheelyclock.wheel;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Wheel extends Pane
{
	//--- Fields ---
	//- Wheel -
	private Arc[] wheelPins;
	private Arc[] wheelNullPins;
	private Color wheelColor;
	private double wheelRadius;
	private double wheelThickness;
	private boolean wheelStroked;
	private Color wheelNullColor;
	
	//- Value -
	private Text valueLabel;
	private Color valueColor;
	private int valueMaximum;
	private int value;
	private Font valueFont;
	private int valueUnit;
	
	//- Text -
	private Text textLabel;
	private Color textColor;
	private String textSg;
	private String textPl;
	private Font textFont;
	private double textY;
	
	//--- Methods ---
	//-- Constructor --
	public Wheel()
	{
		super();
	}
	public Wheel(Color wheelColor, double wheelRadius, double wheelThickness, boolean wheelStroked, Color wheelNullColor, Color valueColor, int valueMaximum, int value, Font valueFont, int valueUnit, Color textColor, String textSg, String textPl, Font textFont, double textY)
	{
		this();
		
		this.wheelColor = wheelColor;
		this.wheelRadius = wheelRadius;
		this.wheelThickness = wheelThickness;
		this.wheelStroked = wheelStroked;
		this.wheelNullColor = wheelNullColor;
		
		this.valueColor = valueColor;
		this.valueMaximum = valueMaximum;
		this.value = value;
		this.valueFont = valueFont;
		this.valueUnit = valueUnit;
		
		this.textColor = textColor;
		this.textSg = textSg;
		this.textPl = textPl;
		this.textFont = textFont;
		this.textY = textY;
		this.refreshLayout();
	}
	
	//-- Update --
	private void refreshLayout()
	{
		this.wheelPins = new Arc[this.valueMaximum];
		this.wheelNullPins = new Arc[this.valueMaximum];
		for(int i = 0; i < this.valueMaximum; i++)
		{
			this.wheelPins[i] = new Arc(
					this.wheelRadius, // CenterX
					this.wheelRadius, // CenterY
					this.wheelRadius - (this.wheelThickness*this.wheelRadius)/2, // RadiusX
					this.wheelRadius - (this.wheelThickness*this.wheelRadius)/2, // RadiusY
					90 + (i * (-360d/this.valueMaximum)), // StartAngle
					this.wheelStroked ? (-360d/(2*this.valueMaximum)) : (-360d/this.valueMaximum)); // Lenght
			this.wheelPins[i].setStrokeLineCap(StrokeLineCap.BUTT);
			this.wheelPins[i].setStrokeLineJoin(StrokeLineJoin.ROUND);
			this.wheelPins[i].setStroke(this.wheelColor);
			this.wheelPins[i].setStrokeWidth(this.wheelThickness*this.wheelRadius);
			this.wheelPins[i].setFill(Color.TRANSPARENT);
			
			this.wheelNullPins[i] = new Arc(
					this.wheelRadius, // CenterX
					this.wheelRadius, // CenterY
					this.wheelRadius - (this.wheelThickness*this.wheelRadius)/2, // RadiusX
					this.wheelRadius - (this.wheelThickness*this.wheelRadius)/2, // RadiusY
					90 + (i * (-360d/this.valueMaximum)), // StartAngle
					this.wheelStroked ? (-360d/(2*this.valueMaximum)) : (-360d/this.valueMaximum)); // Lenght
			this.wheelNullPins[i].setStrokeLineCap(StrokeLineCap.BUTT);
			this.wheelNullPins[i].setStrokeLineJoin(StrokeLineJoin.ROUND);
			this.wheelNullPins[i].setStroke(this.wheelNullColor);
			this.wheelNullPins[i].setStrokeWidth(this.wheelThickness*this.wheelRadius);
			this.wheelNullPins[i].setFill(Color.TRANSPARENT);
		}
		
		this.textLabel = new Text(0, this.wheelRadius + (this.textY*this.wheelRadius), this.value == 1 ? this.textSg : this.textPl);
		this.textLabel.setFont(this.textFont);
		this.textLabel.setFill(this.textColor);
		
		this.valueLabel = new Text(0, 0, String.valueOf(this.value));
		this.valueLabel.setFont(this.valueFont);
		this.valueLabel.setFill(this.valueColor);
		this.draw();
	}
	public void draw()
	{
		if(this.valueMaximum <= 0)
			return;
		this.getChildren().clear();
		for(int i = 0; i < this.wheelPins.length; i++)
		{
			this.getChildren().add(this.wheelNullPins[i]);
			if(i < this.value)
				this.getChildren().add(this.wheelPins[i]);
		}
		
		this.textLabel.setText(this.value == 1 ? this.textSg : this.textPl);
		this.textLabel.setX(this.wheelRadius - (this.textLabel.getBoundsInParent().getWidth() / 2));
		this.getChildren().add(this.textLabel);
		
		this.valueLabel.setText(String.valueOf(this.value));
		this.valueLabel.setX(this.wheelRadius - (this.valueLabel.getBoundsInParent().getWidth() / 2));
		this.valueLabel.setY(this.wheelRadius + (this.valueLabel.getBoundsInParent().getHeight() / 4));
		this.getChildren().add(this.valueLabel);
	}

	//-- Getters --
	//- Wheel -
	public Color getWheelColor()
	{
		return wheelColor;
	}
	public double getWheelRadius()
	{
		return wheelRadius;
	}
	public double getWheelThickness()
	{
		return wheelThickness;
	}
	public boolean isWheelStroked()
	{
		return wheelStroked;
	}
	public Color getWheelNullColor()
	{
		return this.wheelNullColor;
	}
	
	//- Value -
	public Color getValueColor()
	{
		return valueColor;
	}
	public int getValueMaximum()
	{
		return valueMaximum;
	}
	public int getValue()
	{
		return value;
	}
	public Font getValueFont()
	{
		return this.valueFont;
	}
	public int getValueUnit()
	{
		return this.valueUnit;
	}
	
	//- Text -
	public Color getTextColor()
	{
		return textColor;
	}
	public String getTextSg()
	{
		return textSg;
	}
	public String getTextPl()
	{
		return textPl;
	}
	public Font getTextFont()
	{
		return this.textFont;
	}
	public double getTextY()
	{
		return this.textY;
	}
	
	//- EventHandler -
	public EventHandler<MouseEvent> getWheelEditorEventHandler(final Wheel[] wheels, final int pos, final WheelHandler wheelHandler)
	{
		return new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent ev)
			{
				Wheel[] tmpWheels = (Wheel[]) wheels.clone();
				Wheel toFront = wheels[pos];
				tmpWheels[pos] = tmpWheels[0];
				tmpWheels[0] = toFront;
				
				if(ev.getButton() == MouseButton.PRIMARY
				&& ev.getClickCount() == 2)
				{
					if(wheelHandler.getWheelEditor() != null)
					{
						wheelHandler.getWheelEditor().exit();
					}
					wheelHandler.setWheelEditor(new WheelEditor(tmpWheels));
					try
					{
						wheelHandler.getWheelEditor().start(new Stage());
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		};
	}
	
	//-- Setters --
	//- Wheel -
	public void setWheelColor(Color wheelColor)
	{
		this.wheelColor = wheelColor;
		this.refreshLayout();
	}
	public void setWheelRadius(double wheelRadius)
	{
		if(wheelRadius >= 0)
		{
			this.wheelRadius = wheelRadius;
			this.refreshLayout();
		}
	}
	public void setWheelThickness(double wheelThickness)
	{
		if(wheelThickness >= 0)
		{
			this.wheelThickness = wheelThickness;
			this.refreshLayout();
		}
	}
	public void setWheelStroked(boolean wheelStroked)
	{
		this.wheelStroked = wheelStroked;
		this.refreshLayout();
	}
	public void setWheelNullColor(Color wheelNullColor)
	{
		this.wheelNullColor = wheelNullColor;
		this.refreshLayout();
	}
	
	//- Value -
	public void setValueColor(Color valueColor)
	{
		this.valueColor = valueColor;
		this.refreshLayout();
	}
	public void setValueMaximum(int valueMaximum)
	{
		if(valueMaximum >= 0)
		{
			this.valueMaximum = valueMaximum;
			this.refreshLayout();
		}
	}
	public void setValue(int value)
	{
		if(value >= 0
		&& this.value != value)
		{
			this.value = value;
			this.draw();
		}
	}
	public void setValueFont(Font valueFont)
	{
		this.valueFont = valueFont;
		refreshLayout();
	}
	public void setvalueUnit(int valueUnit)
	{
		this.valueUnit = valueUnit;
	}
	
	//- Text -
	public void setTextColor(Color textColor)
	{
		this.textColor = textColor;
		this.refreshLayout();
	}
	public void setTextSg(String textSg)
	{
		this.textSg = textSg;
		this.draw();
	}
	public void setTextPl(String textPl)
	{
		this.textPl = textPl;
		this.draw();
	}
	public void setTextFont(Font textFont)
	{
		this.textFont = textFont;
		refreshLayout();
	}
	public void setTextY(double textY)
	{
		this.textY = textY;
		this.refreshLayout();
	}
}
