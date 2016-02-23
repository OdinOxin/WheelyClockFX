package de.odinoxin.wheelyclock;

import de.odinoxin.wheelyclock.clock.Clock;
import de.odinoxin.wheelyclock.countdown.Countdown;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Menu extends Application
{
	private Stage stage;
	private Scene scene;
	private GridPane grid;
	
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception
	{
		boolean launched = false;
		if(this.getParameters() != null)
		{
			String[] parameters = this.getParameters().getUnnamed().toArray(new String[this.getParameters().getUnnamed().size()]);
			for(int i = 0; i < parameters.length; i++)
			{
				if(parameters[i].equalsIgnoreCase("clock"))
				{
					try
					{
						new Clock(parameters).start(new Stage());
						launched = true;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else if(parameters[i].equalsIgnoreCase("countdown"))
				{
					try
					{
						new Countdown(parameters).start(new Stage());
						launched = true;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		if(launched)
		{
			stage.close();
			return;
		}
		
		this.stage = stage;
		this.stage.setTitle("WheelyClock - Menu");
		
		this.grid = new GridPane();
		this.grid.setAlignment(Pos.CENTER);
		this.grid.setHgap(10);
		this.grid.setVgap(10);
		this.grid.setPadding(new Insets(10, 10, 10, 10));
		
		Label lblQuestion = new Label("What would you like to run?");
		grid.add(lblQuestion, 0, 0, 3, 1);
		
		Button btnClock = new Button("Clock");
		btnClock.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent ev)
			{
				try
				{
					new Clock().start(new Stage());
					Menu.this.stage.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		grid.add(btnClock, 0, 1);
		
		Button btnCountdown = new Button("Countdown");
		btnCountdown.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent ev)
			{
				try
				{
					new Countdown().start(new Stage());
					Menu.this.stage.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		grid.add(btnCountdown, 1, 1);
		
		Button btnStopwatch = new Button("Stopwatch");
		btnStopwatch.setDisable(true);
		grid.add(btnStopwatch, 2, 1);
		
		grid.add(new Separator(Orientation.VERTICAL), 3, 1);
		
		Button btnExit = new Button("Exit");
		btnExit.setTextFill(Color.RED);
		btnExit.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent ev)
			{
				Platform.exit();
			}
		});
		grid.add(btnExit, 4, 1);
		
		this.scene = new Scene(this.grid);
		this.stage.setScene(this.scene);
		this.stage.sizeToScene();
		this.stage.show();
	}
}
