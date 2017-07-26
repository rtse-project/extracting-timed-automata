import java.util.Map;

public class NotWellParsed {
	public void m(){
		if ( dht_plugin_pi != null ){

			dht_plugin_public = (DHTPlugin)dht_plugin_pi.getPlugin();

			/*
			if ( Constants.isCVSVersion()){

				addListener(
						new SubscriptionManagerListener()
						{
							public void
							subscriptionAdded(
								Subscription subscription )
							{
							}

							public void
							subscriptionChanged(
								Subscription		subscription )
							{
							}

							public void
							subscriptionRemoved(
								Subscription subscription )
							{
							}

							public void
							associationsChanged(
								byte[] hash )
							{
								System.out.println( "Subscriptions changed: " + ByteFormatter.encodeString( hash ));

								Subscription[] subs = getKnownSubscriptions( hash );

								for (int i=0;i<subs.length;i++){

									System.out.println( "    " + subs[i].getString());
								}
							}
						});
			}
			*/

			default_pi.getDownloadManager().addListener(
					new DownloadManagerListener()
					{
						public void
						downloadAdded(
								Download	download )
						{
							Torrent	torrent = download.getTorrent();

							if ( torrent != null ){

								byte[]	hash = torrent.getHash();

								Object[] entry;

								synchronized( potential_associations2 ){

									entry = (Object[])potential_associations2.remove( new HashWrapper( hash ));
								}

								if ( entry != null ){

									SubscriptionImpl[] subs = (SubscriptionImpl[])entry[0];

									String	subs_str = "";
									for (int i=0;i<subs.length;i++){
										subs_str += (i==0?"":",") + subs[i].getName();
									}

									log( "Applying deferred asocciation for " + ByteFormatter.encodeString( hash ) + " -> " + subs_str );

									recordAssociationsSupport(
											hash,
											subs,
											((Boolean)entry[1]).booleanValue());
								}
							}
						}

						public void
						downloadRemoved(
								Download	download )
						{
						}
					},
					false );

			default_pi.getDownloadManager().addDownloadWillBeAddedListener(
					new DownloadWillBeAddedListener() {

						public void
						initialised(
								Download download )
						{
							Torrent	torrent = download.getTorrent();

							if ( torrent != null ){

								byte[]	hash = torrent.getHash();

								Object[] entry;

								synchronized( potential_associations2 ){

									entry = (Object[])potential_associations2.get( new HashWrapper( hash ));
								}

								if ( entry != null ){

									SubscriptionImpl[] subs = (SubscriptionImpl[])entry[0];

									prepareDownload( download, subs );
								}
							}
						}
					});

			TorrentUtils.addTorrentAttributeListener(
					new TorrentUtils.torrentAttributeListener()
					{
						public void
						attributeSet(
								TOTorrent 	torrent,
								String 		attribute,
								Object 		value )
						{
							if ( attribute == TorrentUtils.TORRENT_AZ_PROP_OBTAINED_FROM ){

								try{
									checkPotentialAssociations( torrent.getHash(), (String)value );

								}catch( Throwable e ){

									Debug.printStackTrace(e);
								}
							}
						}
					});

			DelayedTask delayed_task = UtilitiesImpl.addDelayedTask( "Subscriptions",
					new Runnable()
					{
						public void
						run()
						{
							new AEThread2( "Subscriptions:delayInit", true )
							{
								public void
								run()
								{
									asyncInit();
								}
							}.start();

						}

						protected void
						asyncInit()
						{
							Download[] downloads = default_pi.getDownloadManager().getDownloads();

							for (int i=0;i<downloads.length;i++){

								Download download = downloads[i];

								if ( download.getBooleanAttribute( ta_subs_download )){

									Map rd = download.getMapAttribute( ta_subs_download_rd );

									boolean	delete_it;

									if ( rd == null ){

										delete_it = true;

									}else{

										delete_it = !recoverSubscriptionUpdate( download, rd );
									}

									if ( delete_it ){

										removeDownload( download, true );
									}
								}
							}

							default_pi.getDownloadManager().addListener(
									new DownloadManagerListener()
									{
										public void
										downloadAdded(
												final Download	download )
										{
											// if ever changed to handle non-persistent then you need to fix init deadlock
											// potential with share-hoster plugin

											if ( !downloadIsIgnored( download )){

												if ( !dht_plugin_public.isInitialising()){

													// if new download then we want to check out its subscription status

													lookupAssociations( download.getMapAttribute( ta_subscription_info ) == null );

												}else{

													new AEThread2( "Subscriptions:delayInit", true )
													{
														public void
														run()
														{
															lookupAssociations( download.getMapAttribute( ta_subscription_info ) == null );
														}
													}.start();
												}
											}
										}

										public void
										downloadRemoved(
												Download	download )
										{
										}
									},
									false );

							for (int i=0;i<PUB_ASSOC_CONC_MAX;i++){

								if ( publishAssociations()){

									break;
								}
							}

							publishSubscriptions();

							COConfigurationManager.addParameterListener(
									CONFIG_MAX_RESULTS,
									new ParameterListener()
									{
										public void
										parameterChanged(
												String	 name )
										{
											final int	max_results = COConfigurationManager.getIntParameter( CONFIG_MAX_RESULTS );

											new AEThread2( "Subs:max results changer", true )
											{
												public void
												run()
												{
													checkMaxResults( max_results );
												}
											}.start();
										}
									});

							SimpleTimer.addPeriodicEvent(
									"SubscriptionChecker",
									TIMER_PERIOD,
									new TimerEventPerformer()
									{
										private int	ticks;

										public void
										perform(
												TimerEvent event )
										{
											ticks++;

											checkStuff( ticks );
										}
									});
						}
					});

			delayed_task.queue();
		}
	}
}