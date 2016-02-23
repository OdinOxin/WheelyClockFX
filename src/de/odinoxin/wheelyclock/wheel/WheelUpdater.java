package de.odinoxin.wheelyclock.wheel;

public class WheelUpdater implements Runnable
{
	private final Wheel wheel;
	private final int value;
	
	public WheelUpdater(Wheel wheel, int value)
	{
		this.wheel = wheel;
		this.value = value;
	}
	
	@Override
	public void run()
	{
		if(this.wheel != null)
			this.wheel.setValue(this.value);
	}
}
