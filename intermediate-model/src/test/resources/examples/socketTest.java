import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

public class SocketTest {

	protected void
	found(
			DeviceTivoManager	_tivo_manager,
			InetAddress _address,
			String				_server_name,
			String				_machine )
	{
		boolean	first_time = false;

		synchronized( this ){

			if ( server_name == null ){

				server_name	= _server_name;

				first_time = true;
			}
		}

		if ( _machine == null && !tried_tcp_beacon ){

			try{
				Socket socket = new Socket();

				try{
					socket.connect( new InetSocketAddress( _address, 2190 ), 5000 );

					socket.setSoTimeout( 5000 );

					DataOutputStream dos = new DataOutputStream( socket.getOutputStream());

					byte[]	beacon_out = _tivo_manager.encodeBeacon( false, 0 );

					dos.writeInt( beacon_out.length );

					dos.write( beacon_out );

					DataInputStream dis = new DataInputStream( socket.getInputStream());

					int len = dis.readInt();

					if ( len < 65536 ){

						byte[] bytes = new byte[len];

						int	pos = 0;

						while( pos < len ){

							int read = dis.read( bytes, pos, len-pos );

							pos += read;
						}

						Map<String,String> beacon_in = _tivo_manager.decodeBeacon( bytes, len );

						_machine = beacon_in.get( "machine" );
					}
				}finally{

					socket.close();
				}
			}catch( Throwable e ){

			}finally{

				tried_tcp_beacon = true;
			}
		}

		if ( _machine != null ){

			String existing = getMachineName();

			if ( existing == null || !existing.equals( _machine )){

				setPersistentStringProperty( PP_TIVO_MACHINE, _machine );
			}
		}

		setAddress( _address );

		alive();

		if ( first_time ){

			browseReceived();
		}
	}

	protected long
	getEstimatedTargetSize()
	{
		// TODO: we need access to max bitrate info... and then use duration and increase by, say, 5%

		try{
			long	duration_secs = getDurationMillis()/1000;

			if ( duration_secs == 0 ){

				long length = file.getSourceFile().getLength();

				return( length * 10 );

			}else{
				long mb_per_sec = 3;	// upper limit of 3 MB a sec assumed

				return( duration_secs * mb_per_sec*1024*1024L );
			}
		}catch( Throwable e ){
		}

		return( 0 );
	}

}