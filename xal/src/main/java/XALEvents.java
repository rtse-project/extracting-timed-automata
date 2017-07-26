package XAL;
/**
 * Interface to implement in order to receive the events for the convertion.
 *
 * @author      Giovanni Liva (@thisthatDC)
 * @version     %I%, %G%
 */
public interface XALEvents {

    /**
     * The event must be implement in order to listen to the event of generation of XAL files
     */
    void OnGeneratingStart();

    /**
     * The event must be implement in order to listen to the event finish of the conversion phase
     */
    void OnGeneratingFinished();
}
