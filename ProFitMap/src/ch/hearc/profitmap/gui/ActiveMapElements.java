package ch.hearc.profitmap.gui;


public class ActiveMapElements
{
	private static volatile ActiveMapElements instance = null;

	private MapElements mapElements;

	private ActiveMapElements()
	{
		mapElements = new MapElements();
	}

	public static ActiveMapElements getInstance()
	{
		if (instance == null)
		{
			synchronized (ActiveMapElements.class)
			{
				if (instance == null)
				{
					instance = new ActiveMapElements();
				}
			}
		}
		return instance;
	}

	public MapElements getMapElements()
	{
		return mapElements;
	}
}