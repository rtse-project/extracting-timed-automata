
package com.aelitis.azureus.plugins.net.buddy.swt;

public class
BuddyPluginViewBetaChat
		implements ChatListener {

	private
	BuddyPluginViewBetaChat(
			BuddyPluginView	_view,
			BuddyPlugin		_plugin,
			ChatInstance	_chat )
	{

		shell.addControlListener(
				new ControlListener()
				{
					private volatile Rectangle last_position;

					private FrequencyLimitedDispatcher disp =
							new FrequencyLimitedDispatcher(
									new AERunnable() {

										@Override
										public void
										runSupport()
										{
											Rectangle	pos = last_position;

											String str = pos.x+","+pos.y+","+pos.width+","+pos.height;

											COConfigurationManager.setParameter( "azbuddy.dchat.ui.last.win.pos", str );
										}
									},
									1000 );

					public void
					controlResized(
							ControlEvent e)
					{
						handleChange();
					}

					public void
					controlMoved(
							ControlEvent e)
					{
						try {
							handleChange();
						} catch(Exception e) {}
					}

					private void
					handleChange()
					{
						last_position = shell.getBounds();

						disp.dispatch();
					}
				});


		int DEFAULT_WIDTH	= 500;


	}

	protected
	BuddyPluginViewBetaChat(
			BuddyPluginView	_view,
			BuddyPlugin		_plugin,
			ChatInstance	_chat,
			Composite		_parent )
	{
		view	= _view;
		plugin	= _plugin;
		chat	= _chat;
		beta	= plugin.getBeta();

		lu		= plugin.getPluginInterface().getUtilities().getLocaleUtilities();

		build( _parent );
	}
}